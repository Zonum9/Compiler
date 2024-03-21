package regalloc;

import gen.asm.AssemblyPass;
import gen.asm.AssemblyProgram;

import java.util.ArrayList;
import java.util.List;

public class GraphColouringRegAlloc implements AssemblyPass {

    public static final GraphColouringRegAlloc INSTANCE = new GraphColouringRegAlloc();

    public List<ControlFlowGraph> graphs = new ArrayList<>();

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

            graphs.add(new ControlFlowGraph(section));

        }

        return newProg;
    }



}
