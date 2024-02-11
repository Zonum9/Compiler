import lexer.Scanner;
import lexer.Token;
import lexer.Tokeniser;
import static lexer.Token.Category.*;
import parser.Parser;

import org.junit.jupiter.api.Test;
import java.io.*;


import static org.junit.jupiter.api.Assertions.*;


class Part1Tests {
    @Test
    void basicTest() throws IOException {
        Tokeniser t = Utils.createTokeniserFromString("hello world");
        assertTokenEquals(t.nextToken(),IDENTIFIER,"hello");
        assertTokenEquals(t.nextToken(),IDENTIFIER,"world");
    }
    @Test
    void commentsTest() throws IOException {
        Tokeniser t = Utils.createTokeniserFromString("//this is the thing\\ th//a/a/sd/sdlkjld/.dsflsdkfxcxcvat is a thing\n" +
                "/*sfssfkslf;lskfsfkjlsfsf//a/s//w//****** / *kld;lskjf*/");
        assertTokenEquals(t.nextToken(),EOF);
        t = Utils.createTokeniserFromString("/*sfssfkslf;lskfsfkjlsfsfkld;lskjf");
        assertTokenEquals(t.nextToken(),INVALID);

        t = Utils.createTokeniserFromString("/ ");
        assertTokenEquals(t.nextToken(),DIV);

    }

    @Test
    void randomTest1() throws IOException {
        Tokeniser t = Utils.createTokeniserFromString("123456abc =!");
        assertTokenEquals(t.nextToken(),INT_LITERAL,"123456");
        assertTokenEquals(t.nextToken(),IDENTIFIER,"abc");
        assertTokenEquals(t.nextToken(),ASSIGN);
        assertTokenEquals(t.nextToken(),INVALID);
    }
    @Test
    void structTest() throws IOException {
        Tokeniser t = Utils.createTokeniserFromString("struct mystruct { int* thing;};");
        assertTokenEquals(t.nextToken(),STRUCT);
        assertTokenEquals(t.nextToken(),IDENTIFIER);
        assertTokenEquals(t.nextToken(),LBRA);
        assertTokenEquals(t.nextToken(),INT);
        assertTokenEquals(t.nextToken(),ASTERISK);
        assertTokenEquals(t.nextToken(),IDENTIFIER);
        assertTokenEquals(t.nextToken(),SC);
        assertTokenEquals(t.nextToken(),RBRA);
        assertTokenEquals(t.nextToken(),SC);
        assertTokenEquals(t.nextToken(),EOF);
    }
    @Test
    void pointerTest() throws IOException {
        Tokeniser t = Utils.createTokeniserFromString("int**");
        assertTokenEquals(t.nextToken(),INT);
        assertTokenEquals(t.nextToken(),ASTERISK);
        assertTokenEquals(t.nextToken(),ASTERISK);
        assertTokenEquals(t.nextToken(),EOF);
    }
    @Test
    void arrayTest() throws IOException {
        Tokeniser t = Utils.createTokeniserFromString("int[][]");
        assertTokenEquals(t.nextToken(),INT);
        assertTokenEquals(t.nextToken(),LSBR);
        assertTokenEquals(t.nextToken(),RSBR);
        assertTokenEquals(t.nextToken(),LSBR);
        assertTokenEquals(t.nextToken(),RSBR);
        assertTokenEquals(t.nextToken(),EOF);
    }

    @Test
    void fibonacciLexer() throws Exception {// test to verify that any changes i make don't break existing solutions
        Utils.assertFileEqualsTokenization("src/test/referenceFiles/fibonacciOut.txt","src/test/textFiles/fibonacci.c");
    }

    @Test
    void tictacLexer() throws Exception {// test to verify that any changes i make don't break existing solutions
        Utils.assertFileEqualsTokenization("src/test/referenceFiles/tictactoeOut.txt","src/test/textFiles/tictactoe.c");
    }

