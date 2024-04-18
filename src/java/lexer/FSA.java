package lexer;

import java.util.HashMap;
import java.util.Set;

import lexer.Token.Category;

public class FSA {

    private static class Key{
        State key1;
        Character key2;        

        public Key(State key1,Character key2){
            assert key1!= null && key2!=null;
            this.key1=key1;
            this.key2=key2;
        }
        @Override
        public boolean equals(Object obj) {
            if (this==obj)
                return true;
            if (obj==null||getClass()!=obj.getClass())
                return false;
    
            Key temp = (Key) obj;    
            // Compare each field for equality
            if (!key1.equals(temp.key1))
                return false;
            return key2.equals(temp.key2);
        }
        @Override
        public int hashCode() {
            return 31 * key1.hashCode() + key2.hashCode();
        }
        @Override
        public String toString() {            
            return "("+key1.toString()+", "+key2+")";
        }
    }

    private static final HashMap<Key,State> transitionMap= new HashMap<>();

    private static void populateMapKeywords (String s){
        State state=State.INTIAL_STATE;        
        for (int i =1; i <= s.length();i++){
            char currentChar=s.charAt(i-1);
            Key key = new Key(state, Character.toLowerCase(currentChar));
            state = State.valueOf(s.substring(0,i));
            transitionMap.put(key,state);

            for (char c = 0; c < 128; c++) {
                key =new Key(state, c);
                if(!canBeInIdentifier(c,state))
                    transitionMap.put(key, State.IDENTIFIER_F);
                else if (!transitionMap.containsKey(key))
                    transitionMap.put(key, State.IDENTIFIER);
            }
        }
        for (char c = 0; c < 128; c++) {
            if(canBeInIdentifier(c,state))
                transitionMap.put(new Key(state, c), State.IDENTIFIER);
            else
                transitionMap.put(new Key(state,c),State.valueOf(state +"_F"));
        }
    }

    private static boolean canBeInIdentifier(char c,State currentState){
        if (!Character.isAlphabetic(c) && !Character.isDigit(c) && c != '_')
            return false;
        return !Character.isDigit(c) || currentState != State.INTIAL_STATE;
    }

    private static void populateMapOthers(){
        for (char c = 0; c < 128; c++) {
            Key initialStateKey = new Key(State.INTIAL_STATE,c);            
            if(!transitionMap.containsKey(initialStateKey) && canBeInIdentifier(c,State.INTIAL_STATE))
                transitionMap.put(initialStateKey, State.IDENTIFIER);
            else
                transitionMap.put(initialStateKey,State.INVALID);
            Key identifierStateKey = new Key(State.IDENTIFIER,c);
            if(!transitionMap.containsKey(identifierStateKey)){
                if(!canBeInIdentifier(c,State.IDENTIFIER))
                    transitionMap.put(identifierStateKey, State.IDENTIFIER_F);   
                else
                    transitionMap.put(identifierStateKey, State.IDENTIFIER); 
            }        
        }
    }

    static {
        populateMapOthers();
        populateMapKeywords("INT");        
        populateMapKeywords("VOID");
        populateMapKeywords("CHAR");
        populateMapKeywords("IF");
        populateMapKeywords("ELSE");
        populateMapKeywords("WHILE");
        populateMapKeywords("RETURN");
        populateMapKeywords("STRUCT");
        populateMapKeywords("SIZEOF");
        populateMapKeywords("CONTINUE");
        populateMapKeywords("BREAK");
        populateMapKeywords("CLASS");
        populateMapKeywords("NEW");
        populateMapKeywords("EXTENDS");
    }


    private static final StringBuilder currentData= new StringBuilder();

    public static String getCurrentData(){
        String s = currentData.toString();
        currentData.setLength(0);
        return s;
    }

    public static Token.Category getTokenCategory(char c,Scanner scanner) {
        State currentState= transitionMap.get(new Key(State.INTIAL_STATE,c));
        currentData.setLength(0);
        currentData.append(c);
        while(scanner.hasNext() && currentState != State.INVALID){
            c = scanner.peek();
            currentState= transitionMap.get(new Key(currentState,c));
            if(finalStates.contains(currentState) || currentState == State.INVALID){
                break;
            }
            scanner.next();
            currentData.append(c);
        }
        //If the loop ended, it means eof was reached. Make sure we have proper final states
        if (!scanner.hasNext() && !finalStates.contains(currentState) && currentState!= State.INVALID){
            currentState=transitionMap.get(new Key(currentState,' '));
        }

        if (currentState == State.INVALID || !finalStates.contains(currentState))
            return Token.Category.INVALID;
        if(currentState != State.IDENTIFIER_F)
            currentData.setLength(0);
        return sateToTokenCat(currentState);
    }

    //state being provided must be a final state for this to work
    private static Category sateToTokenCat(State state) {
        String strCurrentState=state.toString();
        String cat= strCurrentState.substring(0,strCurrentState.length()-2);
        return Category.valueOf(cat);
    }
    
    private final static Set<State> finalStates= Set.of(
        State.INT_F,
        State.VOID_F,
        State.CHAR_F,
        State.ELSE_F,
        State.WHILE_F,
        State.RETURN_F,
        State.STRUCT_F,
        State.SIZEOF_F,
        State.CONTINUE_F,
        State.BREAK_F,
        State.IDENTIFIER_F,
        State.IF_F,
        State.CLASS_F,
        State.NEW_F,
        State.EXTENDS_F
    );

    private enum State{
        INTIAL_STATE,
        
        IDENTIFIER,
        IDENTIFIER_F,

        I,
        IF,
        IF_F,

        IN,
        INT,
        INT_F,

        V,
        VO,
        VOI,
        VOID, 
        VOID_F,

        C,
        CH,
        CHA,
        CHAR, 
        CHAR_F,

        // keywords
        E,
        EL,
        ELS,
        ELSE,   
        ELSE_F,

        W,
        WH,
        WHI,
        WHIL,
        WHILE,  
        WHILE_F,

        R,
        RE,
        RET,
        RETU,
        RETUR,
        RETURN, 
        RETURN_F,

        S,
        ST,
        STR,
        STRU,
        STRUC,
        STRUCT, 
        STRUCT_F,

        SI,
        SIZ,
        SIZE,
        SIZEO,
        SIZEOF,
        SIZEOF_F,

        CO,
        CON,
        CONT,
        CONTI,
        CONTIN,
        CONTINU,
        CONTINUE, 
        CONTINUE_F,

        B,
        BR,
        BRE,
        BREA,
        BREAK,
        BREAK_F,

        CL,
        CLA,
        CLAS,
        CLASS,
        CLASS_F,

        N,
        NE,
        NEW,
        NEW_F,

        EX,
        EXT,
        EXTE,
        EXTEN,
        EXTEND,
        EXTENDS,
        EXTENDS_F,

        INVALID
    }
}
