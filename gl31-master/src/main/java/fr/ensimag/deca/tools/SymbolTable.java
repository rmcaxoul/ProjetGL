package fr.ensimag.deca.tools;

import java.util.HashMap;
import java.util.Map;

/**
 * Manage unique symbols.
 * 
 * A Symbol contains the same information as a String, but the SymbolTable
 * ensures the uniqueness of a Symbol for a given String value. Therefore,
 * Symbol comparison can be done by comparing references, and the hashCode()
 * method of Symbols can be used to define efficient HashMap (no string
 * comparison or hashing required).
 * 
 * @author gl31
 * @date 01/01/2021
 */
public class SymbolTable {
    // Symbol = name of a variable, a function, a class....
    private Map<String, Symbol> map = new HashMap<String, Symbol>();

    /**
     * Create or reuse a symbol.
     * 
     * If a symbol already exists with the same name in this table, then return
     * this Symbol. Otherwise, create a new Symbol and add it to the table.
     */
    public Symbol create(String name) {
        if (map.containsKey(name)) {
            return map.get(name);
        } else {
            Symbol newSymbol = new Symbol(name);
            map.put(name, newSymbol);
            return newSymbol;
        }
    }

    /**
     * Same function, different name.
     * @param name
     * @return
     */
    public Symbol get(String name) {
        return create(name);
    }

    public static class Symbol {
        // Constructor is private, so that Symbol instances can only be created
        // through SymbolTable.create factory (which thus ensures uniqueness
        // of symbols).
        private Symbol(String name) {
            super();
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }

        private String name;
    }
}
