package lexer;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;
import static lexer.Token.Category.*;

class TokeniserTest {
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
    void randomTes1() throws IOException {
        Tokeniser t = createTokeniser("123456abc =!");
        assertTokenEquals(t.nextToken(),INT_LITERAL,"123456");
        assertTokenEquals(t.nextToken(),IDENTIFIER,"abc");
        assertTokenEquals(t.nextToken(),ASSIGN);
        assertTokenEquals(t.nextToken(),INVALID);
    }

    @Test
    void fibonacci() throws Exception {// test to verify that any changes i make don't break existing solutions
        assertFileEquals("src/test/referenceFiles/fibonacciOut.txt","src/test/textFiles/fibonacci.c");
    }

    @Test
    void tictac() throws Exception {// test to verify that any changes i make don't break existing solutions
        assertFileEquals("src/test/referenceFiles/tictactoeOut.txt","src/test/textFiles/tictactoe.c");
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

    private void assertFileEquals(String  referenceFile,String fileToTest) throws Exception {
        File file=new File(fileToTest);
        BufferedReader fibReference = new BufferedReader(new FileReader(referenceFile));
        String line;
        Tokeniser t = new Tokeniser(new Scanner(file));
        while((line=fibReference.readLine())!=null) {
            Token next = t.nextToken();

            if (next.category == EOF)
                break;
            assertEquals(line,next.toString());
        }
        assertEquals(t.getNumErrors(), 0);

    }
}