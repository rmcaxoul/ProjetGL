package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tree.Location;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestOpDomaineAttributs {

    @Test
    public void assignCompatible(){
        EnvironnementType envType = new EnvironnementType();

        Type intType = envType.get(DecacCompiler.symbolTable.create("int")).getType();
        Type floatType = envType.get(DecacCompiler.symbolTable.get("float")).getType();

        assertTrue(Type.assignCompatible(floatType, intType));

    }

    @Test
    public void castCompatible(){
        EnvironnementType envType = new EnvironnementType();

        Type intType = envType.get(DecacCompiler.symbolTable.get("int")).getType();
        Type floatType = envType.get(DecacCompiler.symbolTable.get("float")).getType();
        Type voidType = envType.get(DecacCompiler.symbolTable.get("void")).getType();

        assertTrue(Type.castCompatible(floatType, intType));
        assertTrue(Type.castCompatible(intType, floatType));
        assertFalse(Type.castCompatible(voidType, floatType));
        assertFalse(Type.castCompatible(voidType, intType));
        assertFalse(Type.castCompatible(floatType, voidType));
        assertFalse(Type.castCompatible(intType, voidType));
    }

    @Test
    public void subType(){
        EnvironnementType envType = new EnvironnementType();

        Type intType = envType.get(DecacCompiler.symbolTable.get("int")).getType();
        Type floatType = envType.get(DecacCompiler.symbolTable.get("float")).getType();
        Type voidType = envType.get(DecacCompiler.symbolTable.get("void")).getType();
        Type nullType = envType.get(DecacCompiler.symbolTable.get("null")).getType();

        assertTrue(Type.subType(intType, intType));
        assertTrue(Type.subType(floatType, floatType));
        assertFalse(Type.subType(intType, floatType));
        assertFalse(Type.subType(floatType, intType));
        assertTrue(Type.subType(nullType, voidType));
        assertTrue(Type.subType(nullType, intType));
    }

}
