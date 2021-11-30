package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Class declaration.
 *
 * @author gl31
 * @date 01/01/2021
 */
public abstract class AbstractDeclClass extends Tree {
    private ListDeclField declField;
    private ListDeclMeth declMeth;

    protected  ListDeclField getDeclField(){
        return declField;
    }
    protected ListDeclMeth getDeclMeth(){
        return declMeth;
    }
    /**
     * Pass 1 of [SyntaxeContextuelle]. Verify that the class declaration is OK
     * without looking at its content.
     */
    protected abstract void verifyClass(DecacCompiler compiler)
            throws ContextualError;

    /**
     * Pass 2 of [SyntaxeContextuelle]. Verify that the class members (fields and
     * methods) are OK, without looking at method body and field initialization.
     */
    protected abstract void verifyClassMembers(DecacCompiler compiler)
            throws ContextualError;

    /**
     * Pass 3 of [SyntaxeContextuelle]. Verify that instructions and expressions
     * contained in the class are OK.
     */
    protected abstract void verifyClassBody(DecacCompiler compiler)
            throws ContextualError;
    
    /**
     * Pass 1 of [CodeGen]
     */
    protected abstract void codeGenTab(DecacCompiler compiler);
    
    protected abstract void codeGenMeth(DecacCompiler compiler);

    public abstract void initFields(DecacCompiler compiler);
}
