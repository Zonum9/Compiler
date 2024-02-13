package sem;

import ast.*;

import java.util.HashMap;

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
				yield BaseType.NONE;
			}

			case FunDecl fd -> {
				for (ASTNode child: fd.children()){
					visit(child);
				}
				yield BaseType.NONE;
			}

			case Program p -> {
				for(ASTNode c: p.decls){
					visit(c);
				}
				yield BaseType.NONE;
			}

			case VarDecl vd -> {
				if (vd.type == VOID)
					error("variables cannot have type void");
				else {
					visit(vd.type);
				}
				yield BaseType.NONE;
			}

			case VarExpr v -> {
				v.type= v.origin.type;
				yield v.type;
			}

			case StructType st->{
				if (!declaredStructTypes.containsKey(st.strTypeName))
				{
					error("Struct type [" + st.strTypeName + "] has not been declared");
					yield BaseType.UNKNOWN;
				}
				yield st;
			}
			case StructTypeDecl std -> {
				String name= ((StructType) std.type).strTypeName;
				if (declaredStructTypes.containsKey(name)) {
					error("Struct type ["+name+"] already declared");
					yield BaseType.UNKNOWN;
				}
				declaredStructTypes.put(name,std);
				for (VarDecl vd: std.varDecls){
					visit(vd);
				}
				yield BaseType.NONE;
			}
			case BaseType type -> type;

			case ArrayType arrayType -> {
				 Type t= visit(arrayType.type);
				 if (t == BaseType.UNKNOWN){
					 yield t;
				 }
				if (t == VOID){
					error("array of type VOID is not allowed");
					yield BaseType.UNKNOWN;
				}
				 yield arrayType;
			}
			case PointerType pointerType -> {
				Type t= visit(pointerType.type);
				if (t == BaseType.UNKNOWN ){
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
				strLiteral.type = new ArrayType(BaseType.CHAR,len);
				yield strLiteral.type;
			}

			case ChrLiteral chrLiteral -> {
				chrLiteral.type=BaseType.CHAR;
				yield BaseType.CHAR;
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
				if (!(lhs instanceof ArrayType) && !(lhs instanceof PointerType)){
					error("array access on non array/pointer expression");
					arrayAccessExpr.type = UNKNOWN;
					yield UNKNOWN;
				}
				Type index = visit(arrayAccessExpr.index);
				if (index != INT){
					error("invalid array index type, expected int but got "+index);
					arrayAccessExpr.type = UNKNOWN;
					yield UNKNOWN;
				}
				arrayAccessExpr.type=lhs;
				yield lhs;
			}

			//todo
			case Return aReturn -> {
				//fixme has to match function type
				if (aReturn.expr.isPresent())
					yield visit(aReturn.expr.get());
				yield NONE;
			}
			case FieldAccessExpr fieldAccessExpr -> null;
            case While stmt -> null;
            case AddressOfExpr addressOfExpr -> null;
            case Assign assign -> null;
            case SizeOfExpr sizeOfExpr -> null;
            case TypecastExpr typecastExpr -> null;
            case ValueAtExpr valueAtExpr -> null;
            case If anIf -> null;


			//done?
			case FunProto funProto -> BaseType.NONE;
			case Op op -> BaseType.NONE;
			case ExprStmt exprStmt -> visit(exprStmt.expr);
			case Break aBreak -> NONE;
			case Continue aContinue -> NONE;


        };

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
		}
	}


}
