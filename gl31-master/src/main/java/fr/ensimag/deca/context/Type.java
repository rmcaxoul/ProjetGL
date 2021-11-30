package fr.ensimag.deca.context;

import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.Location;

/**
 * Deca Type (internal representation of the compiler)
 *
 * @author gl31
 * @date 01/01/2021
 */

public abstract class Type {


    /**
     * True if this and otherType represent the same type (in the case of
     * classes, this means they represent the same class).
     */
    public abstract boolean sameType(Type otherType);

    private final Symbol name;

    public Type(Symbol name) {
        this.name = name;
    }

    public Symbol getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName().toString();
    }

    public boolean isClass() {
        return false;
    }

    public boolean isInt() {
        return false;
    }

    public boolean isFloat() {
        return false;
    }

    public boolean isBoolean() {
        return false;
    }

    public boolean isVoid() {
        return false;
    }

    public boolean isString() {
        return false;
    }

    public boolean isNull() {
        return false;
    }

    public boolean isClassOrNull() {
        return false;
    }

    /**
     * Returns the same object, as type ClassType, if possible. Throws
     * ContextualError(errorMessage, l) otherwise.
     *
     * Can be seen as a cast, but throws an explicit contextual error when the
     * cast fails.
     */
    public ClassType asClassType(String errorMessage, Location l)
            throws ContextualError {
        try {
            return (ClassType) this;
        } catch(ClassCastException e) {
            throw new ContextualError("Le type: " + this + " n'est pas une classe.", l);
        }
    }

    public static boolean subType(Type T1, Type T2){
        if (T1.isNull() || T1.sameType(T2)){
            return true;
        }
        if (T2.isClass()){
            if (T2.getName().getName().equals("Object")){
                return true;
            }
            if (T1.isClass()){
                return ((ClassType) T1).isSubClassOf((ClassType) T2);
            }
        } else if (T1.isInt() && T2.isFloat()){
            return true;
        }
        return false;
    }

    public static boolean assignCompatible(Type T1,Type T2){
        if (T1.isFloat() && T2.isInt()){
            return true;
        }
        return subType(T2, T1);
    }

    public static boolean castCompatible(Type T1, Type T2){
        return !T1.isVoid() && (assignCompatible(T1, T2)|| assignCompatible(T2, T1));
    }

    public  static boolean instanceOf(Type T1, Type T2){
        return (T1.isClassOrNull() && T2.isClass());
    }

}
