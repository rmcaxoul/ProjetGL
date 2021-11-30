package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BGE;
import fr.ensimag.ima.pseudocode.instructions.BLT;

/**
 *
 * @author gl31
 * @date 01/01/2021
 */
public class Lower extends AbstractOpIneq {

    public Lower(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "<";
    }

    @Override
    public  void codeGenCondition(DecacCompiler compiler, Boolean bool, Label elseLabel){
        // Generate the Comparison code, from AbstractOpCmp
        codeGenExpr(compiler);
        if (!bool){
            compiler.addInstruction(new BGE(elseLabel));
        } else {
            compiler.addInstruction(new BLT(elseLabel));

        }
    }

}
