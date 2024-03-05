import gen.asm.AssemblyProgram;
import lexer.Tokeniser;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
public class Part3Tests {

    @Test
    void printing1(){
        assertCorrectOutput(
                """
                void main(){
                    print_i(9);
                }
                ""","9");
    }

    @Test
    void binOps(){
        assertCorrectOutput(
                """
                void main(){
                    print_i(9+10);
                }
                ""","19");
        assertCorrectOutput(
                """
                void main(){
                    print_i(9*0+10*7+1);
                }
                ""","71");
        assertCorrectOutput(
                """
                void main(){
                    print_i(88/3);
                }
                ""","29");
        assertCorrectOutput(
                """
                void main(){
                    print_i(88%3);
                }
                ""","1");
    }

    @Test
    void simpleOr(){
        assertCorrectOutput(
                """
                void main(){
                    print_i(0 || 1);
                }
                ""","1");
        assertCorrectOutput(
                """
                void main(){
                    print_i(0 || 0);
                }
                ""","0");
        assertCorrectOutput(
                """
                void main(){
                    print_i(1 || 0);
                }
                ""","1");
        assertCorrectOutput(
                """
                void main(){
                    print_i(1 || 1);
                }
                ""","1");
    }

    @Test
    void simpleAnd(){
        assertCorrectOutput(
                """
                void main(){
                    print_i(0 && 1);
                }
                ""","0");
        assertCorrectOutput(
                """
                void main(){
                    print_i(0 && 0);
                }
                ""","0");
        assertCorrectOutput(
                """
                void main(){
                    print_i(1 && 0);
                }
                ""","0");
        assertCorrectOutput(
                """
                void main(){
                    print_i(1 && 1);
                }
                ""","1");
    }

    @Test
    void complexBoolOp(){
        assertCorrectOutput(
                """
                void main(){
                    print_i(99 < 1 || 99-99 == 0);
                }
                ""","1");

        assertCorrectOutput(
                """
                void main(){
                    print_i(99 < 1 || (99-99 == 0 && 0 == -0));
                }
                ""","1");
    }

    @Test
    void basicAssignment(){
        assertCorrectOutput("""
                void main(){
                    int x;
                    x=5;
                    print_i(x);
                }
                """,
                "5");
        assertCorrectOutput("""
                void main(){
                    char x;
                    x='5';
                    print_c(x);
                }
                """,
                "5");
    }

    @Test
    void advancedAssignment(){
        assertCorrectOutput("""
                int x;
                void main(){
                    int y;
                    x=5;
                    y=x;
                    print_i(x);
                    print_i(y);
                }
                """,
                "55");
        assertCorrectOutput("""
                char x;
                void main(){
                    char y;
                    x='5';
                    y=x;
                    print_c(x);
                    print_c(y);
                }
                """,
                "55");
        assertCorrectOutput("""
                char x;
                int z;
                void main(){
                    char y;
                    int w;
                    x='5';
                    y=x;
                    w=11;
                    z=w+1;
                    print_c(x);
                    print_c(y);
                    print_i(w);
                    print_i(z);
                }
                """,
                "551112");
    }

    @Test
    void structAssignment(){
        assertCorrectOutput("""
                struct A{
                    int x;
                    int y;
                    char c;
                    struct A* ptr;
                    int z;
                    int w;
                    void * n;
                };
                
                void main(){
                    struct A strct;
                    
                    strct.x=1;
                    strct.y=2;
                    strct.c='3';
                    strct.z=4;
                    strct.w=5;
                    
                    print_i(strct.x);
                    print_i(strct.y);
                    print_c(strct.c);
                    print_i(strct.z);
                    print_i(strct.w);
                }
                
                """,
                "12345");
    }

