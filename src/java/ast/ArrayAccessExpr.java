package ast;

import java.util.Arrays;
import java.util.List;

public final class ArrayAccessExpr extends Expr {
    Expr arr;
    Expr index;

    public ArrayAccessExpr(Expr arr, Expr index) {
        this.arr = arr;
        this.index = index;
    }

    @Override
    public List<ASTNode> children() {
        return Arrays.asList(arr,index);
    }
}
