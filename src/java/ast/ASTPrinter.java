package ast;

import java.io.PrintWriter;

public class ASTPrinter {

    private final PrintWriter writer;

    public ASTPrinter(PrintWriter writer) {
            this.writer = writer;
    }

    public void visit(ASTNode node) {
        switch(node) {
            case null -> {
                throw new IllegalStateException("Unexpected null value");
            }

            case Block blc -> {
                writer.print("Block(");
                String delimiter = "";
                for (ASTNode child : blc.children()) {
                    writer.print(delimiter);
                    delimiter = ",";
                    visit(child);
                }
                writer.print(")");
            }

            case FunDecl fd -> {
                writer.print("FunDecl(");
                visit(fd.type);
                writer.print(","+fd.name+",");
                for (VarDecl vd : fd.params) {
                    visit(vd);
                    writer.print(",");
                }
                visit(fd.block);
                writer.print(")");
            }
            case FunProto fp -> {
                writer.print("FunProto(");
                visit(fp.type);
                writer.print(","+fp.name);
                for (VarDecl vd : fp.params) {
                    writer.print(",");
                    visit(vd);
                }
                writer.print(")");
            }

            case Program p -> {
                writer.print("Program(");
                String delimiter = "";
                for (Decl d : p.decls) {
                    writer.print(delimiter);
                    delimiter = ",";
                    visit(d);
                }
                writer.print(")");
                writer.flush();
            }

            case VarDecl vd -> {
                writer.print("VarDecl(");
                visit(vd.type);
                writer.print(","+vd.name);
                writer.print(")");
            }

            case VarExpr v -> {
                writer.print("VarExpr(");
                writer.print(v.name);
                writer.print(")");
            }

            case BaseType bt -> {
                writer.print(bt);
            }

            case StructTypeDecl std -> {
                writer.print("StructTypeDecl(");
                visit(std.type);
                for (ASTNode n : std.varDecls){
                    writer.print(",");
                    visit(n);
                }
                writer.print(")");
            }
            case StructType st->{
                writer.print("StructType("+st.strTypeName+")");
            }
            case Return aReturn -> {
                writer.print("Return(");
                //visit return expression
                aReturn.expr.ifPresent(this::visit);
                writer.print(")");
            }
            case While aWhile -> {
                writer.print("While(");
                visit(aWhile.expr);
                writer.print(",");
                visit(aWhile.stmt);
                writer.print(")");
            }
            case If anIf -> {
                writer.print("If(");
                visit(anIf.expr);
                writer.print(",");
                visit(anIf.stmt);
                if(anIf.els.isPresent()){
                    writer.print(",");
                    visit(anIf.els.get());
                }
                writer.print(")");
            }
            case ExprStmt exprStmt -> {
                writer.print("ExprStmt(");
                visit(exprStmt.expr);
                writer.print(")");
            }
            case Continue ignored -> {
                writer.print("Continue()");
            }
            case Break ignored -> {
                writer.print("Break()");
            }
            case ArrayType arrayType -> {
                writer.print("ArrayType(");
                visit(arrayType.type);
                writer.print(",");
                writer.print(arrayType.numElement+")");
            }
            case PointerType pointerType -> {
                writer.print("PointerType(");
                visit(pointerType.type);
                writer.print(")");
            }
            case BinOp binOp -> {
                writer.print("BinOp(");
                visit(binOp.expr1);
                writer.print(",");
                visit(binOp.op);
                writer.print(",");
                visit(binOp.expr2);
                writer.print(")");
            }
            case Op op -> {
                writer.print(op);
            }
            case IntLiteral intLiteral -> {
                writer.print("IntLiteral(");
                writer.print(intLiteral.value);
                writer.print(")");
            }
            case StrLiteral strLiteral -> {
                writer.print("StrLiteral(");
                writer.print(strLiteral.value);
                writer.print(")");
            }
            case ChrLiteral chrLiteral -> {
                writer.print("ChrLiteral(");
                writer.print(chrLiteral.value);
                writer.print(")");
            }

            case AddressOfExpr addressOfExpr -> {
                writer.print("AddressOfExpr(");
                visit(addressOfExpr.expr);
                writer.print(")");
            }
            case ArrayAccessExpr arrayAccessExpr -> {
                writer.print("ArrayAccessExpr(");
                visit(arrayAccessExpr.arr);
                writer.print(",");
                visit(arrayAccessExpr.index);
                writer.print(")");
            }
            case Assign assign -> {
                writer.print("Assign(");
                visit(assign.expr1);
                writer.print(",");
                visit(assign.expr2);
                writer.print(")");
            }

            case FieldAccessExpr fieldAccessExpr -> {
                writer.print("FieldAccessExpr(");
                visit(fieldAccessExpr.expr);
                writer.print(",");
                writer.print(fieldAccessExpr.fieldName);
                writer.print(")");
            }

            case FunCallExpr funCallExpr -> {
                writer.print("FunCallExpr(");
                writer.print(funCallExpr.name);
                for (Expr vd : funCallExpr.exprs) {
                    writer.print(",");
                    visit(vd);
                }
                writer.print(")");
            }
            case TypecastExpr typecastExpr -> {
                writer.print("TypecastExpr(");
                visit(typecastExpr.type);
                writer.print(",");
                visit(typecastExpr.expr);
                writer.print(")");
            }

            case SizeOfExpr sizeOfExpr -> {
                writer.print("SizeOfExpr(");
                visit(sizeOfExpr.type);
                writer.print(")");
            }

            case ValueAtExpr valueAtExpr -> {
                writer.print("ValueAtExpr(");
                visit(valueAtExpr.expr);
                writer.print(")");
            }
        }

    }


    
}