    @Test void ifs(){
        assertCorrectOutput("""
                void main(){
                    int x;
                    int y;
                    y=x=1;
                    if(x==y){
                        print_s((char*)"true");
                    }
                    else{
                        print_s((char*)"false");
                    }
                }
                """);
        assertCorrectOutput("""
                void main(){
                    int x;
                    int y;
                    x=1;
                    y=0;
                    if(x==y){
                        print_s((char*)"true");
                    }
                    else{
                        print_s((char*)"false");
                    }
                }
                """);
        assertCorrectOutput("""
                void main(){
                    int x;
                    int y;
                    int z;
                    x=1;
                    z=1;
                    y=0;
                    if(x==y){
                        print_s((char*)"true");
                    }
                    else if(x==z){
                        print_s((char*)"z equals x");
                    }
                    else{
                        print_s((char*)"false");
                    }
                }
                """);
        assertCorrectOutput("""
                void main(){
                    int x;
                    int y;
                    int z;
                    x=1;
                    z=1;
                    y=0;
                    if(x==y){
                        print_s((char*)"true");
                    }
                    else if(z==y){
                        print_s((char*)"z equals x");
                    }
                    else{
                        print_s((char*)"false");
                    }
                }
                """);
        assertCorrectOutput("""
                void main(){
                    int x;
                    int y;
                    int z;
                    x=1;
                    z=1;
                    y=0;
                    if(0){
                        print_s((char*)"true");
                    }
                    else if(0){
                        print_s((char*)"z equals x");
                    }
                    else if(0){
                        print_s((char*)"false");
                    }
                }
                """);
    }

    @Test
    void globalArrayAccess(){
        assertCorrectOutput("""
                int x[4];
                void main(){
                    x[0]=0;
                    x[1]=1;
                    x[2]=2;
                    x[3]=3;
                    
                    print_i(0);
                    print_i(1);
                    print_i(2);
                    print_i(3);
                    
                }
                """,
                "0123");
    }
    //todo test func calls with arrays


    @Test
    void structCopy(){
        assertCorrectOutput("""
                struct A{
                    int x;
                    int y;
                    char c;
                    int z;
                    int w;
                };
                
                void main(){
                    struct A strct;
                    struct A cpy;
                    
                    strct.x=1;
                    strct.y=2;
                    strct.c='3';
                    strct.z=4;
                    strct.w=5;
                    cpy=strct;
                    
                    print_i(strct.x);
                    print_i(strct.y);
                    print_c(strct.c);
                    print_i(strct.z);
                    print_i(strct.w);
                    
                    print_i(cpy.x);
                    print_i(cpy.y);
                    print_c(cpy.c);
                    print_i(cpy.z);
                    print_i(cpy.w);
                    
                    cpy.x=6;
                    cpy.y=7;
                    cpy.c='8';
                    cpy.z=9;
                    cpy.w=10;
                    
                    print_i(strct.x);
                    print_i(strct.y);
                    print_c(strct.c);
                    print_i(strct.z);
                    print_i(strct.w);
                    
                    print_i(cpy.x);
                    print_i(cpy.y);
                    print_c(cpy.c);
                    print_i(cpy.z);
                    print_i(cpy.w);
                    
                }
                
                """,
                "123451234512345678910");
    }

    @Test void aWildContinueAppears(){
        assertCorrectOutput("""
                void main(){
                    int x;
                    x=9;
                    continue;
                    break;
                    print_i(x);
                }
                """
        ,"9");

    }

