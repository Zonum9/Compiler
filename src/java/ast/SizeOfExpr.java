package ast;

import java.util.Collections;
import java.util.List;

public final class SizeOfExpr extends Expr {
    public Type sizeOfType;

    public SizeOfExpr(Type sizeOfType) {
        this.sizeOfType = sizeOfType;
    }

    @Override
    public List<ASTNode> children() {
        return Collections.singletonList(sizeOfType);
    }
}
