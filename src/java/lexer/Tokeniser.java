package lexer;

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



    /*
     * To be completed
     */
    public Token nextToken() {

        int line = scanner.getLine();
        int column = scanner.getColumn();

        if (!scanner.hasNext())
            return new Token(Token.Category.EOF, scanner.getLine(), scanner.getColumn());

        // get the next character
        char c = scanner.next();

        // skip white spaces between lexems
        if (Character.isWhitespace(c))
            return nextToken();            

        // recognises the plus operator
        switch (c) {            
            case '+':
                return new Token(Token.Category.PLUS, line, column);
            case '-':
                return new Token(Token.Category.MINUS, line, column);
            case '*':
                return new Token(Token.Category.ASTERIX, line, column);
            case '%':
                return new Token(Token.Category.REM, line, column);
            case '/':{
                Token.Category cat = Token.Category.DIV;          
                if (scanner.peek() == '*'){
                    handleBlockComment();
                    return nextToken();
                }
                else if (scanner.peek() == '/'){
                    handleLineComment();
                    return nextToken();
                }            
                return  new Token(cat, line, column); 
            } 
            case '{':
                return new Token(Token.Category.LBRA, line, column);

            case '}':
                return new Token(Token.Category.RBRA, line, column);

            case '(':
                return new Token(Token.Category.LPAR, line, column);

            case ')':
                return new Token(Token.Category.RPAR, line, column);

            case '[':
                return new Token(Token.Category.LSBR, line, column);

            case ']':
                return new Token(Token.Category.RSBR, line, column);

            case ';':
                return new Token(Token.Category.SC, line, column);

            case ',':
                return new Token(Token.Category.COMMA, line, column); 

            case '&':{
                Token.Category cat = Token.Category.AND;          
                if (scanner.peek() == '&'){
                    cat=Token.Category.LOGAND;
                    scanner.next();
                }
                return  new Token(cat, line, column); 
            } 
            //todo does our language not support or opperations?
            case '|':{       
                if (scanner.peek() == '|'){                    
                    scanner.next();
                    return  new Token(Token.Category.LOGOR, line, column); 
                } 
                break;               
            }
            //todo does our language not support negation for booleans?
            case '!':{       
                if (scanner.peek() == '='){                    
                    scanner.next();
                    return  new Token(Token.Category.NE, line, column); 
                } 
                break;               
            }
            case '=':{
                Token.Category cat = Token.Category.ASSIGN;          
                if (scanner.peek() == '='){
                    cat=Token.Category.EQ;
                    scanner.next();
                }
                return  new Token(cat, line, column); 
            } 
            case '<':{
                Token.Category cat = Token.Category.LT;          
                if (scanner.peek() == '='){
                    cat=Token.Category.LE;
                    scanner.next();
                }
                return  new Token(cat, line, column); 
            } 
            case '>':{
                Token.Category cat = Token.Category.GT;          
                if (scanner.peek() == '='){
                    cat=Token.Category.GE;
                    scanner.next();
                }
                return  new Token(cat, line, column); 
            } 
            case '.':
                return new Token(Token.Category.DOT, line, column);

            //single quote
            case '\'':
                return handleSingleQuote(line,column);
            //double quote
            case '\"':
                return handleDoubleQuote(line,column);
                
        }

        //todo INT_LITERAL, all types, all keywords, IDENTIFIER, INCLUDE, STRING_LITERAL


        // if we reach this point, it means we did not recognise a valid token
        error(c, line, column);
        return new Token(Token.Category.INVALID, line, column);
    }

    private Token handleDoubleQuote(int line, int column) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleDoubleQuote'");
    }

    //checks if the current peek value is a valid character to be escaped
    private boolean checkIfEscapedChar(){
        switch (scanner.peek()) {
            case 'a':
                return true;                        
            case 'b':
                return true;
            case 'n':
                return true;
            case 'r':
                return true;
            case 't':
                return true;
            case '\\':
                return true;
            case '\'':
                return true;
            case '\"':
                return true;
            case '0':
                return true;
        }
        return false;
    }
    private Token handleSingleQuote(int line, int column) {
        char c= scanner.next();
        String data= String.valueOf(c);
        if (c == '\''){ //nothing between two single quotes, invalid
            return new Token(Token.Category.INVALID, line,column);
        } 
        if (c=='\\'){
            if (!checkIfEscapedChar()){
                return new Token(Token.Category.INVALID, line,column);
            }
            data+=scanner.next();
        }
        if ( scanner.peek() !='\''){ //if the character is not  properly enclosed by two single quotes
            return new Token(Token.Category.INVALID, line,column);
        }
        scanner.next(); //consume right side single quote
        return new Token(Token.Category.CHAR_LITERAL, data, line, column);        
    }

    private void handleLineComment() {        
        while (scanner.hasNext() && scanner.next()!= '\n' );
    }

    private void handleBlockComment() {
        while (scanner.hasNext()){
            if(scanner.next() == '*' && scanner.hasNext() && scanner.next()== '/')
                break;        
        }
    }

}
