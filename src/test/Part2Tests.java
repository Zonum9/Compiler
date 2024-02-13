import ast.Program;
import org.junit.jupiter.api.Test;
import parser.Parser;
import sem.NameAnalyzer;
import sem.SemanticAnalyzer;

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
    void arrayTests(){
        assertEqualsAST("Program(VarDecl(ArrayType(ArrayType(ArrayType(VOID,1),2),3),x))",
                """
                        void x[1][2][3];
                        """);
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
                void fun(){
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
                void fun(){
                int x[6];
                char *y;
                return x[11];
                return y[200];
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
