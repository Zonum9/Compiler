package gen;

import ast.Expr;
import ast.FunDecl;
import ast.Program;
import ast.Return;
import gen.asm.AssemblyProgram;
import gen.asm.Directive;
import gen.asm.OpCode;

import static ast.BaseType.CHAR;
import static gen.asm.OpCode.*;

/**
 * This visitor should produce a program.
 */
public class ProgramCodeGen extends CodeGen {


    private final AssemblyProgram.Section dataSection ;

    public ProgramCodeGen(AssemblyProgram asmProg) {
        this.asmProg = asmProg;
        this.dataSection = asmProg.newSection(AssemblyProgram.Section.Type.DATA);
    }



    public void generate(Program p) {
        dataSection.emit(new Directive("globl main"));
        // allocate all variables
        MemAllocCodeGen allocator = new MemAllocCodeGen(asmProg);
        allocator.visit(p);

        StringAllocCodeGen stringAllocator = new StringAllocCodeGen(asmProg);
        stringAllocator.visit(p);



        // generate the code for each function
        p.decls.forEach((d) -> {
            switch(d) {
                case FunDecl fd -> {
                    FunCodeGen fcg = new FunCodeGen(asmProg);
                    fcg.visit(fd);
                }
                default -> {}// nothing to do
            }});
    }

    public static OpCode.Load loadByteOrWord(Expr x){
        return x.type == CHAR? LBU:LW;
//        return LW;
    }

    public static OpCode.Store storeByteOrWord(Expr x){
        return x.type == CHAR? SB:SW;
//        return SW;
    }
    public static Store storeByteOrWord(Return aReturn) {
        return aReturn.functionReturnType == CHAR? SB:SW;
//        return SW;
    }





}
