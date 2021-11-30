package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.GenUtils;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tree.ConvFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;

/**
 *
 * @author gl31
 * @date 01/01/2021
 */
public abstract class AbstractOpCmp extends AbstractBinaryExpr {

    public AbstractOpCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {

        getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        getRightOperand().verifyExpr(compiler, localEnv, currentClass);

        Type t1 = getLeftOperand().getType();
        Type t2 = getRightOperand().getType();

        if ((t1.isInt()||t1.isFloat())&&(t2.isInt()||t2.isFloat())) {
            setType(compiler.envType.get(DecacCompiler.symbolTable.get("boolean")).getType());
            if (!(t1.isInt() && t2.isInt()) ) {
                if (t1.isInt()) {
                    ConvFloat f = new ConvFloat(getLeftOperand());
                    f.verifyExpr(compiler, localEnv, currentClass);
                    setLeftOperand(f);
                } else if (t2.isInt()){
                    ConvFloat f = new ConvFloat(getRightOperand());
                    f.verifyExpr(compiler, localEnv, currentClass);
                    setRightOperand(f);
                }}
                return getType();
        }
        if (getOperatorName().equals("==") || getOperatorName().equals("!=")) {
            setType(compiler.envType.get(DecacCompiler.symbolTable.get("boolean")).getType());
            if (t1.isClassOrNull()||t2.isClassOrNull()) {
                return getType();
            }
            if ((t1.isBoolean()) && t2.isBoolean()){
                return getType();
            }
        }
        throw new ContextualError("Incorrect type for compare, given " + getRightOperand().getType() + " " + getLeftOperand().getType() + " [3.54-3.61]", getLocation());
    }

    @Override
    public void codeGenExpr(DecacCompiler compiler){
        // Calculate both expressions
        getLeftOperand().codeGenExpr(compiler);
        // If 2 register are accesible
        if (compiler.reg.getCurrentRegId() < compiler.reg.getNbRegister() - 1) {
            if (GenUtils.dval(getRightOperand()) != null){
                compiler.addInstruction(new CMP(GenUtils.dval(getRightOperand()), Register.getR(compiler.reg.getCurrentRegId())));
            } else {
                compiler.reg.incrementCurRegId();
                getRightOperand().codeGenExpr(compiler);
                compiler.addInstruction(new CMP(Register.getR(compiler.reg.getCurrentRegId()), Register.getR(compiler.reg.getCurrentRegId() - 1)));
                compiler.reg.decrementCurRegId();
            }

        } else {
            //PUSH Rn
            compiler.pile.incrementTSTO();
            compiler.addInstruction(new PUSH(Register.getR(compiler.reg.getCurrentRegId())));
            //<codeExp(e2, n)>
            getRightOperand().codeGenExpr(compiler);
            //LOAD Rn, R0
            compiler.addInstruction(new LOAD(Register.getR(compiler.reg.getCurrentRegId()), Register.getR(0)));
            //POP Rn
            compiler.addInstruction(new POP(Register.getR(compiler.reg.getCurrentRegId())));
            //<mnemo(op)> R0, Rn
            compiler.addInstruction(new CMP(Register.getR(0), Register.getR(compiler.reg.getCurrentRegId())));
            //Increase TSTO
            compiler.pile.incrementTSTO();
        }
    }

}
