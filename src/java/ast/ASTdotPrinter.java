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
            case null -> throw new IllegalStateException("Unexpected null value");
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

            case FunDecl funDecl -> {
                nodeCnt++;
                ArrayList<String> children = new ArrayList<>();
                String nid="Node"+nodeCnt;
                writer.println(nid+"[label=\"FunDecl("+funDecl.name+")\"];");
                for(ASTNode c : funDecl.children()){
                    children.add(visit(c));
                }
                // write out edges
                for( String s :children){
                    writer.println(nid + "->"+ s +";");
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

            case ChrLiteral x -> {
                nodeCnt++;
                String nid="Node"+nodeCnt;
                writer.println(nid+"[label=\""+x.value+"\"];");
                yield nid;
            }
            case FunProto x-> {
                nodeCnt++;
                ArrayList<String> children = new ArrayList<>();
                String nid="Node"+nodeCnt;
                writer.println(nid+"[label=\"FunProto("+x.name+"\"];");
                for(ASTNode c : x.children()){
                    children.add(visit(c));
                }
                // write out edges
                for( String s :children){
                    writer.println(nid + "->"+ s+";");
                }
                yield nid;
            }

            case FunCallExpr x -> {
                nodeCnt++;
                ArrayList<String> children = new ArrayList<>();
                String nid="Node"+nodeCnt;
                writer.println(nid+"[label=\"FunCallExpr("+x.name+")\"];");
                for(ASTNode c : x.children()){
                    children.add(visit(c));
                }
                // write out edges
                for( String s :children){
                    writer.println(nid + "->"+ s+";");
                }
                yield nid;
            }

            case StrLiteral x -> {
                nodeCnt++;
                ArrayList<String> children = new ArrayList<>();
                String nid="Node"+nodeCnt;
                writer.println(nid+"[label=\""+x.getClass().getSimpleName()+"("+x.value+")\"];");
                for(ASTNode c : x.children()){
                    children.add(visit(c));
                }
                // write out edges
                for( String s :children){
                    writer.println(nid + "->"+ s+";");
                }
                yield nid;
            }

            case VarDecl x -> {
                nodeCnt++;
                ArrayList<String> children = new ArrayList<>();
                String nid="Node"+nodeCnt;
                writer.println(nid+"[label=\""+x.getClass().getSimpleName()+"("+x.name+")\"];");
                for(ASTNode c : x.children()){
                    children.add(visit(c));
                }
                // write out edges
                for( String s :children){
                    writer.println(nid + "->"+ s+";");
                }
                yield nid;
            }
            case ArrayType x -> {
                nodeCnt++;
                ArrayList<String> children = new ArrayList<>();
                String nid="Node"+nodeCnt;
                writer.println(nid+"[label=\""+x.getClass().getSimpleName()+"("+x.numElement+")\"];");
                for(ASTNode c : x.children()){
                    children.add(visit(c));
                }
                // write out edges
                for( String s :children){
                    writer.println(nid + "->"+ s+";");
                }
                yield nid;
            }
            case StructType x -> {
                nodeCnt++;
                ArrayList<String> children = new ArrayList<>();
                String nid="Node"+nodeCnt;
                writer.println(nid+"[label=\""+x.getClass().getSimpleName()+"("+x.strTypeName+")\"];");
                for(ASTNode c : x.children()){
                    children.add(visit(c));
                }
                // write out edges
                for( String s :children){
                    writer.println(nid + "->"+ s+";");
                }
                yield nid;
            }
            case ASTNode x -> {
                nodeCnt++;
                ArrayList<String> children = new ArrayList<>();
                String nid="Node"+nodeCnt;
                writer.println(nid+"[label=\""+x.getClass().getSimpleName()+"\"];");
                for(ASTNode c : x.children()){
                    children.add(visit(c));
                }
                // write out edges
                for( String s :children){
                    writer.println(nid + "->"+ s+";");
                }
                yield nid;
            }
        };

    }



}
