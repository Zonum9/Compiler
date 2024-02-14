package sem;

import ast.*;

import java.util.*;

import static sem.TypeAnalyzer.equalTypes;

public class NameAnalyzer extends BaseSemanticAnalyzer {

	Scope scope = new Scope(); //original outer scope has null value for its scope field
	HashMap<String,List<FunCallExpr>> hangingFunCallRefs = new HashMap<>();
	public void visit(ASTNode node) {
		switch(node) {
			case null -> throw new IllegalStateException("Unexpected null value");

			case Program p -> {
				Scope oldScope= scope;
				for (Decl d : p.decls) {
					visit(d);
				}
				scope=oldScope;
				for (Decl decl : p.decls) {//check for hanging function calls and protos without declarations
                    if (decl instanceof FunProto fp) {
                        Symbol s = scope.lookupCurrent(fp.name);
						if(s instanceof FunProtoSymbol fps){// if lookup returns a prototype, then it must have hanging fun calls

							if(fps.decl==null){//if decl == null, then even after checking the whole program no decl was found
								error("function prototype ["+fps.name+"] does not have a declaration");
								continue;
							}
							if(hangingFunCallRefs.isEmpty()){
								continue;
							}
							hangingFunCallRefs.get(fps.name).forEach((x)->x.origin=fps.decl);
							hangingFunCallRefs.remove(fps.name);
						}
                    }
				}
				for(List<FunCallExpr> list: hangingFunCallRefs.values()){
					for (FunCallExpr fcx :list) {
						error("function call on undeclared function prototype [" + fcx.name + "]");
					}
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
						if (protoSym.decl!=null) {
							funCall.origin = protoSym.decl;
						}
						else {
							if (hangingFunCallRefs.containsKey(funCall.name)){
								hangingFunCallRefs.get(funCall.name).add(funCall);
							}else {
								ArrayList<FunCallExpr> temp=new ArrayList<>();
								temp.add(funCall);
								hangingFunCallRefs.put(funCall.name, temp);
							}
						}
					}
					case null -> error("function call on undefined function ["+funCall.name+"]");
					default -> error("function ["+funCall.name+"] is either undefined or was shadowed by a variable");
				}
				for (ASTNode child : funCall.exprs){
					visit(child);
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
