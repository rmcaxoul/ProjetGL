package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;
import java.lang.reflect.Field;

/**
 * Declaration of field
 *
 * @author gl31
 * @date 15/01/2021
 *
 */

public class DeclField extends AbstractDeclField{
    final private Visibility visibility;
    final private AbstractIdentifier type;
    final private AbstractIdentifier fieldName;
    final private AbstractInitialization initialization;

    public DeclField(Visibility v, AbstractIdentifier type, AbstractIdentifier fieldName, AbstractInitialization initialization){
        Validate.notNull(v);
        Validate.notNull(type);
        Validate.notNull(fieldName);
        Validate.notNull(initialization);
        this.visibility = v;
        this.type = type;
        this.fieldName = fieldName;
        this.initialization = initialization;
    }

    @Override
    protected void verifyDeclField(DecacCompiler compiler,
       EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        //Verify if superClass has a method with same name as the new attribute
        if (currentClass.getSuperClass() != null){
            ExpDefinition superName = localEnv.get(fieldName.getName());
            if (superName != null){
                if (!superName.isField()){
                    throw new ContextualError("SuperClass has same attribute but is not field [2.5]", getLocation());
                }
            }
        }

        Type typeField = type.verifyType(compiler);

        if (typeField.isVoid()){
            throw new ContextualError("Field is of type void [3.17]", getLocation());
        }

        currentClass.incNumberOfFields();

        SymbolTable.Symbol name = fieldName.getName();
        ExpDefinition fieldDefinition = new FieldDefinition(typeField, getLocation(), visibility, currentClass, currentClass.getNumberOfFields());

        try {
            localEnv.declare(name, fieldDefinition);
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new ContextualError("Field " + name + " already defined", getLocation());
        }

        fieldName.verifyExpr(compiler, localEnv, currentClass);
        initialization.verifyInitialization(compiler, typeField, localEnv, currentClass);

        if (typeField.isString()){
            throw  new ContextualError("Cannot assign string", getLocation());
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        if(visibility.equals(Visibility.PROTECTED)){
            s.print("protected ");
        }
        type.decompile(s);
        s.print(" ");
        fieldName.decompile(s);
        if(initialization instanceof NoInitialization){
            s.print(";");
        }
        else{
            s.print(" = ");
            initialization.decompile(s);
            s.print(";");
        }
    }

    @Override
    protected void initField(DecacCompiler compiler, boolean zeroInit){
        // save current register ID, don't know if it's necessary
        compiler.reg.setCurrentRegId(0);

        // Init in r0
        compiler.addComment("Initialisation de " + fieldName.getName().getName());
        if (zeroInit || initialization instanceof NoInitialization){
            if (compiler.envType.get(type.getName()).isClass()){
                compiler.addInstruction(new LOAD(new NullOperand(), Register.R0));

            } else {
                compiler.addInstruction(new LOAD(new ImmediateInteger(0), Register.R0));
            }
        } else if (initialization instanceof Initialization) {
            initialization.codeGenDeclVar(compiler);
        }

        RegisterOffset fieldOffset = new RegisterOffset(this.fieldName.getFieldDefinition().getIndex(), Register.R1);
        fieldName.getExpDefinition().setOperand(fieldOffset);
        compiler.addInstruction(new STORE(Register.getR(compiler.reg.getCurrentRegId()), fieldOffset));

    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        fieldName.iter(f);
        initialization.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        fieldName.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);
    }
}
