package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import org.apache.commons.lang.Validate;

/**
 * Deca Identifier
 *
 * @author gl31
 * @date 01/01/2021
 */
public class Identifier extends AbstractIdentifier {
    
    @Override
    protected void checkDecoration() {
        if (getDefinition() == null) {
            throw new DecacInternalError("Identifier " + this.getName() + " has no attached Definition");
        }
    }

    @Override
    public Definition getDefinition() {
        return definition;
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * ClassDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a class definition.
     */
    @Override
    public ClassDefinition getClassDefinition() {
        try {
            return (ClassDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a class identifier, you can't call getClassDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * MethodDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a method definition.
     */
    @Override
    public MethodDefinition getMethodDefinition() {
        try {
            return (MethodDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a method identifier, you can't call getMethodDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * FieldDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a field definition.
     */
    @Override
    public FieldDefinition getFieldDefinition() {
        try {
            return (FieldDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a field identifier, you can't call getFieldDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * VariableDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a field definition.
     */
    @Override
    public VariableDefinition getVariableDefinition() {
        try {
            return (VariableDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a variable identifier, you can't call getVariableDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a ExpDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a field definition.
     */
    @Override
    public ExpDefinition getExpDefinition() {
        try {
            return (ExpDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a Exp identifier, you can't call getExpDefinition on it");
        }
    }

    @Override
    public void setDefinition(Definition definition) {
        this.definition = definition;
    }

    @Override
    public Symbol getName() {
        return name;
    }

    private Symbol name;

    public Identifier(Symbol name) {
        Validate.notNull(name);
        this.name = name;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        if (localEnv.get(name) != null) {
            Definition def = localEnv.get(name);
            setDefinition(def);
            setType(def.getType());
            return def.getType();
        } else {
            throw new ContextualError("Identifier doesn't correspond to any expr, [0.1]", getLocation());
        }
    }

    /**
     * Implements non-terminal "type" of [SyntaxeContextuelle] in the 3 passes
     * @param compiler contains "env_types" attribute
     */
    @Override
    public Type verifyType(DecacCompiler compiler) throws ContextualError {
        if (compiler.envType.get(name) != null) {
            Definition def = compiler.envType.get(getName());
            setDefinition(def);
            setType(def.getType());
            return def.getType();
        } else {
            throw new ContextualError("Identifier doesn't correspond to any Type, [0.2]", getLocation());
        }
    }
    
    
    private Definition definition;
    
    @Override
    protected void codeGenExpr(DecacCompiler compiler){
        Definition def = getDefinition();

        if (def.isField()){
            // On charge l'instance
            RegisterOffset instanceAddr = new RegisterOffset(-2, Register.LB);
            compiler.addInstruction(new LOAD(instanceAddr, Register.getR(compiler.reg.getCurrentRegId())));
            // On charge le champ
            RegisterOffset fieldAddr = new RegisterOffset(getFieldDefinition().getIndex(), Register.getR(compiler.reg.getCurrentRegId()));
            compiler.addInstruction(new LOAD(fieldAddr,Register.getR(compiler.reg.getCurrentRegId())));
        } else {
            // C'est une variable locale.
            compiler.addInstruction(new LOAD(this.getExpDefinition().getOperand(), Register.getR(compiler.reg.getCurrentRegId())));
        }
    }

    /**
     * Set the
     * @param compiler
     */
    @Override
    public void codeGenDeclVar(DecacCompiler compiler){
        Definition def = getDefinition();
        if (def.isExpression()){
            getExpDefinition().setOperand(new RegisterOffset(compiler.pile.getOffset(), Register.GB));
            compiler.pile.incOffset();
        }
    }

    @Override
    public void codeGenDeclVarMeth(DecacCompiler compiler){
        Definition def = getDefinition();
        if (def.isExpression()){
            getExpDefinition().setOperand(new RegisterOffset(compiler.pile.getOffsetLB(), Register.LB));
            compiler.pile.incOffsetLB();
        }
    }

    /**
     * Instruction to print the identifier value if it's a variable.
     * @param compiler
     */
    public void codeGenPrint(DecacCompiler compiler) {
        Definition def = getDefinition();
        if (def.isExpression()) {
            DAddr addr = getExpDefinition().getOperand();
            compiler.addInstruction(new LOAD(addr, Register.R1));
        }
    }


    @Override
    public void codeGenCondition(DecacCompiler compiler, Boolean bool, Label elseLabel){
        compiler.addInstruction(new LOAD(getExpDefinition().getOperand(), Register.R0));
        if (bool) {
            compiler.addInstruction(new BNE(elseLabel));
        } else {
            compiler.addInstruction(new BEQ(elseLabel));
        }
    }
    
    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(name.toString());
    }

    @Override
    String prettyPrintNode() {
        return "Identifier (" + getName() + ")";
    }

    @Override
    protected void prettyPrintType(PrintStream s, String prefix) {
        Definition d = getDefinition();
        if (d != null) {
            s.print(prefix);
            s.print("definition: ");
            s.print(d);
            s.println();
        }
    }

}
