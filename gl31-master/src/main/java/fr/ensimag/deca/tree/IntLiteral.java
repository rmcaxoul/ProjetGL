package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.Register;

/**
 * Integer literal
 *
 * @author gl31
 * @date 01/01/2021
 */
public class IntLiteral extends AbstractExpr {
    public int getValue() {
        return value;
    }

    private int value;

    public IntLiteral(int value) {
        this.value = value;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        setType(compiler.envType.get(DecacCompiler.symbolTable.get("int")).getType());
        if (this.getType().isInt()){
            return this.getType();
        }
        else{
            throw new ContextualError("Incorrect Int type [3.44] ", this.getLocation());
        }
    }

    @Override
    public void codeGenCondition(DecacCompiler compiler, Boolean bool, Label elseLabel) {
        codeGenExpr(compiler);
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        compiler.addInstruction(new LOAD(new ImmediateInteger(value), Register.R1));
    }

    @Override
    protected  void codeGenExpr(DecacCompiler compiler){
        compiler.addInstruction(new LOAD(new ImmediateInteger(value), Register.getR(compiler.reg.getCurrentRegId())));
    }
    
    @Override
    String prettyPrintNode() {
        return "Int (" + getValue() + ")";
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(Integer.toString(value));
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

}
