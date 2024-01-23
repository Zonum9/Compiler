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
        Tokeniser t = createTokeniser("//this is the thing that is a thing\n/*sfssfkslf;lskfsfkjlsfsfkld;lskjf*/");
        assertTokenEquals(t.nextToken(),EOF);
        t = createTokeniser("/*sfssfkslf;lskfsfkjlsfsfkld;lskjf");
        assertTokenEquals(t.nextToken(),INVALID);
    }
    @Test
    void fibonacci() throws Exception {
        File fib=new File("src/test/textFiles/fibonacci.c");
        BufferedReader fibReference = new BufferedReader(new FileReader("src/test/referenceFiles/fibonacciOut.txt"));
        String line;
        Tokeniser t = new Tokeniser(new Scanner(fib));
        while((line=fibReference.readLine())!=null) {
            Token next = t.nextToken();
            if (next.category == EOF)
                break;
            assertEquals(next.toString(), line);
            assertEquals(t.getNumErrors(), 0);
        }
        assertEquals(t.getNumErrors(), 0);

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
}