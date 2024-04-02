package regalloc;

import gen.asm.*;

import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

import static gen.asm.Register.Arch.*;

public class GraphColouringRegAlloc implements AssemblyPass {

    public static final GraphColouringRegAlloc INSTANCE = new GraphColouringRegAlloc();

    private List<ControlFlowGraph> flowGraphs ;
    private List<InterferenceGraph> interferenceGraphs ;

    private final static List<Register> allAvailableRegs= List.of(
            t0, t1, t2,
            t3, t4, t5, t6, t7, t8, t9,
            s0, s1, s2, s3, s4, s5, s6, s7);
    private static List<Register> opRegs;


    private static List<Register> spillRegs;

    @Override
    public AssemblyProgram apply(AssemblyProgram program) {
        spillRegs = allAvailableRegs.subList(0,3);
        opRegs = allAvailableRegs.subList(3,18); //todo change 18 to smaller values to stress spilling



        flowGraphs= new ArrayList<>();
        interferenceGraphs = new ArrayList<>();
        AssemblyProgram newProg = new AssemblyProgram();

        // we assume that each function has a single corresponding text section

        for(AssemblyProgram.Section section:program.sections){
            if(section.type == AssemblyProgram.Section.Type.DATA) {
                newProg.emitSection(section);
                continue;
            }
            assert (section.type == AssemblyProgram.Section.Type.TEXT);


            AssemblyProgram.Section newSection = newProg.newSection(AssemblyProgram.Section.Type.TEXT);

            ControlFlowGraph g = new ControlFlowGraph(section);
            flowGraphs.add(g);
            InterferenceGraph ig= new InterferenceGraph(g,opRegs.size());
            interferenceGraphs.add(ig);

            //todo restore this
            Set<Instruction> coveredInstructions = g.getNodesReversePreOrder().stream()
                    .filter(node-> node.data instanceof Instruction)
                    .map(node-> (Instruction)node.data)
                    .collect(Collectors.toSet());

            Map<Register,Label> vrMap= collectUsedRegisters(section,coveredInstructions,ig);

            // allocate one label for each spilled register in a new data section
            AssemblyProgram.Section dataSec = newProg.newSection(AssemblyProgram.Section.Type.DATA);
            dataSec.emit("Allocated labels for used registers");
            for (Map.Entry<Register, Label> e : vrMap.entrySet()) {
                Label lbl = e.getValue();
                dataSec.emit(new Directive("align 2"));
                dataSec.emit(lbl);
                dataSec.emit(new Directive("space " + 4));
            }
            List<Map.Entry<Register, Label>> labelPairs= vrMap.entrySet().stream().toList();
            List<Map.Entry<Register, Label>> reversedRegLabelPairs = new ArrayList<>(labelPairs);
            Collections.reverse(reversedRegLabelPairs);

            for(AssemblyItem item:section.items){
                switch (item){
                    case Comment x->newSection.emit(x);
                    case Label x->newSection.emit(x);
                    case Directive x->newSection.emit(x);

                    case Instruction insn->{
//                        if(!coveredInstructions.contains(insn)){ todo restore this
//                            continue;
//                        }
                        if (insn == Instruction.Nullary.pushRegisters) {
                            newSection.emit("---PUSH REGISTERS START---");

                            for (Map.Entry<Register, Label> entry : labelPairs) {
                                Register register = entry.getKey();
                                Label label = entry.getValue();
                                newSection.emit(OpCode.ADDIU, Arch.sp, Arch.sp, -4);
                                if (ig.spilled.contains(register)) {
                                    newSection.emit(OpCode.LA, Arch.t0, label);
                                    newSection.emit(OpCode.LW, Arch.t0, Arch.t0, 0);

                                    // push $t0 onto stack
                                    newSection.emit(OpCode.SW, Arch.t0, Arch.sp, 0);
                                } else {
                                    // push register onto stack
                                    newSection.emit(OpCode.SW, register, Arch.sp, 0);
                                }
                            }
                            newSection.emit("---PUSH REGISTERS END---");

                        } else if (insn == Instruction.Nullary.popRegisters) {
                            newSection.emit("---POP REGISTERS START---");
                            for (Map.Entry<Register, Label> pair : reversedRegLabelPairs) {
                                Register reg = pair.getKey();
                                Label label= pair.getValue();
                                if(ig.spilled.contains(reg)) {
                                    Register t0= spillRegs.getFirst();
                                    Register t1= spillRegs.get(1);
                                    // pop from stack into $t0
                                    newSection.emit(OpCode.LW, t0, sp, 0);

                                    // store content of $t0 in memory at label
                                    newSection.emit(OpCode.LA, t1, label);
                                    newSection.emit(OpCode.SW, t0, t1, 0);
                                }else{
                                    // pop from stack into previous reg
                                    newSection.emit(OpCode.LW, reg, sp, 0);
                                }
                                newSection.emit(OpCode.ADDIU, sp, sp, 4);
                            }
                            newSection.emit("---POP REGISTERS END---");
                        } else
                            emitInstructionWithoutVirtualRegister(insn,newSection,ig,vrMap);
                    }
                }
            }
        }
        return newProg;
    }

