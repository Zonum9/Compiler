package gen;

import ast.*;
import gen.asm.AssemblyProgram;
import gen.asm.Label;
import gen.asm.Register;

import static gen.asm.OpCode.*;
import static gen.asm.Register.Arch.zero;

public class StmtCodeGen extends CodeGen {

    public StmtCodeGen(AssemblyProgram asmProg) {
        this.asmProg = asmProg;
    }

    void visit(Stmt s) {
        AssemblyProgram.Section currentSection = asmProg.getCurrentSection();
        currentSection.emit("----Start of "+s.getClass().getSimpleName()+"----");
        switch (s) {
            case Block b -> {
                // no need to do anything with varDecl (memory allocator takes care of them)
                b.stmts.forEach(this::visit);
            }
            case ExprStmt exprStmt -> {
                ExprCodeGen exprCodeGen =  new ExprCodeGen(asmProg);
                exprCodeGen.visit(exprStmt.expr);
            }

            case If anIf -> {
                Label ifFailed = Label.create("ifFailed");
                Label end = Label.create("endOfIfBlock");
                Register condValue = new ExprCodeGen(asmProg).visit(anIf.condition);
                currentSection.emit(BEQ,condValue,zero,ifFailed); //if cond var == 0 then go to ifFailed

                visit(anIf.stmt);
                currentSection.emit(J,end);//end of if, need to go to the end of if block (don't run else)

                //IF failed
                currentSection.emit(ifFailed);
                anIf.els.ifPresent(this::visit); //visit ELSE if it is present

                //IF passed, so go straight to the end
                currentSection.emit(end);
            }

            case While aWhile -> {
                Label loopStart = Label.create("LoopStart");
                Label loopEnd = Label.create("LoopEnd");

                //pre test
                Register condValue = new ExprCodeGen(asmProg).visit(aWhile.expr);
                currentSection.emit(BEQ,condValue,zero,loopEnd); //if cond var == 0 then go to loopEnd

                //loop body
                currentSection.emit(loopStart);
                visit(aWhile.stmt); //visit loop body todo make sure continue/break know the label of the loop

                //post test
                condValue = new ExprCodeGen(asmProg).visit(aWhile.expr);
                currentSection.emit(BNE,condValue,zero,loopStart); //if cond var != 0 then go to loopStart

                //end of loop
                currentSection.emit(loopEnd);

            }

            // To complete other cases
            case Break aBreak -> {
            }
            case Continue aContinue -> {
            }


            case Return aReturn -> {
            }

        }
        currentSection.emit("----End of "+s.getClass().getSimpleName()+"----");
    }
}
