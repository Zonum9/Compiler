package gen;

import ast.*;
import gen.asm.AssemblyProgram;
import gen.asm.Label;
import gen.asm.OpCode;
import gen.asm.Register;

import static ast.BaseType.CHAR;
import static gen.asm.OpCode.*;
import static gen.asm.Register.Arch.*;


/**
 * Generates code to evaluate an expression and return the result in a register.
 */
public class ExprCodeGen extends CodeGen {

    public ExprCodeGen(AssemblyProgram asmProg) {
        this.asmProg = asmProg;
    }

    public Register visit(Expr e) {
        AssemblyProgram.Section currSection= asmProg.getCurrentSection();
        currSection.emit("----Start of "+e.getClass().getSimpleName()+"----");
        Register retReg= switch (e){
            case IntLiteral it->{
                Register resReg= Register.Virtual.create();
                currSection.emit(OpCode.LI,resReg,it.value);
                yield resReg;
            }
            case ChrLiteral chrLiteral -> {
                Register resReg= Register.Virtual.create();
                currSection.emit(OpCode.LI,resReg,chrLiteral.value);
                yield resReg;
            }

            case VarExpr vx->{
                Register resReg= Register.Virtual.create();
                if(vx.origin.isGlobal) {
                    currSection.emit(OpCode.LA, resReg, Label.get(vx.name));
                    currSection.emit(OpCode.LW, resReg, resReg, 0);
                }else {
                    currSection.emit(LW,resReg,fp,vx.origin.fpOffset);
                }

                yield resReg;
            }
            case Assign assign -> {
                if(assign.expr1.type instanceof StructType){
                    yield new AddrCodeGen(asmProg).visit(assign);
                }
                Register addrReg= new AddrCodeGen(asmProg).visit(assign.expr1);
                Register valReg= visit(assign.expr2);
                Store storeType = assign.expr1.type == CHAR? SB:SW;
                currSection.emit(storeType,valReg,addrReg,0);
                yield valReg;
            }

            case ArrayAccessExpr arrayAccessExpr -> {
                Register arrAddress = new AddrCodeGen(asmProg).visit(arrayAccessExpr);
                Register r = Register.Virtual.create();

                //if array access returns an array,
                //and that array is not being accessed then return reference to the array
                if(arrayAccessExpr.arr.type instanceof ArrayType &&
                        !(arrayAccessExpr.arr instanceof ArrayAccessExpr)){
                    yield arrAddress;
                }
                Load loadType = arrayAccessExpr.type == CHAR? LB:LW;
                currSection.emit(loadType,r,arrAddress,0);
                yield r;
            }
            case FieldAccessExpr fax -> {
                Register fieldAddress = new AddrCodeGen(asmProg).visit(fax);
                Register r = Register.Virtual.create();
                Load loadType = fax.type == CHAR? LB:LW;
                currSection.emit(loadType,r,fieldAddress,0);
                yield r;
            }


            case FunCallExpr x->{//todo
                if(builtIns.contains(x.name)){
                    yield handleBuiltInFunc(x);
                }
                yield null;
            }

            case BinOp binOp -> {
                Register lhsReg= visit(binOp.expr1);
                Register resultReg= Register.Virtual.create();
                if(binOp.op == Op.AND || binOp.op == Op.OR){
                    switch (binOp.op){
                        case OR->{
                            Label trueLbl = Label.create("TRUE");
                            Label endLbl = Label.create("END");
                            //lhs of OR
                            currSection.emit(BNE,lhsReg,zero,trueLbl);

                            //if lhs failed, check rhs
                            Register rhsReg= visit(binOp.expr2);
                            currSection.emit(BNE,rhsReg,zero,trueLbl);

                            //if both failed, return 0
                            currSection.emit(LI,resultReg,0);
                            currSection.emit(J,endLbl);

                            //labels
                            currSection.emit(trueLbl);
                            currSection.emit(LI,resultReg,1);
                            currSection.emit(endLbl);

                            yield resultReg;
                        }

                        case AND->{
                            Label falseLbl = Label.create("FALSE");
                            Label endLbl = Label.create("END");
                            //lhs of AND
                            currSection.emit(BEQ,lhsReg,zero,falseLbl);

                            //if lhs passed, check rhs
                            Register rhsReg= visit(binOp.expr2);
                            currSection.emit(BEQ,rhsReg,zero,falseLbl);

                            //if passed , return 1
                            currSection.emit(LI,resultReg,1);
                            currSection.emit(J,endLbl);

                            //labels
                            currSection.emit(falseLbl);
                            currSection.emit(LI,resultReg,0);
                            currSection.emit(endLbl);

                            yield resultReg;
                        }

                        default -> throw new IllegalStateException();

                    }

                }

                Register rhsReg= visit(binOp.expr2);
                switch (binOp.op){
                    case ADD ->{
                        currSection.emit(ADD,resultReg,lhsReg,rhsReg);
                    }

                    case SUB ->{
                        currSection.emit(SUB,resultReg,lhsReg,rhsReg);
                    }

                    case MUL ->{
                        currSection.emit(MULT,lhsReg,rhsReg);
                        currSection.emit(MFLO,resultReg);
                    }

                    case DIV ->{
                        currSection.emit(DIV,lhsReg,rhsReg);
                        currSection.emit(MFLO,resultReg);
                    }

                    case MOD ->{
                        currSection.emit(DIV,lhsReg,rhsReg);
                        currSection.emit(MFHI,resultReg);
                    }

                    case GT -> {//lhs > rhs  ->  rhs < lhs
                        currSection.emit(SLT,resultReg,rhsReg,lhsReg);
                    }

                    case LT -> { //lhs < rhs
                        currSection.emit(SLT,resultReg,lhsReg,rhsReg);
                    }

                    case GE -> {//lhs >= rhs ->  !(lhs<rhs)
                        currSection.emit(SLT,resultReg,lhsReg,rhsReg);
                        currSection.emit(XORI,resultReg,resultReg,1);
                    }

                    case LE -> {//lhs <= rhs ->  !(rhs<lhs)
                        currSection.emit(SLT,resultReg,rhsReg,lhsReg);
                        currSection.emit(XORI,resultReg,resultReg,1);
                    }

                    case EQ -> {
                        currSection.emit(XOR,resultReg,lhsReg,rhsReg);
                        Register tempReg= Register.Virtual.create();
                        currSection.emit(LI,tempReg,1);
                        currSection.emit(SLTU,resultReg,resultReg,tempReg);
                    }

                    case NE -> {
                        currSection.emit(XOR,resultReg,lhsReg,rhsReg);
                        currSection.emit(SLTU,resultReg,zero,resultReg);
                    }
                }
                yield resultReg;
            }
            case ValueAtExpr valueAtExpr -> {
                Register address = visit(valueAtExpr.expr);
                Load loadType = valueAtExpr.type == CHAR? LB:LW;
                currSection.emit(loadType,address,address,0);
                yield address;
            }
            case AddressOfExpr addressOfExpr -> new AddrCodeGen(asmProg).visit(addressOfExpr.expr);

            case TypecastExpr typecastExpr -> visit(typecastExpr.expr);

            case StrLiteral s -> {
                Register stringAddress = Register.Virtual.create();
                currSection.emit(LA,stringAddress,s.label);
                yield stringAddress;
            }

            case SizeOfExpr sizeOfExpr -> {
                Register reg = Register.Virtual.create();
                currSection.emit(LI,reg,MemAllocCodeGen.sizeofType(sizeOfExpr.sizeOfType));
                yield reg;
            }



        };
        currSection.emit("----End of "+e.getClass().getSimpleName()+"----");
        return retReg;
    }

