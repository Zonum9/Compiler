package gen;

import ast.ASTNode;
import ast.Program;
import ast.StrLiteral;
import gen.asm.AssemblyProgram;
import gen.asm.Directive;
import gen.asm.Label;

public class StringAllocCodeGen extends CodeGen {
    public StringAllocCodeGen(AssemblyProgram asmProg) {
        this.asmProg=asmProg;
    }

    void visit(ASTNode n){
        AssemblyProgram.Section currentSection = asmProg.getCurrentSection();

        switch (n){
            case StrLiteral s->{
                Label strLbl = Label.create();
                currentSection.emit(strLbl);
                s.label=strLbl;
                String modified = revertEscapeCharReplacement(s.value);
                currentSection.emit(new Directive("asciiz \""+modified+"\""));
                currentSection.emit(new Directive("align 2"));
            }
            default -> n.children().forEach(this::visit);

        }
    }

    private String revertEscapeCharReplacement(String s){
        String[][] replacements = {
                {"\\\\","\\"},
                {"\\a",String.valueOf('\u0007')},
                {"\\b","\b"},
                {"\\n","\n"},
                {"\\r","\r"},
                {"\\t","\t"},
                {"\\'", "'"},
                {"\\\"","\""},
                {"\\0","\0"}
        };
        for (String[] pairToReplace : replacements) {
            s=s.replace(pairToReplace[1],pairToReplace[0]);
        }
        return s;
    }
}
