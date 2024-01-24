package parser;


import lexer.Token;
import lexer.Token.Category;
import lexer.Tokeniser;
import util.CompilerPass;

import java.util.LinkedList;
import java.util.Queue;


/**
 * @author cdubach
 */
public class Parser  extends CompilerPass {

    private Token token;

    private Queue<Token> buffer = new LinkedList<>();

    private final Tokeniser tokeniser;



    public Parser(Tokeniser tokeniser) {
        this.tokeniser = tokeniser;
    }

    public void parse() {
        // get the first token
        nextToken();

        parseProgram();
    }



    //private int error = 0;
    private Token lastErrorToken;

    private void error(Category... expected) {

        if (lastErrorToken == token) {
            // skip this error, same token causing trouble
            return;
        }

        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (Category e : expected) {
            sb.append(sep);
            sb.append(e);
            sep = "|";
        }
        String msg = "Parsing error: expected ("+sb+") found ("+token+") at "+token.position;
        System.out.println(msg);

        incError();
        lastErrorToken = token;
    }

    /*
     * Look ahead the i^th element from the stream of token.
     * i should be >= 1
     */
    private Token lookAhead(int i) {
        // ensures the buffer has the element we want to look ahead
        while (buffer.size() < i)
            buffer.add(tokeniser.nextToken());

        int cnt=1;
        for (Token t : buffer) {
            if (cnt == i)
                return t;
            cnt++;
        }

        assert false; // should never reach this
        return tokeniser.nextToken();
    }


    /*
     * Consumes the next token from the tokeniser or the buffer if not empty.
     */
    private void nextToken() {
        if (!buffer.isEmpty())
            token = buffer.remove();
        else
            token = tokeniser.nextToken();
    }

    /*
     * If the current token is equals to the expected one, then skip it, otherwise report an error.
     */
    private void expect(Category... expected) {
        for (Category e : expected) {
            if (e == token.category) {
                nextToken();
                return;
            }
        }
        error(expected);
    }

    /*
    * Returns true if the current token is equals to any of the expected ones.
    */
    private boolean accept(Category... expected) {
        for (Category e : expected) {
            if (e == token.category)
                return true;
        }
        return false;
    }


    private void parseProgram() {
        parseIncludes();

        while (accept(Category.STRUCT, Category.INT, Category.CHAR, Category.VOID)) {
            if (token.category == Category.STRUCT &&
                    lookAhead(1).category == Category.IDENTIFIER &&
                    lookAhead(2).category == Category.LBRA) {
                parseStructDecl();
            }
            else {
                // to be completed ...todo
                nextToken(); // this line should be modified/removed
            }
        }
        // to be completed ...todo

        expect(Category.EOF);
    }

    // includes are ignored, so does not need to return an AST node
    private void parseIncludes() {
        if (accept(Category.INCLUDE)) {
            nextToken();
            expect(Category.STRING_LITERAL);
            parseIncludes();
        }
    }

    private void parseStructDecl(){
        expect(Category.STRUCT);
        expect(Category.IDENTIFIER);
        expect(Category.LBRA);
        parseVarDeclaration();
        parse0orMOreVarDeclaration();
        expect(Category.RBRA);
        expect(Category.SC);
        // to be completed ...todo
    }

    private void parseVarDeclaration(){
        parseType();
        expect(Category.IDENTIFIER);
        parse0orMoreArray();
        expect(Category.SC);
    }
    private void parse0orMOreVarDeclaration(){//todo idk what todo
        if (lookAhead(1).category!= Category.LSBR)
            return;
    }

    private void parse0orMoreArray(){
        if (lookAhead(1).category!= Category.LSBR)
            return;
        nextToken();
        expect(Category.INT_LITERAL);
        expect(Category.RSBR);
        parse0orMoreArray();
    }

    private void parseType(){
        //if the type is struct, it must be followed by an identifier
        if (accept(Category.STRUCT)){
            nextToken();
            expect(Category.IDENTIFIER);
            return;
        }
        expect(Category.INT,Category.CHAR,Category.VOID);
        parse0orMorePointers();
    }

    private void parse0orMorePointers(){
        if (lookAhead(1).category!= Category.ASTERIX)
            return;
        nextToken();
        parse0orMorePointers();
    }


    // to be completed ...
}
