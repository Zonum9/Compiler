package ast;

import java.util.List;

public final class ClassType implements Type{//todo
    public final String identifier;

    public ClassType(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public List<ASTNode> children() {
        return List.of();
    }
}
