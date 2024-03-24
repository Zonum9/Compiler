package regalloc;

import gen.asm.*;

import java.io.PrintWriter;
import java.util.*;

import static gen.asm.Register.Arch.*;

public class GraphColouringRegAlloc implements AssemblyPass {

    public static final GraphColouringRegAlloc INSTANCE = new GraphColouringRegAlloc();

    public List<ControlFlowGraph> flowGraphs = new ArrayList<>();
    public List<InterferenceGraph> interferenceGraphs = new ArrayList<>();

    private final static List<Register> opRegs= List.of(
            t3, t4, t5, t6, t7, t8, t9,
            s0, s1, s2, s3, s4, s5, s6, s7);


    private final static List<Register> spillRegs= List.of(
            t0, t1, t2
    );

    @Override
    public AssemblyProgram apply(AssemblyProgram program) {


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
            InterferenceGraph ig= new InterferenceGraph(g);
            interferenceGraphs.add(ig);

            Map<Register.Virtual,Label> vrMap=NaiveRegAlloc.collectVirtualRegisters(section);

            // allocate one label for each spilled register in a new data section
            AssemblyProgram.Section dataSec = newProg.newSection(AssemblyProgram.Section.Type.DATA);
            dataSec.emit("Allocated labels for spilled registers");
            vrMap.forEach((vr, lbl) -> {
                dataSec.emit(lbl);
                dataSec.emit(new Directive("space " + 4));
            });

            List<Map.Entry<Virtual, Label>> reversedRegLabelPairs = vrMap.entrySet().stream().toList().reversed();

            for(AssemblyItem item:section.items){
                switch (item){
                    case Comment x->newSection.emit(x);
                    case Label x->newSection.emit(x);
                    case Directive x->newSection.emit(x);

                    case Instruction insn->{
                        if(g.getNodesPostOrder().stream()
                                .filter(node-> node.data ==insn).findAny().isEmpty()){
                            //instruction is never even reached in the flow graph, we can ignore this instruction
                            continue;
                        }
                        if (insn == Instruction.Nullary.pushRegisters) {
                            newSection.emit("---PUSH REGISTERS START---");

                            vrMap.forEach((register, label) -> {
                                newSection.emit(OpCode.ADDI, Arch.sp, Arch.sp, -4);
                                if (ig.spilled.contains(register)) {
                                    newSection.emit(OpCode.LA, Arch.t0, label);
                                    newSection.emit(OpCode.LW, Arch.t0, Arch.t0, 0);

                                    // push $t0 onto stack
                                    newSection.emit(OpCode.SW, Arch.t0, Arch.sp, 0);
                                } else {
                                    // push register onto stack
                                    newSection.emit(OpCode.SW, opRegs.get(ig.colorings.get(register)), Arch.sp, 0);
                                }
                            });
                            newSection.emit("---PUSH REGISTERS END---");

                        } else if (insn == Instruction.Nullary.popRegisters) {
                            newSection.emit("---POP REGISTERS START---");
                            for (Map.Entry<Virtual, Label> pair : reversedRegLabelPairs) {
                                Register reg = pair.getKey();
                                Label label= pair.getValue();
                                if(ig.spilled.contains(reg)) {
                                    // pop from stack into $t0
                                    newSection.emit(OpCode.LW, Register.Arch.t0, Register.Arch.sp, 0);

                                    // store content of $t0 in memory at label
                                    newSection.emit(OpCode.LA, Register.Arch.t1, label);
                                    newSection.emit(OpCode.SW, Register.Arch.t0, Register.Arch.t1, 0);
                                }else{
                                    // pop from stack into previous reg
                                    newSection.emit(OpCode.LW, opRegs.get(ig.colorings.get(reg)), Register.Arch.sp, 0);
                                }

                                newSection.emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.sp, 4);
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

    private void emitInstructionWithoutVirtualRegister(Instruction insn, AssemblyProgram.Section section,
                                                       InterferenceGraph ig, Map<Virtual, Label> vrMap) {
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

        insn.uses().forEach(reg -> {
            if (ig.spilled.contains(reg)) {
                Register tmp = vrToAr.get(reg);
                Label label = vrMap.get(reg);
                section.emit(OpCode.LA, tmp, label);
                section.emit(OpCode.LW, tmp, tmp, 0);
            }
        });
        section.emit(insn.rebuild(vrToAr));

        if (insn.def() != null) {
            if (ig.spilled.contains(insn.def())) {
                Register tmpVal = vrToAr.get(insn.def());
                Register tmpAddr = freeTempRegs.removeFirst();
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
