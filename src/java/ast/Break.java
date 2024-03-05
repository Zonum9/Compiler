package ast;

import gen.asm.Label;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class Break extends Stmt {
    public Optional<Label> loopEndLabel = Optional.empty();
    @Override
    public List<ASTNode> children() {
        return Collections.emptyList();
    }
}
