package gen;

import ast.FunDecl;
import ast.VarDecl;
import gen.asm.*;

import static gen.asm.Register.Arch.*;

/**
 * A visitor that produces code for a single function declaration
 */
public class FunCodeGen extends CodeGen {


    public FunCodeGen(AssemblyProgram asmProg) {
        this.asmProg = asmProg;
    }

    void visit(FunDecl fd) {
        if(builtIns.contains(fd.name))
            return;

        // Each function should be produced in its own section.
        // This is necessary for the register allocator.
        asmProg.newSection(AssemblyProgram.Section.Type.TEXT);
        AssemblyProgram.Section currSect= asmProg.getCurrentSection();
        currSect.emit(new Directive("globl "+fd.name));
        if(fd.name.equals("main")){
            currSect.emit(new Directive("globl _start"));
            currSect.emit(Label.get("_start"));
        }
        currSect.emit(Label.get(fd.name));


        // 1) emit the prolog
        currSect.emit(OpCode.ADDIU, sp, sp, -4);
        currSect.emit(OpCode.SW, fp, sp, 0); //push frame pointer on stack

        currSect.emit(OpCode.ADDI, fp, sp, 0);//move stack to frame pointer(initially fp)

        currSect.emit(OpCode.ADDIU, sp, sp, -4);
        currSect.emit(OpCode.SW, ra, sp, 0); //push return address on stack

        //allocate space on stack for local variables
        for (VarDecl vd:fd.block.vds){
            currSect.emit("------Var decl for "+vd.name);
            currSect.emit(OpCode.ADDIU,sp,sp, -vd.space);
        }
        if(!fd.name.equals("main")){
            currSect.emit(OpCode.PUSH_REGISTERS);
        }

        // 2) emit the body of the function
        StmtCodeGen scd = new StmtCodeGen(asmProg);
        scd.visit(fd.block);

        // 3) emit the epilog
        if(fd.name.equals("main")){
            currSect.emit(OpCode.LI,Register.Arch.v0,10);
            currSect.emit(Instruction.Nullary.syscall);
        }
        else{
            emitFunctionExit(currSect);
        }
    }

    public static void emitFunctionExit(AssemblyProgram.Section currSect){
        currSect.emit(OpCode.POP_REGISTERS);

        currSect.emit(OpCode.LW,ra,fp,-4);//restore ra from stack
        currSect.emit(OpCode.ADDIU,sp,fp,4);//restore stack pointer
        currSect.emit(OpCode.LW,fp,fp,0);//restore the frame pointer

        currSect.emit(OpCode.JR,ra);//jump to return address
    }



}
