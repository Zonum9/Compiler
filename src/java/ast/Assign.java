package ast;

import java.util.Arrays;
import java.util.List;

public final class Assign extends Expr {
    public final Expr expr1;
    public final Expr expr2;

    public Assign(Expr expr1, Expr expr2) {
        this.expr1 = expr1;
        this.expr2 = expr2;
    }

    @Override
    public List<ASTNode> children() {
        return Arrays.asList(expr1,expr2);
    }
}
