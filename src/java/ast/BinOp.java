package ast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class BinOp extends Expr {
    public final Expr expr1;
    public final Op op;
    public final Expr expr2;

    public BinOp(Expr expr1, Op op, Expr expr2) {
        this.expr1 = expr1;
        this.op = op;
        this.expr2 = expr2;
    }

    @Override
    public List<ASTNode> children() {
        return Arrays.asList(expr1,op,expr2);
    }
}
