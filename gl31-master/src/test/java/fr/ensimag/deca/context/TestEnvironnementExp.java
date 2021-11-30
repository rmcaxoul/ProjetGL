package fr.ensimag.deca.context;

import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.tree.Location;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestEnvironnementExp {



    @Test
    public void testEnvironementExp() throws EnvironmentExp.DoubleDefException {
        SymbolTable table = new SymbolTable();
        Symbol a = table.create("a");
        Symbol b = table.create("b");
        Symbol c = table.create("c");

        EnvironmentExp racine = new EnvironmentExp(null);
        Location r = new Location(0,0,"test");
        EnvironmentExp fils = new EnvironmentExp(racine);
        Location f = new Location(1,0,"test");
        EnvironmentExp sousFils = new EnvironmentExp(fils);
        Location sf = new Location(2,0,"test");

        ParamDefinition aIntR = new ParamDefinition(new IntType(a), r);
        ParamDefinition bFloatR = new ParamDefinition(new FloatType(b), r);
        racine.declare(a, aIntR);
        racine.declare(b, bFloatR);

        // Test insertion et get
        assertEquals(racine.get(a), aIntR);
        assertEquals(racine.get(b), bFloatR);

        ParamDefinition aIntF = new ParamDefinition(new IntType(a), f);
        fils.declare(a, aIntF);

        // Test empilement
        assertEquals(fils.get(a), aIntF);
        assertEquals(racine.get(a), aIntR);
        assertEquals(fils.get(b), bFloatR);

        // Test double insertion
        assertThrows(DoubleDefException.class, () -> fils.declare(a, aIntF));

        ParamDefinition cFloatSF = new ParamDefinition(new FloatType(c), sf);
        sousFils.declare(c, cFloatSF);

        // Test non declared variable
        assertEquals(fils.get(c), null);

        // Test empilement 2 Environement above.
        assertEquals(sousFils.get(b), bFloatR);

    }
}
