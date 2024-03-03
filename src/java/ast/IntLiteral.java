package ast;

import java.util.Collections;
import java.util.List;

public final class IntLiteral extends Expr {
    public int value;

    public IntLiteral(int value) {
        this.value = value;
    }

    @Override
    public List<ASTNode> children() {
        return Collections.emptyList();
    }
}
