import ast.Program;
import org.junit.jupiter.api.Test;
import parser.Parser;
import sem.NameAnalyzer;
import sem.SemanticAnalyzer;
import sem.TypeAnalyzer;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


public class Part2Tests {

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
    @Test
    void typeTests(){
        assertEqualsAST("Program(FunProto(VOID,fun,VarDecl(StructType(mystr),x)))","void fun(struct mystr x);");
        assertEqualsAST("Program(FunProto(CHAR,fun,VarDecl(StructType(mystr),x)))","char fun(struct mystr x);");
        assertEqualsAST("Program(FunProto(PointerType(CHAR),fun))","char* fun();");
        assertEqualsAST("Program(FunProto(VOID,fun,VarDecl(ArrayType(INT,3),x),VarDecl(CHAR,y)))","void fun(int x [3],char y);");
        assertEqualsAST("Program(FunProto(PointerType(StructType(B)),fun))","struct B* fun();");

    }

    @Test
    void simpleFunction(){
        assertEqualsAST("Program(FunDecl(VOID, foo, Block(Return())))","void foo() { return; }");
    }

    @Test
    void precedencteTest(){
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

//    @Test
//    void ticTacTree(){
//        Utils.writeASTDotFromFile(path+"textFiles/tictactoe.c");
//        assertCorrectASTFromFile("tictacAST","tictactoe.c");
//    }

//    @Test
//    void linkedListTree(){
//        Utils.writeASTDotFromFile(path+"textFiles/linkedList.c");
////        assertCorrectASTFromFile("tictacAST","linkedList.c");
//    }

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
        assertFailNameErrors("""
                int foo(){}
                void fun(){
                  int foo;
                }
                """);
    }
    @Test
    void nameDefaultFuncs(){
        failType("void print_s();");
        passType("void print_s(char* s);");
        failType("void print_s(char* s){}");
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

    void passType(String s){
        Parser pa =Utils.createParserFromString(s);
        Program p =pa.parse();
        assertEquals(0,pa.getNumErrors());
        SemanticAnalyzer n = new SemanticAnalyzer();
        n.analyze(p);
        assertEquals(0,n.getNumErrors());
    }
    void failType(String s){
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
