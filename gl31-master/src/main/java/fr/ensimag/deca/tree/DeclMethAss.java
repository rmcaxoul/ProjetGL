package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenUtils;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;
import java.io.PrintStream;

/**
 * Declaration of Method in assembly
 *
 * @author gl31
 * @date 15/01/2021
 *
 */

public class DeclMethAss extends AbstractDeclMeth{
    private AbstractIdentifier name;
    private String body;
    private AbstractIdentifier returnType;
    private ListDeclParam declParam;
    private EnvironmentExp methEnv;
    private Signature signature;

    public DeclMethAss(AbstractIdentifier returnType, AbstractIdentifier name, ListDeclParam listDeclParam, String body){
        Validate.notNull(name);
        Validate.notNull(body);
        Validate.notNull(returnType);
        Validate.notNull(listDeclParam);
        this.name = name;
        this.body = body;
        this.declParam = listDeclParam;
        this.returnType = returnType;
    }
    @Override
    protected void verifyDeclMeth(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {

        // Allmost same method as deca method declaration, but no verification for body return type
        methEnv = new EnvironmentExp(localEnv);
        declParam.verifyListDeclParam(compiler, methEnv, currentClass);
        returnType.setType(returnType.verifyType(compiler));
        this.signature = declParam.getSignature();

        int index=0;
        if (currentClass.getSuperClass() != null) {
            MethodDefinition superMethod = (MethodDefinition) currentClass.getSuperClass().getMembers().get(name.getName());
            if (superMethod != null) {
                MethodDefinition other = superMethod.asMethodDefinition("Super method not found",
                        getLocation());
                if (signature.getArgs().equals(other.getSignature().getArgs())) {

                    if (!Type.subType(returnType.getType(), other.getType())) {

                        throw new ContextualError("Method return type must be subtype of super method retType [2.7]", getLocation());
                    }
                } else {
                    throw new ContextualError("Signatures is not the same as superclass method", getLocation());
                }
                index = superMethod.getIndex();
                currentClass.listOfMethods.set(index, this);
            }
            else {
                index = currentClass.getNumberOfMethods();
                currentClass.incNumberOfMethods();
                currentClass.listOfMethods.add(this);
            }
        }
        try {
            MethodDefinition methodDefinition = new MethodDefinition(returnType.getType(), getLocation(), signature, index);
            name.setDefinition(methodDefinition);
            name.setType(returnType.getType());
            methodDefinition.classOfDefinition = currentClass;
            localEnv.declare(name.getName(), methodDefinition);
        }
        catch (Exception e){
            throw new ContextualError("Method already defined", getLocation());
        }
    }

    @Override
    protected void verifyDeclMethBody(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
    }

    @Override
    protected void codeGenTab(DecacCompiler compiler, ClassDefinition currentClass) {
        MethodDefinition methDef = (MethodDefinition) currentClass.getMembers().get(this.name.getName());
        String nom = "Code." + methDef.classOfDefinition.getType().getName().getName() + "." + this.name.getName().getName();
        compiler.putLabel(nom);

        methDef.setLabel(compiler.getLabel(nom));

        compiler.addInstruction(new LOAD(new LabelOperand(compiler.getLabel(nom)), Register.getR(0)));
        compiler.addInstruction(new STORE(Register.getR(0), new RegisterOffset(compiler.pile.getOffset() , Register.GB)));
        compiler.pile.incOffset();

    }

    @Override
    protected void codeGenMeth(DecacCompiler compiler, ClassDefinition currentClass) {
        MethodDefinition methDef = (MethodDefinition) currentClass.getMembers().get(this.name.getName());

        String longName = "Code." + methDef.classOfDefinition.getType().getName().getName() + "." + this.name.getName().getName();
        Label endLabel = new Label("Fin." + methDef.classOfDefinition.getType().getName().getName() + "." + this.name.getName().getName());
        compiler.currentMethod = endLabel;


        if(!compiler.getIsCoded(longName)){
            //On vide le programme
            compiler.clearProg();

            //Code de la méthode
            this.declParam.codeGenParam(compiler);

            compiler.add(new InlinePortion(body.substring(1, body.length() - 1)
                    .replace("\\\"", "\"").replace("\\\\", "\\")));

            //Fin de la méthode
            compiler.addLabel(endLabel);

            compiler.getProgram().addFirst(new Line(compiler.getLabel(longName)));

            //On ajoute le programme au programme final, puis on le vide
            compiler.mergeProgs();
            compiler.clearProg();

        } else {
            // On ne fait rien, la méthode existe déjà
        }

    }

    @Override
    public void decompile(IndentPrintStream s) {
        returnType.decompile(s);
        s.print(" ");
        name.decompile(s);
        s.print("(");
        declParam.decompile(s);
        s.print(")");
        s.print(" asm ");
        s.println("(");
        s.indent();
        s.print(body);
        s.unindent();
        s.println(");");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        returnType.iter(f);
        name.iter(f);
        declParam.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        returnType.prettyPrint(s, prefix, false);
        name.prettyPrint(s, prefix, false);
        declParam.prettyPrint(s, prefix, true);
    }
}
