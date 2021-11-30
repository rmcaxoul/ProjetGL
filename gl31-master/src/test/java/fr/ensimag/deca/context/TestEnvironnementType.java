package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tree.Location;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestEnvironnementType {

    // Test multiple definition of same type
    @Test
    public void testPredef() {
        EnvironnementType envType = new EnvironnementType();

        assertThrows(DecacInternalError.class,
                () ->envType.setEnvTypePredef());

        Type intType = new IntType(DecacCompiler.symbolTable.create("int"));
        TypeDefinition intTypeDef = new TypeDefinition(intType, Location.BUILTIN);

        TypeDefinition predefInt = envType.get(DecacCompiler.symbolTable.get("int"));
        assertThrows(EnvironmentExp.DoubleDefException.class,
                () ->envType.declare(DecacCompiler.symbolTable.create(("int")), intTypeDef));

        TypeDefinition newInt = envType.get(DecacCompiler.symbolTable.get("int"));

        assertEquals(predefInt.hashCode(), newInt.hashCode());
}
    @Test
    public void mutltipleTable() {
        EnvironnementType envType1 = new EnvironnementType();
        EnvironnementType envType2 = new EnvironnementType();

        TypeDefinition int1 = envType1.get(DecacCompiler.symbolTable.get("int"));
        TypeDefinition int2 = envType2.get(DecacCompiler.symbolTable.get("int"));

        assertNotEquals(int1.hashCode(), int2.hashCode());
    }
}

