import ast.Program;
import org.junit.jupiter.api.Test;
import parser.Parser;
import sem.NameAnalyzer;
import sem.SemanticAnalyzer;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


public class Part2And5Tests {

    String path= "src/test/";
    @Test
    void voidFunProtoAST(){
        assertCorrectASTFromString("simpleFunAST","void fun ();");
    }
    @Test
    void emptyProgram() {
        assertEqualsAST("Program()","");
    }

    @Test
    void structDecl(){
        assertEqualsAST("Program(StructTypeDecl(StructType(A),VarDecl(INT,x)))","struct A {int x;};");
        assertEqualsAST("Program(StructTypeDecl(StructType(node_t),VarDecl(INT,field1),VarDecl(CHAR,field2)))",
                "struct node_t { int field1; char field2; };");

    }

    @Test void randomEdTest(){
        assertEqualsAST("Program(VarDecl(ArrayType(ArrayType(CHAR, 6), 3),foo))","char foo[3][6];");

    }
    @Test
    void typeTests(){
        assertEqualsAST("Program(FunProto(VOID,fun,VarDecl(StructType(mystr),x)))","void fun(struct mystr x);");
        assertEqualsAST("Program(FunProto(CHAR,fun,VarDecl(StructType(mystr),x)))","char fun(struct mystr x);");
        assertEqualsAST("Program(FunProto(PointerType(CHAR),fun))","char* fun();");
        assertEqualsAST("Program(FunProto(VOID,fun,VarDecl(ArrayType(INT,3),x),VarDecl(CHAR,y)))","void fun(int x [3],char y);");
        assertEqualsAST("Program(FunProto(PointerType(StructType(B)),fun))","struct B* fun();");

    }
    @Test
    void arrayTests(){
        assertEqualsAST("Program(VarDecl(ArrayType(ArrayType(ArrayType(VOID,3),2),1),x))",
                """
                        void x[1][2][3];
                        """);
    }


    @Test
    void simpleFunction(){
        assertEqualsAST("Program(FunDecl(VOID, foo, Block(Return())))","void foo() { return; }");
    }

    @Test
    void precedenceTest(){
        String s="void fun(){" +
                "return 1+2/3+5 && x*11%2+3;" +
                "}";
        Utils.writeASTDotFromString(s);

        assertEqualsAST("Program(" +
                "FunDecl(VOID,fun," +
                    "Block(" +
                        "Return(" +
                            "BinOp(BinOp(BinOp(IntLiteral(1),ADD,BinOp(IntLiteral(2),DIV,IntLiteral(3))),ADD," +
                            "IntLiteral(5)),AND,BinOp(BinOp(BinOp(VarExpr(x),MUL,IntLiteral(11))," +
                            "MOD,IntLiteral(2)),ADD,IntLiteral(3))))" +
                        ")" +
                    ")" +
                ")",
                s);
    }

    @Test
    void ticTacTree(){
        Utils.writeASTDotFromFile(path+"textFiles/tictactoe.c");
//        assertCorrectASTFromFile("tictacAST","tictactoe.c");
    }

    @Test
    void linkedListTree(){
        Utils.writeASTDotFromFile(path+"textFiles/linkedList.c");
//        assertCorrectASTFromFile("tictacAST","linkedList.c");
    }

    @Test
    void fibAST()  {
        assertCorrectASTFromFile("fibonacciAST","fibonacci.c");
    }

