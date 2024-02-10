import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


public class Part2Tests {

    String path= "src/test/";
    @Test
    void voidFunProtoAST(){
        assertCorrectASTFromString("simpleFunAST","void fun ();");
    }
    @Test
    void emptyProgram() throws IOException {
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
    }

    @Test
    void simpleFunction(){
        assertEqualsAST("Program(FunDecl(VOID, foo, Block(Return())))","void foo() { return; }");
    }


    @Test
    void fibAST() throws  Exception {
        assertCorrectASTFromFile("fibonacciAST","fibonacci.c");
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
        String result = null;
        try {
            result = Utils.getASTstring(stringToAST);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        expected=expected.replaceAll("\\s+","");
        assertEquals(expected,result);
    }
}
