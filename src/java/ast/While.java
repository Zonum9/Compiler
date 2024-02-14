package ast;

import java.util.Arrays;
import java.util.List;

public final class While extends Stmt {
    public final Expr expr;
    public final Stmt stmt;

    public While(Expr expr, Stmt stmt) {
        this.expr = expr;
        this.stmt = stmt;
    }
    @Override
    public List<ASTNode> children() {
        return Arrays.asList(expr,stmt);
    }
}
