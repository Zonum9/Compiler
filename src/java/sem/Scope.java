package sem;

import java.util.HashMap;
import java.util.Map;

public class Scope {
	private Scope outer;
	private Map<String, Symbol> symbolTable = new HashMap<>();//todo should i do this?
	
	public Scope(Scope outer) { 
		this.outer = outer; 
	}
	
	public Scope() { this(null); }
	
	public Symbol lookup(String name) {
		Symbol currSym=lookupCurrent(name);
		if (outer == null || currSym != null){
			return currSym;
		}
		return outer.lookup(name);
	}

	public Symbol lookupGlobal(String name) {
		if (outer == null){
			return lookupCurrent(name);
		}
		return outer.lookup(name);
	}
	
	public Symbol lookupCurrent(String name) {
		return symbolTable.get(name);
	}
	
	public void put(Symbol sym) {
		symbolTable.put(sym.name, sym);
	}
}
