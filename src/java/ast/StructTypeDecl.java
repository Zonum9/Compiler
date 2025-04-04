package ast;

import java.util.ArrayList;
import java.util.List;

public final class StructTypeDecl extends Decl {
    public final List<VarDecl> varDecls;
    public StructTypeDecl(StructType structType,List<VarDecl> varDecls) {
        type= structType;
        name= structType.strTypeName;
        this.varDecls= List.copyOf(varDecls);
    }

    // to be completed
    public List<ASTNode> children() {
        ArrayList<ASTNode> temp = new ArrayList<>();
        temp.add(type);
        temp.addAll(varDecls);
        return temp;
    }

}