    @Test void whileLoops(){

        assertCorrectOutput("""
                void main(){
                    while (1){                    
                    }
                }
                """);

        assertCorrectOutput("""
                void main(){
                    int x;
                    int range;
                    x=1;
                    range=100;
                    while (x<range){
                        if( range %x ==0){                            
                            x= x+1;
                            continue;
                        }
                        print_i(x);
                        x= x+1;
                    }
                }
                """);

        assertCorrectOutput("""
                void main(){
                    int x;
                    int range;
                    x=0;
                    range=100;
                    while (x<range){
                        print_i(x);
                        if( x== range/2){
                            print_s((char*)"|half way there|");
                        }
                        x= x+1;
                    }
                }
                """);
        assertCorrectOutput("""
                void main(){
                    int x;
                    int range;
                    x=0;
                    range=100;
                    while (x<range){
                        print_i(x);
                        if( x== range/2){
                            break;
                        }
                        x= x+1;
                    }
                }
                """);
        assertCorrectOutput("""
                void main(){
                    int x;
                    x=0;
                    while (x<10){
                        print_i(x);
                        x= x+1;
                    }
                }
                """);
        assertCorrectOutput("""
                void main(){
                    int i;
                    int j;
                    i=j=0;
                    while (i<20){
                        j=0;
                        while (j<20){
                            if(i<j){
                                print_i(i);
                                print_c(' ');
                            }
                            j = j+1;
                        }
                        i=i+1;
                    }
                }
                """);
    }

    @Test void nestedAdvancedWhile(){

        assertCorrectOutput("""
                void main(){
                    int i;
                    int j;
                    i=j=0;
                    while (i<20){
                        j=0;
                        while (j<20){
                            if(i<j){
                                print_c('|');
                                break;                                
                            }
                            print_i(j);                            
                            j = j+1;
                        }
                        i=i+1;
                        print_c(' ');
                    }
                }
                """);
        assertCorrectOutput("""
                void main(){
                    int i;
                    int j;
                    i=j=0;
                    while (i<20){
                        j=0;
                        while (j<20){
                            if(i<j){
                                print_c('|');
                                j = j+1;
                                continue;
                            }
                            print_i(j);
                            j = j+1;
                        }
                        i=i+1;
                        print_c(' ');
                    }
                }
                """);

    }

    @Test void twoDimArrayOfDifferentSizes(){
        assertCorrectOutput("""
                int x[3][6];
                
                void main(){
                    int i;
                    int j;
                    i=j=0;
                    while (i<3){
                        j=0;
                        while (j<6){
                            x[i][j]=i*6+j;
                            j = j+1;
                        }
                        i=i+1;
                    }
                    i=j=0;
                    while (i<3){
                        j=0;
                        while (j<6){
                            print_i(x[i][j]);
                            print_c(' ');
                            j = j+1;
                        }
                        i=i+1;
                    }
                }
                
                """);
    }


    @Test
    void structShenanigans(){
        assertCorrectOutput("""
                struct A{
                    int x;
                    int y;
                    char c;
                    struct A* ptr;
                    int z;
                    int w;
                    void * n;
                };
                
                void main(){
                    struct A strct;
                    
                    strct.x=1;
                    strct.y=2;
                    strct.c='3';
                    strct.ptr= &strct;
                    strct.z=4;
                    strct.w=5;
                    
                    strct.n= (void*)strct.ptr;
                    
                    print_i(strct.x);
                    print_i(strct.y);
                    print_c(strct.c);
                    print_i((*strct.ptr).z);
                    print_i(strct.z);
                    print_i(strct.w);
                    print_i((*(*(struct A*)strct.n).ptr).x);
                }
                
                """,
                "1234451");
    }
    @Test void equality(){
        assertCorrectOutput("""
                void main(){
                    print_i(1==1);
                    print_i(1==0);
                    print_i(1==-1);
                    print_i(99==99);
                    print_i(99==-99);
                    
                }
                ""","10010");

    }


    @Test
    void fibonacci(){
        fileCompareToCompiled("textFiles/fibonacci.c",
                "8");
    }

    @Test
    void tictactoe(){
        fileCompareToCompiled("textFiles/tictactoe.c",
                "a1a2b1b2c1n");
    }
    @Test
    void linkedLIst(){
        fileCompareToCompiled("textFiles/linkedList.c");
    }

