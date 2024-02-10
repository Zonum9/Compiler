package ast;

import java.util.ArrayList;
import java.util.List;

public final class StructTypeDecl extends Decl {
    List<ASTNode> varDecls;
    public StructTypeDecl(StructType structType,List<VarDecl> varDecls) {
        type= structType;
        this.varDecls= List.copyOf(varDecls);
    }

    // to be completed
    public List<ASTNode> children() {
        return varDecls; // To change!
    }

}
