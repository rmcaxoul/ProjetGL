package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.Location;

import java.util.HashMap;
import java.util.Map;

/**
 * Dictionary associating identifier's TypeDefinition to their names.
 *
 * @author gl31
 * @date 01/01/2021
 */
public class EnvironnementType {
    // A FAIRE : implémenter la structure de donnée représentant un
    // environnement (association nom -> définition, avec possibilité
    // d'empilement).
    HashMap<Symbol, TypeDefinition> envType;

    public EnvironnementType() {

        envType = new HashMap<Symbol, TypeDefinition>();
        setEnvTypePredef();
    }

    public static class DoubleDefException extends Exception {
        private static final long serialVersionUID = -2733379901827316441L;
    }

    /**
     * Initialize the Type environnement
     */
    public void setEnvTypePredef() {

        Type intType = new IntType(DecacCompiler.symbolTable.create("int"));
        Type floatType = new FloatType(DecacCompiler.symbolTable.create("float"));
        Type booleanType = new BooleanType(DecacCompiler.symbolTable.create("boolean"));
        Type nullType = new NullType(DecacCompiler.symbolTable.create("null"));
        Type voidType = new VoidType(DecacCompiler.symbolTable.create("void"));
        ClassType objectType = new ClassType(DecacCompiler.symbolTable.create("Object"), Location.BUILTIN, null);

        //Type objectType = new ClassType(DecacCompiler.symbolTable.create("Object"));

        TypeDefinition booleanTypeDef = new TypeDefinition(booleanType, Location.BUILTIN);
        TypeDefinition floatTypeDef = new TypeDefinition(floatType, Location.BUILTIN);
        TypeDefinition intTypeDef = new TypeDefinition(intType, Location.BUILTIN);
        TypeDefinition nullTypeDef = new TypeDefinition(nullType, Location.BUILTIN);
        TypeDefinition voidTypeDef = new TypeDefinition(voidType, Location.BUILTIN);
        //TypeDefinition objectTypeDef = new TypeDefinition(objectType, Location.BUILTIN);


        Type stringType = new StringType(DecacCompiler.symbolTable.create("string"));
        TypeDefinition stringTypeDef = new TypeDefinition(stringType, Location.BUILTIN);

        try {
            declare(DecacCompiler.symbolTable.get("int"), intTypeDef);
            declare(DecacCompiler.symbolTable.get("float"), floatTypeDef);
            declare(DecacCompiler.symbolTable.get("boolean"), booleanTypeDef);
            declare(DecacCompiler.symbolTable.get("null"), nullTypeDef);
            declare(DecacCompiler.symbolTable.get("void"), voidTypeDef);
            declare(DecacCompiler.symbolTable.get("Object"),objectType.getDefinition());

            declare(DecacCompiler.symbolTable.get("string"), stringTypeDef);

            //declare(DecacCompiler.symbolTable.get("Object"), objectTypeDef);
        } catch (EnvironmentExp.DoubleDefException e){
            throw new DecacInternalError("EnvType as already been predefined: " + e);
        }
    }

    /**
     * Return the definition of the symbol in the environment, or null if the
     * symbol is undefined.
     */
    public TypeDefinition get(Symbol key) {
        if (envType.containsKey(key)) {
            return envType.get(key);
        }
        return null;
    }

    /**
     * Add the definition def associated to the symbol name in the environment.
     *
     * Adding a symbol which is already defined in the environment,
     * - throws DoubleDefException if the symbol is in the dictionary
     *
     * @param name
     *            Name of the symbol to define
     * @param def
     *            Definition of the symbol
     * @throws EnvironmentExp.DoubleDefException
     *             if the symbol is already defined at the "current" dictionary
     *
     */
    public void declare(Symbol name, TypeDefinition def) throws EnvironmentExp.DoubleDefException {
        if (envType.containsKey(name)){
            throw new EnvironmentExp.DoubleDefException();
        }
        else{
            envType.put(name, def);
        }
    }
}