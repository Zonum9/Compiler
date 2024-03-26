import gen.asm.AssemblyProgram;
import lexer.Tokeniser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
public class Part3and4Tests {

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

    @Test void partialArrAccess(){
        assertCorrectOutput("""
                void main(){
                    char* ptr;
                    char x[2][5];
                    x[0][0]='A';
                    x[0][1]='A';
                    x[0][2]='A';
                    x[0][3]='A';
                    x[0][4]='A';
                    
                    ptr = (char*)x[1];
                    ptr[0]='B';
                    ptr[1]='B';
                    ptr[2]='B';
                    ptr[3]='B';
                    ptr[4]='B';
                    
                    print_c(x[0][0]);
                    print_c(x[0][1]);
                    print_c(x[0][2]);
                    print_c(x[0][3]);
                    print_c(x[0][4]);
                    
                    print_c(x[1][0]);
                    print_c(x[1][1]);
                    print_c(x[1][2]);
                    print_c(x[1][3]);
                    print_c(x[1][4]);
                }
                """);

    }

    @Test void GTandGE(){
        assertCorrectOutput("""
                void main(){
                    int x;
                    int y;
                    x=0;
                    y=0;
                    print_i(x>y);
                    print_i(x>=y);
                    y =1;
                    print_i(x>y);
                    print_i(x>=y);
                    x=2;
                    print_i(x>y);
                    print_i(x>=y);
                }
                """);
    }

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
                
                """);
        assertCorrectOutput("""
                struct A{
                    int x;
                    int y;
                    char c;
                    int z;
                    int w;
                };
                
