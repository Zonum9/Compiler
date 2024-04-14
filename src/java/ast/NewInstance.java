package ast;

import java.util.List;

public final class NewInstance extends Expr { //todo
    public final ClassType classType;

    public NewInstance(ClassType classType) {
        this.classType = classType;
    }

    @Override
    public List<ASTNode> children() {
        return List.of(classType);
    }
}
