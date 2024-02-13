package sem;

import ast.VarDecl;

public class VarSymbol extends Symbol {
    VarDecl varDecl;
    public VarSymbol(VarDecl varDecl) {
        super(varDecl.name);
        this.varDecl = varDecl;
    }
}