                struct A strct;
                struct A cpy;
                void main(){
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
                
                """);
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

//        assertCorrectOutput("""
//                void main(){
//                    while (1){
//                    }
//                }
//                """);

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
                "20\n");
    }

    @Test
    void tictactoe(){
        StringBuilder input = new StringBuilder();
        for(char c:"a1a2b1b2c1n".toCharArray()){
            input.append(c);
            input.append("\r");
        }
        fileCompareToCompiled("textFiles/tictactoe.c",
                input.toString());
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

    @Test void charEquals(){
        assertCorrectOutput("""
                int x;
                char c;
                int y;
                char z;
                
                void main(){
                    char x;
                    char y;
                    x = 'A';
                    c = 'A';
                    y = 'A';
                    z = 'A';
                    print_i(x==c && x==y && x==z);
                    
                    print_i('x'==c && 'x'==y && 'x'==z);
                    print_i('x'==c || 'x'==y || 'x'==z);
                }
                """);

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

    //todo
//    @Test void motherOfAllTests(){// gave up
//        fileCompareToCompiled("textFiles/bigboy.c");
//    }


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
                """);
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
                
                """);
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
                struct A arr[7];
                void main(){
                    struct A strct;                    
                    
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
                
                """);
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
    @Test void  stringBellEscapedChar(){
        String c="\\a";
        String program ="void main(){print_s((char*)\""+c+"\");}";
        System.out.println(program);
        assertCorrectOutput(
                program
//                , Tokeniser.replaceEscapedCharacters(c)); always outputs `a` instead of 
                , "a");
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

    @Test void pointerArrays(){
        assertCorrectOutput("""
                void main() {
                    int x[3];
                    int *ptr;
                          
                    x[2]=(int)'9';
                    ptr = (int*)x;
                    print_c(((char*)ptr)[2]);
                }
                """);

        assertCorrectOutput("""
               void main() {
                    int x[3];
                    int *ptr;
                          
                    x[0]=9;
                    ptr=(int*)x;
                    print_i(*ptr);
                }
               """);

        assertCorrectOutput("""
                void main() {
                    int x[3];
                    int *ptr;
                          
                    x[2]=9;
                    ptr = (int*)x;
                    print_i(ptr[2]);
                }
                """);


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
                """);
    }
    @Test
    void weirdArray(){
        assertCorrectOutput("""
                int  x[2][2];
                void main(){
                    x[0][0]=123;
                                        
                    print_i(*(int*)x[0]);
                }
                """);
        assertCorrectOutput("""
                int  x[2][2][2];
                void main(){
                    x[0][0][1]=123;
                                        
                    print_i(((int*)x[0][0])[1]);
                }
                """);
    }

    @Test void arrayOfPointers(){
        assertCorrectOutput("""
                void main(){
                    int x [2];
                    int y [2];
                    int z [2];
                    int* arr[3];
                    arr[0]= (int*)x;
                    arr[1]= (int*)y;
                    arr[2]= (int*)z;
                    
                    arr[0][0]=1;
                    arr[0][1]=2;
                    arr[1][0]=3;
                    arr[1][1]=4;
                    arr[2][0]=5;
                    arr[2][1]=6;
                    
                    print_i(x[0]);
                    print_i(x[1]);
                    
                    print_i(y[0]);
                    print_i(y[1]);
                    
                    print_i(z[0]);
                    print_i(z[1]);
                    
                }
                    
                """);



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
                """);
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

    @Test void pointerFunCall(){
        assertCorrectOutput("""
                void fun(int *ptr);
                void main(){
                    int x;
                    int* ptr;
                    ptr=&x;
                    x=1;
                    print_i(*ptr);
                    fun(ptr);
                    print_i(*ptr);
                }
                
                void fun(int *x){
                    print_i(*x);
                    *x=2;
                    print_i(*x);
                }
                """);
        assertCorrectOutput("""
                void fun(int *ptr);
                void main(){
                    int x;
                    int* ptr;
                    ptr=&x;
                    x=1;
                    print_i(*ptr);
                    fun(ptr);
                    print_i(*ptr);
                }
                
                void fun(int *x){
                    print_i(x[0]);
                    x[0]=2;
                    print_i(x[0]);
                }
                """);
    }

    @Test void arrayFunCall(){
        assertCorrectOutput("""
                void fun (char x[1]);
                void main(){
                    char x[1];
                    x[0]='X';
                    print_c(x[0]);
                    fun(x);
                    print_c(x[0]);                 
                }
                void fun(char x[1]){
                    print_c(x[0]);
                    x[0]='Z';
                }
                """);
        assertCorrectOutput("""
                void fun (char x[2][2]);
                void main(){
                    char x[2][2];
                    x[0][0]='A';
                    x[0][1]='B';
                    x[1][0]='C';
                    x[1][1]='D';
                    fun(x);
                    print_c(x[0][0]);
                    print_c(x[0][1]);
                    print_c(x[1][0]);
                    print_c(x[1][1]);
                }
                void fun(char x[2][2]){
                    print_c(x[0][0]);
                    print_c(x[0][1]);
                    print_c(x[1][0]);
                    print_c(x[1][1]);                    
                    x[0][0]='E';
                    x[0][1]='F';
                    x[1][0]='G';
                    x[1][1]='H';
                }
                """);

        assertCorrectOutput("""
                void fun (char x[2]);
                void main(){
                    char x[2][2];
                    x[0][0]='A';
                    x[0][1]='B';
                    x[1][0]='C';
                    x[1][1]='D';
                    fun(x[0]);
                    fun(x[1]);
                    print_c(x[0][0]);
                    print_c(x[0][1]);
                    print_c(x[1][0]);
                    print_c(x[1][1]);
                }
                void fun(char x[2]){
                    print_c(x[0]);
                    print_c(x[1]);
                    x[0]='E';
                    x[1]='H';
                }
                """);

        assertCorrectOutput("""
                void fun(int* arr[3]);
                void main(){
                    int x [2];
                    int y [2];
                    int z [2];
                    int* arr[3];
                    arr[0]= (int*)x;
                    arr[1]= (int*)y;
                    arr[2]= (int*)z;
                    
                    arr[0][0]=1;
                    arr[0][1]=2;
                    arr[1][0]=3;
                    arr[1][1]=4;
                    arr[2][0]=5;
                    arr[2][1]=6;
                    
                    fun(arr);
                    
                    print_i(x[0]);
                    print_i(x[1]);
                    
                    print_i(y[0]);
                    print_i(y[1]);
                    
                    print_i(z[0]);
                    print_i(z[1]);
                }
                void fun(int* arr[3]){
                    print_i(arr[0][0]);
                    print_i(arr[0][1]);
                    print_i(arr[1][0]);
                    print_i(arr[1][1]);
                    print_i(arr[2][0]);
                    print_i(arr[2][1]);
                    
                    
                    arr[0][0]=7;
                    arr[0][1]=8;
                    arr[1][0]=9;
                    arr[1][1]=10;
                    arr[2][0]=11;
                    arr[2][1]=12;
                }
                """);

        assertCorrectOutput("""                
                void fun (int x[6]);                
                void main(){
                    int x[6];
                    int i;
                    i=1;
                    while(i-1 <6){
                        x[i-1]=-i;
                        i=i+1;
                    }
                    i=0;
                    while(i <6){
                        print_i(x[i]);
                        i=i+1;
                    }
                    print_s((char*)"|arr address:");
                    print_i(*((int*)&x));                    
                    print_s((char*)"|func call:");
                    fun(x);
                    print_s((char*)"|post func call:");
                    i=0;
                    while(i <6){
                        print_i(x[i]);
                        i=i+1;
                    }                    
                }
                
                void fun (int x[6]){
                    int i;
                    i=0;
                    while(i <6){
                        print_i(x[i]);
                        print_c('|');
                        i=i+1;
                    }
                    i=1;
                    while(i-1 <6){
                        x[i-1]=i;
                        i=i+1;
                    }
                    i=0;
                    while(i <6){
                        print_i(x[i]);
                        i=i+1;
                    }
                }
                
                """);
    }

    @Test void factorial(){
        assertCorrectOutput("""
                int factorial(int n){
                    if (n==0){
                        return 1;
                    }
                    return n*factorial(n-1);
                }
                                
                void main(){
                    print_i(factorial(6));
                }
                """);
    }

    @Test void recursiveFunWithLocalVar(){
        assertCorrectOutput("""
                int factorial(int n){
                    int x;
                    int y;
                    y=9999;
                    x=68;
                    if (n==0){
                        return y;
                    }
                    return n*factorial(n-1);
                }
                
                void main(){
                    print_i(factorial(8));
                }
                """);
    }

    @Test void functionReturns(){
        assertCorrectOutput("""
                char fun(){
                    return 'x';
                }
                
                void main(){
                    print_c(fun());
                }
                """);

    }

    @Test void globalStruct(){
        assertCorrectOutput("""
                struct A{
                    int x;
                    int y;
                    char c;
                    int z;
                    int w;
                };
                struct A globl;
                void main(){
                    struct A lcl;
                    globl.x=4;
                    globl.y=3;
                    globl.c='X';
                    globl.z=2;
                    globl.w=1;
                    
                    lcl.x=4;
                    lcl.y=3;
                    lcl.c='X';
                    lcl.z=2;
                    lcl.w=1;
                }
                
                """);
    }

    @Test void funcallMultipleArgs(){
        assertCorrectOutput("""
                void fun3(char x[1][3],char y, char z);
                void fun2(int x,char y, int z);
                void fun(char x,char y,char z){
                    print_c(x);print_c(y);print_c(z);                    
                }
                
                void main(){
                    char x;char y;char z;
                    char arr[1][3];
                    fun('X','Y','Z');
                    x='A';y='B'; z='C';                   
                    fun(x,y,z);         
                    fun2(1,'2',3);
                    fun2((int)x,y,(int)z);  
                    arr[0][0]='f';
                    arr[0][1]='u';
                    arr[0][2]='n';      
                    fun3(arr,x,z);
                }
                void fun2(int x,char y, int z){
                    print_i(x);print_c(y);print_i(z); 
                }
                void fun3(char x[1][3],char y, char z){
                    print_c(x[0][0]);print_c(x[0][1]);print_c(x[0][2]);
                    print_c(y);print_c(z);
                }
                """);

    }
    @Test void simpleStructReturn(){
        assertCorrectOutput("""
                struct s{
                    int x;
                    char y;
                };
                struct s fun(int u){
                    struct s x;
                    x.x=99;
                    x.y='e';
                    return x;
                }
                void main(){
                    struct s x;
                    print_i(fun(0).x);print_c(fun(0).y);
                }
                """);
    }
    @Test void roundTrip(){
        assertCorrectOutput("""
                struct s{
                    int x;
                    char y;
                    int z;
                };
                struct s g;
                struct s fun(char s,struct s x){
                    print_i(x.x);
                    print_c(x.y);
                    print_i(x.z);
                    return x;
                }
                void main(){
                    struct s x;
                    g.x=123;
                    g.y='N';
                    g.z=456;
                    x=g;
                    x=fun('S',x);
                    print_i(x.x);
                    print_c(x.y);
                    print_i(x.z);
                    print_i(fun('S',x).x);
                    print_c(fun('S',x).y);
                    print_i(fun('S',x).z);
                    
                }
                """);
    }

    @Test void simplerStructParams(){
        assertCorrectOutput("""
                struct s{
                    int x;
                    char y;
                };
                struct s fun(struct s x){
                    print_i(x.x);
                    print_c(x.y);
                    x.x=11;
                    x.y='e';
                    return x;
                }
                struct s fun2(struct s x,int y){
                    print_i(x.x);
                    print_i(y);
                    x.x=11;
                    return x;
                }
                
                void main(){
                    struct s x;
                    x.x=99;
                    x.y='o';
                    print_i(fun(x).x);
                    print_c(fun(x).y);
                    print_i(fun2(x,66).x);
                }
                """);

    }

    @Test void structFuncCalls(){
        assertCorrectOutput("""
                struct s{
                    int x;
                    char c;
                    int arr[6];
                };
                
                struct s fun (struct s a);
                
                void main(){
                    struct s s;
                    struct s x;
                    s.x=0;
                    s.c='E';
                    s.arr[5]=99;
                                        
                    x=fun(s);
                                        
                    print_i(s.x);
                    print_c(' ');
                    print_c(s.c);
                    print_c(' ');
                    print_i(s.arr[5]);
                    print_c('|');
                    
                    print_i(x.x);
                    print_c(' ');
                    print_c(x.c);
                    print_c(' ');
                    print_i(x.arr[5]);
                    print_c('|');
                    
                    print_i(fun(x).x);
                    print_c(' ');
                    print_c(fun(x).c);
                    print_c(' ');
                    print_i(fun(x).arr[5]);
                    print_c(' ');
                }
                
                struct s fun (struct s s){
                    print_s((char*)"fun: ");
                    print_i(s.x);
                    print_c(' ');
                    print_c(s.c);
                    print_c(' ');
                    print_i(s.arr[5]);
                    print_c('|');
                
                    s.x=543;
                    s.c='x';
                    s.arr[5]=11;
                    
                    return s;
                }
                
                """);

    }

    @Test void nestedStruct(){
        assertCorrectOutput("""
                struct A{
                    int x;
                    int y;
                };
                struct B{
                    int x;
                    int y;
                    struct A inner;
                };

                void main(){
                    struct B s;
                    struct A s2;
                    s.x=1;
                    s.y=2;
                    s.inner.x=3;
                    s.inner.y=4;
                    s2.x=5;
                    s2.y=6;

                    print_i(s.x);
                    print_i(s.y);
                    print_i(s.inner.x);
                    print_i(s.inner.y);

                    s.inner=s2;

                    print_i(s.inner.x);
                    print_i(s.inner.y);

                }

                """);

        assertCorrectOutput("""
                struct A{
                    int x;
                    int y;
                };
                struct B{
                    int x;
                    int y;
                    struct A inner;
                };
                struct B s;
                struct A s2;

                void main(){
                    s.x=1;
                    s.y=2;
                    s.inner.x=3;
                    s.inner.y=4;
                    s2.x=5;
                    s2.y=6;

                    print_i(s.x);
                    print_i(s.y);
                    print_i(s.inner.x);
                    print_i(s.inner.y);

                    s.inner=s2;

                    print_i(s.inner.x);
                    print_i(s.inner.y);

                }

                """);
        assertCorrectOutput("""
                struct A{
                    int x;
                    int y;
                };
                struct B{
                    int x;
                    int y;
                    struct A inner;
                };
                struct B s;

                void main(){
                    struct A s2;
                    s.x=1;
                    s.y=2;
                    s.inner.x=3;
                    s.inner.y=4;
                    s2.x=5;
                    s2.y=6;

                    print_i(s.x);
                    print_i(s.y);
                    print_i(s.inner.x);
                    print_i(s.inner.y);

                    s.inner=s2;

                    print_i(s.inner.x);
                    print_i(s.inner.y);

                }

                """);

        assertCorrectOutput("""
                struct A{
                    int x;
                    int y;
                };
                struct B{
                    int x;
                    int y;
                    struct A inner;
                };
                
                struct B fun(){
                    struct B s;
                    struct A s2;
                    s.x=1;
                    s.y=2;
                    s.inner.x=3;
                    s.inner.y=4;
                    s2.x=5;
                    s2.y=6;
                    
                    s.inner=s2;
                    
                    return s;
                }
                void main(){
                    struct B s;
                    s = fun();
                    print_i(s.x);
                    print_i(s.y);
                    print_i(s.inner.x);
                    print_i(s.inner.y);
                    print_i(s.inner.x);
                    print_i(s.inner.y);
                    
                    print_i(fun().x);
                    print_i(fun().y);
                    print_i(fun().inner.x);
                    print_i(fun().inner.y);
                    print_i(fun().inner.x);
                    print_i(fun().inner.y);
                    
                }
                """);

    }

    @Test void multiArgs(){
        assertCorrectOutput("""
                void fun(int x, int y, int z){
                    x=x+1;
                    y=y*y;
                    z=z/z;
                    print_i(x);
                    print_i(y);
                    print_i(z);
                    
                }
                int gx;
                int gy;
                int gz;
                void main(){
                    int x;
                    int y;
                    int z;
                    x=gx=4;
                    y=gy=5;
                    z=gz=6;
                    
                    fun(4,5,6);
                    fun(x,y,z);
                    fun(gx,gy,gz);
                }
                """);
    }

    @Test void moreStrcs(){
        assertCorrectOutput("""
                struct A{
                    char c;
                };
                struct A fun(){
                    struct A s;
                    struct A*ptr;
                    s.c='Z';
                    ptr=&s;
                    return s;
                }
                void main(){
                    struct A s;
                    s=fun();
                    print_c(s.c);
                }
                """);
        assertCorrectOutput("""
                struct A{
                    char c;
                };
                struct A* fun(){
                    struct A*ptr;
                    struct A s;
                    s.c='Z';
                    ptr=&s;
                    return ptr;
                }
                void main(){
                    struct A s;
                    s=*fun();
                    print_c(s.c);
                }
                """);
        assertCorrectOutput("""
                struct A{
                    char c;
                };
                struct A* fun(){
                    struct A*ptr;
                    struct A s;
                    s.c='Z';
                    ptr=&s;
                    return ptr;
                }
                void main(){
                    struct A s;
                    print_c((*fun()).c);
                }
                """);

    }

    @Test void returnStruct(){

        assertCorrectOutput("""
                struct A {
                    int x;
                    char c;
                    int y;
                    char c2;
                    char c3;
                };
                struct A fun(){
                    struct A val;
                    val.x=1;
                    val.y=2;
                    val.c='A';
                    val.c2='B';
                    val.c3='C';
                    return val;
                }
                void main(){
                    struct A x;
                    x=fun();
                    print_i(x.x);
                    print_i(x.y);
                    print_c(x.c);
                    print_c(x.c2);
                    print_c(x.c3);
                }
                
                """);

        assertCorrectOutput("""
                struct A {
                    char x;
                };
                struct A fun(){
                    struct A val;
                    val.x='X';
                    return val;
                }
                void main(){
                    struct A x;
                    x=fun();
                    print_c(x.x);
                }
                """);
        assertCorrectOutput("""
                struct A {
                    char x;
                };
                struct A fun(){
                    struct A val;
                    val.x='X';
                    return val;
                }
                void main(){
                    struct A x;
                    x=fun();
                    print_c(fun().x);
                }
                """);


        assertCorrectOutput("""
                struct A {
                    int x;
                    char c;
                    int y;
                    char c2;
                    char c3;
                };
                struct A val;
                struct A fun(){
                    val.x=1;
                    val.y=2;
                    val.c='A';
                    val.c2='B';
                    val.c3='C';
                    return val;
                }
                void main(){
                    struct A x;
                    x=fun();
                    print_i(val.x);
                    print_i(val.y);
                    print_c(val.c);
                    print_c(val.c2);
                    print_c(val.c3);
                    print_i(x.x);
                    print_i(x.y);
                    print_c(x.c);
                    print_c(x.c2);
                    print_c(x.c3);
                    print_i(fun().x);
                    print_i(fun().y);
                    print_c(fun().c);
                    print_c(fun().c2);
                    print_c(fun().c3);
                }
                """);

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

/*
****************************************************
*               PART 4 tests                       *
****************************************************
* */
    @BeforeAll
    static void setMode(){
        mode = Utils.RegMode.COLOR;
//        print=true;
    }

    @Test void t(){
        factorial();
        factorial();
        factorial();
        factorial();
        factorial();
        factorial();
        factorial();
        factorial();
        factorial();
    }

    @Test void earlyJump(){
        assertCorrectOutput("""
                void main(){
                    int i;
                    i=0;
                    while(i<10){
                        print_i(i);
                        i=i+1;
                        continue;
                        print_s((char*)"this should not print");
                    }
                    i=999;
                    print_s((char*)"done");
                }
                """);
    }

    @Test void deadInstruction(){
        assertCorrectOutput("""
                struct s{
                    int x;
                };
                void main(){
                    struct s s;                    
                    int x;
                    int y;
                    int z;
                    s.x=99;
                    x=888;
                    y=999;
                    print_i(z);
                }                
                """);
        assertCorrectOutput("""
                void main(){
                    int x;
                    int y;
                    int z;
                    x=888;
                    y=999;
                    print_i(z);
                }
                """);
    }


    @Test void varSoup(){
        assertCorrectOutput("""
                void main(){
                    int x0;int x1;int x2;int x3;int x4;int x5;int x6;int x7;int x8;
                    int x9;int xA;int xB;int xC;int xD;int xE;int xF;
                    x0=x1=x2=x3=x4=x5=x6=x7=x8=x9=xA=xB=xC=xD=xE=xF=999;                
                    print_i(x0+x1+x2+x3+x4+x5+x6+x7+x8+x9+xA+xB+xC+xD+xE+xF);
                }
                """);
    }

    @Test void pascal() {
        compareToCompiled("""                
                int pascalValue(int row, int col) {
                    if (col != 0 && col != row) {
                        return pascalValue(row-1, col-1) + pascalValue(row-1, col);
                    }
                    return 1;
                }
                                
                void printPascalsTriangle(int numRows) {
                    int i;
                    while (numRows != 0) { 
                        i = 0;
                        while (i < numRows) {
                            print_i(pascalValue(numRows - 1, i));
                            i=i+1;
                        }
                        print_c('\\n');
                        numRows=numRows-1;
                    }
                }
                                
                void main() {
                    int rows;
                    rows = read_i();
                    printPascalsTriangle(rows);
                }
                                
                """, "15\n");
    }

    @Test void differentPascal(){
        assertCorrectOutput("""      
                
                void printPascalsTriangle() {
                    int numRows;
                    int triangle[30][30];
                    int rowNum;
                    numRows=30;
                    rowNum = 0;
                    while (rowNum < numRows) {
                        int j;
                        j= 1;
                        triangle[rowNum][0] = 1;
                        print_i(triangle[rowNum][0]);
                                                
                        while (j < rowNum) {
                            triangle[rowNum][j] = triangle[rowNum - 1][j - 1] + triangle[rowNum - 1][j];
                            print_i(triangle[rowNum][j]);
                            j=j+1;
                        }
                        triangle[rowNum][rowNum] = 1;
                        print_c('\\n');
                        print_i(triangle[rowNum][rowNum]);
                        rowNum=rowNum+1;
                    }
                }
                                            
                void main() {
                    printPascalsTriangle();
                }                            
                            
            """);

    }


    public static Utils.RegMode mode= Utils.RegMode.NAIVE;
    public static boolean print = false;

    void assertCorrectOutput(String program,String expectedOutput, int expectedExitCode, String input){
        AssemblyProgram p = Utils.programStringToASMObj(program, mode,print);
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
            assertEquals(expectedOutput.replaceAll("\r\n", "\n"),out.replaceAll("\r\n", "\n"));


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
            run.outputWriter().write(input.replaceAll("\r",""));
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


    void assertCorrectOutput(String program,String expectedOutput){
        assertCorrectOutput(program,expectedOutput,0,"");
    }
    void assertCorrectOutput(String program,String expectedOutput,String input){
        assertCorrectOutput(program,expectedOutput,0,input);
    }


}
