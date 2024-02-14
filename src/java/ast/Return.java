package ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class Return extends Stmt {
    public final Optional<Expr> expr;
    public Type functionReturnType;//to be filled by type analyzer;
    public Return(Optional<Expr> expr) {
        this.expr = expr;
    }

    @Override
    public List<ASTNode> children() {
        ArrayList<ASTNode> temp = new ArrayList<>();
        expr.ifPresent(temp::add);
        return temp;
    }
}
