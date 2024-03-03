package gen;

import ast.*;
import gen.asm.AssemblyProgram;
import gen.asm.Label;
import gen.asm.Register;

import java.util.Objects;

import static gen.asm.OpCode.*;
import static gen.asm.Register.Arch.fp;

/**
 * Generates code to calculate the address of an expression and return the result in a register.
 */
public class AddrCodeGen extends CodeGen {

    public AddrCodeGen(AssemblyProgram asmProg) {
        this.asmProg = asmProg;
    }

    public Register visit(Expr e) {
        AssemblyProgram.Section currSect = asmProg.getCurrentSection();
        return switch (e){
            case ArrayAccessExpr arrayAccessExpr -> {
                Register higherAddress = visit(arrayAccessExpr.arr);
                int typeSize= MemAllocCodeGen.sizeofType(arrayAccessExpr.type);

                Register desiredIndex = new ExprCodeGen(asmProg).visit(arrayAccessExpr.index);
                Register temp = Register.Virtual.create();

                //get index offset
                currSect.emit(LI,temp,typeSize);
                currSect.emit(MULT,temp,desiredIndex); //typesize*desiredIndex
                currSect.emit(MFLO,desiredIndex);

                //get the desired final address
                currSect.emit(ADD,higherAddress,higherAddress,desiredIndex);
                yield higherAddress;
            }
            case FieldAccessExpr fieldAccessExpr -> visit(fieldAccessExpr.expr);//fixme this must be wrong
            case ValueAtExpr valueAtExpr -> visit(valueAtExpr.expr);//fixme this must be wrong
            case VarExpr varExpr -> {
                VarDecl vd = varExpr.origin;
                Register reg= Register.Virtual.create();
                if(vd.isGlobal){
                    currSect.emit(LA,reg, Label.get(vd.name));
                }else {
                    currSect.emit(ADDI,reg,fp,vd.fpOffset);
                }
                yield reg;
            }

            case Assign x->{
                Register lhs = visit(x.expr1);
                if(x.type instanceof StructType){
                    Register rhs = visit(x.expr2);
                    VarDecl decl= getVarExpression(x.expr1).origin;
                    for (int i = 0; i < decl.space; i+=4) {//copy struct
                        currSect.emit(SW,rhs,lhs,decl.fpOffset+i*4);
                    }
                }
                else {
                    Register rhs = new ExprCodeGen(asmProg).visit(x.expr2);

                    //can be : array, int, char, ptr (todo should chars be loaded as words?)
                    currSect.emit(SW,rhs,lhs,0);
                }

                yield lhs;
            }

            default -> throw new Error("Invalid address access");
        };
    }
    private VarExpr getVarExpression(Expr expr) {
        switch (expr){
            case VarExpr x->{
                return x;
            }
            case FieldAccessExpr x->{
                return getVarExpression(x.expr);
            }
            case ArrayAccessExpr x ->{
                return getVarExpression(x.arr);
            }
            case ValueAtExpr x ->{
                return getVarExpression(x.expr);
            }
            default -> throw new IllegalStateException("how did this happen?");

        }
    }



}
