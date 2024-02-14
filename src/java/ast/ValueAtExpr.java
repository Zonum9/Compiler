package ast;

import java.util.Collections;
import java.util.List;

public final class ValueAtExpr extends Expr {
    public final Expr expr;

    public ValueAtExpr(Expr expr) {
        this.expr = expr;
    }

    @Override
    public List<ASTNode> children() {
        return Collections.singletonList(expr);
    }
}
