package ast;

import java.util.Collections;
import java.util.List;

import static ast.BaseType.INT;

public final class IntLiteral extends Expr {
    public int value;

    public IntLiteral(int value) {
        this.value = value;
        this.type= INT;
    }

    @Override
    public List<ASTNode> children() {
        return Collections.emptyList();
    }
}
