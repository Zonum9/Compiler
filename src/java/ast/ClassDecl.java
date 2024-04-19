package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class ClassDecl extends Decl{//todo
    public final Optional<ClassType> extension;
    public final List<VarDecl> varDecls;
    public final List<FunDecl> funDecls;

    public List<VarDecl> parentVarDecls= new ArrayList<>();
    public List<FunDecl> parentFunDecls= new ArrayList<>();

    public ClassDecl(ClassType classType, Optional<ClassType> extension, List<Decl> decls) {

        type = classType;
        name = classType.identifier;
        this.extension = extension;
        this.varDecls = decls.stream().filter(x->x instanceof VarDecl).map(x->(VarDecl)x).toList();
        this.funDecls = decls.stream().filter(x->x instanceof FunDecl).map(x->(FunDecl)x).toList();

    }

    @Override
    public List<ASTNode> children() {
        ArrayList<ASTNode> temp = new ArrayList<>(varDecls);
        temp.addAll(funDecls);
        return temp;
    }
}