    @Test
    void NameAnalysisDeclsAndProtos(){
        assertPassNameErrors("""
                void fun();
                void fun(){}
                """);
        assertFailNameErrors("""
                void fun();
                void fun();
                """);
        assertFailNameErrors("""
                void fun(){}
                void fun(){}
                """);
        assertPassNameErrors("""
                void fun();
                void fun(){}
                """);
        assertPassNameErrors("""
                void fun(){}
                void fun();
                """);
        assertFailNameErrors("""
                void fun();
                void fun(){}
                void fun(){}
                """);
        assertFailNameErrors("""
                void fun();
                void fun(){}
                void fun();
                """);
        assertFailNameErrors("""
                void fun(){}
                void fun();
                void fun();
                """);
    }
    @Test
    void NameAnaFunctionAndProtoTyping(){
        assertFailNameErrors("""
                void fun(int x);
                void fun(){}
                """);
        assertFailNameErrors("""
                char fun();
                void fun(){}
                """);
        assertFailNameErrors("""
                void x1(){}
                void x2(int x);
                void x3(){}
                void x3();
                """);
        assertPassNameErrors("""
                void x1(){}
                void x2(int x);
                void x3(){}
                void x3();
                void x2(int woof){}
                """);
    }
    @Test
    void nameVarDecl(){
        assertFailNameErrors("""
                void fun(){
                return x;
                }
                """);
    }
    @Test
    void nameFunCall(){
        assertFailNameErrors("""
                void fun(){
                    x();
                }
                """);
        assertFailNameErrors("""
                void x();
                void fun(){
                    x();
                }
                """);
        assertPassNameErrors("""
                void x();
                void fun(){
                    x();
                }
                void x(){}
                """);
        assertFailNameErrors("""                
                void fun(){
                    x();
                }
                void x();
                void x(){}
                """);
    }
    @Test
    void nameStructDecl(){
        assertPassNameErrors("""
                struct foo {
                    int x;
                    int y;
                };
                """);
        assertPassNameErrors("""
                struct foo {
                    int x;
                    int y;
                };
                struct foo {
                    int x;
                    int y;
                };
                """);
        assertFailNameErrors("""
                struct foo {
                    int x;
                    int x;
                };
                """);
    }

    @Test
    void nameShadowing(){
        assertPassNameErrors("""
                int x;
                int y;
                void fun(){
                    x=0;
                    y=0;
                    return x+y;
                }
                """);
        assertPassNameErrors("""
                int x;
                int y;
                void fun(){
                    int x;
                    x=0;
                    y=0;
                    return x+y;
                }
                """);
        assertPassNameErrors("""
                int foo(){}
                void fun(){
                  int foo;
                }
                """);
        assertFailNameErrors("""
                int foo(){}
                void fun(){
                  int foo;
                  return foo();
                }
                """);
    }

    @Test
    void binOpsTypeCheck(){
        failTypeAnalyzis("""
                void fun(){
                char x;
                int y;
                x + y;
                return;
                }
                """);
        passTypeAnalyzis("""
                int fun(){
                int x;
                int y;
                return x == y;
                }
                """);
        failTypeAnalyzis("""
                void fun(){
                char x;
                int y;
                return x == y;
                }
                """);

    }
    @Test
    void voidsTest(){
        failTypeAnalyzis("""
                struct s{
                    void x;
                    void y[2];
                };
                """);
        passTypeAnalyzis("""
                struct s{
                    void* x;
                };
                """);
        passTypeAnalyzis("""
                struct s{
                    void* x;
                };
                void main(){
                    struct s s;
                    return;
                    s.x=s.x;
                }
                """);
    }

    @Test
    void recursiveStruct(){
        passTypeAnalyzis("""
                struct s{
                    struct s *x;
                };
                """);
        failTypeAnalyzis("""
                struct s{
                    struct s x[6];
                    struct s y[6][6];
                };
                """);
        failTypeAnalyzis("""
                struct s{
                    struct s x;
                };
                """);
        passTypeAnalyzis("""
                struct A {
                  int x;
                };
                
                struct B {
                  struct A x;
                };
                """);
    }

    @Test
    void funCallTypeCheck(){
        passTypeAnalyzis("""
                void fun(int x){}
                
                void main(){
                int x;
                int y;
                fun(x);
                }
                """);
        passTypeAnalyzis("""
                void fun(int x){}
                
                void main(){
                int x;
                int y;
                fun(x+y);
                }
                """);
        passTypeAnalyzis("""
                void fun(int x){}
                
                void main(){
                int x;
                int y;
                fun(x+1/2);
                }
                """);
        failTypeAnalyzis("""
                void fun(int x){}
                
                void main(){
                fun();
                }
                """);
        failTypeAnalyzis("""
                void fun(int x){}
                
                void main(){
                int x;
                int y;
                fun(x,y);
                }
                """);
    }

    @Test
    void nameDefaultFuncs(){
        failTypeAnalyzis("void print_s();");
        passTypeAnalyzis("void print_s(char* s);");
        failTypeAnalyzis("void print_s(char* s){}");
    }
    @Test
    void fibTypeAnalyzis(){
        passTypeAnalyzisFile("fibonacci.c");
    }
    @Test
    void tictacTypeAnalyzis(){
        passTypeAnalyzisFile("tictactoe.c");
    }
    @Test
    void linkedListTypeAnalyzis(){
        passTypeAnalyzisFile("linkedList.c");
    }

