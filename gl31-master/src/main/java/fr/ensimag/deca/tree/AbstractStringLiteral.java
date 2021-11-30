package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;

/**
 *
 * @author gl31
 * @date 01/01/2021
 */
public abstract class AbstractStringLiteral extends AbstractExpr {

    public abstract String getValue();

    @Override
    public void codeGenCondition(DecacCompiler compiler, Boolean bool, Label elseLabel) {}
    
    @Override
    protected void codeGenExpr(DecacCompiler compiler){}
    
}
