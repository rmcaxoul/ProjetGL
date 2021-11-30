package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenUtils;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

public class New extends AbstractExpr {

    private AbstractIdentifier object;

    public New(AbstractIdentifier object){
        Validate.notNull(object);
        this.object = object;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        Type objetType = object.verifyType(compiler);
        
        //On vérifie que la classe est bien une classe valide
        String nom = object.getName().getName();
        if(nom.equals("int") || nom.equals("float") ||
                nom.equals("boolean") || nom.equals("void")
                || nom.equals("string")){
            throw new ContextualError(nom + " is not a class.", getLocation());
        }
        
        
        if (compiler.envType.get(objetType.getName()) != null) {
            setType(objetType);
            return objetType;
        } else {
            throw new ContextualError(object + " is not a class.", getLocation());
        }
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {

        compiler.addComment("Sauvegarde des registres");
        int exRegId = compiler.reg.getCurrentRegId();
        for(int i = 2; i < exRegId; i++){
            compiler.pile.incrementTSTO();
            compiler.addInstruction(new PUSH(Register.getR(i)));
        }
        compiler.reg.resetCurrentRegId();


        ClassDefinition classDef = ((ClassType) compiler.envType.get(object.getName()).getType()).getDefinition();
        compiler.putLabel("init." + classDef.getType().getName());

        int numberOfFields;
        if (classDef.getSuperClass() != null){
            numberOfFields = classDef.getNumberOfFields() + classDef.getSuperClass().getNumberOfFields();
        } else {
            numberOfFields = classDef.getNumberOfFields();
        }

        RegisterOffset regOff = new RegisterOffset(0, Register.getR(compiler.reg.getCurrentRegId()));

        // Alloue bloc de mémoire de taille NumberofFields + 1, stock l'adresse dans le currentRegister
        compiler.addInstruction(new NEW(numberOfFields + 1, Register.getR(compiler.reg.getCurrentRegId())));
        GenUtils.setErrorLabel(compiler, "tas_plein");

        // Charge l'adresse de la classe dans R0.
        compiler.addInstruction(new LEA(object.getClassDefinition().getOperand(), Register.R0));

        // Store l'adresse de la classe dans la première case du bloc de mémoire.
        compiler.addInstruction(new STORE(Register.R0, regOff));

        // BSR 2 empilements -> TSTO + 2
        compiler.pile.incrementTSTO(3);
        compiler.addInstruction(new PUSH(Register.getR(compiler.reg.getCurrentRegId())));
        compiler.addInstruction(new BSR(compiler.getLabel("init." + classDef.getType().getName().getName())));
        compiler.addInstruction(new POP(Register.getR(compiler.reg.getCurrentRegId())));


        compiler.addComment("Restoration des registres");
        for(int i = compiler.reg.getCurrentRegId(); i > 2; i--){
            compiler.addInstruction(new POP(Register.getR(i)));
        }
        compiler.reg.setCurrentRegId(exRegId);


    }
    
    @Override
    protected void codeGenExpr(DecacCompiler compiler) {
        codeGenInst(compiler);
    }
    
    @Override
    protected void codeGenCondition(DecacCompiler compiler, Boolean bool, Label elseLabel) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("new ");
        object.decompile(s);
        s.print("()");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        object.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        object.prettyPrint(s, prefix, true);
    }


}
