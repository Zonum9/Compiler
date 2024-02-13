package ast;

import java.util.Arrays;
import java.util.List;

public final class TypecastExpr extends Expr {
    Type castType;
    Expr expr;

    public TypecastExpr(Type castType, Expr expr) {
        this.castType = castType;
        this.expr = expr;
    }

    @Override
    public List<ASTNode> children() {
        return Arrays.asList(castType,expr);
    }
}
