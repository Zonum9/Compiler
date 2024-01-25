import lexer.Scanner;
import lexer.Token;
import lexer.Tokeniser;
import org.junit.jupiter.api.Test;
import parser.Parser;

import java.io.*;
import java.nio.file.Files;

import static lexer.Token.Category.*;
import static org.junit.jupiter.api.Assertions.*;


class Part1Tests {
    @Test
    void basicTest() throws IOException {
        Tokeniser t = createTokeniser("hello world");
        assertTokenEquals(t.nextToken(),IDENTIFIER,"hello");
        assertTokenEquals(t.nextToken(),IDENTIFIER,"world");
    }
    @Test
    void commentsTest() throws IOException {
        Tokeniser t = createTokeniser("//this is the thing\\ th//a/a/sd/sdlkjld/.dsflsdkfxcxcvat is a thing\n" +
                "/*sfssfkslf;lskfsfkjlsfsf//a/s//w//****** / *kld;lskjf*/");
        assertTokenEquals(t.nextToken(),EOF);
        t = createTokeniser("/*sfssfkslf;lskfsfkjlsfsfkld;lskjf");
        assertTokenEquals(t.nextToken(),INVALID);

        t = createTokeniser("/ ");
        assertTokenEquals(t.nextToken(),DIV);

    }

    @Test
    void randomTest1() throws IOException {
        Tokeniser t = createTokeniser("123456abc =!");
        assertTokenEquals(t.nextToken(),INT_LITERAL,"123456");
        assertTokenEquals(t.nextToken(),IDENTIFIER,"abc");
        assertTokenEquals(t.nextToken(),ASSIGN);
        assertTokenEquals(t.nextToken(),INVALID);
    }
    @Test
    void structTest() throws IOException {
        Tokeniser t = createTokeniser("struct mystruct { int* thing;};");
        assertTokenEquals(t.nextToken(),STRUCT);
        assertTokenEquals(t.nextToken(),IDENTIFIER);
        assertTokenEquals(t.nextToken(),LBRA);
        assertTokenEquals(t.nextToken(),INT);
        assertTokenEquals(t.nextToken(),ASTERIX);
        assertTokenEquals(t.nextToken(),IDENTIFIER);
        assertTokenEquals(t.nextToken(),SC);
        assertTokenEquals(t.nextToken(),RBRA);
        assertTokenEquals(t.nextToken(),SC);
        assertTokenEquals(t.nextToken(),EOF);
    }
    @Test
    void pointerTest() throws IOException {
        Tokeniser t = createTokeniser("int**");
        assertTokenEquals(t.nextToken(),INT);
        assertTokenEquals(t.nextToken(),ASTERIX);
        assertTokenEquals(t.nextToken(),ASTERIX);
        assertTokenEquals(t.nextToken(),EOF);
    }
    @Test
    void arrayTest() throws IOException {
        Tokeniser t = createTokeniser("int[][]");
        assertTokenEquals(t.nextToken(),INT);
        assertTokenEquals(t.nextToken(),LSBR);
        assertTokenEquals(t.nextToken(),RSBR);
        assertTokenEquals(t.nextToken(),LSBR);
        assertTokenEquals(t.nextToken(),RSBR);
        assertTokenEquals(t.nextToken(),EOF);
    }

    @Test
    void fibonacciLexer() throws Exception {// test to verify that any changes i make don't break existing solutions
        assertFileEquals("src/test/referenceFiles/fibonacciOut.txt","src/test/textFiles/fibonacci.c");
    }

    @Test
    void tictacLexer() throws Exception {// test to verify that any changes i make don't break existing solutions
        assertFileEquals("src/test/referenceFiles/tictactoeOut.txt","src/test/textFiles/tictactoe.c");
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
        assertParseFail("char* func ()");
        assertParseFail("char* func (){");
        assertParseFail("char* func ();{}");
        assertParsePass("char* func (int x, int y, int* z){" +
                "int x;" +
                "struct mystruct x;" +
                "char* amazing[12];}");
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

    private void assertTokenEquals(Token t, Token.Category cat, String data){
        assertEquals(cat,t.category);
        assertEquals(data,t.data);
    }
    private void assertTokenEquals(Token t, Token.Category cat){
        assertEquals(cat,t.category);
    }
    private Tokeniser createTokeniser(String content) throws IOException{
        File f= File.createTempFile("hello",".c");
        Files.writeString(f.toPath(), content);
        return new Tokeniser(new Scanner(f));
    }
    private void assertParsePass(String stringToParse){
        try {
            Parser p = new Parser(createTokeniser(stringToParse));
            p.parse();
            assertEquals(0,p.getNumErrors());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void assertParseFail(String stringToParse){
        try {
            Parser p = new Parser(createTokeniser(stringToParse));
            p.parse();
            assertNotEquals(0,p.getNumErrors());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void assertFileEquals(String  referenceFile,String fileToTest) throws Exception {
        File file=new File(fileToTest);
        BufferedReader fileToReference = new BufferedReader(new FileReader(referenceFile));
        String line;
        Tokeniser t = new Tokeniser(new Scanner(file));
        while((line=fileToReference.readLine())!=null) {
            Token next = t.nextToken();

            if (next.category == EOF)
                break;
            //Tokens with new lines are still considered a single token, so need to split them
            if (next.toString().contains("\n")){
                String[] splits= next.toString().split("\n");
                for (int i = 0; i < splits.length; i++) {
                    assertEquals(line,splits[i]);
                    if (i != splits.length-1)
                        line=fileToReference.readLine();
                    if (line == null){
                        fail();
                    }
                }
            }else
                assertEquals(line,next.toString());
        }
        fileToReference.close();
        assertEquals(t.getNumErrors(), 0);

    }
}