    @Test
    void twoDimArrays(){
        assertCorrectOutput("""
                int  x[2][2];
                void main(){
                    x[0][0]=0;
                    x[0][1]=1;
                    x[1][0]=2;
                    x[1][1]=3;
                    
                    print_i(x[0][0]);
                    print_i(x[0][1]);
                    print_i(x[1][0]);
                    print_i(x[1][1]);
                }
                """,
                "0123");
    }
    @Test
    void threeDimArrays(){
        assertCorrectOutput("""
                int  x[2][2][2];
                void main(){
                    x[0][0][0]=0;
                    x[0][0][1]=1;
                    x[0][1][0]=2;
                    x[0][1][1]=3;
                    x[1][0][0]=4;
                    x[1][0][1]=5;
                    x[1][1][0]=6;
                    x[1][1][1]=7;
                    
                    print_i(x[0][0][0]);
                    print_i(x[0][0][1]);
                    print_i(x[0][1][0]);
                    print_i(x[0][1][1]);
                    print_i(x[1][0][0]);
                    print_i(x[1][0][1]);
                    print_i(x[1][1][0]);
                    print_i(x[1][1][1]);
                }
                """,
                "01234567");
    }

    @Test
    void charTwoDimArrays(){
        assertCorrectOutput("""
                char  x[2][2];
                void main(){
                    x[0][0]='0';
                    x[0][1]='1';
                    x[1][0]='2';
                    x[1][1]='3';
                    
                    print_c(x[0][0]);
                    print_c(x[0][1]);
                    print_c(x[1][0]);
                    print_c(x[1][1]);
                }
                """,
                "0123");
    }
    @Test
    void charThreeDimArrays(){
        assertCorrectOutput("""
                char  x[2][2][2];
                void main(){
                    x[0][0][0]='0';
                    x[0][0][1]='1';
                    x[0][1][0]='2';
                    x[0][1][1]='3';
                    x[1][0][0]='4';
                    x[1][0][1]='5';
                    x[1][1][0]='6';
                    x[1][1][1]='7';
                    
                    print_c(x[0][0][0]);
                    print_c(x[0][0][1]);
                    print_c(x[0][1][0]);
                    print_c(x[0][1][1]);
                    print_c(x[1][0][0]);
                    print_c(x[1][0][1]);
                    print_c(x[1][1][0]);
                    print_c(x[1][1][1]);
                }
                """,
                "01234567");
    }

    @Test
    void weirdArray(){
        assertCorrectOutput("""
                int  x[2][2];
                void main(){
                    x[0][0]=123;
                                        
                    print_i(*(int*)x[0]);
                }
                """,
                "123");
        assertCorrectOutput("""
                int  x[2][2][2];
                void main(){
                    x[0][0][1]=123;
                                        
                    print_i(((int*)x[0][0])[1]);
                }
                """,
                "123");
    }
    @Test
    void read(){
        assertCorrectOutput("""
                void main(){
                    char x;
                    x=read_c();
                    print_c(x);
                }
                ""","x","x");
        assertCorrectOutput("""
                void main(){
                    int x;
                    x=read_i();
                    print_i(x);
                }
                ""","9","9");
        assertCorrectOutput("""
                void main(){
                    print_i(read_i());
                }
                ""","1009","1009");

    }


