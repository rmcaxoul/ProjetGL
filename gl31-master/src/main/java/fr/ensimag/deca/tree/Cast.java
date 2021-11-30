package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenUtils;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.ima.pseudocode.instructions.INT;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

/**
 * Cast object
 *
 * @author gl31
 * @date 15/01/2021
 *
 */

public class Cast extends AbstractExpr {

    private AbstractIdentifier type;
    private AbstractExpr expr;

    public Cast(AbstractIdentifier type, AbstractExpr expr){
        Validate.notNull(type);
        Validate.notNull(expr);
        this.expr = expr;
        this.type = type;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        Type typeExpr = expr.verifyExpr(compiler, localEnv, currentClass);
        type.verifyType(compiler);
        if (Type.castCompatible(typeExpr, type.getType())){
            setType(type.getType());
            return getType();
        }
        throw new ContextualError("Type " + typeExpr.getName().getName() + " not castable into type " + type.getType().getName(), getLocation());
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }
    
    @Override
    protected void codeGenExpr(DecacCompiler compiler) {
        GPRegister reg = Register.getR(compiler.reg.getCurrentRegId());
        if(type.getType().isBoolean()){
            if (expr.getType().isBoolean()){
                expr.codeGenExpr(compiler);
            }
        } else if(type.getType().isFloat()){
            if (expr.getType().isFloat()){
                expr.codeGenExpr(compiler);
            } else if (expr.getType().isInt()){
                expr.codeGenExpr(compiler);
                compiler.addInstruction(new FLOAT(reg, reg));
            }
        } else if (type.getType().isInt()) {
            if (expr.getType().isInt()) {
                expr.codeGenExpr(compiler);
            } else if (expr.getType().isFloat()) {
                expr.codeGenExpr(compiler);
                compiler.addInstruction(new INT(reg, reg));
            }
        } else if (expr.getType().isNull()){
            expr.codeGenExpr(compiler);
        } else {
            compiler.putLabel("casting." + compiler.pile.getCastId());
            InstanceOf instanceOfTest = new InstanceOf(expr, type);
            instanceOfTest.codeGenCondition(compiler, true, compiler.getLabel("casting." + compiler.pile.getCastId()));
            GenUtils.setErrorLabelBRA(compiler, "cast_error");
            compiler.addLabel(compiler.getLabel("casting." + compiler.pile.getCastId()));
            compiler.pile.incCastId();
            expr.codeGenExpr(compiler);
        }
    }
    
    @Override
    protected void codeGenCondition(DecacCompiler compiler, Boolean bool, Label elseLabel) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        type.decompile(s);
        s.print(") ");
        s.print("(");
        expr.decompile(s);
        s.print(")");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        type.iter(f);
        expr.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        expr.prettyPrint(s, prefix, true);
    }

}
