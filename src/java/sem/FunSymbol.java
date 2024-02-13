package sem;

import ast.FunDecl;
import ast.FunProto;

public class FunSymbol extends Symbol{
    FunDecl funDecl;
    public FunProto proto=null;
    public FunSymbol(FunDecl funDecl) {
        super(funDecl.name);
        this.funDecl = funDecl;
    }
}
