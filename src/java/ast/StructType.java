package ast;

import java.util.Collections;
import java.util.List;

public final class StructType  implements Type{
    String strTypeName;
    public StructType(String strTypeName) {
        this.strTypeName = strTypeName;
    }
    @Override
    public List<ASTNode> children() {
        return Collections.emptyList();
    }
}
