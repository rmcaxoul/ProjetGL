package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;

/**
 *
 * @author gl31
 * @date 01/01/2021
 */
public class And extends AbstractOpBool {

    public And(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "&&";
    }

    @Override
    public void codeGenCondition(DecacCompiler compiler, Boolean bool, Label elseLabel) {
        if (bool) {
            Label fin = new Label("Fin." + compiler.pile.getFinId());
            compiler.pile.incFinId();
            getLeftOperand().codeGenCondition(compiler, !bool, fin);
            getRightOperand().codeGenCondition(compiler, bool, elseLabel);
            compiler.addLabel(fin);
        } else {
            getLeftOperand().codeGenCondition(compiler, bool, elseLabel);
            getRightOperand().codeGenCondition(compiler, bool, elseLabel);
        }
    }
}

