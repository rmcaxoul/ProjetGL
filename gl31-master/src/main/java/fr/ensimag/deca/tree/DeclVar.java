package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import org.apache.commons.lang.Validate;

/**
 * @author gl31
 * @date 01/01/2021
 */
public class DeclVar extends AbstractDeclVar {

    
    final private AbstractIdentifier type;
    final private AbstractIdentifier varName;
    final private AbstractInitialization initialization;

    public DeclVar(AbstractIdentifier type, AbstractIdentifier varName, AbstractInitialization initialization) {
        Validate.notNull(type);
        Validate.notNull(varName);
        Validate.notNull(initialization);
        this.type = type;
        this.varName = varName;
        this.initialization = initialization;
    }

    @Override
    protected void verifyDeclVar(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError{

        Type typeVar = type.verifyType(compiler);
        if (typeVar.isVoid()){
            throw new ContextualError("Declaration is of type void [3.17]", getLocation());
        }
        SymbolTable.Symbol name = varName.getName();
        ExpDefinition varDef = new VariableDefinition(typeVar, getLocation());
        try {
            localEnv.declare(name, varDef);
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new ContextualError("Variable " + name + " already defined : " + e + "[3.17] ", getLocation());
        }

        varName.verifyExpr(compiler, localEnv, currentClass);
        initialization.verifyInitialization(compiler, typeVar, localEnv, currentClass);
        if (typeVar.isString()){
            throw  new ContextualError("Cannot assign string [3.17] ", getLocation());
        }
    }

    @Override
    public void codeGenDeclVar(DecacCompiler compiler){
        varName.codeGenDeclVar(compiler);
        initialization.codeGenDeclVar(compiler);

        if (!(initialization instanceof NoInitialization)) {
            compiler.addInstruction(new STORE(Register.getR(compiler.reg.getInitRegister()), varName.getExpDefinition().getOperand()));
        } else if (varName.getType().isClass()){
            compiler.addInstruction(new LOAD(new NullOperand(), Register.getR(compiler.reg.getCurrentRegId())));
            compiler.addInstruction(new STORE(Register.getR(compiler.reg.getInitRegister()), varName.getExpDefinition().getOperand()));


        }
        //On incremente TSTO car on vient de créer une nouvelle variable
        compiler.pile.incrementTSTO();
    }


    @Override
    public void codeGenDeclVarMeth(DecacCompiler compiler){
        varName.codeGenDeclVarMeth(compiler);
        initialization.codeGenDeclVar(compiler);

        if (!(initialization instanceof NoInitialization)) {
            compiler.addInstruction(new STORE(Register.getR(compiler.reg.getInitRegister()), varName.getExpDefinition().getOperand()));
        } else if (varName.getType().isClass()){
            compiler.addInstruction(new LOAD(new NullOperand(), Register.getR(compiler.reg.getCurrentRegId())));
            compiler.addInstruction(new STORE(Register.getR(compiler.reg.getInitRegister()), varName.getExpDefinition().getOperand()));


        }
        //On incremente TSTO car on vient de créer une nouvelle variable
        compiler.pile.incrementTSTO();
    }

    public AbstractIdentifier getVarName(){
        return varName;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        varName.decompile(s);
        if(!(initialization instanceof NoInitialization)){
            s.print(" = ");
            initialization.decompile(s);
        }
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        type.iter(f);
        varName.iter(f);
        initialization.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        varName.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);
    }
}
