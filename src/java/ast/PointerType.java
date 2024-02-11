package ast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class PointerType implements Type{
    Type type;
    public PointerType(Type type) {
        this.type = type;
    }
    @Override
    public List<ASTNode> children() {
        return Collections.singletonList(type);
    }
}
