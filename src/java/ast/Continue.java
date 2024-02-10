package ast;

import java.util.Collections;
import java.util.List;

public final class Continue extends Stmt {
    @Override
    public List<ASTNode> children() {
        return Collections.emptyList();
    }
}
