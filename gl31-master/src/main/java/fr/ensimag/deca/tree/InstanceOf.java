package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

/**
 * InstanceOf object
 *
 * @author gl31
 * @date 15/01/2021
 *
 */

public class InstanceOf extends AbstractExpr {
    private AbstractExpr expr;
    private AbstractIdentifier type;

    public InstanceOf(AbstractExpr expr, AbstractIdentifier type){
        Validate.notNull(expr);
        Validate.notNull(type);
        this.expr = expr;
        this.type = type;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        Type typeClass = type.verifyType(compiler);
        Type typeExpr = expr.verifyExpr(compiler, localEnv, currentClass);
        Type booleanType = compiler.envType.get(DecacCompiler.symbolTable.get("boolean")).getType();
        setType(booleanType);
        return booleanType;

        // On veut vérifier en Deca, pas en java.
        //if (Type.castCompatible(typeExpr, typeClass) && Type.instanceOf(typeExpr, typeClass)) {
        //    Type booleanType = compiler.envType.get(DecacCompiler.symbolTable.get("boolean")).getType();
        //    setType(booleanType);
        //    return booleanType;
        //}
        //throw new ContextualError(typeExpr + " Cannot be cast into " + typeClass,getLocation());
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        codeGenExpr(compiler);
    }
    
    @Override
    protected void codeGenExpr(DecacCompiler compiler) {
        // a instanceOf B
        // on charge l'adresse dy Type B
        compiler.addInstruction(new LEA(type.getClassDefinition().getOperand(), Register.R0));


        //charger Adresse du Type de a
        GPRegister currentReg = Register.getR(compiler.reg.getCurrentRegId());
        expr.codeGenExpr(compiler);
        compiler.addInstruction(new LOAD(new RegisterOffset(0, currentReg), currentReg));


        compiler.putLabel("InstanceOf.start"+compiler.pile.getInstanceOfId());
        compiler.putLabel("InstanceOf.false"+compiler.pile.getInstanceOfId());
        compiler.putLabel("InstanceOf.true"+compiler.pile.getInstanceOfId());
        compiler.putLabel("InstanceOf.fin"+compiler.pile.getInstanceOfId());


        compiler.addLabel(compiler.getLabel("InstanceOf.start"+compiler.pile.getInstanceOfId()));

        // Chargement de l'adresse de L'objet de a.
        // Comparaison du type de a à B
        compiler.addInstruction(new CMP(Register.R0, currentReg));
        // Si égal, on sort, c'est bon
        compiler.addInstruction(new BEQ(compiler.getLabel("InstanceOf.true" + compiler.pile.getInstanceOfId())));

        // Si le type est rendu à Null, on a remonté toute la table
        compiler.addInstruction(new CMP(new NullOperand(), currentReg));
        compiler.addInstruction(new BEQ(compiler.getLabel("InstanceOf.false" + compiler.pile.getInstanceOfId())));

        compiler.addInstruction(new LOAD(new RegisterOffset(0, currentReg), currentReg));

        // On recommence
        compiler.addInstruction(new BRA(compiler.getLabel("InstanceOf.start" + compiler.pile.getInstanceOfId())));



        compiler.addLabel(compiler.getLabel("InstanceOf.false"+compiler.pile.getInstanceOfId()));
        compiler.addInstruction(new LOAD(new ImmediateInteger(0), currentReg));
        compiler.addInstruction(new BRA(compiler.getLabel("InstanceOf.fin"+compiler.pile.getInstanceOfId())));


        compiler.addLabel(compiler.getLabel("InstanceOf.true"+compiler.pile.getInstanceOfId()));
        compiler.addInstruction(new LOAD(new ImmediateInteger(1), currentReg));


        compiler.addLabel(compiler.getLabel("InstanceOf.fin"+compiler.pile.getInstanceOfId()));
        compiler.pile.incInstanceOfId();
    }
    
    @Override
    protected void codeGenCondition(DecacCompiler compiler, Boolean bool, Label elseLabel) {
        codeGenExpr(compiler);
        if (!bool){
            compiler.addInstruction(new BEQ(elseLabel));
        } else {
            compiler.addInstruction(new BNE(elseLabel));

        }

    }

    @Override
    public void decompile(IndentPrintStream s) {
        expr.decompile(s);
        s.print(" instanceof ");
        type.decompile(s);
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        expr.iter(f);
        type.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expr.prettyPrint(s, prefix, false);
        type.prettyPrint(s, prefix, true);
    }
}
