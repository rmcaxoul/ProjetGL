package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BGE;
import fr.ensimag.ima.pseudocode.instructions.BLT;

/**
 * Operator "x >= y"
 * 
 * @author gl31
 * @date 01/01/2021
 */
public class GreaterOrEqual extends AbstractOpIneq {

    public GreaterOrEqual(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return ">=";
    }

    @Override
    public  void codeGenCondition(DecacCompiler compiler, Boolean bool, Label elseLabel){
        // Generate the Comparison Code from AbstractOpCmp
        codeGenExpr(compiler);
        if (!bool){
            compiler.addInstruction(new BLT(elseLabel));
        } else {
            compiler.addInstruction(new BGE(elseLabel));

        }
    }
}
