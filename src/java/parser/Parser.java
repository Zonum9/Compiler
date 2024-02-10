package parser;


import ast.*;
import lexer.Token;
import lexer.Token.Category;
import lexer.Tokeniser;
import util.CompilerPass;

import java.util.*;

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

    public Program parse() {
        // get the first token
        nextToken();

        return parseProgram();
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
    private Token expect(Category... expected) {
        for (Category e : expected) {
            if (e == token.category) {
                Token ret = token;
                nextToken();
                return ret;
            }
        }
        error(expected);
        return token;
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


    private Program parseProgram() {
        parseIncludes();

        List<Decl> decls = new ArrayList<>();
        while (accept(Category.STRUCT, Category.INT, Category.CHAR, Category.VOID)) {
            if (token.category == Category.STRUCT &&
                    lookAhead(1).category == Category.IDENTIFIER &&
                    lookAhead(2).category == Category.LBRA) {
                decls.add(parseStructDecl());
            }
            //if this fails, then the program is automatically not valid, since
            //we must have a variable declaration, or a function or a struct declaration
            else {
                Type t=parseType();
                //current token should be an identifier
                //a left parenthesis must mean a function
                if(lookAhead(1).category == LPAR) {
                    decls.add(parseFunc(t));
                }else{
                    decls.add(parseVarDeclaration(t));
                }
            }
        }

        expect(Category.EOF);
        return new Program(decls);
    }

    private Decl parseFunc(Type funType){
        String name=expect(IDENTIFIER).data;
        expect(LPAR);
        List<VarDecl> params = new ArrayList<>();
        parseParams(params);
        expect(RPAR);
        if(accept(LBRA)) {
            Block block=parseBlock();
            return new FunDecl(funType,name,params,block);
        }
        else {
            expect(SC);
            return new FunProto(funType,name,params);
        }

    }

    private Block parseBlock(){//todo
        expect(LBRA);
        List<VarDecl>varDecls = new ArrayList<>();
        parse0orMoreVarDeclaration(varDecls);
        List<Stmt> stmts = new ArrayList<>();
        parse0orMoreStatements(stmts);//todo
        expect(RBRA);
        return new Block(varDecls,stmts);
    }
    private While parseWhile(){
        expect(WHILE);
        expect(LPAR);
        Expr expr= parseExpression();
        expect(RPAR);
        Stmt stmt= parseStatement();
        return new While(expr,stmt);
    }
    private Expr parseExpression(){//todo
        switch (token.category){
            case INT_LITERAL,CHAR_LITERAL,STRING_LITERAL -> nextToken();
            case PLUS,MINUS,ASTERISK,AND -> {nextToken(); parseExpression();}
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
                    return null;//todo
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
            default -> error();//do not accept empty expressions
        }
        parsePostExpression();
        return null; //todo
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

    private If parseIf(){
        expect(IF);
        expect(LPAR);
        Expr expr= parseExpression();
        expect(RPAR);
        Stmt stmt= parseStatement();
        if(!accept(ELSE))
            return new If(expr,stmt, Optional.empty());
        nextToken();//consume else
        Stmt els=parseStatement();
        return new If(expr,stmt,Optional.of(els));
    }
    private Return parseReturn(){
        expect(RETURN);
        if(accept(SC)) {//return statement without expression
            nextToken();
            return new Return(Optional.empty());
        }
        Expr expr= parseExpression();
        expect(SC);
        return  new Return(Optional.of(expr));
    }

    private Stmt parseStatement(){
        Stmt stmt;
        switch (token.category){
            case LBRA -> stmt= parseBlock();
            case WHILE -> stmt = parseWhile();
            case IF -> stmt = parseIf();
            case RETURN -> stmt= parseReturn();
            case CONTINUE -> {expect(CONTINUE); expect(SC); stmt = new Continue();}
            case BREAK -> {expect(BREAK); expect(SC); stmt = new Break();}
            default -> {
                Expr expr= parseExpression();
                stmt = new ExprStmt(expr);
                expect(SC);
            }
        }
        return stmt;
    }

    private void parse0orMoreStatements(List<Stmt> stmts){
        while(acceptStatement()) {
            stmts.add(parseStatement());
        }
    }

    private boolean acceptStatement(){
        return accept(LBRA,WHILE,IF,RETURN,CONTINUE,BREAK, //statement
                LPAR,IDENTIFIER,INT_LITERAL,MINUS,PLUS, //exp
                CHAR_LITERAL,STRING_LITERAL, ASTERISK,AND,SIZEOF); //exp
    }

    private void parseParams(List<VarDecl> params){
        //no params
        if(!acceptType()) {
            return;
        }
        Type type=parseType();//consume type
        String name= expect(IDENTIFIER).data;
        type = parse0orMoreArray(type);
        params.add(new VarDecl(type,name));
        parse0orMoreParams(params);
    }

    private void parse0orMoreParams(List<VarDecl> params){
        if (!accept(COMMA))
            return;
        nextToken();//consume comma
        if (!acceptType()){
            error(STRUCT,INT,CHAR,VOID);
        }
        parseParams(params);
    }

    // includes are ignored, so does not need to return an AST node
    private void parseIncludes() {
        if (accept(INCLUDE)) {
            nextToken();
            expect(STRING_LITERAL);
            parseIncludes();
        }
    }


    private StructTypeDecl parseStructDecl(){
        expect(Category.STRUCT);
        Token id = expect(Category.IDENTIFIER);
        StructType structType = new StructType(id.data);
        expect(Category.LBRA);
        Type delcType= parseType();
        VarDecl varDecl= parseVarDeclaration(delcType);
        ArrayList<VarDecl> varDecls = new ArrayList<>();
        varDecls.add(varDecl);
        parse0orMoreVarDeclaration(varDecls);
        expect(RBRA);
        expect(SC);
        return new StructTypeDecl(structType,varDecls);
    }

    //type must be parsed before calling this
    private VarDecl parseVarDeclaration(Type type){
        String name = expect(IDENTIFIER).data;
        type = parse0orMoreArray(type);
        expect(SC);
        return new VarDecl(type,name);

    }
    private void parse0orMoreVarDeclaration(List<VarDecl> varDecls){
        if (!acceptType())
            return;
        Type declType= parseType();
        VarDecl varDecl= parseVarDeclaration(declType);
        varDecls.add(varDecl);
        parse0orMoreVarDeclaration(varDecls);
    }

    private Type parse0orMoreArray(Type type){
        if (!accept(LSBR))
            return type;
        nextToken();
        Token token = expect(INT_LITERAL);
        int i= Integer.MIN_VALUE; //not sure what else to put
        if (token.category == INT_LITERAL)
            i = Integer.parseInt(token.data);
        expect(RSBR);
        return parse0orMoreArray(new ArrayType(type,i));
    }

    private Type parseType(){
        //if the type is struct, it must be followed by an identifier
        Type type;
        if (accept(STRUCT)){
            nextToken();
            type = new StructType(expect(IDENTIFIER).data);
        }
        else {
            Token token = expect(INT, CHAR, VOID);
            switch (token.category) {
                case INT -> type = BaseType.INT;
                case CHAR -> type = BaseType.CHAR;
                case VOID -> type = BaseType.VOID;
                default -> type = BaseType.UNKNOWN;
            }
        }
        return parse0orMorePointers(type);
    }
    private boolean acceptType(){
        return accept(STRUCT,INT,CHAR,VOID);
    }

    private Type parse0orMorePointers(Type type){
        if (!accept(ASTERISK))
            return type;
        nextToken();
        return parse0orMorePointers(new PointerType(type));
    }
}
