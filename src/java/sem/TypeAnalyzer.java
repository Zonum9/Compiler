package sem;

import ast.*;

import java.util.HashMap;
import java.util.Objects;

import static ast.BaseType.*;

public class TypeAnalyzer extends BaseSemanticAnalyzer {
	HashMap<String,StructTypeDecl>declaredStructTypes = new HashMap<>();

	public Type visit(ASTNode node) {
		return switch(node) {
			case null -> {
				throw new IllegalStateException("Unexpected null value");
			}

			case Block b -> {
				for (ASTNode c : b.children())
					visit(c);
				yield NONE;
			}

			case FunDecl fd -> {
				for (ASTNode child: fd.children()){
					visit(child);
				}
				yield NONE;
			}

			case Program p -> {
				for(ASTNode c: p.decls){
					if(c instanceof FunDecl fd){
						informReturnsOfFuncType(fd);
					}
				}
				for(ASTNode c: p.decls){
					visit(c);
				}
				yield NONE;
			}

			case VarDecl vd -> {
				if (vd.type == VOID)
					error("variables cannot have type void");
				else {
					visit(vd.type);
				}
				yield NONE;
			}

			case VarExpr v -> {
				v.type= v.origin.type;
				yield v.type;
			}


			case StructType st->{
				if (!declaredStructTypes.containsKey(st.strTypeName))
				{
					error("Struct type [" + st.strTypeName + "] has not been declared");
					yield UNKNOWN;
				}
				st.origin=declaredStructTypes.get(st.strTypeName);
				yield st;
			}


			case StructTypeDecl std -> {
				String name= std.name;
				if (declaredStructTypes.containsKey(name)) {
					error("Struct type ["+name+"] already declared");
					yield UNKNOWN;
				}
				declaredStructTypes.put(name,std);
				for (VarDecl vd: std.varDecls){
					if(isRecursiveStructType(vd.type,name)){
						error(String.format("Recursive struct definition of struct [%s]",name));
					}
					visit(vd);
				}
				yield NONE;
			}


			case BaseType type -> type;


			case ArrayType arrayType -> {
				 Type t= visit(arrayType.type);
				 if (t == UNKNOWN){
					 yield t;
				 }
				if (t == VOID){
					error("array of type VOID is not allowed");
					yield UNKNOWN;
				}
				 yield arrayType;
			}


			case PointerType pointerType -> {
				Type t= visit(pointerType.type);
				if (t == UNKNOWN ){
					yield t;
				}
				yield pointerType;
			}

			case IntLiteral intLiteral -> {
				intLiteral.type= INT;
				yield INT;
			}


			case StrLiteral strLiteral -> {
				int len= strLiteral.value.length()+1;
				strLiteral.type = new ArrayType(CHAR,len);
				yield strLiteral.type;
			}

			case ChrLiteral chrLiteral -> {
				chrLiteral.type= CHAR;
				yield CHAR;
			}


			case BinOp binOp -> {
				Type lhs= visit(binOp.expr1);
				Type rhs= visit(binOp.expr2);
				switch (binOp.op){
					case NE,EQ ->{
						switch (lhs){
							case StructType ignored -> {
								error("equality check performed on invalid type");
								binOp.type=UNKNOWN;
								yield UNKNOWN;
							}
							case ArrayType ignored -> {
								error("equality check performed on invalid type");
								binOp.type=UNKNOWN;
								yield UNKNOWN;
							}
							case VOID,UNKNOWN -> {
								error("equality check performed on invalid type");
								binOp.type=UNKNOWN;
								yield UNKNOWN;
							}
							default -> {
								if (!equalTypes(rhs,lhs)){
									error("equality check performed two different types");
									binOp.type=UNKNOWN;
									yield UNKNOWN;
								}
								binOp.type=INT;
								yield INT;
							}
						}
					}
					default -> {
						if(lhs == INT && rhs == INT){
							binOp.type=INT;
							yield INT;
						}
						error("bin op performed on non int values");
						binOp.type=UNKNOWN;
						yield UNKNOWN;
					}
				}
			}


			case FunCallExpr funCallExpr -> {
				FunDecl decl = funCallExpr.origin;
				assert decl != null;
				if(funCallExpr.exprs.size() != decl.params.size()){
					error("function call with differing amount of arguments from declaration");
					funCallExpr.type=UNKNOWN;
					yield UNKNOWN;
				}
				for (int i = 0; i < funCallExpr.exprs.size(); i++) {
					Type argType= visit(funCallExpr.exprs.get(i));
					Type funParamType=decl.params.get(i).type;
					if( !equalTypes(argType,funParamType)){
						error(String.format("invalid argument type, expected %s and got %s",funParamType,argType));
						funCallExpr.type=UNKNOWN;
						yield UNKNOWN;
					}
				}
				funCallExpr.type=decl.type;
				yield decl.type;
			}

			case ArrayAccessExpr arrayAccessExpr -> {
				Type lhs = visit(arrayAccessExpr.arr);

				switch (lhs){
					case ArrayType x->{
						arrayAccessExpr.type=x.type;
					}
					case PointerType x->{
						arrayAccessExpr.type=x.type;
					}
					default -> {
						error("array access on non array/pointer expression");
						arrayAccessExpr.type = UNKNOWN;
						yield UNKNOWN;
					}
				}
				Type index = visit(arrayAccessExpr.index);
				if (index != INT){
					error("invalid array index type, expected int but got "+index);
					arrayAccessExpr.type = UNKNOWN;
					yield UNKNOWN;
				}

				yield arrayAccessExpr.type;
			}

			case FieldAccessExpr fieldAccessExpr -> {
				Type lhs = visit(fieldAccessExpr.expr);
				if (lhs instanceof StructType st){
					if(!declaredStructTypes.containsKey(st.strTypeName)){
						error(String.format("Struct type [%s] does not exist",st.strTypeName));
						fieldAccessExpr.type=UNKNOWN;
						yield UNKNOWN;
					}
					StructTypeDecl decl = declaredStructTypes.get(st.strTypeName);
					st.origin=decl;
					String desiredField = fieldAccessExpr.fieldName;
					for (VarDecl field: decl.varDecls){
						if (desiredField.equals(field.name)){
							fieldAccessExpr.type=field.type;
							yield field.type;
						}
					}
					error(String.format("Trying to access field [%s], which does not exist",desiredField));
					fieldAccessExpr.type=UNKNOWN;
					yield UNKNOWN;
				}
				error(String.format("Trying to access fields of non struct type. Actual type is [%s]",lhs));
				fieldAccessExpr.type=UNKNOWN;
				yield UNKNOWN;
			}
			case ValueAtExpr valueAtExpr -> {
				Type exprType= visit(valueAtExpr.expr);
				if (exprType instanceof PointerType pt){
					valueAtExpr.type=pt.type;
					yield pt.type;
				}
				error(String.format("Trying to dereference non pointer type. Actual type is [%s]",exprType));
				valueAtExpr.type=UNKNOWN;
				yield UNKNOWN;
			}
			case AddressOfExpr addressOfExpr -> {
				if(!isLvalue(addressOfExpr.expr)) {
					error(String.format("Trying to get address non left value [%s]", addressOfExpr.expr));
					addressOfExpr.type=UNKNOWN;
					yield UNKNOWN;
				}
				addressOfExpr.type = new PointerType(visit(addressOfExpr.expr));
				yield addressOfExpr.type;
			}
			case SizeOfExpr szof -> {
				szof.type= visit(szof.sizeOfType);
				if(szof.type == UNKNOWN)
					yield UNKNOWN;
				yield INT;
			}

			case TypecastExpr typecastExpr -> {
				Type rhs=visit(typecastExpr.expr);
				Type t= switch (typecastExpr.castType){
					case INT -> {//casting to int
						if (rhs != CHAR){
							error(String.format("Cannot do casts type [%s] to int", rhs));
							yield UNKNOWN;
						}
						yield INT;
					}
					case PointerType pt->{//casting to pointer
						switch (rhs){
							case ArrayType x->{//casting an array to a pointer only if same type
								if (equalTypes(x.type,pt.type))
									yield pt;
								error(String.format("Cannot do casts array of type [%s] to [%s]]", x.type,pt));
								yield UNKNOWN;
							}
							case PointerType ignored ->{//pointers can be cast between them freely
								yield pt;
							}
							default -> {//cannot cast any other type to a pointer type
								error(String.format("Cannot do casts type [%s] to [%s]]", rhs,pt));
								yield UNKNOWN;
							}
						}
					}
					default -> 	{
						error(String.format("Cannot do casts to type [%s]", typecastExpr.castType));
						yield UNKNOWN;
					}
				};
				typecastExpr.type=t;
				yield t;
			}

			case Assign assign -> {
				if(!isLvalue(assign.expr1)) {
					error(String.format("Trying to assign to non left value [%s]", assign.expr1));
					assign.type=UNKNOWN;
					yield UNKNOWN;
				}
				Type lhs= visit(assign.expr1);
				if (lhs == VOID || lhs instanceof ArrayType){
					error(String.format("Cannot assign to expression of type [%s]", lhs));
					assign.type=UNKNOWN;
					yield UNKNOWN;
				}
				Type rhs = visit(assign.expr2);
				if(!equalTypes(rhs,lhs)){
					error(String.format("Cannot assign type [%s] to type [%s]", rhs,lhs));
					assign.type=UNKNOWN;
					yield UNKNOWN;
				}
				assign.type=lhs;
				yield lhs;
			}


			case While aWhile -> {
				Type condition= visit(aWhile.expr);
				if (condition != INT){
					error(String.format("While loop with non integer condition, received type [%s]",condition));
					yield UNKNOWN;
				}
				yield visit(aWhile.stmt);
			}

			case If anIf -> {
				Type condition= visit(anIf.condition);
				if (condition != INT){
					error(String.format("If statement with non integer condition, received type [%s]",condition));
					yield UNKNOWN;
				}
				Type stmpType= visit(anIf.stmt);
				if (!anIf.els.isPresent())
					yield stmpType;
				Type elsType= visit(anIf.els.get());
				if(elsType == UNKNOWN || stmpType == UNKNOWN)
					yield UNKNOWN;
				yield NONE;
			}

			case ExprStmt exprStmt -> visit(exprStmt.expr);

			case Return aReturn -> {
				if(aReturn.functionReturnType ==VOID){
					if (aReturn.expr.isPresent()) {
						error(String.format("Void function should not return anything, but is returning [%s]",aReturn.expr.get()));
						yield UNKNOWN;
					}
					yield NONE;
				}
				if (!aReturn.expr.isPresent()) {
					error(String.format("Empty return statement for function of type [%s]",aReturn.functionReturnType));
					yield UNKNOWN;
				}
				Type exprType= visit(aReturn.expr.get());

				if(!equalTypes(exprType,aReturn.functionReturnType)){
					error(String.format("Expected return of type [%s] but got [%s]",aReturn.functionReturnType,exprType));
					yield UNKNOWN;
				}
				yield NONE;
			}

			//done?
			case FunProto funProto -> NONE;
			case Op op -> NONE;
			case Break aBreak -> NONE;
			case Continue aContinue -> NONE;

			//todo
            case ClassDecl classDecl -> null;
            case ClassType classType -> null;
            case InstanceFunCallExpr instanceFunCallExpr -> null;
            case NewInstance newInstance -> null;
        };
	}

	private boolean isRecursiveStructType(Type type,String name) {
		return switch (type){
			case StructType st-> Objects.equals(st.strTypeName, name);
			case ArrayType arr-> isRecursiveStructType(arr.type,name);
			default->false;
		};
	}

	private void informReturnsOfFuncType(FunDecl fd) {
		informReturnsHelper(fd.block, fd.type);
	}

	private void informReturnsHelper(ASTNode astNode, Type type){
		switch (astNode){
			case Return rt->rt.functionReturnType =type;
			default -> {
				for (ASTNode child: astNode.children()){
					informReturnsHelper(child,type);
				}
			}
		}
	}

	private boolean isLvalue(Expr expr) {
		switch (expr){
			case VarExpr ignored->{
				return true;
			}
			case FieldAccessExpr x->{
				return isLvalue(x.expr);
			}
			case ArrayAccessExpr x ->{
				return isLvalue(x.arr);
			}
			case ValueAtExpr x ->{
				return isLvalue(x.expr);
			}
			default -> {
				return false;
			}
		}
	}

	public static boolean equalTypes(Type t1, Type t2){
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
            case ClassType x1 -> {
				ClassType x2 = (ClassType) t2;
				return x1.identifier.equals(x2.identifier);
            }
        }
	}


}
