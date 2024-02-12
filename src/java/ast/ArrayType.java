package ast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class ArrayType implements Type{
    public final Type type;
    public final int numElement;

    public ArrayType(Type type, int numElement) {
        this.type = type;
        this.numElement = numElement;
    }

    @Override
    public List<ASTNode> children() {
        return Collections.singletonList(type);
    }
}
