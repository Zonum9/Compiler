package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class StructType  implements Type{
    public final String strTypeName;
    public StructTypeDecl origin;
    public StructType(String strTypeName) {
        this.strTypeName = strTypeName;
    }
    @Override
    public List<ASTNode> children() {
        return Collections.emptyList();
    }
}
