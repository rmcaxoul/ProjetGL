package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BGT;
import fr.ensimag.ima.pseudocode.instructions.BLE;

/**
 *
 * @author gl31
 * @date 01/01/2021
 */
public class Greater extends AbstractOpIneq {

    public Greater(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return ">";
    }


    @Override
    public  void codeGenCondition(DecacCompiler compiler, Boolean bool, Label elseLabel){
        // Generate the Comparison Code from AbstractOpCmp
        codeGenExpr(compiler);
        if (!bool){
            compiler.addInstruction(new BLE(elseLabel));
        } else {
            compiler.addInstruction(new BGT(elseLabel));

        }
    }
}
