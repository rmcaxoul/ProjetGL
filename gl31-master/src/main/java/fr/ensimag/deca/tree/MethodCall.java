package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.codegen.GenUtils;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.*;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.NullOperand;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;
import java.util.Iterator;

/**
 * Call the method of an object
 */
public class MethodCall extends AbstractExpr{

    private AbstractExpr objet;
    private final AbstractIdentifier methode;
    private ListExpr args = new ListExpr();

    public MethodCall(AbstractExpr objet, AbstractIdentifier methode, ListExpr args){
        Validate.notNull(methode);
        Validate.notNull(args);
        //Validate.notNull(objet);
        this.objet = objet;
        this.methode = methode;
        this.args = args;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        Signature tmp = new Signature();

        if (objet == null){
            objet = new This();
        }

        objet.verifyExpr(compiler, localEnv, currentClass);

        // Environnement de la classe dont l'objet est une instance.
        EnvironmentExp classEnv = ((ClassDefinition) compiler.envType.get(objet.getType().getName())).getMembers();

        if (classEnv.get(methode.getName())==null){
            throw new ContextualError("Method non defined", getLocation());
        }
        methode.setDefinition(classEnv.get(methode.getName()));
        for (AbstractExpr arg: args.getList()){
            arg.verifyExpr(compiler, localEnv, currentClass);
            tmp.add(arg.getType());
        }
        if (!tmp.subSign(methode.getMethodDefinition().getSignature())){
            throw new ContextualError("Arguments given do not correspond to method signature", getLocation());
        }

        if (!(objet instanceof This) && localEnv.get(((Identifier) objet).getName())==null){
            throw new ContextualError("Object non defined", getLocation());
        }

        Definition def = methode.getDefinition();
        Type t = def.getType();
        setType(t);
        return getType();
    }
    
    @Override
    protected void codeGenExpr(DecacCompiler compiler) {
        //On alloue la place dans la pile
        compiler.addComment("Appel méthode " + methode.getName().getName());

        int oldLb = compiler.pile.getOffsetLB();
        compiler.pile.resetOffsetLb();
        compiler.addInstruction(new ADDSP(this.args.size() + methode.getMethodDefinition().nbDeclVar + 1));
        
        //Pas sûr : on remet l'id de registre à 2 ?
        //compiler.reg.resetCurrentRegId();
        
        //On stocke le paramètre implicite
        objet.codeGenExpr(compiler);
        //On vérifie que le paramètre implicite n'est pas null
        compiler.addInstruction(new CMP(new NullOperand(), Register.getR(compiler.reg.getCurrentRegId())));
        GenUtils.setErrorLabelBEQ(compiler, "dereferencement.null");

        // On stock le paramètre implicite au sommet de la pile
        compiler.addInstruction(new STORE(Register.getR(compiler.reg.getCurrentRegId()), new RegisterOffset(0, Register.SP)));
        
        //On stocke les paramètres de la méthode
        Iterator<AbstractExpr> iter = args.iterator();
        int id = -1;
        while(iter.hasNext()){
            iter.next().codeGenExpr(compiler);
            compiler.addInstruction(new STORE(Register.getR(compiler.reg.getCurrentRegId()), new RegisterOffset(id, Register.SP)));
            id --;
        }

        //On récupère l'@ de la table des méthodes
        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.SP), Register.getR(compiler.reg.getCurrentRegId())));
        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.getR(compiler.reg.getCurrentRegId())), Register.getR(compiler.reg.getCurrentRegId())));
        
        //On saute à la méthode voulu -- À VÉRIFIER, PAS SÛR DE LA METHODE POUR L'INDEX !!!
        // +1 nécessaire, sinon on appel la méthode d'avant..
        compiler.addInstruction(new BSR(new RegisterOffset(methode.getMethodDefinition().getIndex()+1,Register.getR(compiler.reg.getCurrentRegId()))));
        
        //On dépile les paramètres
        compiler.addInstruction(new SUBSP(this.args.size() + methode.getMethodDefinition().nbDeclVar + 1));
        compiler.pile.resetOffsetLb();
        //Load returned value in current Reg
        if(!methode.getMethodDefinition().getType().isVoid()) {
            compiler.addInstruction(new LOAD(Register.R0, Register.getR(compiler.reg.getCurrentRegId())));
        }
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
    protected void codeGenPrint(DecacCompiler compiler){
        codeGenExpr(compiler);
        compiler.addInstruction(new LOAD(Register.R0, Register.R1));

    }
    @Override
    public void decompile(IndentPrintStream s) {
        objet.decompile(s);
        s.print(".");
        methode.decompile(s);
        s.print("(");
        int i = 0;
        for (AbstractExpr a: args.getList()){
            a.decompile(s);
            i++;
            if(args.size() > i){
                s.print(",");
            }
        }
        s.print(")");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        if (objet != null) {
            objet.iter(f);
        }
        methode.iter(f);
        args.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        if (objet != null) {
            objet.prettyPrint(s, prefix, false);
        }
        methode.prettyPrint(s, prefix, false);
        args.prettyPrint(s, prefix, true);
    }
}

