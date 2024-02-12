package sem;

import ast.FunDecl;
import ast.FunProto;

public class FunSymbol extends Symbol{
    FunDecl fd;
    public FunProto proto=null;
    public FunSymbol(FunDecl fd) {
        super(fd.name);
        this.fd = fd;
    }
}
