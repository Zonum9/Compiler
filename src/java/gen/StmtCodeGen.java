package gen;

import ast.*;
import gen.asm.AssemblyProgram;
import gen.asm.Label;
import gen.asm.Register;

import java.util.Optional;

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
                Label loopContinue = Label.create("LoopContinue");
                Label loopEnd = Label.create("LoopEnd");

                informBreakAndContinueOflabel(aWhile.stmt,loopEnd,loopContinue);

                //pre test
                Register condValue = new ExprCodeGen(asmProg).visit(aWhile.expr);
                currentSection.emit(BEQ,condValue,zero,loopEnd); //if cond var == 0 then go to loopEnd

                //loop body
                currentSection.emit(loopStart);
                visit(aWhile.stmt); //visit loop body

                //post test
                currentSection.emit(loopContinue);
                condValue = new ExprCodeGen(asmProg).visit(aWhile.expr);
                currentSection.emit(BNE,condValue,zero,loopStart); //if cond var != 0 then go to loopStart

                //end of loop
                currentSection.emit(loopEnd);

            }

            // To complete other cases
            case Break aBreak -> {
                aBreak.loopEndLabel.ifPresent(lbl->{
                    currentSection.emit(J,lbl);
                });

            }
            case Continue aContinue -> {
                aContinue.loopContinueLabel.ifPresent(lbl->{
                    currentSection.emit(J,lbl);
                });
            }


            case Return aReturn -> {

                //this could result in redundant code, but oh well
                FunCodeGen.emitFunctionExit(currentSection);//todo
            }

        }
        currentSection.emit("----End of "+s.getClass().getSimpleName()+"----");
    }

    void informBreakAndContinueOflabel(Stmt stmt, Label breakLabel,Label continueLabel){
        switch (stmt){
            case Break b->{
                b.loopEndLabel = Optional.of(breakLabel);
            }
            case Continue c-> c.loopContinueLabel= Optional.of(continueLabel);
            case While ignored->{}
            case Stmt x->x.children().forEach(ch -> {
                if(ch instanceof Stmt st){
                    informBreakAndContinueOflabel(st,breakLabel,continueLabel);
                }
            }
            );
        }
    }

}
