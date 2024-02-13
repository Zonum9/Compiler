package ast;

import java.util.Collections;
import java.util.List;

public final class AddressOfExpr extends Expr {
    public final Expr expr;

    public AddressOfExpr(Expr expr) {
        this.expr = expr;
    }

    @Override
    public List<ASTNode> children() {
        return Collections.singletonList(expr);
    }
}
