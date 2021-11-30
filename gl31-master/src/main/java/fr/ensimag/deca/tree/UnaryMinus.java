package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenUtils;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.Register;

/**
 * @author gl31
 * @date 01/01/2021
 */
public class UnaryMinus extends AbstractUnaryExpr {

    public UnaryMinus(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {

        Type operandType = getOperand().verifyExpr(compiler, localEnv, currentClass);
        if (operandType.isInt() || operandType.isFloat()){
            setType(operandType);
            return operandType;
        } else {
            throw new ContextualError("UnaryMinus takes a float or an int. Given: " + operandType + " [3.62] ", getLocation());
        }
    }
    
    @Override
    protected void codeGenExpr(DecacCompiler compiler){
        if(GenUtils.dval(this.getOperand()) != null){
            compiler.addInstruction(GenUtils.mnemo(this, GenUtils.dval(this.getOperand()), compiler.reg.getCurrentRegId()));
        } else {
            this.getOperand().codeGenExpr(compiler);
            compiler.addInstruction(GenUtils.mnemo(this, Register.getR(compiler.reg.getCurrentRegId()), compiler.reg.getCurrentRegId()));
        }
    }


    @Override
    protected String getOperatorName() {
        return "-";
    }

}