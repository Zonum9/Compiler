package ast;

public sealed abstract class Expr implements ASTNode
        permits IntLiteral,StrLiteral,ChrLiteral,VarExpr,FunCallExpr,BinOp,ArrayAccessExpr,FieldAccessExpr,
        ValueAtExpr,AddressOfExpr,
        SizeOfExpr,TypecastExpr,Assign{//missing their type assignment

    public Type type; // to be filled in by the type analyser
}