    private Map<Register, Label> collectUsedRegisters(AssemblyProgram.Section section,
                                                      Set<Instruction> coveredInstructions,
                                                      InterferenceGraph ig) {
        final Map<Register, Label> vrMap = new HashMap<>();
        for (AssemblyItem item : section.items) {
            if (!(item instanceof Instruction insn)) {
                continue;
            }
//            if (!coveredInstructions.contains(insn)) { todo restore this
//                continue;
//            }
            for (Register reg : insn.registers()) {
                if (reg instanceof Virtual vr) {
                    if(ig.spilled.contains(vr)) {
                        Label l = Label.create(vr.toString());
                        vrMap.put(vr, l);
                    }
                    else {
                        Register mappedReg = opRegs.get(ig.colorings.get(vr));
                        vrMap.putIfAbsent(mappedReg,Label.create(mappedReg.toString()));
                    }
                }
            }
        }
        return vrMap;
    }

    private void emitInstructionWithoutVirtualRegister(Instruction insn, AssemblyProgram.Section section,
                                                       InterferenceGraph ig, Map<Register, Label> vrMap) {
        Map<Register,Register> vrToAr= new HashMap<>();
        Stack<Register> freeTempRegs = new Stack<>();
        freeTempRegs.addAll(spillRegs);

        for(Register r:insn.registers()){
            if(!r.isVirtual()){//only need to modify virtual regs
                continue;
            }
            if(ig.spilled.contains(r)){
                Register temp= freeTempRegs.pop();
                vrToAr.put(r,temp);
            }
            else{
                int index = ig.colorings.get(r);
                Register newReg = opRegs.get(index);
                vrToAr.put(r, newReg);
            }
        }

        for (Register reg : insn.uses()) {
            if (ig.spilled.contains(reg)) {
                Register tmp = vrToAr.get(reg);
                Label label = vrMap.get(reg);
                section.emit(OpCode.LA, tmp, label);
                section.emit(OpCode.LW, tmp, tmp, 0);
            }
        }
        section.emit(insn.rebuild(vrToAr));

        if (insn.def() != null) {
            if (ig.spilled.contains(insn.def())) {
                Register tmpVal = vrToAr.get(insn.def());
                Register tmpAddr = spillRegs.stream().filter(x->x!=tmpVal).findFirst().get();
                Label label = vrMap.get(insn.def());

                section.emit(OpCode.LA, tmpAddr, label);
                section.emit(OpCode.SW, tmpVal, tmpAddr, 0);
            }
        }
    }

    public void printLiveness(PrintWriter writer){
        writer.println("digraph liveness {");
        for (ControlFlowGraph g: flowGraphs)
            g.print(writer);
        writer.println("}");
    }

    public void printInterference(PrintWriter writer){
        writer.println("graph interference {");
        for (InterferenceGraph g: interferenceGraphs)
            g.print(writer);
        writer.println("}");
    }



}
