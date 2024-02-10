package ast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class ArrayType implements Type{
    Type type;
    int numElement;

    public ArrayType(Type type, int numElement) {
        this.type = type;
        this.numElement = numElement;
    }

    @Override
    public List<ASTNode> children() {
        return Collections.emptyList();
    }
}
