package gen;

import ast.*;
import gen.asm.AssemblyProgram;
import gen.asm.Directive;
import gen.asm.Label;
import gen.asm.Register;

import static ast.BaseType.*;
import static gen.asm.OpCode.LW;
import static gen.asm.OpCode.SW;

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
                for (ASTNode child:x.children()){
                    fpOffset=-4;
                    visit(child);
                    global=true;
                }
            }

            case VarDecl vd->{
                if(global){
                    vd.isGlobal=true;
                    currSect.emit(new Directive("globl "+vd.name));
                    currSect.emit(new Directive("align "+2));
                    currSect.emit(Label.get(vd.name));
                    currSect.emit(new Directive("space "+ wordAlign(sizeofType(vd.type))));
                    return;
                }
                //local variables
                int space=wordAlign(sizeofType(vd.type));
                vd.space=space;
                vd.isGlobal=false;
                if(vd.type instanceof StructType) {
                    vd.fpOffset = fpOffset - 4;
                }else {
                    vd.fpOffset=fpOffset-space;
                }
                fpOffset-=space;

            }

            case FunDecl x -> {
                global=false;
                x.returnValueSize=wordAlign(sizeofType(x.type));
                fpOffset= x.returnValueSize +4;//space taken by return value
                for (int i = 0; i < x.params.size(); i++) {//arguments are arranged bottom to top on the stack
                    VarDecl decl = x.params.reversed().get(i);
                    //positive offset for function arguments
                    int space;
                    if(decl.type instanceof ArrayType){//arrays are passed by reference, thus treat them like pointers
                        space = wordAlign(sizeofType(new PointerType(INT)));//the type of the pointer does not matter
                        decl.isPtrNow=true;
                    }
                    else {
                        space = wordAlign(sizeofType(decl.type));
                    }
                    //structs are written in memory top to bottom
                    if(decl.type instanceof StructType) {
                        decl.fpOffset = fpOffset+space;
                    }
                    else {
                        decl.fpOffset = fpOffset;
                    }
                    decl.space=space;
                    decl.isGlobal=false;
                    fpOffset+=space;
                }
                fpOffset=-4;
                x.block.children().forEach(this::visit);
            }

            case StructTypeDecl x->{
                global=false;
                fpOffset=4;
                x.children().forEach(this::visit);
            }

            case Type t->{}
            case FunProto t ->{}

            case ASTNode x -> {
                global=false;
                for (ASTNode a:x.children()){
                    visit(a);
                }
            }



        }
    }

    public static int wordAlign(int size){
        return Math.ceilDiv(size,4)*4;
    }

    public static void copyStruct(Register targetBaseAddress, StructType type,Register originBaseAddress,
                                      AssemblyProgram.Section currSect){
        Register temp = Register.Virtual.create();
        int size=wordAlign(sizeofType(type));
        currSect.emit("-----------COPY STRUCT-------------");
        for (int i = 0; i < size ; i+=4) {
            currSect.emit(LW,temp,originBaseAddress,-i);
            currSect.emit(SW,temp,targetBaseAddress,-i);
        }
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
