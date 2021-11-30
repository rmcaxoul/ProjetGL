package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Method declaration
 *
 * @author gl31
 * @date 15/01/2021
 *
 */

public abstract class AbstractDeclMeth extends Tree {
    /**
     * Pass 2 of [SyntaxeContextuelle]. Verify that the class members (fields and
     * methods) are OK, without looking at method body and field initialization.
     */
    protected abstract void verifyDeclMeth(DecacCompiler compiler,
                                            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError;

    /**
     * Pass 3 of [SyntaxeContextuelle]. Verify that instructions and expressions
     * contained in the class are OK.
     */
    protected abstract void verifyDeclMethBody(DecacCompiler compiler,
                                               EnvironmentExp localEnv, ClassDefinition currentClass)
        throws  ContextualError;
    
    protected abstract void codeGenTab(DecacCompiler compiler, ClassDefinition currentClass);
    
    protected abstract void codeGenMeth(DecacCompiler compiler, ClassDefinition currentClass);

}
