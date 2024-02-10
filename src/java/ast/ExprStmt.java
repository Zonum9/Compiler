package ast;

import java.util.Collections;
import java.util.List;

public final class ExprStmt extends Stmt {
    Expr expr;

    public ExprStmt(Expr expr) {
        this.expr = expr;
    }

    @Override
    public List<ASTNode> children() {
        return Collections.singletonList(expr);
    }
}
