package ast;

import java.util.Collections;
import java.util.List;

public final class SizeOfExpr extends Expr {
    Type type;

    public SizeOfExpr(Type type) {
        this.type = type;
    }

    @Override
    public List<ASTNode> children() {
        return Collections.singletonList(type);
    }
}