    @Test
    void structParser(){
        assertParsePass("struct mystruct { int thing;};");
        assertParseFail("struct mystruct { int thing;}");
        assertParseFail("struct mystruct { int thing};");
        assertParseFail("struct mystruct { int thing}");
        assertParseFail("struct mystruct {thing;};");
        assertParseFail("struct mystruct");
        assertParsePass("struct mystruct { int* thing;};");
        assertParseFail("struct mystruct { int[8] thing;};");
        assertParseFail("struct mystruct { int thing[];};");
        assertParsePass("struct mystruct { int thing [8];};");
        assertParsePass("struct mystruct { int thing;" +
                "int thing[123];" +
                "int* thing;" +
                "int** thing;" +
                "int*** thing;" +
                "int thing[2][3][3];" +
                "int thing;};");
    }

    @Test
    void functionParser(){
        assertParsePass("char* func (){}");
        assertParsePass("char* func (int x, int y, int* z){" +
                "int x;" +
                "struct mystruct x;" +
                "char* amazing[12];" +
                "x[1];" +
                "3+4;" +
                "return;" +
                "return;" +
                "return;" +
                "}");
        assertParsePass("char* func (int x, int y, int* z){" +
                "struct aStruct* x;" +
                "struct aStruct x;" +
                "struct aStruct x[123];" +
                "fun(0);" +
                "fun(1==2);" +
                "fun(*x);" +
                "fun(1,2,3,4,5,6,7);" +
                "fun();" +
                "fun (&hello);"+
                "}");
        assertParseFail("char* func (int x, int y, int* z){" +
                "struct;}");
        assertParsePass("void fun(){" +
                "(struct a)x;" +
                "}");
        assertParseFail("void fun(){==}");
        assertParseFail("char* func ()");
        assertParseFail("char* func (){");
        assertParseFail("char* func ();{}");
    }
    @Test
    void missedTest(){
        assertParseFail("void fun (int x,);");
        assertParsePass("void fun(){" +
                "(void)x+y;" +
                "}");
        assertParsePass("void fun(){" +
                "(void)x+y+x;" +
                "}");
        assertParsePass("void fun(){" +
                "(void)x;" +
                "}");

    }
    @Test
    void operationAfterPred1(){
        assertParsePass("void fun(){" +
                "fun()[1];" +
                "}");
        assertParsePass("void fun(){" +
                "fun()[1][2];" +
                "}");
        assertParsePass("void fun(){" +
                "fun()[1][2] = 11;" +
                "}");
        assertParsePass("void fun(){" +
                "fun()[1] = 11;" +
                "}");


    }

    @Test
    void operationsTest(){
//        assertParseFail("1+2=3==3==4==5");
//        assertParsePass("void fun(){" +
//                "1+2=3==3==4==5;" +
//                "}");
        assertParsePass("void fun(){" +
                "x[1+2+ex+111*omega!=x]=11;" +
                "}");
        assertParseFail("void fun(){" +
                "x[1+2+ex+111*omega!=x]=11;" +
                "&&&x&&x;" +
                "****x;" +
                "(struct mystruct*)hello;" +
                "(*hello).hello;" +
                "}");
        assertParsePass("void fun(){" +
                "x[1+2+ex+111*omega!=x]=11;" +
                "&x&&x;" +
                "****x;" +
                "(struct mystruct*)hello;" +
                "(*hello).hello;" +
                "}");
    }
    @Test
    void emptyIfAndWhile(){
        assertParseFail("void fun(){" +
                "while(){}" +
                "}");
        assertParseFail("void fun(){" +
                "while(true)" +
                "}");
        assertParseFail("void fun(){" +
                "while(true);" +
                "}");
        assertParseFail("void fun(){" +
                "if();" +
                "}");
        assertParseFail("void fun(){" +
                "if(){}" +
                "}");
        assertParseFail("void fun(){" +
                "if(true);" +
                "}");
        assertParsePass("void fun(){" +
                "if(true){}" +
                "}");
        assertParsePass("void fun(){" +
                "while(true){}" +
                "}");
        assertParsePass("void fun(){" +
                "while(true){while(true){while(true){}}}" +
                "}");
    }
    @Test
    void functionApplication(){
        assertParseFail("void fun(){" +
                "fun()" +
                "}");
        assertParseFail("void fun(){" +
                "fun(return);" +
                "}");
        assertParseFail("void fun(){" +
                "fun(if);" +
                "}");
        assertParseFail("void fun(){" +
                "fun(return;);" +
                "}");
        assertParseFail("void fun(){" +
                "fun(if(true){});" +
                "}");
        assertParsePass("void fun(){" +
                "fun();" +
                "}");
        assertParsePass("void fun(){" +
                "fun(*thing);" +
                "}");
        assertParsePass("void fun(){" +
                "fun(&thing);" +
                "}");
        assertParsePass("void fun(){" +
                "fun((void*)thing);" +
                "}");
        assertParseFail("void fun(){" +
                "fun(1+4+2/11 & 123);" +
                "}");
        assertParsePass("void fun(){" +
                "fun(1+4+2/11 && 123);" +
                "}");
        assertParseFail("void fun(){" +
                "fun(1+4+2/);" +
                "}");
    }


