package gen;

import gen.asm.AssemblyProgram;

import java.util.Set;

public abstract class CodeGen {
    protected AssemblyProgram asmProg;
    public static final Set<String> builtIns= Set.of("print_s","print_i","print_c","read_c","read_i","mcmalloc");
}
