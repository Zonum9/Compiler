package ast;

import java.util.ArrayList;
import java.util.List;

public final class FunProto extends Decl {
    public List<VarDecl> params;
    public FunProto(Type type, String name, List<VarDecl> params)  {
        this.type = type;
        this.name = name;
        this.params = params;
    }

    @Override
    public List<ASTNode> children() {
        ArrayList<ASTNode> temp=new ArrayList<ASTNode>();
        temp.add(type);
        temp.addAll(params);
        return temp;
    }
}
