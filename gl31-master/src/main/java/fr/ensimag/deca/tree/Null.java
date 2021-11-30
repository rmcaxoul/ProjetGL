package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

import java.io.PrintStream;

public class Null extends AbstractExpr {

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        setType(compiler.envType.get(DecacCompiler.symbolTable.get("null")).getType());
        if (this.getType().isNull()){
            return this.getType();
        }
        else{
            throw new ContextualError("Incorrect Int type [3.44] ", this.getLocation());
        }
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        compiler.addInstruction(new LOAD(new NullOperand(), Register.R1));
    }
    
    @Override
    protected void codeGenExpr(DecacCompiler compiler) {
        compiler.addInstruction(new LOAD(new NullOperand(), Register.getR(compiler.reg.getCurrentRegId())));
    }
    
    @Override
    protected void codeGenCondition(DecacCompiler compiler, Boolean bool, Label elseLabel) {
        compiler.addInstruction(new LOAD(new NullOperand(), Register.getR(compiler.reg.getCurrentRegId())));
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("null");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {}

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {}

}


