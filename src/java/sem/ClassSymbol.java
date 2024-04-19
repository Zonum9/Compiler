package sem;

import ast.ClassDecl;

public class ClassSymbol extends Symbol{
    ClassDecl classDecl;
    public ClassSymbol(ClassDecl classDecl) {
        super(classDecl.name);
        this.classDecl=classDecl;
    }
}
