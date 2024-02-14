package sem;

import ast.*;
import lexer.Scanner;
import lexer.Tokeniser;
import parser.Parser;
import util.CompilerPass;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;

public class SemanticAnalyzer extends CompilerPass {
	
	public void analyze(ast.Program prog) {
		StringReader sr= new StringReader("""
				void print_s(char* s){}
				void print_i(int i){}
				void print_c(char c){}
				char read_c(){}
				int read_i(){}
				void* mcmalloc(int size){}
				""");
		Tokeniser t = new Tokeniser(new Scanner(new BufferedReader(sr)));
		Program builtIns = new Parser(t).parse();
		prog.decls.addAll(0,builtIns.decls);

		NameAnalyzer na = new NameAnalyzer();
		na.visit(prog);
		this.numErrors += na.getNumErrors();
		if (this.numErrors!=0){
			return;
		}
		TypeAnalyzer tc = new TypeAnalyzer();
		tc.visit(prog);
		this.numErrors += tc.getNumErrors();
	}
}
