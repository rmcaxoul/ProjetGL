package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Parameter of method declaration declaration
 *
 * @author gl31
 * @date 15/01/2021
 *
 */
public abstract class AbstractDeclParam extends Tree {
    /**
     * Verifies the parameters used inside methods
     *
     * @param compiler
     * @param localEnv
     * @param currentClass
     * @return
     * @throws ContextualError
     */

    protected abstract Type verifyDeclParam(DecacCompiler compiler,
                                          EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError;
    
    protected abstract void codeGenParam(DecacCompiler compiler, int indiceParam);
}
