package regalloc;

import gen.asm.*;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gen.asm.Register.Arch.*;

public class GraphColouringRegAlloc implements AssemblyPass {

    public static final GraphColouringRegAlloc INSTANCE = new GraphColouringRegAlloc();

    public List<ControlFlowGraph> flowGraphs = new ArrayList<>();
    public List<InterferenceGraph> interferenceGraphs = new ArrayList<>();

    private final static List<Register> opRegs= List.of(
            t0, t1, t2, t3, t4, t5, t6, t7, t8, t9,
            s0, s1, s2, s3, s4, s5, s6, s7);

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

            for(AssemblyItem item:section.items){
                switch (item){
                    case Comment x->newSection.emit(x);
                    case Label x->newSection.emit(x);
                    case Directive x->newSection.emit(x);

                    case Instruction insn->{
                        if (insn == Instruction.Nullary.pushRegisters) {
                            //todo
                        } else if (insn == Instruction.Nullary.popRegisters) {
                            //todo
                        } else
                            emitInstructionWithoutVirtualRegister(insn,newSection,ig);
                    }
                }
            }

        }
        return newProg;
    }

    private void emitInstructionWithoutVirtualRegister(Instruction insn, AssemblyProgram.Section section, InterferenceGraph ig) {
        Map<Register,Register> vrToAr= new HashMap<>();
        for(Register r:insn.registers()){
            if(!r.isVirtual()){
                continue;
            }
            //todo handle cases where r is not in colorings
            int index=ig.colorings.get(r);
            Register newReg = opRegs.get(index);
            vrToAr.put(r,newReg);
        }
        section.emit(insn.rebuild(vrToAr));
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
