package ast;

import java.io.PrintWriter;
import java.util.ArrayList;

public class ASTdotPrinter {

    private final PrintWriter writer;

    public ASTdotPrinter(PrintWriter writer) {
        this.writer = writer;
        writer.println("digraph ast {");
    }

    int nodeCnt=0;
    public String visit(ASTNode node) {
        return switch(node) {
            case null -> {
                throw new IllegalStateException("Unexpected null value");
            }
            case Program program -> {
                nodeCnt++;
                ArrayList<String> children = new ArrayList<>();
                String nid="Node"+nodeCnt;
                writer.println(nid+"[label=\"Program\"];");
                for(ASTNode c : program.children()){
                    children.add(visit(c));
                }
                // write out edges
                for( String s :children){
                    writer.println(nid + "->"+ s+";");
                }
                writer.println("}");
                yield nid;
            }
            case IntLiteral intLiteral -> {
                nodeCnt++;
                writer.println("Node"+nodeCnt+
                        "[label=\"" +intLiteral.value+"\"];");
                yield "Node"+nodeCnt;
            }

            case BinOp bo -> {
                nodeCnt++;
                ArrayList<String> children = new ArrayList<>();
                String nid="Node"+nodeCnt;
                writer.println(nid+"[label=\"BinOp\"];");
                for(ASTNode c : bo.children()){
                    children.add(visit(c));
                }
                // write out edges
                for( String s :children){
                    writer.println(nid + "->"+ s +";");
                }
                yield nid;
            }
            case FunDecl funDecl -> {
                nodeCnt++;
                ArrayList<String> children = new ArrayList<>();
                String nid="Node"+nodeCnt;
                writer.println(nid+"[label=\"FunDecl\"];");
                for(ASTNode c : funDecl.children()){
                    children.add(visit(c));
                }
                // write out edges
                for( String s :children){
                    writer.println(nid + "->"+ s +";");
                }
                yield nid;
            }
            case Block block ->{
                nodeCnt++;
                ArrayList<String> children = new ArrayList<>();
                String nid="Node"+nodeCnt;
                writer.println(nid+"[label=\"Block\"];");
                for(ASTNode c : block.children()){
                    children.add(visit(c));
                }
                // write out edges
                for( String s :children){
                    writer.println(nid + "->"+ s+";");
                }
                yield nid;
            }
            case Op op -> {
                nodeCnt++;
                String nid="Node"+nodeCnt;
                String symb;
                symb= switch (op){
                    case ADD -> "+";
                    case SUB -> "-";
                    case MUL -> "*";
                    case DIV -> "/";
                    case MOD -> "%";
                    case GT -> ">";
                    case LT -> "<";
                    case GE -> ">=";
                    case LE -> "<=";
                    case NE -> "!=";
                    case EQ -> "==";
                    case OR -> "||";
                    case AND -> "&&";
                };
                writer.println(nid+"[label=\""+symb+"\"];");
                yield nid;
            }
            case Return aReturn -> {
                nodeCnt++;
                ArrayList<String> children = new ArrayList<>();
                String nid="Node"+nodeCnt;
                writer.println(nid+"[label=\"Return\"];");
                for(ASTNode c : aReturn.children()){
                    children.add(visit(c));
                }
                // write out edges
                for( String s :children){
                    writer.println(nid + "->"+ s+";");
                }
                yield nid;
            }

            case BaseType baseType->{
                nodeCnt++;
                String nid="Node"+nodeCnt;
                writer.println(nid+"[label=\""+baseType+"\"];");
                yield nid;
            }
            case VarExpr varExpr -> {
                nodeCnt++;
                String nid="Node"+nodeCnt;
                writer.println(nid+"[label=\""+varExpr.name+"\"];");
                yield nid;
            }

            case AddressOfExpr addressOfExpr -> null;
            case ArrayAccessExpr arrayAccessExpr -> null;
            case Assign assign -> null;
            case ChrLiteral chrLiteral -> null;
            case FunProto proto-> "";
            case FieldAccessExpr fieldAccessExpr -> null;
            case FunCallExpr funCallExpr -> null;

            case SizeOfExpr sizeOfExpr -> null;

            case StrLiteral strLiteral -> null;

            case TypecastExpr typecastExpr -> null;
            case ValueAtExpr valueAtExpr -> null;


            case StructTypeDecl structTypeDecl -> null;
            case VarDecl varDecl -> null;
            case Break aBreak -> null;
            case Continue aContinue -> null;
            case ExprStmt exprStmt -> null;
            case If anIf -> null;

            case While aWhile -> null;
            case ArrayType arrayType -> null;
            case PointerType pointerType -> null;
            case StructType structType -> null;
        };

    }



}
