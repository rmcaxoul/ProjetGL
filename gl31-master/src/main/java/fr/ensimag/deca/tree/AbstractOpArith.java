package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenUtils;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;

/**
 * Arithmetic binary operations (+, -, /, ...)
 * 
 * @author gl31
 * @date 01/01/2021
 */
public abstract class AbstractOpArith extends AbstractBinaryExpr {

    public AbstractOpArith(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {

        this.optim();

        Type t1 = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type t2 = getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        if ((t1.isInt() && t2.isInt()) || (t1.isFloat() && t2.isFloat())){
            setType(t1);
            return t1;
        }
        if (t1.isInt() && t2.isFloat()){
            ConvFloat flo = new ConvFloat(getLeftOperand());
            setLeftOperand(flo);
            getLeftOperand().setType(flo.verifyExpr(compiler, localEnv, currentClass));
            setType(t2);
            return  t2;
        }
        if (t2.isInt() && t1.isFloat()){
            ConvFloat flo = new ConvFloat(getRightOperand());
            setRightOperand(flo);
            getRightOperand().setType(flo.verifyExpr(compiler, localEnv, currentClass));
            setType(t2);
            return t1;
        }
        throw new ContextualError("Incorrect type for arith op [3.33]", getLeftOperand().getLocation());
    }

    /**
     * Les additions et multiplications sont commutatives
     * Si l'opérande de droite est un identifier, inverser les opérands permet de ne pas charger l'identifier dans un
     * registre. On diminue le nombre d'instructions, et le nombre de registres utilisés.
     */
    public void optim(){
        if (getLeftOperand() instanceof Identifier && !(getRightOperand() instanceof Identifier)) {
            if (this.getOperatorName().equals("*") || this.getOperatorName().equals("+")) {
                AbstractExpr rightOperand = getRightOperand();
                setRightOperand(getLeftOperand());
                setLeftOperand(rightOperand);
            }
        }
    }

    @Override
    public void codeGenCondition(DecacCompiler compiler, Boolean bool, Label elseLabel) {}

    @Override
    protected void codeGenExpr(DecacCompiler compiler) {

        //<codeExp(e1, n)>
        this.getLeftOperand().codeGenExpr(compiler);

        if (GenUtils.dval(this.getRightOperand()) != null) {
            //<mnemo(op)> <dval(e2)>, Rn
            compiler.addInstruction(GenUtils.mnemo(this, GenUtils.dval(this.getRightOperand()), compiler.reg.getCurrentRegId()));
        } else if (compiler.reg.getCurrentRegId() < compiler.reg.getNbRegister() - 1) {
            //<codeExp(e2, n + 1)>
            compiler.reg.incrementCurRegId();
            this.getRightOperand().codeGenExpr(compiler);
            //<mnemo(op)> Rn+1, Rn
            compiler.addInstruction(GenUtils.mnemo(this, Register.getR(compiler.reg.getCurrentRegId()), compiler.reg.getCurrentRegId() - 1));
            compiler.reg.decrementCurRegId();

        } else if (compiler.reg.getCurrentRegId() == compiler.reg.getNbRegister()) {
            //PUSH Rn
            compiler.addInstruction(new PUSH(Register.getR(compiler.reg.getCurrentRegId())));
            //<codeExp(e2, n)>
            this.getRightOperand().codeGenExpr(compiler);
            //LOAD Rn, R0
            compiler.addInstruction(new LOAD(Register.getR(compiler.reg.getCurrentRegId()), Register.getR(0)));
            //POP Rn
            compiler.addInstruction(new POP(Register.getR(compiler.reg.getCurrentRegId())));
            //<mnemo(op)> R0, Rn
            compiler.addInstruction(GenUtils.mnemo(this, Register.getR(0), compiler.reg.getCurrentRegId()));

            //Un PUSH/POP est une opération qui incrémente TSTO
            compiler.pile.incrementTSTO();

        }
        if (this.getLeftOperand().getType().isInt() && this.getRightOperand().getType().isInt()) {
            if (this instanceof Divide || this instanceof Modulo) {
                GenUtils.setErrorLabel(compiler, "div_zero_error");
            }
        } else if (this instanceof Divide) {
            GenUtils.setErrorLabel(compiler, "div_zero_error");
            GenUtils.setErrorLabel(compiler, "overflow_error");
        } else {
            GenUtils.setErrorLabel(compiler, "overflow_error");
        }
    }
}
