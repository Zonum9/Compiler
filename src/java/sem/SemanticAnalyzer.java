package sem;

import ast.*;
import util.CompilerPass;

import java.util.Collections;
import java.util.List;

public class SemanticAnalyzer extends CompilerPass {
	
	public void analyze(ast.Program prog) {
		//fixme this is awful, but ig it works
		prog.decls.add(
				new FunDecl(BaseType.VOID,"print_s",
						List.of(new VarDecl(new PointerType(BaseType.CHAR),"s")),
						new Block(Collections.emptyList(),Collections.emptyList())
				)
		);
		prog.decls.add(
				new FunDecl(BaseType.VOID,"print_i",
						List.of(new VarDecl(BaseType.INT,"i")),
						new Block(Collections.emptyList(),Collections.emptyList())
				)
		);
		prog.decls.add(
				new FunDecl(BaseType.VOID,"print_c",
						List.of(new VarDecl(BaseType.CHAR,"c")),
						new Block(Collections.emptyList(),Collections.emptyList())
				)
		);
		prog.decls.add(
				new FunDecl(BaseType.CHAR,"read_c",
						Collections.emptyList(),
						new Block(Collections.emptyList(),Collections.emptyList())
				)
		);
		prog.decls.add(
				new FunDecl(BaseType.INT,"read_i",
						Collections.emptyList(),
						new Block(Collections.emptyList(),Collections.emptyList())
				)
		);
		prog.decls.add(
				new FunDecl(new PointerType(BaseType.VOID),"mcmalloc",
						List.of(new VarDecl(BaseType.INT,"size")),
						new Block(Collections.emptyList(),Collections.emptyList())
				)
		);
		NameAnalyzer na = new NameAnalyzer();
		na.visit(prog);
		this.numErrors += na.getNumErrors();

		TypeAnalyzer tc = new TypeAnalyzer();
		tc.visit(prog);
		this.numErrors += tc.getNumErrors();
		// To complete
	}
}