    @Test
    void structDeclsTypeAnalyzis(){
        passTypeAnalyzis("""
                struct c{
                int x;
                int y;
                int z;
                };
                """);
        failTypeAnalyzis("""
                struct c{
                int x;
                int y;
                int y;
                };
                """);
        failTypeAnalyzis("""
                struct c{
                int x;
                int y;
                int z;
                };
                struct c{
                int x;
                };
                """);
        failTypeAnalyzis("""
                struct c{
                int x;
                int y;
                struct h z;
                };
                """);
        failTypeAnalyzis("""
                struct c{
                int x;
                int y;
                struct h z;
                };
                
                struct h{
                int x;
                int y;
                };
                """);
        passTypeAnalyzis("""
                struct h{
                int x;
                int y;
                };
                struct c{
                int x;
                int y;
                struct h z;
                };
                """);
    }
    @Test
    void voidDelcs(){
        failTypeAnalyzis("""
                void fun(){
                void x;
                void y;
                }
                """);
        failTypeAnalyzis("""
                void fun(){
                void x[6];
                }
                """);
        passTypeAnalyzis("""
                void fun(){
                int x[6];
                void *y;
                }
                """);
    }
    @Test
    void arrAccess(){
        passTypeAnalyzis("""
                int foo(){}
                int fun(){
                int x[6];
                char *y;
                return x[11];
                return (int)y[200];
                x[foo()];
                y[foo()+1];
                "hello world"[11];
                }
                """);
        failTypeAnalyzis("""
                void foo(){}
                void main(){
                int x[6];
                char *y;
                int b;
                b[1];
                }
                """);
        failTypeAnalyzis("""
                void foo(){}
                void main(){
                int x[6];
                char *y;
                int b;
                x[foo()];
                }
                """);
        failTypeAnalyzis("""
                void foo(){}
                void main(){
                int x[6];
                char *y;
                int b;
                y["hello world"];
                }
                """);
    }
    @Test
    void fieldAccess(){
        passTypeAnalyzis("""
                struct foo{
                    int x;
                    int y;
                    char c[6];
                };
                
                void foo(){
                    struct foo x;
                    x.x;
                    x.y;
                    x.c;
                }
                """);
        failTypeAnalyzis("""
                struct foo{
                    int x;
                    int y;
                    char c[6];
                };
                
                void foo(){
                    struct foo x;
                    int y;
                    y.x; //should fail
                    x.x;
                    x.y;
                    x.c;
                    x.nope; //should fail
                }
                """);
    }

    @Test
    void addressOf(){
        passTypeAnalyzis("""
                struct foo{
                    int x;
                };
                void main(){
                    int x;
                    struct foo strct;
                    &strct.x;
                }
                """);
        failTypeAnalyzis("""
                void main(){
                    &1;
                }
                """);
    }

