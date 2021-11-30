package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Field declaration
 *
 * @author gl31
 * @date 15/01/2021
 *
 */

public abstract class AbstractDeclField extends Tree {
    Visibility visibility;
    DeclVar declVar;

    /**
     * Verifies the declaration of a field in a class
     * using the class DeclVar
     *
     * @param compiler
     * @param localEnv
     * @param currentClass
     * @throws ContextualError
     */

    protected abstract void verifyDeclField(DecacCompiler compiler,
                                          EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError;

    protected abstract void initField(DecacCompiler compiler, boolean zeroInit);
}
