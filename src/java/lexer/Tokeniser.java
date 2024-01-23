package lexer;

import java.util.Map;
import java.util.Optional;

import util.CompilerPass;


/**
 * @author cdubach
 */

public class Tokeniser extends CompilerPass {

    private Scanner scanner;


    public Tokeniser(Scanner scanner) {
        this.scanner = scanner;
    }

    private void error(char c, int line, int col) {
        String msg = "Lexing error: unrecognised character ("+c+") at "+line+":"+col;
        System.out.println(msg);
        incError();
    }

    Map<Character,Token.Category> simpleEntries= Map.ofEntries(
        Map.entry('{',Token.Category.LBRA),
        Map.entry('}',Token.Category.RBRA),
        Map.entry('(',Token.Category.LPAR),
        Map.entry(')',Token.Category.RPAR),
        Map.entry('[',Token.Category.LSBR),
        Map.entry(']',Token.Category.RSBR),
        Map.entry(';',Token.Category.SC),
        Map.entry(',',Token.Category.COMMA),
        Map.entry('+',Token.Category.PLUS),
        Map.entry('-',Token.Category.MINUS),        
        Map.entry('%',Token.Category.REM),
        Map.entry('.',Token.Category.DOT),
        Map.entry('*',Token.Category.ASTERIX)
    );

    public Token nextToken() {

        int line = scanner.getLine();
        int column = scanner.getColumn();

        if (!scanner.hasNext())
            return new Token(Token.Category.EOF, scanner.getLine(), scanner.getColumn());

        // get the next character
        char c = scanner.next();

        // skip white spaces between lexemes
        if (Character.isWhitespace(c))
            return nextToken();            

        //check the trivial cases
        if (simpleEntries.containsKey(c)){
            return new Token(simpleEntries.get(c), line, column);
        }
        
        boolean isInvalid=false;

        switch (c) {            
            case '/':{//division or comment
                if (scanner.hasNext()){    
                    if (scanner.peek() == '*'){
                        scanner.next();//consume the asterisk
                        if(!handleBlockComment()){
                            isInvalid=true;
                            break;
                        }
                        return nextToken();
                    }
                    else if (scanner.peek() == '/'){
                        handleLineComment();
                        return nextToken();
                    }      
                }      
                return  new Token(Token.Category.DIV, line, column); 
            } 
            //'#' implies include statement
            case '#':{
                if(hasProperInclude())
                    return new Token(Token.Category.INCLUDE, line, column);
                isInvalid=true;
                break;
            }
            case '&':{//bitwise and or logical and
                Token.Category cat = chooseBetweenCategory(Token.Category.AND,Token.Category.LOGAND,'&');          
                return  new Token(cat, line, column); 
            } 
            case '|':{
                Token.Category cat = chooseBetweenCategory(Token.Category.INVALID,Token.Category.LOGOR,'|');
                if (cat == Token.Category.INVALID){
                    isInvalid=true;
                    break;      
                }
                return  new Token(cat, line, column);          
            }
            case '!':{       
                Token.Category cat = chooseBetweenCategory(Token.Category.INVALID,Token.Category.NE,'=');
                if (cat == Token.Category.INVALID){
                    isInvalid=true;
                    break;
                }
                return new Token(cat, line, column);             
            }
            case '=':{
                Token.Category cat = chooseBetweenCategory(Token.Category.ASSIGN,Token.Category.EQ,'=');          
                return  new Token(cat, line, column); 
            } 
            case '<':{
                Token.Category cat = chooseBetweenCategory(Token.Category.LT,Token.Category.LE,'=');          
                return  new Token(cat, line, column); 
            } 
            case '>':{
                Token.Category cat = chooseBetweenCategory(Token.Category.GT,Token.Category.GE,'=');          
                return  new Token(cat, line, column); 
            } 
            //single quote
            case '\'':{//todo test me
                Optional<String> data=handleSingleQuote();
                if(data.isEmpty()){
                    line = scanner.getLine();
                    column= scanner.getColumn();
                    isInvalid=true;
                    break;
                }
                return new Token(Token.Category.CHAR_LITERAL,data.get(), line, column);
            }        
            //double quote
            case '\"':{//todo test me
                Optional<String> data=handleDoubleQuote();
                if(data.isEmpty()){
                    line = scanner.getLine();
                    column= scanner.getColumn();
                    isInvalid=true;
                    break;
                }
                return new Token(Token.Category.STRING_LITERAL,data.get(), line, column);}                
        }
        if (!isInvalid){
            if (Character.isDigit(c)){
                String data = handleDigit(c);
                return  new Token(Token.Category.INT_LITERAL,data, line, column);
            }            

            Token.Category fsaToken = FSA.getTokenCategory(c,scanner);
            if (fsaToken != Token.Category.INVALID){
                return new Token(fsaToken,FSA.getCurrentData(),line,column);
            }     
        }
        // if we reach this point, it means we did not recognise a valid token
        error(c, line, column);
        return new Token(Token.Category.INVALID, line, column);
    }

