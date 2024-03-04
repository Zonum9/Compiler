package gen;

import ast.*;
import gen.asm.AssemblyProgram;
import gen.asm.Directive;
import gen.asm.Label;

import static ast.BaseType.*;

/* This allocator should deal with all global and local variable declarations. */

public class MemAllocCodeGen extends CodeGen {

    public MemAllocCodeGen(AssemblyProgram asmProg) {
        this.asmProg = asmProg;
    }

    boolean global = true;
    int fpOffset = 0;

void visit(ASTNode n) {
        AssemblyProgram.Section currSect= asmProg.getCurrentSection();
        switch (n){
            case Program x->{
                fpOffset=-4;
                for (ASTNode child:x.children()){
                    visit(child);
                    global=true;
                }
            }

            case VarDecl vd->{
                if(global){
                    vd.isGlobal=true;
                    currSect.emit(new Directive("globl "+vd.name));
                    currSect.emit(Label.get(vd.name));
                    currSect.emit(new Directive("align "+2));
                    currSect.emit(new Directive("space "+ wordAlign(sizeofType(vd.type))));
                    return;
                }
                //local variables
                int space=wordAlign(sizeofType(vd.type));
                vd.space=space;
                fpOffset-=space;
                vd.isGlobal=false;
                vd.fpOffset=fpOffset;

            }

            case FunDecl x -> {
                global=false;
                x.returnValueFPOffset=4;
                fpOffset= wordAlign(sizeofType(x.type)) +4;//space taken by return value
                for(VarDecl decl:x.params.reversed()){//arguments are arranged bottom to top on the stack
                    //positive offset for function arguments
                    int space=wordAlign(sizeofType(decl.type));
                    decl.fpOffset=fpOffset;
                    decl.space=space;
                    decl.isGlobal=false;
                    fpOffset+=space;
                }
                fpOffset=-4;
                x.block.children().forEach(this::visit);
            }

            case ASTNode x -> {
                global=false;
                x.children().forEach(this::visit);
            }



        }
    }

    public static int wordAlign(int size){
        return Math.ceilDiv(size,4)*4;
    }

    public static int sizeofType(Type t){
        return switch (t){
            case INT-> 4;
            case PointerType ignored -> 4;
            case CHAR -> 1;
            case VOID->0;
            case ArrayType arrayType ->  arrayType.numElement* sizeofType(arrayType.type);
            case StructType structType -> {
                StructTypeDecl decl = structType.origin;
                int totalSize=0;
                for (VarDecl vd: decl.varDecls){
                    int size = sizeofType(vd.type);
                    totalSize+= wordAlign(size);
                }
                yield totalSize;
            }

            case UNKNOWN, NONE-> {
                throw new IllegalStateException();
            }
        };
    }

}