    @Test
    void typeCast(){
        passTypeAnalyzis("""
                void fun(){
                int x;
                char c;
                int *p1;
                char *p2;
                int *p3;
                
                x = (int)c;
                p1=&x;
                p2=&c;
                p3=p1;
                p3=(int*)p2;
                p3 = (int*)p1;
                }
                """);
        failTypeAnalyzis("""
                void fun(){
                int x;
                char c;
                int *p1;
                char *p2;
                int *p3;
                
                x = (char)c;
                p1=&x;
                p2=&c;
                p3=p1;
                p3=(int*)x;
                p3 = (int*)c;
                }
                """);
    }
    @Test
    void assign(){
        passTypeAnalyzis("""
                struct s{
                    int x;
                };
                void main (){
                    struct s str;
                    str.x=1;
                }
                """);
        passTypeAnalyzis("""
                struct s{
                    int x;
                };
                void main (){
                    struct s str;
                    struct s* p;
                    p=&str;
                    (*p).x=1;
                }
                """);
        failTypeAnalyzis("""
                int x;
                int y;
                char c;
                
                void main(){
                    x='c';
                    y='c';
                    c=1;
                }
                """);

    }
    @Test
    void testReturn(){
        passTypeAnalyzis("""
                    int main(){
                        return 1;
                    }
                """);
        passTypeAnalyzis("""
                    void main(){
                
                    }
                """);
        passTypeAnalyzis("""
                    void main(){
                        return;
                    }
                """);
        failTypeAnalyzis("""
                    void main(){
                        return 1;
                    }
                """);
        failTypeAnalyzis("""
                    int main(){
                        return 'c';
                    }
                """);
    }
    @Test
    void nonInitStruct(){
        failTypeAnalyzis("""
                void main(){
                struct b b;
                b.x;
                }
                """);
    }
    @Test
    void arrAccessFromFunc(){
        passTypeAnalyzis("""
                int * foo(){
                    int *x;
                    int y;
                    x=&y;
                    return x;
                }
                void main(){
                    int x;
                    x= foo()[0];
                }
                """);
    }
    /*
     ****************************************************
     *               PART 5 tests                       *
     ****************************************************
     * */
    @Test void classExtensions(){
        failTypeAnalyzis("""
                class C{
                    int x;
                    int y;
                    int fun(){
                        return x+y;
                    }
                }
                void main(){
                    class C c;
                    c = new class C();
                    x.z;
                }
                """);
        failTypeAnalyzis("""
                class C{
                }
                class C{
                }
                """);
        failTypeAnalyzis("""
                class C extends C{
                }
                """);
        failTypeAnalyzis("""
                class C extends A{
                }
                """);
        passTypeAnalyzis("""
                class C{
                }
                class A extends C{
                }
                """);
        passTypeAnalyzis("""
                class C{
                }
                class A extends C{
                }
                void main(){
                    class C c;
                    class A a;
                    c = (class C) a;
                }
                """);
        failTypeAnalyzis("""
                class C{
                }
                class A {
                }
                void main(){
                    class C c;
                    class A a;
                    c = (class C) a;
                }
                """);
        failTypeAnalyzis("""
                class C{
                }
                class A {
                }
                void main(){
                    class C c;
                    class X a;
                }
                """);
    }
    @Test void funsAndClasses(){
        passTypeAnalyzis("""
                class c{
                    int x; int y;
                }
                class c fun(class c c){
                    return c;
                }
                int fun2(int x){
                    return x;
                }
                void main(){
                    class c c;
                    print_i(fun2((new class c()).x));
                    print_i(fun(new class c()).x);
                }
                """);
    }
    @Test void accessingParentFields(){
        failTypeAnalyzis("""
                class Parent{
                    int x;
                    int fun(){
                        return 0;
                    }
                }
                class Child extends Parent{
                    int x;
                }
                """);
        passTypeAnalyzis("""
                class Parent{
                    int x;
                    int fun(){
                        return 0;
                    }
                }
                class Child extends Parent{
                
                }
                void main(){
                    class Child c;
                    class Parent p;
                    c = new class Child();
                    p = (class Parent)c;
                    print_i(c.x);
                    print_i(c.fun());
                    print_i(p.x);
                    print_i(p.fun());
                }
                """);
        failTypeAnalyzis("""
                class GrandParent{
                    int x;
                    int fun(){
                        return 0;
                    }
                }
                class Parent extends GrandParent{
                    int x;
                    int fun(){
                        return 0;
                    }
                }
                class Child extends Parent{
                
                }
                void main(){
                    class Child c;
                    class Parent p;
                    c = new class Child();
                    p = (class Parent)c;
                    print_i(c.x);
                    print_i(c.fun());
                    print_i(p.x);
                    print_i(p.fun());
                }
                """);
        passTypeAnalyzis("""
                class GrandParent{
                    int x;
                    int fun(){
                        return 0;
                    }
                }
                class Parent extends GrandParent{
                    int fun(){
                        return 0;
                    }
                    int parentfun(){return 0;}
                }
                class Child extends Parent{
                    int childFun(){return 0;}
                }
                void main(){
                    class Child c;
                    class Parent p;
                    c = new class Child();
                    p = (class Parent)c;
                    print_i(c.x);
                    print_i(c.fun());
                    c.parentfun();
                    print_i(p.x);
                    print_i(p.fun());
                }
                """);
    }

