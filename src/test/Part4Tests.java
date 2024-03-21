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
        assertCorrectOutput("""
                void main() {
                  while (0) {
                    continue;
                    print_i(1);
                  }
                }
                """);
    }
}
