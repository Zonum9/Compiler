package ast;

import java.util.List;

public final class InstanceFunCallExpr extends Expr{//todo
    public final Expr self;
    public final FunCallExpr fun;

    public InstanceFunCallExpr(Expr self, FunCallExpr fun) {
        this.self = self;
        this.fun = fun;
    }

    @Override
    public List<ASTNode> children() {
        return List.of(self,fun);
    }
}
