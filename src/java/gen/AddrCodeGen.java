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
                Register baseAddress= visit(arrayAccessExpr.arr);

                //if accessing a pointer, BaseAddress will have the address of the pointer, and not the address the pointer
                //is pointing to. Need to load it.
                //However, if arr is a Pointer because of a Typecast on an array, then the address of arr is already the address
                // it's pointing to
                if((arrayAccessExpr.arr.type instanceof PointerType && !(isArrayToPointerCast(arrayAccessExpr.arr))
                        || arrayAccessExpr.arr instanceof VarExpr vx && vx.origin.isPtrNow)){
                    currSect.emit(LW,baseAddress,baseAddress,0);
                }
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

            case FieldAccessExpr fieldAccessExpr -> {
                Register baseAddress =visit(fieldAccessExpr.expr);
                StructTypeDecl origin = ((StructType)fieldAccessExpr.expr.type).origin;
                VarDecl fieldDecl= origin.varDecls.stream()
                        .filter( vd->vd.name.equals(fieldAccessExpr.fieldName))
                        .findFirst().get();
                currSect.emit(ADDIU,baseAddress,baseAddress,fieldDecl.fpOffset);
                yield baseAddress;
            }

            //the address of *(expr) is the value of expr
            case ValueAtExpr valueAtExpr -> new ExprCodeGen(asmProg).visit(valueAtExpr.expr);


            case VarExpr varExpr -> {
                VarDecl vd = varExpr.origin;
                Register reg= Register.Virtual.create();
                if(vd.isGlobal){
                    currSect.emit(LA,reg, Label.get(vd.name));
                }else {
                    currSect.emit(ADDIU,reg,fp,vd.fpOffset);
                }
                yield reg;
            }

            case Assign x->{
                Register addrReg = visit(x.expr1);
                if(x.expr1.type instanceof StructType st){
                    MemAllocCodeGen.copyStruct(addrReg,st,visit(x.expr2),currSect);
                }
                else {
                    Register rhs = new ExprCodeGen(asmProg).visit(x.expr2);
                    //can be :  int, char, ptr
                    currSect.emit(ProgramCodeGen.storeByteOrWord(x),rhs,addrReg,0);
                }

                yield addrReg;
            }

            case StrLiteral st->new ExprCodeGen(asmProg).visit(st);

            case TypecastExpr x->visit(x.expr);

            case FunCallExpr x->new ExprCodeGen(asmProg).visit(x);

            default -> throw new Error("Invalid address access");
        };
        currSect.emit("----End of of "+e.getClass().getSimpleName()+"----");
        return r;
    }

    private boolean isArrayToPointerCast(Expr arr) {
        if (arr instanceof TypecastExpr cast){
            return isArrayToPointerCast(cast.expr);
        }
        return arr.type instanceof ArrayType;
    }
}