    private Register handleBuiltInFunc(FunCallExpr builtIn) {
        AssemblyProgram.Section currSection= asmProg.getCurrentSection();
        return switch (builtIn.name){
            case "print_i"->{
                syscallWithArgs(currSection,builtIn,1);
                yield null;
            }

            case "print_c" ->{
                syscallWithArgs(currSection,builtIn,11);
                yield null;
            }

            case "print_s" ->{
                syscallWithArgs(currSection,builtIn,4);
                yield null;
            }

            case "read_c" ->{
                syscall(currSection,12);
                yield v0;
            }
            case "read_i" ->{
                syscall(currSection,5);
                yield v0;
            }

            case "mcmalloc" -> {
                syscallWithArgs(currSection, builtIn, 9);
                yield v0;
            }

            default -> throw new IllegalStateException("Unexpected value: " + builtIn.name);
        };
    }

    private void syscallWithArgs(AssemblyProgram.Section currSection, FunCallExpr funcall, int code){
        //type checker should have ensured that this only has one param
        Register inner= visit(funcall.exprs.getFirst());

        //load args
        currSection.emit(OpCode.ADDI,a0,inner,0);

        //perform syscall
        syscall(currSection,code);
    }
    private void syscall(AssemblyProgram.Section currSection,int code){
        currSection.emit(OpCode.LI,v0,code);
        currSection.emit(OpCode.SYSCALL);
    }

}
