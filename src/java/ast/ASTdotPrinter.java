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

            case AddressOfExpr x -> {
                nodeCnt++;
                ArrayList<String> children = new ArrayList<>();
                String nid="Node"+nodeCnt;
                writer.println(nid+"[label=\"AddressOfExpr\"];");
                for(ASTNode c : x.children()){
                    children.add(visit(c));
                }
                // write out edges
                for( String s :children){
                    writer.println(nid + "->"+ s+";");
                }
                yield nid;
            }
            case ArrayAccessExpr x -> {
                nodeCnt++;
                ArrayList<String> children = new ArrayList<>();
                String nid="Node"+nodeCnt;
                writer.println(nid+"[label=\"ArrayAccessExpr\"];");
                for(ASTNode c : x.children()){
                    children.add(visit(c));
                }
                // write out edges
                for( String s :children){
                    writer.println(nid + "->"+ s+";");
                }
                yield nid;
            }
            case Assign x -> {
                nodeCnt++;
                ArrayList<String> children = new ArrayList<>();
                String nid="Node"+nodeCnt;
                writer.println(nid+"[label=\"Assign\"];");
                for(ASTNode c : x.children()){
                    children.add(visit(c));
                }
                // write out edges
                for( String s :children){
                    writer.println(nid + "->"+ s+";");
                }
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
            case FieldAccessExpr x -> {
                nodeCnt++;
                ArrayList<String> children = new ArrayList<>();
                String nid="Node"+nodeCnt;
                writer.println(nid+"[label=\"FieldAccessExpr\"];");
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

            case SizeOfExpr x -> {
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

            case TypecastExpr x -> {
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
            case ValueAtExpr x -> {
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


            case StructTypeDecl x -> {
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
            case Break x -> {
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
            case Continue x -> {
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
            case ExprStmt x -> {
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
            case If x -> {
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

            case While x -> {
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
            case PointerType x -> {
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
        };

    }



}
