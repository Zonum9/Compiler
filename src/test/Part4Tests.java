import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class Part4Tests extends Part3Tests{
    @BeforeAll
    static void setMode(){
        mode = Utils.RegMode.COLOR;
        print=true;
    }

    @Test
    void test(){
        simplerStructParams();
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
                void main(){
                    int x;
                    int y;
                    x=888;
                    y=999;
                    print_i(x);
                }
                
                """);
    }


    @Test void varSoup(){
        assertCorrectOutput("""
                void main(){
                    int x0;
                    int x1;
                    int x2;
                    int x3;
                    int x4;
                    int x5;
                    int x6;
                    int x7;
                    int x8;
                    int x9;
                    int xA;
                    int xB;
                    int xC;
                    int xD;
//                    int xE;
//                    int xF;
                x0=
                x1=
                x2=
                x3=
                x4=
                x5=
                x6=
                x7=
                x8=
                x9=
                xA=
                xB=
                xC=
                xD=
//                xE=
//                xF=
                999;
                
                print_i(x0+
                    x1+
                    x2+
                    x3+
                    x4+
                    x5+
                    x6+
                    x7+
                    x8+
                    x9+
                    xA+
                    xB+
                    xC+
                    xD
//                    +xE
//                    +xF
                    );
                }
                """);
    }
}
