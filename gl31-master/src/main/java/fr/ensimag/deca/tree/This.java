package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

import java.io.PrintStream;

public class This extends AbstractExpr {

    public This(){}

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        if (currentClass == null){
            throw new ContextualError("Not inside the body of a class.", getLocation());
        }
        setType(currentClass.getType());
        return currentClass.getType();
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }
    
    @Override
    protected void codeGenExpr(DecacCompiler compiler) {
        RegisterOffset thisAddr = new RegisterOffset(-2 , Register.LB);
        compiler.addInstruction(new LOAD(thisAddr, Register.getR(compiler.reg.getCurrentRegId())));
    }
    
    @Override
    protected void codeGenCondition(DecacCompiler compiler, Boolean bool, Label elseLabel) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("this");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {}

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {}

}