    @Test void structWithArray(){
        assertCorrectOutput("""
                struct foo{
                    int x[5];
                };
                struct foo x;
                struct foo y;
                void main(){
                    
                    
                    x.x[0]=0;
                    x.x[1]=1;
                    x.x[2]=2;
                    x.x[3]=3;
                    x.x[4]=4;
                                        
                    y=x;
                    
                    print_i(x.x[0]);
                    print_i(x.x[1]);
                    print_i(x.x[2]);
                    print_i(x.x[3]);
                    print_i(x.x[4]);
                    
                    print_i(y.x[0]);
                    print_i(y.x[1]);
                    print_i(y.x[2]);
                    print_i(y.x[3]);
                    print_i(y.x[4]);
                    
                }
                """,
                "0123401234");
        assertCorrectOutput("""
                struct foo{
                    int x[5];
                };
                struct foo x;
                struct foo y;
                void main(){
                    
                    
                    x.x[0]=0;
                    x.x[1]=1;
                    x.x[2]=2;
                    x.x[3]=3;
                    x.x[4]=4;
                                        
                    y=x;
                    
                    print_i(x.x[0]);
                    print_i(x.x[1]);
                    print_i(x.x[2]);
                    print_i(x.x[3]);
                    print_i(x.x[4]);
                    
                    print_i(y.x[0]);
                    print_i(y.x[1]);
                    print_i(y.x[2]);
                    print_i(y.x[3]);
                    print_i(y.x[4]);
                    
                    y.x[0]=5;
                    y.x[1]=6;
                    y.x[2]=7;
                    y.x[3]=8;
                    y.x[4]=9;
                    
                    print_i(x.x[0]);
                    print_i(x.x[1]);
                    print_i(x.x[2]);
                    print_i(x.x[3]);
                    print_i(x.x[4]);
                    
                    print_i(y.x[0]);
                    print_i(y.x[1]);
                    print_i(y.x[2]);
                    print_i(y.x[3]);
                    print_i(y.x[4]);
                }
                """,
                "01234012340123456789");
        assertCorrectOutput("""
                struct foo{
                    int x[5];
                };
                                
                void main(){
                    struct foo x;
                    struct foo y;
                    
                    x.x[0]=0;
                    x.x[1]=1;
                    x.x[2]=2;
                    x.x[3]=3;
                    x.x[4]=4;
                                        
                    y=x;
                    
                    print_i(x.x[0]);
                    print_i(x.x[1]);
                    print_i(x.x[2]);
                    print_i(x.x[3]);
                    print_i(x.x[4]);
                    
                    print_i(y.x[0]);
                    print_i(y.x[1]);
                    print_i(y.x[2]);
                    print_i(y.x[3]);
                    print_i(y.x[4]);
                    
                }
                """,
                "0123401234");
    }

    @Test
    void structArray(){
        assertCorrectOutput("""
                struct A{
                    int x;
                    int y;
                    char c;
                    struct A* ptr;
                    int z;
                    int w;
                    void * n;
                };
                
                void main(){
                    struct A strct;
                    struct A arr[7];
                    
                    strct.x=1;
                    strct.y=2;
                    strct.c='3';
                    strct.ptr= &strct;
                    strct.z=4;
                    strct.w=5;
                    
                    strct.n= (void*)strct.ptr;
                    
                    arr[0]=strct;
                    arr[1]=strct;
                    arr[2]=strct;
                    arr[3]=strct;
                    arr[4]=strct;
                    arr[5]=strct;
                    arr[6]=strct;
                    
                    
                    print_i(arr[0].x);
                    print_i(arr[1].y);
                    print_c(arr[2].c);
                    print_i((*arr[3].ptr).z);
                    print_i(arr[4].z);
                    print_i(arr[5].w);
                    print_i((*(*(struct A*)arr[6].n).ptr).x);
                }
                
                """,
                "1234451");
    }

    @Test
    void sizeof(){
        assertCorrectOutput("""
                void main(){
                    print_i(sizeof(int));
                    print_i(sizeof(char));
                    print_i(sizeof(int*));
                    print_i(sizeof(char*));
                    print_i(sizeof(void));
                    print_i(sizeof(void*));
                    
                }
                """,
                "414404");

        assertCorrectOutput("""
                struct s{
                    int x;
                    char c;
                    void* ptr;
                    struct s* next;
                };
                
                struct b{
                    struct s x;
                    int y;
                };
                
                void main(){
                    print_i(sizeof(struct s));
                    print_c('\\n');
                    print_i(sizeof(struct b));
                    
                }
                """,
                "16\n20");
    }
    @Test
    void stringEscapeCharactersExceptBell(){
        String[] escapedChar = { "\\b", "\\n", "\\r", "\\t", "\\\\", "\\'", "\\\"", ""};
        for(String c : escapedChar){
            String program ="void main(){print_s((char*)\""+c+"\");}";
            System.out.println(program);
            assertCorrectOutput(
                    program
                    , Tokeniser.replaceEscapedCharacters(c));
        }
    }
    @Test void  stringBellEscapedChar(){//can this even pass?
        String c="\\a";
        String program ="void main(){print_s((char*)\""+c+"\");}";
        System.out.println(program);
        assertCorrectOutput(
                program
                , Tokeniser.replaceEscapedCharacters(c));
    }

