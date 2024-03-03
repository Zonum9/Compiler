import gen.asm.AssemblyParser;
import gen.asm.AssemblyProgram;
import org.junit.jupiter.api.Test;
import regalloc.NaiveRegAlloc;

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
    void mysteryTest(){
        assertCorrectOutput("""
                int  x[2][2];
                void main(){
                    x[0][0]=0;
                    x[0][1]=1;
                    x[1][0]=2;
                    x[1][1]=3;
                    
                    print_i(*(int*)x[0]);
                }
                """,
                "?");
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
    }


    void assertCorrectOutput(String program,String expectedOutput){
        AssemblyProgram p = Utils.programStringToASMObj(program);
        p = NaiveRegAlloc.INSTANCE.apply(p);
        try {
            File f= File.createTempFile("temp",".asm");
            p.print(new PrintWriter(f));
            p.print(new PrintWriter("src/test/asmFiles/out.asm"));
            Process process = new ProcessBuilder((
                    "java -jar parts/part3/Mars4_5.jar sm nc me "+f.toPath()
            ).split(" ")
            ).start();
            String out = new String(process.getInputStream().readAllBytes());
            assertEquals(expectedOutput,out);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
