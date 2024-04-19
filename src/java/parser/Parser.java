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
        while (accept(STRUCT, INT, CHAR, VOID,CLASS)) {
            if (token.category == STRUCT &&
                    lookAhead(1).category == IDENTIFIER &&
                    lookAhead(2).category == LBRA) {
                decls.add(parseStructDecl());
            }
            else if (accept(CLASS) && lookAhead(2).category == EXTENDS
                    || lookAhead(2).category==LBRA){
                decls.add(parseClassDecl());
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

        expect(EOF);
        return new Program(decls);
    }

    private ClassDecl parseClassDecl() {
        ClassType type;
        if(parseType() instanceof ClassType ct) {
            type=ct;
        }
        else {
            type = null;
        }
        Optional<ClassType> extension= Optional.empty();
        if(accept(EXTENDS)){
            nextToken();
            extension= Optional.of(new ClassType(expect(IDENTIFIER).data));
        }
        expect(LBRA);
        List<Decl> decls = new ArrayList<>();
        while (accept(INT, CHAR, VOID,CLASS)) {
            Type t=parseType();
            //current token should be an identifier
            //a left parenthesis must mean a function
            if(lookAhead(1).category == LPAR) {
                decls.add(parseFunc(t,false));
            }else{
                decls.add(parseVarDeclaration(t));
            }
        }
        if (decls.stream().anyMatch(x -> !(x instanceof VarDecl || x instanceof FunDecl))){
            error();
        }
        expect(RBRA);
        return new ClassDecl(type,extension,decls);
    }

    private Decl parseFunc(Type funType){
        return parseFunc(funType,true);
    }
    private Decl parseFunc(Type funType, boolean includeProtos){
        String name=expect(IDENTIFIER).data;
        expect(LPAR);
        List<VarDecl> params = new ArrayList<>();
        parseParams(params);
        expect(RPAR);
        if( !includeProtos || accept(LBRA)) {
            Block block=parseBlock();
            return new FunDecl(funType,name,params,block);
        }
        else {
            expect(SC);
            return new FunProto(funType,name,params);
        }
    }

    private Block parseBlock(){
        expect(LBRA);
        List<VarDecl>varDecls = new ArrayList<>();
        parse0orMoreVarDeclaration(varDecls);
        List<Stmt> stmts = new ArrayList<>();
        parse0orMoreStatements(stmts);
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

    private Expr parseExpression(){ //pred 9
        Expr lhs = parsePred8();
        if (token.category == ASSIGN){
            nextToken();
            return new Assign(lhs,parseExpression());
        }
        return lhs;
    }
    private Expr parsePred8(){
        Expr lhs = parsePred7();
        while (accept(LOGOR)){
            nextToken();
            lhs= new BinOp(lhs,Op.OR,parsePred7());
        }
        return lhs;
    }

    private Expr parsePred7(){
        Expr lhs = parsePred6();
        while (accept(LOGAND)){
            nextToken();
            lhs= new BinOp(lhs,Op.AND,parsePred6());
        }
        return lhs;
    }
    private Expr parsePred6(){
        Expr lhs = parsePred5();
        while (accept(NE,EQ)){
            lhs= switch (token.category){
                case NE -> {
                    nextToken();
                    yield new BinOp(lhs,Op.NE,parsePred5());
                }
                case EQ ->{
                    nextToken();
                    yield new BinOp(lhs,Op.EQ,parsePred5());
                }
                default -> throw new IllegalStateException("Unexpected value: " + token.category);
            };
        }
        return lhs;
    }
    private Expr parsePred5(){
        Expr lhs = parsePred4();
        while (accept(GT,GE,LT,LE)){
            lhs= switch (token.category){
                case GT -> {
                    nextToken();
                    yield new BinOp(lhs,Op.GT,parsePred4());
                }
                case GE -> {
                    nextToken();
                    yield new BinOp(lhs,Op.GE,parsePred4());
                }
                case LT -> {
                    nextToken();
                    yield new BinOp(lhs,Op.LT,parsePred4());
                }
                case LE->{
                    nextToken();
                    yield new BinOp(lhs,Op.LE,parsePred4());
                }
                default -> throw new IllegalStateException("Unexpected value: " + token.category);
            };
        }
        return lhs;

    }
    private Expr parsePred4(){
        Expr lhs = parsePred3();
        while (accept(PLUS,MINUS)){
            lhs= switch (token.category){
                case PLUS -> {
                    nextToken();
                    yield new BinOp(lhs,Op.ADD,parsePred3());
                }
                case MINUS -> {
                    nextToken();
                    yield new BinOp(lhs,Op.SUB,parsePred3());
                }
                default -> throw new IllegalStateException("Unexpected value: " + token.category);
            };
        }
        return lhs;
    }
    private Expr parsePred3(){
        Expr lhs = parsePred2();
        while (accept(DIV,ASTERISK,REM)){ // "/" | "*" | "%"
            lhs= switch (token.category){
                case DIV -> {
                    nextToken();
                    yield new BinOp(lhs,Op.DIV,parsePred2());
                }
                case ASTERISK -> {
                    nextToken();
                    yield new BinOp(lhs,Op.MUL,parsePred2());
                }
                case REM -> {
                    nextToken();
                    yield new BinOp(lhs,Op.MOD,parsePred2());
                }
                default -> throw new IllegalStateException("Unexpected value: " + token.category);
            };
        }
        return lhs;
    }
    private Expr parsePred2(){
        Expr expr;
        if (accept(PLUS,MINUS,LPAR,AND,ASTERISK,NEW)){ //"+" | "-" | typecast | addressof | valueat | new
            expr= switch (token.category){
                case PLUS -> {
                    nextToken();
                    yield new BinOp(new IntLiteral(0), Op.ADD, parsePred2());
                }
                case MINUS -> {
                    nextToken();
                    yield  new BinOp(new IntLiteral(0), Op.SUB, parsePred2());
                }
                case LPAR -> {
                    switch (lookAhead(1).category){
                        case STRUCT,INT,CHAR,VOID,CLASS: break;
                        default: yield parsePred1();
                    }
                    nextToken();
                    Type type= parseType();
                    expect(RPAR);
                    yield new TypecastExpr (type,parsePred2());
                }
                case AND->{
                    nextToken();
                    yield new AddressOfExpr(parsePred2());
                }
                case ASTERISK -> {
                    nextToken();
                    yield new ValueAtExpr(parsePred2());
                }
                case NEW -> {
                    nextToken();
                    Type type = parseType();
                    if (!(type instanceof ClassType ct)){
                        error(CLASS);
                        yield null;
                    }
                    expect(LPAR);
                    expect(RPAR);
                    yield new NewInstance(ct);
                }
                default -> throw new IllegalStateException("Unexpected value: " + token.category);
            };
        }
        else {
            expr= parsePred1();
        }
        return expr;
    }
    private Expr parsePred1(){
        Expr lhs = parsePredFinal();
        while (accept(LSBR,DOT)){ //arrayaccess | fieldaccess
            lhs= switch (token.category){
                case LSBR -> {
                    nextToken();
                    Expr outer= parseExpression();
                    expect(RSBR);
                    yield new ArrayAccessExpr(lhs,outer);
                }
                case DOT -> {
                    nextToken();
                    String fieldName= expect(IDENTIFIER).data;
                    if (token.category == LPAR){
                         FunCallExpr funCall=parseFunctionCall(fieldName);
                         yield new InstanceFunCallExpr(lhs,funCall);
                    }

                    yield new FieldAccessExpr(lhs,fieldName);
                }
                default -> throw new IllegalStateException("Unexpected value: " + token.category);
            };
        }
        return lhs;
    }

    private Expr parsePredFinal() {
        switch (token.category){
            case LPAR -> {
                nextToken();
                Expr expr = parseExpression();
                expect(RPAR);
                return expr;
            }
            case IDENTIFIER -> {
                Token t=expect(IDENTIFIER);
                if (token.category == LPAR){
                    return parseFunctionCall(t.data);
                }
                return new VarExpr(t.data);
            }
            case INT_LITERAL -> { return new IntLiteral(Integer.parseInt(expect(INT_LITERAL).data));}
            case STRING_LITERAL -> { return new StrLiteral(expect(STRING_LITERAL).data);}
            case CHAR_LITERAL -> { return new ChrLiteral(expect(CHAR_LITERAL).data.charAt(0));}
            case SIZEOF -> {
                nextToken();
                expect(LPAR);
                Type type= parseType();
                expect(RPAR);
                return new SizeOfExpr(type);
            }
            default -> {
                error();
                return null;
            }
        }
    }









    private FunCallExpr parseFunctionCall(String name){
//        String name= expect(IDENTIFIER).data;
        expect(LPAR);
        ArrayList<Expr> exprs = new ArrayList<>();
        parseArgs(exprs);
        expect(RPAR);
        return  new FunCallExpr(name,exprs);
    }

    private void parseArgs(List<Expr> exprs){
        if (token.category == RPAR || token.category == EOF)
            return;
        exprs.add(parseExpression());
        if(token.category == COMMA) {
            nextToken();
            parseArgs(exprs);
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
        return  new Return(Optional.ofNullable(expr));
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
        return accept(LBRA,WHILE,IF,RETURN,CONTINUE,BREAK) || //statement
                acceptExpression();
    }
    private boolean acceptExpression(){
        return accept( LPAR,IDENTIFIER,INT_LITERAL,MINUS,PLUS, //exp
                CHAR_LITERAL,STRING_LITERAL, ASTERISK,AND,SIZEOF,NEW); //exp
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
            error(STRUCT,INT,CHAR,VOID,CLASS);
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
        expect(STRUCT);
        Token id = expect(IDENTIFIER);
        StructType structType = new StructType(id.data);
        expect(LBRA);
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
        return new ArrayType(parse0orMoreArray(type),i);
    }

    private Type parseType(){
        //if the type is struct, it must be followed by an identifier
        Type type;
        if (accept(STRUCT)){
            nextToken();
            type = new StructType(expect(IDENTIFIER).data);
        }
        else if (accept(CLASS)){
            nextToken();
            type = new ClassType(expect(IDENTIFIER).data);
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
        return accept(STRUCT,INT,CHAR,VOID,CLASS);
    }

    private Type parse0orMorePointers(Type type){
        if (!accept(ASTERISK))
            return type;
        nextToken();
        return parse0orMorePointers(new PointerType(type));
    }
}
