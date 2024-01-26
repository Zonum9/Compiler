package parser;


import lexer.Token;
import lexer.Token.Category;
import lexer.Tokeniser;
import util.CompilerPass;

import java.util.LinkedList;
import java.util.Queue;
import static lexer.Token.Category.*;


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

        while (accept(STRUCT, INT, CHAR, VOID)) {
            if (token.category == STRUCT &&
                    lookAhead(1).category == IDENTIFIER &&
                    lookAhead(2).category == LBRA) {
                parseStructDecl();
            }
            //if this fails, then the program is automatically not valid, since
            //we must have a variable declaration, or a function or a struct declaration
            else {
                //a left parenthesis must mean a function
                if(lookAhead(2).category == LPAR || lookAhead(3).category==LPAR) {
                    parseFunc();
                }else{
                    parseVarDeclaration();
                }
            }
        }

        expect(EOF);
    }

    private void parseFunc(){
        parseType();
        expect(IDENTIFIER);
        expect(LPAR);
        parseParams();
        expect(RPAR);
        if(accept(LBRA))
            parseBlock();
        else
            expect(SC);

    }

    private void parseBlock(){
        expect(LBRA);
        parse0orMoreVarDeclaration();
        parse0orMoreStatements();
        expect(RBRA);
    }
    private void parseWhile(){
        expect(WHILE);
        expect(LPAR);
        parseExpression();
        expect(RPAR);
        parseStatement();
    }
    private void parseExpression(){
        switch (token.category){
            case INT_LITERAL,CHAR_LITERAL,STRING_LITERAL -> nextToken();
            case PLUS,MINUS,ASTERISK,AND,LOGAND -> {nextToken(); parseExpression();}//include logAnd because of double ref
            case SIZEOF -> {
                nextToken();
                expect(LPAR);
                parseType();
                expect(RPAR);
            }
            case LPAR -> {
                nextToken();//consume "("
                if(acceptType()) { //it's a type cast  "(" type ")" exp
                    parseType();
                    expect(RPAR);
                    parseExpression();
                    return;
                }
                // if it's not a type cast, then it's just a "(" exp ")"
                parseExpression();
                expect(RPAR);

            }  //typecast
            case IDENTIFIER -> {
                //if there is a "(" after the identifier, then it must be a function call
                if(lookAhead(1).category== LPAR)
                    parseFunctionCall();
                else //lone identifier
                    nextToken();
            }
        }
        parsePostExpression();
    }
    private void parsePostExpression(){
        switch (token.category){
            case LSBR -> { nextToken(); parseExpression(); expect(RSBR); parsePostExpression();} //array access
            case DOT -> {nextToken(); expect(IDENTIFIER);parsePostExpression();} //field access
            case ASSIGN,LT,GT,LE,GE,NE,EQ,PLUS,MINUS,DIV,ASTERISK,REM,LOGOR,LOGAND -> {
                parseOperation(); parsePostExpression();
            }
        }
    }

    private void parseFunctionCall(){
        expect(IDENTIFIER);
        expect(LPAR);
        parseArgs();
        expect(RPAR);
    }

    private void parseArgs(){
        if (token.category == RPAR || token.category == EOF)
            return;
        parseExpression();
        if(token.category == COMMA) {
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
        expect(IF);
        expect(LPAR);
        parseExpression();
        expect(RPAR);
        parseStatement();
        if(!accept(ELSE))
            return;
        nextToken();//consume else
        parseStatement();
    }
    private void parseReturn(){
        expect(RETURN);
        if(accept(SC)) {//return statement without expression
            nextToken();
            return;
        }
        parseExpression();
        expect(SC);
    }

    private void parseStatement(){
        switch (token.category){
            case LBRA -> parseBlock();
            case WHILE -> parseWhile();
            case IF -> parseIf();
            case RETURN -> parseReturn();
            case CONTINUE -> {expect(CONTINUE); expect(SC);}
            case BREAK -> {expect(BREAK); expect(SC);}
            default -> {parseExpression();expect(SC);}
        }

    }

    private void parse0orMoreStatements(){
        while(acceptStatement()) {
            parseStatement();
        }
    }

    private boolean acceptStatement(){
        return accept(LBRA,WHILE,IF,RETURN,CONTINUE,BREAK, //statement
                LPAR,IDENTIFIER,INT_LITERAL,MINUS,PLUS, //exp
                CHAR_LITERAL,STRING_LITERAL, ASTERISK,AND,LOGAND,SIZEOF); //exp
    }

    private void parseParams(){
        //no params
        if(!acceptType()) {
            return;
        }
        //todo remove this
        parseType();//consume type
        expect(IDENTIFIER);
        //todo remove this

        //parseVarDeclaration();

        parse0orMoreParams();
    }

    private void parse0orMoreParams(){
        if (!accept(COMMA))
            return;
        nextToken();//consume comma
        parseParams();
    }

    // includes are ignored, so does not need to return an AST node
    private void parseIncludes() {
        if (accept(INCLUDE)) {
            nextToken();
            expect(STRING_LITERAL);
            parseIncludes();
        }
    }

    private void parseStructDecl(){
        expect(STRUCT);
        expect(IDENTIFIER);
        expect(LBRA);
        parseVarDeclaration();
        parse0orMoreVarDeclaration();
        expect(RBRA);
        expect(SC);
    }

    private void parseVarDeclaration(){
        parseType();
        expect(IDENTIFIER);
        parse0orMoreArray();
        expect(SC);
    }
    private void parse0orMoreVarDeclaration(){
        if (!acceptType())
            return;
        parseVarDeclaration();
        parse0orMoreVarDeclaration();
    }

    private void parse0orMoreArray(){
        if (!accept(LSBR))
            return;
        nextToken();
        expect(INT_LITERAL);
        expect(RSBR);
        parse0orMoreArray();
    }

    private void parseType(){
        //if the type is struct, it must be followed by an identifier
        if (accept(STRUCT)){
            nextToken();
            expect(IDENTIFIER);
        }
        else {
            expect(INT, CHAR, VOID);
        }
        parse0orMorePointers();
    }
    private boolean acceptType(){
        return accept(STRUCT,INT,CHAR,VOID);
    }

    private void parse0orMorePointers(){
        if (!accept(ASTERISK))
            return;
        nextToken();
        parse0orMorePointers();
    }
}
