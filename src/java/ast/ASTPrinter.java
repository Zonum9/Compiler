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

            // todo ...
            case ArrayType arrayType -> {
            }
            case Break aBreak -> {
            }
            case Continue aContinue -> {
            }
            case ExprStmt exprStmt -> {
            }
            case If anIf -> {
            }
            case PointerType pointerType -> {
            }

            case While aWhile -> {
            }
        }

    }


    
}
