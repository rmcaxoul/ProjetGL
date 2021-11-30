package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenUtils;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.Line;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.instructions.*;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import org.apache.commons.lang.Validate;
import java.io.PrintStream;

/**
 * Declaration of Method in deca
 *
 * @author gl31
 * @date 15/01/2021
 *
 */

public class DeclMeth extends AbstractDeclMeth{
    private Signature signature;
    private AbstractIdentifier returnType;
    private AbstractIdentifier name;
    private ListDeclParam declParam;
    private ListInst body;
    private ListDeclVar declVar;
    private EnvironmentExp methEnv;

    private boolean hasReturn = false;


    public DeclMeth(AbstractIdentifier returnType, AbstractIdentifier name, ListDeclParam declParam, ListInst body, ListDeclVar declVar){
        Validate.notNull(returnType);
        Validate.notNull(body);
        Validate.notNull(name);
        Validate.notNull(declParam);
        Validate.notNull(declVar);
        this.returnType = returnType;
        this.name = name;
        this.declParam = declParam;
        this.body = body;
        this.declVar = declVar;
    }

    @Override
    protected void verifyDeclMeth(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {

        // Creation of a unique env for each method
        methEnv = new EnvironmentExp(localEnv);
        declParam.verifyListDeclParam(compiler, methEnv, currentClass);
        returnType.setType(returnType.verifyType(compiler));
        this.signature = declParam.getSignature();

        //Verification that declaration of method is legal according to superclass
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

        // Runtime excpetion
        // et c'était pas correct finalement.
        //if (!returnType.getType().isVoid() && !body.verifReturn()){
        //    throw new ContextualError("Method is of type " + returnType.getType() + " and doesn't have return", getLocation());
        //}

        try {
            MethodDefinition methodDefinition = new MethodDefinition(returnType.getType(), getLocation(), signature, index);
            name.setDefinition(methodDefinition);
            name.setType(returnType.getType());
            methodDefinition.classOfDefinition = currentClass;
            methodDefinition.nbDeclVar = declVar.size();
            localEnv.declare(name.getName(), methodDefinition);
        }
        catch (Exception e){
            throw new ContextualError("Method already defined", getLocation());
        }
    }

    @Override
    protected void verifyDeclMethBody(DecacCompiler compiler, EnvironmentExp localEnv,
                                      ClassDefinition currentClass) throws ContextualError{

        // On passe methEnv, qui contient les paramètres.
        Type type = compiler.envType.get(returnType.getName()).getType();
        declVar.verifyListDeclVariable(compiler, methEnv, currentClass);
        body.verifyListInst(compiler, methEnv, currentClass, type);
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
        compiler.pile.incrementTSTO();
    }
    
    @Override
    protected void codeGenMeth(DecacCompiler compiler, ClassDefinition currentClass) {
        MethodDefinition methDef = (MethodDefinition) currentClass.getMembers().get(this.name.getName());

        String longName = "Code." + methDef.classOfDefinition.getType().getName().getName() + "." + this.name.getName().getName();
        Label endLabel = new Label("Fin." + methDef.classOfDefinition.getType().getName().getName() + "." + this.name.getName().getName());
        compiler.currentMethod = endLabel;

        if(!compiler.getIsCoded(longName)){
            
            //On réinitialise TSTO
            compiler.pile.resetValTsto();
            
            //On vide le programme
            compiler.clearProg();
            
            //On code la méthode
            compiler.addComment("Sauvegarde des registres");
            
            //On sauvegarde les registres
            for(int i = 2; i < compiler.reg.getMainRegId(); i++){
                compiler.addInstruction(new PUSH(Register.getR(i)));
                compiler.pile.incrementTSTO();
            }
            compiler.reg.resetCurrentRegId();
            
            //Code de la méthode
            this.declParam.codeGenParam(compiler);
            this.declVar.codeGenListDeclVarMeth(compiler);
            this.body.codeGenListInst(compiler);
            
            //Code d'erreur
            if (!returnType.getType().isVoid()) {
                compiler.addInstruction(new WSTR("Erreur : sortie de la méthode " + this.name.getName().getName() + " sans return"));
                compiler.addInstruction(new WNL());
                compiler.addInstruction(new ERROR());
            }
            
            //Fin de la méthode
            compiler.addLabel(endLabel);
            
            compiler.addComment("Restoration des registres");

            for(int i = compiler.reg.getMainRegId(); i >= 2; i--){
                compiler.addInstruction(new POP(Register.getR(i)));
            }
            compiler.reg.setCurrentRegId(compiler.reg.getMainRegId());
            compiler.addInstruction(new RTS());
            
            //On ajoute TSTO et le nom du Label au début du programme
            if (!compiler.getCompilerOptions().getNoCheck()) {
                compiler.getProgram().addFirst(new BOV(GenUtils.getErrorLabel("stack_overflow_error")));
                compiler.getProgram().addFirst(new TSTO(compiler.pile.getValTsto()));
            }


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
        s.println(") {");
        s.indent();
        body.decompile(s);
        s.unindent();
        s.println("}");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        returnType.iter(f);
        name.iter(f);
        declParam.iter(f);
        body.iter(f);
        declVar.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        returnType.prettyPrint(s, prefix, false);
        name.prettyPrint(s, prefix, false);
        declParam.prettyPrint(s, prefix, false);
        body.prettyPrint(s, prefix, false);
        declVar.prettyPrint(s, prefix, true);
    }
}
