package ast;

import java.util.Collections;
import java.util.List;

public enum Op implements ASTNode {
    ADD , SUB , MUL , DIV , MOD , GT , LT , GE , LE , NE , EQ , OR , AND
    ;

    @Override
    public List<ASTNode> children() {
        return Collections.emptyList();
    }
}
