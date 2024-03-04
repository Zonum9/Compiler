package ast;

import gen.asm.Label;

import java.util.Collections;
import java.util.List;

public final class StrLiteral extends Expr {
    public String value;
    public Label label;

    public StrLiteral(String value) {
        this.value = value;
    }

    @Override
    public List<ASTNode> children() {
        return Collections.emptyList();
    }
}
