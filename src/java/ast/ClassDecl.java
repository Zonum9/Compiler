package ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ClassDecl extends Decl{//todo
    public final ClassType classType;
    public final Optional<ClassType> extension;
    public final List<VarDecl> varDecls;
    public final List<FunDecl> funDecls;

    public ClassDecl(ClassType classType, Optional<ClassType> extension, List<Decl> decls) {
        if (decls.stream().anyMatch(x -> !(x instanceof VarDecl || x instanceof FunDecl))){
            throw new IllegalStateException("Class decl should only get var decls and fun decls");
        }
        this.classType = classType;
        this.extension = extension;
        this.varDecls = decls.stream().filter(x->x instanceof VarDecl).map(x->(VarDecl)x).toList();
        this.funDecls = decls.stream().filter(x->x instanceof FunDecl).map(x->(FunDecl)x).toList();

    }

    @Override
    public List<ASTNode> children() {
        ArrayList<ASTNode> temp = new ArrayList<>();
        temp.addAll(varDecls);
        temp.addAll(funDecls);
        return temp;
    }
}