    private boolean hasProperInclude() {
        for (char c: "include".toCharArray()){
            if (!scanner.hasNext() || (c != scanner.next()))
                return false;
        }        
        return true;
    }

    private String handleDigit(char firstChar) {
        StringBuilder data= new StringBuilder();
        data.append(firstChar);
        while(scanner.hasNext() && Character.isDigit(scanner.peek())){
            char c= scanner.next();
            data.append(c);
        }
        return data.toString();
    }

    private Token.Category chooseBetweenCategory(Token.Category defaultCat,Token.Category alternative,char altActivator){
        if (scanner.hasNext() && scanner.peek()==altActivator){            
            scanner.next();
            return alternative;
        }
        return defaultCat;
    }

    private Optional<String> handleDoubleQuote() {        
        StringBuilder data= new StringBuilder();
        while(scanner.hasNext() && scanner.peek() !='\"' && scanner.peek() !='\n'){
            char c= scanner.next();
            if (c=='\\'){
                if (!checkIfEscapedChar()){
                    return Optional.empty();
                }
                data.append(c);
                c= scanner.next();
            }
            data.append(c);
        }
        //if you reached eof/new line, it means you didn't close your string correctly, invalid
        if(!scanner.hasNext() || scanner.peek() =='\n')
            return Optional.empty();
        scanner.next(); //consume right side double quote        
        return Optional.of(replaceEscapedCharacters(data.toString()));  
    }

    private String replaceEscapedCharacters(String s){
        String[][] replacements = {
            {"\\a",String.valueOf('\u0007')},
            {"\\b","\b"},
            {"\\n","\n"},
            {"\\r","\r"}, 
            {"\\t","\t"}, 
            {"\\\\","\\"},
            {"\\'", "'"},
            {"\\\"","\""},
            {"\\0","\0"}
        };
        for (String[] pairToReplace : replacements) {
            s=s.replace(pairToReplace[0],pairToReplace[1]);
        }
        return s;
    }


    private Optional<String> handleSingleQuote() {
        //if you reached eof/new line, it means you didn't close your char correctly, invalid
        if(!scanner.hasNext() || scanner.peek() =='\n')
            return Optional.empty();
        char c= scanner.next();
        String data= String.valueOf(c);
        if (c == '\''){ //nothing between two single quotes, invalid
            return Optional.empty();
        } 
        if (c == '\n'){ //newline, invalid
            return Optional.empty();
        } 
        if (c=='\\'){ //check if trying to escape a valid character
            if (!checkIfEscapedChar()){
                return Optional.empty();
            }
            //if check passes, then it means that the scanner has a valid next character
            data+=scanner.next();
        }
        if ( !scanner.hasNext() || scanner.peek() !='\''){ //if the character is not  properly enclosed by two single quotes
            return Optional.empty();
        }
        scanner.next(); //consume right side single quote
        return Optional.of(replaceEscapedCharacters(data));
    }

    //checks if the current peek value is a valid character to be escaped
    private boolean checkIfEscapedChar(){
        if(!scanner.hasNext())
            return false;
        return switch (scanner.peek()) {
            case 'a', 'b', 'n', 'r', 't', '\\', '\'', '\"', '0' -> true;
            default -> false;
        };
    }

    private void handleLineComment() {        
        while (scanner.hasNext() && scanner.next()!= '\n' );
    }

    private boolean handleBlockComment() {        
        while (scanner.hasNext()){
            if(scanner.next() == '*' && scanner.hasNext() && scanner.next()== '/')
                return true;        
        }
        return false;
    }
}
