package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenUtils;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;

/**
 * Conversion of an int into a float. Used for implicit conversions.
 * 
 * @author gl31
 * @date 01/01/2021
 */
public class ConvFloat extends AbstractUnaryExpr {
    public ConvFloat(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
                           ClassDefinition currentClass) {
        setType(compiler.envType.get(DecacCompiler.symbolTable.get("float")).getType());
        return getType();
    }
    
    @Override
    protected void codeGenExpr(DecacCompiler compiler){
        this.getOperand().codeGenExpr(compiler);
        compiler.addInstruction(new FLOAT(Register.getR(compiler.reg.getCurrentRegId()), Register.getR(compiler.reg.getCurrentRegId())));
        GenUtils.setErrorLabel(compiler, "overflow_error");
    }


    @Override
    protected String getOperatorName() {
        return "/* conv float */";
    }

}
