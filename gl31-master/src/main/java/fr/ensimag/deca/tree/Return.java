package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

public class Return extends AbstractInst{

    private AbstractExpr expr;

    public Return(AbstractExpr expr){
        Validate.notNull(expr);
        this.expr = expr;
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
                              ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        Type exprType = expr.verifyExpr(compiler, localEnv, currentClass);
        if (exprType.isVoid()){
            throw new ContextualError("Method is not of Type void and as no return.", getLocation());
        }
        if (exprType.isInt() && returnType.isFloat()){
            expr = new ConvFloat(expr);
            expr.verifyExpr(compiler, localEnv, currentClass);

        } else if (returnType.isInt() && exprType.isFloat()){
            throw new ContextualError("Return type: " + returnType.toString() + " given: "+exprType.toString(), getLocation());
        }
        else if (!exprType.sameType(returnType) && !Type.castCompatible(returnType, exprType)){
            throw new ContextualError("Return type: " + returnType.toString() + " given: "+exprType.toString(), getLocation());
        }

    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        
        int oldReg = compiler.reg.getCurrentRegId();
        compiler.reg.setCurrentRegId(0);
        this.expr.codeGenExpr(compiler);
        compiler.reg.setCurrentRegId(oldReg);
        compiler.addInstruction(new BRA(compiler.currentMethod));
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("return ");
        expr.decompile(s);
        s.print(";");
    }
    @Override
    protected void iterChildren(TreeFunction f) {
        expr.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expr.prettyPrint(s, prefix, true);
    }


}
