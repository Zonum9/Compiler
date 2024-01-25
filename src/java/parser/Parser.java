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
//        nextToken();
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
            //if this fails, then the program is automatically not valid, since
            //we must have a variable declaration, or a function or a struct declaration
            else {
                //a left parenthesis must mean a function
                if(lookAhead(2).category == Category.LPAR || lookAhead(3).category==Category.LPAR) {
                    parseFunc();
                }else{
                    parseVarDeclaration();
                }
            }
        }

        expect(Category.EOF);
    }

    private void parseFunc(){
        parseType();
        expect(Category.IDENTIFIER);
        expect(Category.LPAR);
        parseParams();
        expect(Category.RPAR);
        if(accept(Category.LBRA))
            parseBlock();
        else
            expect(Category.SC);

    }

    private void parseBlock(){
        expect(Category.LBRA);
        parse0orMoreVarDeclaration();
        parse0orMoreStatements();
        expect(Category.RBRA);
    }
    private void parseWhile(){
        expect(Category.WHILE);
        expect(Category.LPAR);
        parseExpression();
        expect(Category.RPAR);
        parseStatement();
    }
    private void parseExpression(){
        switch (token.category){
            case INT_LITERAL,CHAR_LITERAL,STRING_LITERAL -> nextToken();
            case PLUS,MINUS,ASTERIX,AND -> {nextToken(); parseExpression();}
            case SIZEOF -> {
                nextToken();
                expect(Category.LPAR);
                parseType();
                expect(Category.RPAR);
            }
            case LPAR -> {
                nextToken();//consume "("
                if(acceptType()) { //it's a type cast  "(" type ")" exp
                    parseType();
                    expect(Category.RPAR);
                    parseExpression();
                    return;
                }
                // if it's not a type cast, then it's just a "(" exp ")"
                parseExpression();
                expect(Category.RPAR);

            }  //typecast
            case IDENTIFIER -> {
                //if there is a "(" after the identifier, then it must be a function call
                if(lookAhead(1).category== Category.LPAR)
                    parseFunctionCall();
                else //lone identifier
                    nextToken();
            }
        }
        switch (token.category){
            case LSBR -> { nextToken(); parseExpression(); expect(Category.RSBR);} //array access
            case DOT -> {nextToken(); expect(Category.IDENTIFIER);} //field access
            case ASSIGN,LT,GT,LE,GE,NE,EQ,PLUS,MINUS,DIV,ASTERIX,REM,LOGOR,LOGAND ->parseOperation();
        }
    }
    private void parseFunctionCall(){
        expect(Category.IDENTIFIER);
        expect(Category.LPAR);
        parseArgs();
        expect(Category.RPAR);
    }

    private void parseArgs(){
        if (token.category == Category.RPAR || token.category == Category.EOF)
            return;
        parseExpression();
        if(token.category == Category.COMMA) {
            nextToken();
            parseArgs();
        }
    }
    private void parseOperation(){
        nextToken();
        parseExpression();
//        parseOperation();
    }

    private void parseIf(){
        expect(Category.IF);
        expect(Category.LPAR);
        parseExpression();
        expect(Category.RPAR);
        parseStatement();
        if(!accept(Category.ELSE))
            return;
        nextToken();//consume else
        parseStatement();
    }
    private void parseReturn(){
        expect(Category.RETURN);
        if(accept(Category.SC))//return statement without expression
            return;
        parseExpression();
        expect(Category.SC);
    }

    private void parseStatement(){
        switch (token.category){
            case LBRA -> parseBlock();
            case WHILE -> parseWhile();
            case IF -> parseIf();
            case RETURN -> parseReturn();
            case CONTINUE -> {expect(Category.CONTINUE); expect(Category.SC);}
            case BREAK -> {expect(Category.BREAK); expect(Category.SC);}
            default -> {parseExpression();expect(Category.SC);}
        }

    }

    private void parse0orMoreStatements(){
        while(token.category != Category.RBRA && token.category != Category.EOF) {
            parseStatement();
        }
    }

    private void parseParams(){
        //no params
        if(!acceptType()) {
            return;
        }
        parseType();//consume type
        expect(Category.IDENTIFIER);
        parse0orMoreParams();
    }

    private void parse0orMoreParams(){
        if (!accept(Category.COMMA))
            return;
        nextToken();//consume comma
        parseParams();
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
        parse0orMoreVarDeclaration();
        expect(Category.RBRA);
        expect(Category.SC);
    }

    private void parseVarDeclaration(){
        parseType();
        expect(Category.IDENTIFIER);
        parse0orMoreArray();
        expect(Category.SC);
    }
    private void parse0orMoreVarDeclaration(){
        if (!acceptType())
            return;
        parseVarDeclaration();
        parse0orMoreVarDeclaration();
    }

    private void parse0orMoreArray(){
        if (!accept(Category.LSBR))
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
    private boolean acceptType(){
        return accept(Category.STRUCT,Category.INT,Category.CHAR,Category.VOID);
    }

    private void parse0orMorePointers(){
        if (!accept(Category.ASTERIX))
            return;
        nextToken();
        parse0orMorePointers();
    }
}