    @Test void overridingMethods(){

        passTypeAnalyzis("""
                class a{
                    int y;
                    void foo(){}
                    void bar(){}
                    int fun(){return y;}
                }
                class b extends a{
                    int foo(){
                        bar();
                        return y;
                    }
                }
                void main(){
                    class b x;
                    x = new class b();
                    x.y=1;
                    print_i(x.foo());
                    print_i(x.fun());
                }
                """);
    }

    @Test void classes(){
        passTypeAnalyzis("""
                class C{
                    int x;
                    int y;
                    class C next;
                    void init(){
                        x=2;
                        y=2;
                    }
                    int fun(){
                        return x+y;
                    }
                }
                void main(){
                    class C c;
                    c = new class C();
                    c= (class C)c;
                    c.init();
                    c.y;
                    c.x;
                    print_i(c.fun());
                }
                """);
        failTypeAnalyzis("""
                class C{
                    int x;
                    int y;
                    int fun(){
                        return x+y;
                    }
                }
                void main(){
                    class C c;
                    c = new class C();
                    c.z;
                }
                """);
        failTypeAnalyzis("""
                class C{
                    int x;
                    int y;
                    int fun(){
                        return x+y;
                    }
                }
                void main(){
                    class C c;
                    c = new class C();
                    c.xun();
                }
                """);
        failTypeAnalyzis("""
                class C{
                    int x;
                    int y;
                    int fun(){
                        return x+y;
                    }
                }
                void main(){
                    fun();
                }
                """);

        passTypeAnalyzis("""
                class C{
                    int x;
                    int y;
                    class C next;
                    void init(){
                        x=2;
                        y=2;
                    }
                    int fun(){
                        return x+y;
                    }
                }
                void main(){
                    class C c;
                    c = new class C();
                    c.init();
                    c.y;
                    c.x;
                    print_i(c.fun());
                }
                """);
    }




    void assertCorrectASTFromString(String referenceFileName, String s){
        try {
            Utils.writeASTFromString(s);
            Utils.assertFileEquals(path+"referenceFiles/"+referenceFileName,path+"printerOut/out.txt",true);
        }catch (Exception e){
            fail();
        }
    }
    void assertCorrectASTFromFile(String referenceFileName, String fileToTest){
        try {
            Utils.writeASTFromFile(path+"textFiles/"+fileToTest);

            Utils.assertFileEquals(path+"referenceFiles/"+referenceFileName,path+"printerOut/out.txt",true);
        }catch (Exception e){
            fail();
        }
    }
    void assertEqualsAST(String expected,String stringToAST)  {
        String result;
        try {
            result = Utils.getASTstring(stringToAST);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        expected=expected.replaceAll("\\s+","");
        assertEquals(expected,result);
    }
    void assertPassNameErrors(String s){
        Parser pa =Utils.createParserFromString(s);
        Program p =pa.parse();
        assertEquals(0,pa.getNumErrors());
        NameAnalyzer n = new NameAnalyzer();
        n.visit(p);
        assertEquals(0,n.getNumErrors());
    }

    void passTypeAnalyzis(String s){
        Parser pa =Utils.createParserFromString(s);
        Program p =pa.parse();
        assertEquals(0,pa.getNumErrors());
        SemanticAnalyzer n = new SemanticAnalyzer();
        n.analyze(p);
        assertEquals(0,n.getNumErrors());
    }

    void passTypeAnalyzisFile(String filename){
        Parser pa =Utils.createParserFromFile(path+"textFiles/"+filename);
        Program p =pa.parse();
        assertEquals(0,pa.getNumErrors());
        SemanticAnalyzer n = new SemanticAnalyzer();
        n.analyze(p);
        assertEquals(0,n.getNumErrors());
    }

    void failTypeAnalyzis(String s){
        Parser pa =Utils.createParserFromString(s);
        Program p =pa.parse();
        assertEquals(0,pa.getNumErrors());
        SemanticAnalyzer n = new SemanticAnalyzer();
        n.analyze(p);
        assertNotEquals(0,n.getNumErrors());
    }

    void assertFailNameErrors(String s){
        Parser pa =Utils.createParserFromString(s);
        Program p =pa.parse();
        assertEquals(0,pa.getNumErrors());
        NameAnalyzer n = new NameAnalyzer();
        n.visit(p);
        assertNotEquals(0,n.getNumErrors());
    }

}
