package sem;

import ast.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class NameAnalyzer extends BaseSemanticAnalyzer {

	Scope scope = new Scope(); //original outer scope has null value for its scope field
	Stack<FunCallExpr> hangingFunCallRefs = new Stack<>();
	public void visit(ASTNode node) {
		switch(node) {
			case null -> throw new IllegalStateException("Unexpected null value");

			case Program p -> {
				Scope oldScope= scope;
				for (Decl d : p.decls) {
					visit(d);
				}
				scope=oldScope;
				for (Decl decl : p.decls) {
                    if (decl instanceof FunProto fp) {
                        Symbol s = scope.lookupCurrent(fp.name);
						if(s instanceof FunProtoSymbol fps){
							if(fps.decl==null){
								error("function prototype ["+fps.name+"] does not have a declaration");
								continue;
							}
							if(hangingFunCallRefs.empty()){
								continue;
							}
							if( Objects.equals(hangingFunCallRefs.peek().name, fps.name)){
								FunCallExpr fce= hangingFunCallRefs.pop();
								fce.origin=fps.decl;
							}
						}
                    }
				}
				for(FunCallExpr fcx: hangingFunCallRefs){
					error("function call on undeclared function prototype ["+fcx.name+"]");
				}
			}

			case Block b -> {
				Scope oldScope=scope;
				scope= new Scope(oldScope);
				for (ASTNode child : b.children()){
					visit(child);
				}
				scope=oldScope;
			}

			case FunDecl funDecl-> {
				Symbol s = scope.lookupCurrent(funDecl.name);
				switch (s){
					//if this function has already been defined, error
					case FunSymbol fs->error("function ["+fs.name+"] defined multiple times");

					//if a fun proto has been put in scope
					case FunProtoSymbol fps->{
						//but no fun decl has been put in scope
						//then link the  fun proto with this fun decl
						if(fps.decl == null){
							if(sameFunctionForm(fps.fp,funDecl))
								fps.decl=funDecl;
							else
								error("function ["+funDecl.name+"] differs from it's prototype form");

						}
						else {
							error("function ["+fps.name+"] defined multiple times");
						}
					}
					//if a fun proto has not been put in scope, then put this function on scope
					case null->scope.put(new FunSymbol(funDecl));

					default -> error(funDecl.name+" has already been used");
				}
				Scope oldScope= scope;
				scope = new Scope(oldScope);
				for (VarDecl vd : funDecl.params){
					visit(vd);
				}
				Block b = funDecl.block;
				for (ASTNode child : b.children()){
					visit(child);
				}
				scope=oldScope;
			}

			case FunProto funProto -> {
				Symbol s = scope.lookupCurrent(funProto.name);
				switch (s){
					//if this prototype has already been defined, error
					case FunProtoSymbol fps->error("function prototype ["+fps.name+"] defined multiple times");

					//if a fun decl has been put in scope
					case FunSymbol funSymbol ->{
						//but no fun proto has been put in scope
						//then link this  fun proto with the fun decl
						if(funSymbol.proto == null){
							if(sameFunctionForm(funProto,funSymbol.funDecl))
								funSymbol.proto=funProto;
							else
								error("function ["+funSymbol.name+"] differs from it's prototype form");

						}
						else {
							error("function prototype ["+funProto.name+"] defined multiple times");
						}
					}
					//if a fun decl has not been put in scope, then put this proto on scope
					case null->scope.put(new FunProtoSymbol(funProto));
					default -> error(funProto.name+" has already been used");
				}
				Scope oldScope = scope;
				scope= new Scope(oldScope);
				for (VarDecl vd:funProto.params){
					visit(vd);
				}
				scope=oldScope;
			}

			case VarDecl vd -> {
				Symbol s = scope.lookupCurrent(vd.name);
				if (s != null)
					error("["+s.name + "] has already been declared in this scope");
				else {
//					s = scope.lookupGlobal(vd.name); //check that no function exists with this name
//					if(!(s instanceof FunProtoSymbol) && !(s instanceof FunSymbol))
					scope.put(new VarSymbol(vd));
//					else{
//						error("var decl attempting to shadow function ["+s.name + "]");
//					}
				}
			}

			case VarExpr varExpr -> {
				Symbol sym = scope.lookup(varExpr.name);
				switch (sym){
					case VarSymbol vs -> varExpr.origin=vs.varDecl;
                    case null,default -> error("undeclared variable ["+varExpr.name+"]");
                }
			}
			case FunCallExpr funCall -> {
				Symbol sym = scope.lookup(funCall.name);
				switch (sym){
					case FunSymbol funSym -> funCall.origin=funSym.funDecl;
					case FunProtoSymbol protoSym -> {
						if (protoSym.decl!=null)
							funCall.origin=protoSym.decl;
						else
							hangingFunCallRefs.add(funCall);
					}
					case null -> error("function call on undefined function ["+funCall.name+"]");
					default -> error("function ["+funCall.name+"] is either undefined or was shadowed by a variable");
				}
			}

			case StructTypeDecl std -> {
				Scope oldScope = scope;
				scope= new Scope(oldScope);
				for(VarDecl vd:std.varDecls){
					visit(vd);
				}
				scope=oldScope;
			}

			default -> {
				for(ASTNode child: node.children()){
					visit(child);
				}
			}
        };

	}
	private boolean equalTypes(Type t1, Type t2){
		if(!t1.getClass().equals(t2.getClass()))
			return false;
		switch (t1){
            case ArrayType x1 -> {
				ArrayType x2 = (ArrayType) t2;
				return (x1.numElement==x2.numElement && equalTypes(x1.type,x2.type));
            }
            case BaseType x1 -> {//enums, so simple compare
				BaseType x2 = (BaseType) t2;
				return x1 == x2;
            }
            case PointerType x1 -> {
				PointerType x2 = (PointerType) t2;
				return equalTypes(x1.type,x2.type);
            }
            case StructType x1 -> {
				StructType x2 = (StructType) t2;
				return x1.strTypeName.equals(x2.strTypeName);
            }
        }
	}

	private boolean sameFunctionForm(FunProto proto, FunDecl decl) {
		if (!equalTypes(proto.type,decl.type) || proto.params.size() != decl.params.size()){
			return false;
		}
		List<VarDecl> p1 =proto.params;
		List<VarDecl> p2 =decl.params;
		for (int i = 0; i < p1.size(); i++) {
			Type t1= p1.get(i).type;
			Type t2= p2.get(i).type;
			if(! equalTypes(t1,t2))
				return false;
		}
		return true;
	}


}
