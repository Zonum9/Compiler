package ast;

import java.util.ArrayList;
import java.util.List;

public final class FunCallExpr extends Expr {
    String name;
    List<Expr> exprs;

    public FunCallExpr(String name, List<Expr> exprs) {
        this.name = name;
        this.exprs = exprs;
    }

    @Override
    public List<ASTNode> children() {
        return new ArrayList<ASTNode>(exprs);
    }
}
