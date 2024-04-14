package ast;

public abstract sealed class Decl implements ASTNode
        permits ClassDecl, FunDecl, FunProto, StructTypeDecl, VarDecl {

    public Type type;
    public String name;
}
