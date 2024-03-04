import gen.asm.AssemblyProgram;
import lexer.Tokeniser;
import org.junit.jupiter.api.Test;

import java.io.*;

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

    @Test
    void fibonacci(){
        assertCorrectOutputFile("textFiles/fibonacci.c",
                "First 8 terms of Fibonacci series are : 0 1 1 2 3 5 8 13",
                "8");
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
    void escapeCharacters(){
        String[] escapedChar = { "\\b", "\\n", "\\r", "\\t", "\\\\", "\\'", "\\\"", "\\0","\u0007"};
        for(String c : escapedChar){
            String program ="void main(){print_s((char*)\""+c+"\");}";
            System.out.println(program);
            assertCorrectOutput(
                    program
                    , Tokeniser.replaceEscapedCharacters(c));
        }

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
            int exitCode= process.waitFor();
            String out = new String(process.getInputStream().readAllBytes());
            assertEquals(expectedExitCode,exitCode);
            assertEquals(expectedOutput,out);


        } catch (Exception e) {
            throw new RuntimeException(e);
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


//    void assertCorrectASMFromString(String program,String expected){
//        BufferedReader reader= new BufferedReader(new StringReader(expected));
//        AssemblyProgram expectedASM= AssemblyParser.readAssemblyProgram(reader);
//        expectedASM= NaiveRegAlloc.INSTANCE.apply(expectedASM);
//        AssemblyProgram obtained = Utils.programStringToASMObj(program);
//        //use the provided equals comparison between programs
//        if( expectedASM.equals(obtained)){
//            assertTrue(true);
//            return;
//        }
//        assertEquals(Utils.asmOBJtoString(expectedASM),Utils.asmOBJtoString(obtained));
//
//    }
}
