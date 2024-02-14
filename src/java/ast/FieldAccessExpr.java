package ast;

import java.util.Collections;
import java.util.List;

public final class FieldAccessExpr extends Expr {
    public final Expr expr;
    public final String fieldName;

    public FieldAccessExpr(Expr expr, String fieldName) {
        this.expr = expr;
        this.fieldName = fieldName;
    }

    @Override
    public List<ASTNode> children() {
        return Collections.singletonList(expr);
    }
}
