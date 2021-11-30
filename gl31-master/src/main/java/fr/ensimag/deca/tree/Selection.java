package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenUtils;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Iterator;

/**
 * Selection of an attribute of an Object
 */
public class Selection extends AbstractLValue{

    private final AbstractExpr objet;
    private final AbstractIdentifier attribute;

    public Selection(AbstractExpr objet, AbstractIdentifier attribute){
        Validate.notNull(objet);
        Validate.notNull(attribute);
        this.objet = objet;
        this.attribute = attribute;

    }

    public AbstractIdentifier getAttribute(){
        return attribute;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        objet.verifyExpr(compiler,localEnv,currentClass);
        ClassDefinition objectClass = objet.getType().asClassType("not a class", getLocation()).getDefinition();
        attribute.verifyExpr(compiler, objectClass.getMembers(), objectClass);
        ClassDefinition attributeClass = attribute.getFieldDefinition().getContainingClass();
        FieldDefinition field = (FieldDefinition) objectClass.getMembers().get(attribute.getName());
        if (field.getVisibility().equals(Visibility.PROTECTED)){
            if (currentClass == null || !(objectClass.getType().isSubClassOf(currentClass.getType()) && currentClass.getType().isSubClassOf(attributeClass.getType()))){
                throw new ContextualError("Attribute is protected", getLocation());
            }
        }
        setType(field.getType());
        return getType();
    }
    
    @Override
    protected void codeGenExpr(DecacCompiler compiler) {
        // Chargement de l'objet.
        //compiler.addInstruction(new LOAD(((Identifier) objet).getExpDefinition().getOperand(), Register.getR(compiler.reg.getCurrentRegId())));

         objet.codeGenExpr(compiler);

        // Generation de l'adresse du champ.
        RegisterOffset fieldAddr = new RegisterOffset(attribute.getFieldDefinition().getIndex(), Register.getR(compiler.reg.getCurrentRegId()));

        // Si il est null, erreur.
        compiler.addInstruction(new CMP(new NullOperand(), Register.getR(compiler.reg.getCurrentRegId())));
        GenUtils.setErrorLabelBEQ(compiler, "dereferencement.null");

        // On charge la valeur du champ dans le registre courant.
        compiler.addInstruction(new LOAD(fieldAddr, Register.getR(compiler.reg.getCurrentRegId())));

    }

    /**
     * Generation de code différente si la selection est la Lvalue d'un assign
     * @param compiler
     */
    protected void codeGenAssign(DecacCompiler compiler){
        // On charge juste l'adresse de l'objet dans le registre courant.
        objet.codeGenExpr(compiler);
        compiler.addInstruction(new CMP(new NullOperand(), Register.getR(compiler.reg.getCurrentRegId())));
        GenUtils.setErrorLabelBEQ(compiler, "dereferencement.null");
    }

    @Override
    protected void codeGenCondition(DecacCompiler compiler, Boolean bool, Label elseLabel) {
        throw new UnsupportedOperationException("not yet implemented");
    }


    @Override
    public void decompile(IndentPrintStream s) {
        objet.decompile(s);
        s.print(".");
        attribute.decompile(s);
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        objet.iter(f);
        attribute.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        objet.prettyPrint(s, prefix, false);
        attribute.prettyPrint(s, prefix, true);
    }
}
