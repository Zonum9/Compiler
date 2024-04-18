package ast;

import java.util.Collections;
import java.util.List;

public final class ClassType implements Type{//todo
    public final String identifier;
    public ClassDecl origin; //to be filled in by type analyzer

    public ClassType(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public List<ASTNode> children() {
        return Collections.emptyList();
    }
}