    @Test
    void declarationTest(){
        assertParsePass("void* thing [12323];");
        assertParsePass("void*** thing [12323][23];");
        assertParsePass("char* * * thing [2][1][23];");
        assertParseFail("char* * * thing [2][][23];");
    }
    @Test
    void functonPrototypeTest(){
        assertParseFail("void func(int a, struct b);");
        assertParsePass("void func(int a, struct mystruct b);");
        assertParsePass("struct m func();");
        assertParseFail("struct func();");
        assertParseFail("struct func(thing);");
        assertParseFail("struct func(thing thing);");
        assertParseFail("void func(int a struct mystruct b);");
        assertParsePass("int ThIA_sIa0asd ();");
        assertParsePass("void func(int a);");
        assertParsePass("void func(int a,char c, void* a);");
        assertParsePass("void func(int a,char c, void* a);");
    }
    @Test
    void emptyFunction(){
        assertParsePass("char* fun(){}");
    }

    @Test
    void tryingToStackOverflow(){
        assertParseFail("fun{");
        assertParseFail("int fun (thing){{{{}}}");
        assertParseFail("void func (int a,int b)");
        assertParseFail("void func (int a,int b){" +
                "func()}");
        assertParsePass("void func (int a,int b){" +
                "func();}");
    }

    @Test
    void fibParser(){
        assertFileParsePass("src/test/textFiles/fibonacci.c");
    }

    @Test
    void ticTacParser(){
        assertFileParsePass("src/test/textFiles/tictactoe.c");
    }

    @Test
    void mergeLinkedListParser(){
        //code taken from https://www.geeksforgeeks.org/merge-two-sorted-linked-lists/
        assertFileParsePass("src/test/textFiles/linkedList.c");
    }

    @Test
    void idk(){
        assertParseFail("void& fun (){}");
        assertParsePass("void** fun (){}");
        assertParsePass("void* fun (){}");
        assertParseFail("void fun (){" +
                "if(x&&&&x){}" +
                "}");
        assertParsePass("void fun (){" +
                "if(x&&(x&&x)){}" +
                "}");
    }

    private void assertFileParseFail(String filename){
        try {
            File file=new File(filename);
            Parser p = new Parser(new Tokeniser(new Scanner(file)));
            p.parse();
            assertNotEquals(0,p.getNumErrors());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void assertFileParsePass(String filename){
        try {
            File file=new File(filename);
            Parser p = new Parser(new Tokeniser(new Scanner(file)));
            p.parse();
            assertEquals(0,p.getNumErrors());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void assertTokenEquals(Token t, Token.Category cat, String data){
        assertEquals(cat,t.category);
        assertEquals(data,t.data);
    }
    private void assertTokenEquals(Token t, Token.Category cat){
        assertEquals(cat,t.category);
    }


    private void assertParsePass(String stringToParse){
        try {
            Parser p = new Parser(Utils.createTokeniserFromString(stringToParse));
            p.parse();
            assertEquals(0,p.getNumErrors());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void assertParseFail(String stringToParse){
        try {
            Parser p = new Parser(Utils.createTokeniserFromString(stringToParse));
            p.parse();
            assertNotEquals(0,p.getNumErrors());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}