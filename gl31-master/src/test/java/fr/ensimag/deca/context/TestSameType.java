package fr.ensimag.deca.context;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.tree.Location;
import org.junit.Test;
import org.mockito.internal.matchers.Null;

import static org.junit.Assert.*;


public class TestSameType {

    @Test
    public void testSameType() {
        SymbolTable table = new SymbolTable();
        Symbol a = table.create("int");
        Symbol b = table.create("float");
        Symbol c = table.create("boolean");
        Symbol d = table.create("null");
        Symbol e = table.create("void");

        EnvironmentExp racine = new EnvironmentExp(null);
        Location r = new Location(0,0,"test");

        Type intT = new IntType(a);
        Type floatT = new FloatType(b);
        Type booleanT = new BooleanType(c);
        Type voidT = new VoidType(e);
        Type nullT = new NullType(d);
        Type intT2 = new IntType(b);

        // Test not same Type
        assertFalse(intT.sameType(floatT));
        assertFalse(floatT.sameType(intT));
        assertFalse(nullT.sameType(intT));

        // Different type with same symbol
        assertFalse(intT2.sameType(floatT));

        // Same type, different symbol
        assertTrue(intT.sameType(intT2));

    }

}
