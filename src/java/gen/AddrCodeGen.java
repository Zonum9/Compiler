package gen;

import ast.*;
import gen.asm.AssemblyProgram;
import gen.asm.Label;
import gen.asm.Register;

import java.util.Objects;
import java.util.Optional;

import static ast.BaseType.CHAR;
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
        currSect.emit("----Start of "+e.getClass().getSimpleName()+"----");
        Register r= switch (e){
            case ArrayAccessExpr arrayAccessExpr -> {
                Register baseAddress = visit(arrayAccessExpr.arr);
                int typeSize= MemAllocCodeGen.sizeofType(arrayAccessExpr.type);

                Register desiredIndex = new ExprCodeGen(asmProg).visit(arrayAccessExpr.index);
                Register temp = Register.Virtual.create();

                //get index offset
                currSect.emit(LI,temp,typeSize);
                currSect.emit(MULT,temp,desiredIndex); //typesize*desiredIndex
                currSect.emit(MFLO,desiredIndex);

                //get the desired final address
                currSect.emit(ADD,baseAddress,baseAddress,desiredIndex);
                yield baseAddress;
            }

            case FieldAccessExpr fieldAccessExpr -> {//todo test this
                Register baseAddress =visit(fieldAccessExpr.expr);
                StructTypeDecl origin = ((StructType)fieldAccessExpr.expr.type).origin;
                VarDecl fieldDecl= origin.varDecls.stream()
                        .filter( vd->vd.name.equals(fieldAccessExpr.fieldName))
                        .findFirst().get();
                currSect.emit(ADDI,baseAddress,baseAddress,fieldDecl.fpOffset);
                yield baseAddress;
            }

            //the address of *(expr) is the value of expr
            case ValueAtExpr valueAtExpr -> new ExprCodeGen(asmProg).visit(valueAtExpr.expr);//todo test this


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
                Register addrReg = visit(x.expr1);
                if(x.expr1.type instanceof StructType st){
                    for(VarDecl vd:st.origin.varDecls){
                        FieldAccessExpr left = new FieldAccessExpr(x.expr1,vd.name);
                        left.type=vd.type;

                        FieldAccessExpr right = new FieldAccessExpr(x.expr2,vd.name);
                        right.type=vd.type;

                        Assign newAssign= new Assign(left,right);
                        newAssign.type=vd.type;
                        visit(newAssign);
                    }
                }
                else {
                    Register rhs = new ExprCodeGen(asmProg).visit(x.expr2);
                    //can be : array, int, char, ptr
                    Store storeType = x.type == CHAR? SB:SW;
                    currSect.emit(storeType,rhs,addrReg,0);
                }

                yield addrReg;
            }

            case TypecastExpr x->visit(x.expr);


            default -> throw new Error("Invalid address access");
        };
        currSect.emit("----End of of "+e.getClass().getSimpleName()+"----");
        return r;
    }
}
