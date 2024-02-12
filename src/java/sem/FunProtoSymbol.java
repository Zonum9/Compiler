package sem;

import ast.FunDecl;
import ast.FunProto;

public class FunProtoSymbol extends Symbol{
    FunProto fp;
    public FunDecl decl=null;

    public FunProtoSymbol(FunProto fp) {
        super(fp.name);
        this.fp = fp;
    }
}
