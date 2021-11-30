package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;

/**
 *
 * @author gl31
 * @date 01/01/2021
 */
public class Or extends AbstractOpBool {

    public Or(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "||";
    }


    @Override
    public void codeGenCondition(DecacCompiler compiler, Boolean bool, Label elseLabel){
        AbstractExpr expr = new Not(new And(new Not(getLeftOperand()), new Not(getRightOperand())));
        expr.codeGenCondition(compiler, bool, elseLabel);
    }

}
