package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.*;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

/**
 * Expression, i.e. anything that has a value.
 *
 * @author gl31
 * @date 01/01/2021
 */
public abstract class AbstractExpr extends AbstractInst {
    /**
     * @return true if the expression does not correspond to any concrete token
     * in the source code (and should be decompiled to the empty string).
     */
    boolean isImplicit() {
        return false;
    }

    /**
     * Get the type decoration associated to this expression (i.e. the type computed by contextual verification).
     */
    public Type getType() {
        return type;
    }

    protected void setType(Type type) {
        Validate.notNull(type);
        this.type = type;
    }
    private Type type;

    @Override
    protected void checkDecoration() {
        if (getType() == null) {
            throw new DecacInternalError("Expression " + decompile() + " has no Type decoration"); }

    }


    /**
     * Verify the expression for contextual error.
     *
     * implements non-terminals "expr" and "lvalue"
     *    of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler  (contains the "env_types" attribute)
     * @param localEnv
     *            Environment in which the expression should be checked
     *            (corresponds to the "env_exp" attribute)
     * @param currentClass
     *            Definition of the class containing the expression
     *            (corresponds to the "class" attribute)
     *             is null in the main bloc.
     * @return the Type of the expression
     *            (corresponds to the "type" attribute)
     */
    public abstract Type verifyExpr(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError;

    /**
     * Verify the expression in right hand-side of (implicit) assignments
     *
     * implements non-terminal "rvalue" of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler  contains the "env_types" attribute
     * @param localEnv corresponds to the "env_exp" attribute
     * @param currentClass corresponds to the "class" attribute
     * @param expectedType corresponds to the "type1" attribute
     * @return this with an additional ConvFloat if needed...
     */
    public AbstractExpr verifyRValue(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass,
            Type expectedType)
            throws ContextualError {

        Type type2 = verifyExpr(compiler, localEnv, currentClass);
        setType(type2);
        
        if (!type2.sameType(expectedType) && Type.assignCompatible(expectedType, type2)) {
            if (type2.isInt() && expectedType.isFloat()) {
                ConvFloat flo = new ConvFloat(this);
                setType(flo.verifyExpr(compiler, localEnv, currentClass));
                return flo;
            } else {
                return this;
            }
        } else if (type2.sameType(expectedType)) {
            return this;
        }
        else{
            throw new ContextualError("Type: " + type2 + " cannot be cast into " + expectedType + " [3.28]", getLocation());
        }
    }


    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        Type t = verifyExpr(compiler, localEnv, currentClass);
        setType(t);
    }

    /**
     * Verify the expression as a condition, i.e. check that the type is
     * boolean.
     *
     * @param localEnv
     *            Environment in which the condition should be checked.
     * @param currentClass
     *            Definition of the class containing the expression, or null in
     *            the main program.
     */
    void verifyCondition(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type typeExpr = verifyExpr(compiler, localEnv, currentClass);
        if (!typeExpr.isBoolean()){
            throw new ContextualError("Condition must be boolean [3.29]", getLocation());
        }
    }

    /**
     * Generate code to print the expression
     *
     * @param compiler
     */
    protected void codeGenPrint(DecacCompiler compiler) {
        compiler.reg.setCurrentRegId(1);
        codeGenExpr(compiler);
    }

    /**
     * Generate the code of the expression, used as an instruction.
     * So we reset the initRegister to 2.
     * @param compiler
     */
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        compiler.reg.resetCurrentRegId();
        codeGenExpr(compiler);
        compiler.reg.resetCurrentRegId();
    }

    /**
     * Generate the code of the expression.
     * @param compiler
     */
    abstract protected void codeGenExpr(DecacCompiler compiler);

    /**
     * Generate the code, if the expression is used as a condition
     * @param compiler
     * @param bool
     * @param elseLabel
     */
    protected abstract void codeGenCondition(DecacCompiler compiler, Boolean bool, Label elseLabel);

    @Override
    protected void decompileInst(IndentPrintStream s) {
        decompile(s);
        s.print(";");
    }

    @Override
    protected void prettyPrintType(PrintStream s, String prefix) {
        Type t = getType();
        if (t != null) {
            s.print(prefix);
            s.print("type: ");
            s.print(t);
            s.println();
        }
    }
}
