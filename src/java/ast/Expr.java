package ast;

public sealed abstract class Expr implements ASTNode
        permits VarExpr,IntLiteral, StrLiteral,ChrLiteral,SizeOfExpr{ //todo

    public Type type; // to be filled in by the type analyser
}