    @Test void charEscapeChars(){
        String[] escapedChar = { "\\b", "\\n", "\\r", "\\t", "\\\\", "\\'", "\\\"", "\\0","\\a"};
        for(String c : escapedChar){
            String program ="void main(){print_c('"+c+"');}";
            System.out.println(program);
            assertCorrectOutput(
                    program
                    , Tokeniser.replaceEscapedCharacters(c));
        }
    }

    @Test
    void stringLitAccesses(){
        assertCorrectOutput("""
                void main(){
                    char x1;
                    char x2;
                    char x3;
                    char x4;
                    char x5;
                    
                    x1 = "Hello"[0];
                    x2 = "Hello"[1];
                    x3 = "Hello"[2];
                    x4 = "Hello"[3];
                    x5 = "Hello"[4];
                    print_c(x1);
                    print_c(x2);
                    print_c(x3);
                    print_c(x4);
                    print_c(x5);
                }
                ""","Hello");
    }

    @Test
    void printing2(){
        assertCorrectOutput(
                """
                void main(){
                    print_c('c');
                }
                ""","c");
        assertCorrectOutput(
                """
                void main(){
                    print_s((char*)"hello world");
                }
                ""","hello world");
        assertCorrectOutput("""
            void main(){
                print_c('\\n');
        }
        """,
        "\n");

        assertCorrectOutput(
        """
                void main(){
                    print_s((char*)"hello \\n world");
                }
                ""","hello \n world");
    }

    //todo test shadowing


    @Test void shadowing(){
        assertCorrectOutput("""
                void fun (int a, int b, char c);
                int x;
                int y;
                char c;
                void main(){
                    x=1;
                    y=2;
                    c='3';
                    fun(x,y,c);
                    print_i(x);
                    print_i(y);
                    print_c(c);
                }
                
                void fun (int a, int b, char ch){
                    int x;
                    int y;
                    char c;
                    x=y=0;
                    c='0';
                    print_i(a+4);
                    print_i(b+4);
                    print_i((int)ch-(int)'0'+4);
                    
                    print_i(x);
                    print_i(y);
                    print_c(c);
                    
                }
                
                """);

    }

    @Test void pointerArrays(){
       assertCorrectOutput("""
               void main() {
                    int x[3];
                    int *ptr;
                          
                    x[0]=9;
                    ptr=(int*)x;
                    print_i(*ptr);
                }
               """,
               "9");

        assertCorrectOutput("""
                void main() {
                    int x[3];
                    int *ptr;
                          
                    x[2]=9;
                    ptr = (int*)x;
                    print_i(ptr[2]);
                }
                """,
                "9");
        assertCorrectOutput("""
                void main() {
                    int x[2][2];
                    int *ptr;
                    
                    x[0][0]=1;
                    x[0][1]=2;
                    x[1][0]=3;
                    x[1][1]=4;
                    
                    ptr= (int*)x[1];
                    
                    print_i(ptr[0]);
                    print_i(ptr[1]);
                    
                }
                """,
                "34");
    }

    @Test void ptrTypeCats(){
        assertCorrectOutput("""
                void main(){
                    int x;
                    int *ip;
                    char *cp;
                    void *vp;
                    
                    x=99;
                    ip= &x;
                    cp=(char*)ip;
                    vp=(void*)cp;
                    
                    print_i(*ip);
                    print_i(*((int*)cp));
                    print_i(*((int*)vp));
                    
                    ip = (int*)(void*)(char*)(void*)cp;
                    print_i(*ip);
                    
                    *cp='A';
                    print_i(*ip);
                    
                }
                """,
                "99999999"+ (int) 'A');

    }


