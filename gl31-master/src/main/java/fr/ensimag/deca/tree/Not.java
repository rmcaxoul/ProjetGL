package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 *
 * @author gl31
 * @date 01/01/2021
 */
public class Not extends AbstractUnaryExpr {

    public Not(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {

        Type operandType = getOperand().verifyExpr(compiler, localEnv, currentClass);
        if (operandType.isBoolean()){
            setType(operandType);
            return operandType;
        } else {
            throw new ContextualError("Not takes a boolean,given: " + operandType + " [3.63] ", getLocation());
        }
    }
    
    @Override
    protected void codeGenExpr(DecacCompiler compiler){
        this.getOperand().codeGenExpr(compiler);
        compiler.addInstruction(new CMP(new ImmediateInteger(0), Register.getR(compiler.reg.getCurrentRegId())));
        compiler.addInstruction(new SEQ(Register.getR(compiler.reg.getCurrentRegId())));
    }

    @Override
    public void codeGenCondition(DecacCompiler compiler, Boolean bool, Label elseLabel){
        getOperand().codeGenCondition(compiler, !bool, elseLabel);
    }


    @Override
    protected String getOperatorName() {
        return "!";
    }
}
