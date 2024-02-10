package ast;

public sealed abstract class Stmt implements ASTNode
        permits Block,Continue,Break,While,Return, If,ExprStmt {
}