    void assertCorrectOutput(String program,String expectedOutput, int expectedExitCode, String input){
        AssemblyProgram p = Utils.programStringToASMObj(program);
        try {
            File f= File.createTempFile("temp",".asm");
            p.print(new PrintWriter(f));
            p.print(new PrintWriter("src/test/asmFiles/out.asm"));
            Process process = new ProcessBuilder(
                    ("java -jar parts/part3/Mars4_5.jar sm nc me "+f.toPath())
                    .split(" "))
                    .start();
            process.outputWriter().write(input+"\r");
            process.outputWriter().flush();
            boolean finished=process.waitFor(15,TimeUnit.SECONDS);
            if(!finished){
                process.destroyForcibly();
            }
            String out = new String(process.getInputStream().readAllBytes());
            assertEquals(expectedExitCode,finished?0:-1);
            assertEquals(expectedOutput,out);


        } catch (Exception e) {
            fail(e);
        }
    }
    void assertCorrectOutput(String program){
        compareToCompiled(program,"");
    }

    void fileCompareToCompiled(String fileName){
        fileCompareToCompiled(fileName,"");
    }
    void fileCompareToCompiled(String fileName,String input){
        compareToCompiled(Utils.fileToString(fileName),input);
    }
    void compareToCompiled(String program,String input){
        program = program.replaceAll("#include.*[\\n|\\r]","");
        try {
            Path path= Paths.get("src/test/temp/temp.c");
            if (Files.exists(path)){
                Files.delete(path);
            }
            File f =new File(String.valueOf(Files.createFile(path)));


            try (PrintWriter printWriter = new PrintWriter(f)) {
                printWriter.print("""
                        #include <stdio.h>
                        #include <stdlib.h>                                                
                        void print_s(const char* s) {
                          fprintf(stdout,"%s",s);
                        }                                                
                        void print_i(int i) {
                          fprintf(stdout,"%d",i);
                        }                                                
                        void print_c(char c) {
                          fprintf(stdout,"%c",c);
                        }                                                
                        char read_c() {
                          char c;
                          fscanf(stdin, "%c", &c);
                          return c;
                        }                                                
                        int read_i() {
                          int i;
                          fscanf(stdin, "%d", &i);
                          return i;
                        }                                                
                        void* mcmalloc(int size) {
                          return malloc(size);
                        }
                                            
                        """);
                printWriter.print(program);
            }


            Process compile = new ProcessBuilder(
                    "gcc",f.getName(),"-o","out.exe"
            )
                    .directory(new File("src/test/temp/"))
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .start();

            compile.waitFor();

            Process run = new ProcessBuilder(
                    "src/test/temp/out.exe")
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .start();
            run.outputWriter().write(input+"\n");
            run.outputWriter().flush();
            boolean finished=run.waitFor(8,TimeUnit.SECONDS);
            if(!finished){
                run.destroyForcibly();
            }

            String out = new String(run.getInputStream().readAllBytes());
            System.out.println(out);
            assertCorrectOutput(program,out,finished? 0:-1,input);
        } catch (Exception e) {
            fail(e);
        }
    }

    void assertCorrectOutputFile(String filename,String expectedOutput){
        assertCorrectOutput(Utils.fileToString(filename),expectedOutput);
    }
    void assertCorrectOutputFile(String filename,String expectedOutput,String input){
        assertCorrectOutput(Utils.fileToString(filename),expectedOutput,input);
    }

    void assertCorrectOutput(String program,String expectedOutput, int expectedExitCode){
        assertCorrectOutput(program,expectedOutput,expectedExitCode,"");
    }
    void assertCorrectOutput(String program,String expectedOutput){
        assertCorrectOutput(program,expectedOutput,0,"");
    }
    void assertCorrectOutput(String program,String expectedOutput,String input){
        assertCorrectOutput(program,expectedOutput,0,input);
    }


}
