import ast.ASTPrinter;
import ast.ASTdotPrinter;
import ast.Program;
import gen.ProgramCodeGen;
import gen.asm.AssemblyProgram;
import lexer.Scanner;
import lexer.Token;
import lexer.Tokeniser;
import parser.Parser;
import regalloc.GraphColouringRegAlloc;
import regalloc.NaiveRegAlloc;
import sem.SemanticAnalyzer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static lexer.Token.Category.EOF;
import static org.junit.jupiter.api.Assertions.*;

public class Utils {
    public static final String path= "src/test/";
    public static void assertFileEquals(String  referenceFile, String fileToTest,boolean ignoreWhiteSpace) throws Exception {
        if (!ignoreWhiteSpace)
            assertFileEquals(referenceFile,fileToTest);
        else
        {
            String fileToCompare = new String(Files.readAllBytes(Paths.get(fileToTest))).replaceAll("\\s+","");;
            String fileToReference = new String(Files.readAllBytes(Paths.get(referenceFile))).replaceAll("\\s+","");;
            assertEquals(fileToReference,fileToCompare);
        }
    }
    public static void assertFileEquals(String  referenceFile, String fileToTest) throws Exception {
//        File file=new File(fileToTest);
        BufferedReader fileToCompare = new BufferedReader(new FileReader(fileToTest));
        BufferedReader fileToReference = new BufferedReader(new FileReader(referenceFile));
        String lineRef;
        String lineComp;
        while((lineRef=fileToReference.readLine())!=null) {
            lineComp=fileToCompare.readLine();
            assertNotNull(lineComp);
            assertEquals(lineRef,lineComp);
        }
        fileToReference.close();
        assertNull(fileToCompare.readLine());
        fileToCompare.close();
    }
    public static void assertFileEqualsTokenization(String  referenceFile, String fileToTest) throws Exception {
        File file=new File(fileToTest);
        BufferedReader fileToReference = new BufferedReader(new FileReader(referenceFile));
        String line;
        Tokeniser t = new Tokeniser(new Scanner(file));
        while((line=fileToReference.readLine())!=null) {
            Token next = t.nextToken();

            if (next.category == EOF)
                break;
            //Tokens with new lines are still considered a single token, so need to split them
            if (next.toString().contains("\n")){
                String[] splits= next.toString().split("\n");
                for (int i = 0; i < splits.length; i++) {
                    assertEquals(line,splits[i]);
                    if (i != splits.length-1)
                        line=fileToReference.readLine();
                    if (line == null){
                        fail();
                    }
                }
            }else
                assertEquals(line,next.toString());
        }
        fileToReference.close();
        assertEquals(t.getNumErrors(), 0);
    }
    public static Tokeniser createTokeniserFromString(String content) throws IOException {
        File f= File.createTempFile("hello",".c");
        Files.writeString(f.toPath(), content);
        return new Tokeniser(new Scanner(f));
    }
    public static Tokeniser createTokeniserFromFile(String fileName) throws IOException {
        File f= new File(fileName);
        return new Tokeniser(new Scanner(f));
    }

    public static Parser createParserFromFile(String filename) {
        Parser p;
        try {
            p = new Parser(createTokeniserFromFile(filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return p;
    }
    public static Parser createParserFromString(String stringToParse) {
        Parser p;
        try {
            p = new Parser(createTokeniserFromString(stringToParse));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return p;
    }

    static void  writeASTFromFile(String fileName){
        writeAST(Utils.createParserFromFile(fileName).parse());
    }

    static void  writeASTFromString(String stringToParse){
        writeAST(Utils.createParserFromString(stringToParse).parse());
    }
    public static void  writeAST(Program programAst)  {

        PrintWriter writer = null;
        try {
            File out = new File("src/test/printerOut/out.txt");
            if (out.exists()) {
                out.delete();
            }
            out.createNewFile();
            writer = new PrintWriter(out);
            new ASTPrinter(writer).visit(programAst);
            writer.close();
        } catch (Exception ignored) {
            assert writer != null;
            writer.close();
        }
    }
    static void  writeASTDotFromString(String stringToParse){
        writeASTDot(Utils.createParserFromString(stringToParse).parse());
    }
    static void  writeASTDotFromFile(String fileName){
        writeASTDot(Utils.createParserFromFile(fileName).parse());
    }
    public static void  writeASTDot(Program programAst)  {

        PrintWriter writer = null;
        try {
            File out = new File("src/test/printerOut/dotOut.txt");
            if (out.exists()) {
                out.delete();
            }
            out.createNewFile();
            writer = new PrintWriter(out);
            new ASTdotPrinter(writer).visit(programAst);
            writer.close();
        } catch (Exception ignored) {
            assert writer != null;
            writer.close();
        }
    }
    public static String getASTstring(String program) throws IOException {
        writeASTFromString(program);
        String text = new String(Files.readAllBytes(Paths.get("src/test/printerOut/out.txt")));
        return text.replaceAll("\\s+","");
    }


    public static String programStringToASMString(String program, RegMode mode,boolean print) {
        return asmOBJtoString(programStringToASMObj(program,mode,print));
    }

    public static String asmOBJtoString(AssemblyProgram p){
        StringWriter output = new StringWriter();
        PrintWriter writer=new PrintWriter(output);
        p.print(writer);
        writer.close();
        return output.toString();
    }

    public enum RegMode {
        NAIVE,
        COLOR
    }
    public static AssemblyProgram programStringToASMObj(String program, RegMode mode,boolean print){
        Program p = Utils.createParserFromString(program).parse();
        SemanticAnalyzer n = new SemanticAnalyzer();
        n.analyze(p);
        assertEquals(n.getNumErrors(),0);
        AssemblyProgram virtualRegs= new AssemblyProgram();
        ProgramCodeGen progGen = new ProgramCodeGen(virtualRegs);
        progGen.generate(p);
        return switch (mode){
            case NAIVE -> NaiveRegAlloc.INSTANCE.apply(virtualRegs);
            case COLOR -> {
                AssemblyProgram asm=GraphColouringRegAlloc.INSTANCE.apply(virtualRegs);
                if (print){
                    GraphColouringRegAlloc.INSTANCE.printLiveness(new PrintWriter(System.out,true));
                    GraphColouringRegAlloc.INSTANCE.printInterference(new PrintWriter(System.out,true));
                }
                yield  asm;
            }
        };
    }

    public static String fileToString(String filename){
        try {
            return Files.readString(Paths.get(path+filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
