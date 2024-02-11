package ast;

import java.util.Arrays;
import java.util.List;

public final class TypecastExpr extends Expr {
    Type type;
    Expr expr;

    public TypecastExpr(Type type, Expr expr) {
        this.type = type;
        this.expr = expr;
    }

    @Override
    public List<ASTNode> children() {
        return Arrays.asList(type,expr);
    }
}
