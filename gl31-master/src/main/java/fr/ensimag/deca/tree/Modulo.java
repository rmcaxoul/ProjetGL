package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 *
 * @author gl31
 * @date 01/01/2021
 */
public class Modulo extends AbstractOpArith {

    public Modulo(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        getRightOperand().verifyExpr(compiler,localEnv, currentClass);
        if (!(getLeftOperand().getType().isInt() && getRightOperand().getType().isInt())){
            throw new ContextualError("Modulo takes only ints, given:" + getLeftOperand() + " " + getRightOperand() + " [3.51]", getLeftOperand().getLocation());
        }
        setType(getLeftOperand().getType());
        return getLeftOperand().getType();
    }

    @Override
    protected String getOperatorName() {
        return "%";
    }

}
