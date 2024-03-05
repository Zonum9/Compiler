package ast;

import gen.asm.Label;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class Continue extends Stmt {
    public Optional<Label> loopContinueLabel = Optional.empty();
    @Override
    public List<ASTNode> children() {
        return Collections.emptyList();
    }
}
