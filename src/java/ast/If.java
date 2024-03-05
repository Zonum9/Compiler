package ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class If extends Stmt {
    public final Expr condition;
    public final Stmt stmt;
    public final Optional<Stmt> els;

    public If(Expr condition, Stmt stmt, Optional<Stmt> els) {
        this.condition = condition;
        this.stmt = stmt;
        this.els = els;
    }

    @Override
    public List<ASTNode> children() {
        ArrayList<ASTNode> temp = new ArrayList<>();
        temp.add(condition);
        temp.add(stmt);
        els.ifPresent(temp::add);
        return temp;
    }
}
