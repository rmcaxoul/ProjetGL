package fr.ensimag.deca.tree;

import java.util.Iterator;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import org.apache.commons.lang.Validate;

/**
 * Full if/else if/else statement.
 *
 * @author gl31
 * @date 01/01/2021
 */
public class IfThenElse extends AbstractInst {
    
    private final AbstractExpr condition; 
    private final ListInst thenBranch;
    private ListInst elseBranch;

    public IfThenElse(AbstractExpr condition, ListInst thenBranch, ListInst elseBranch) {
        Validate.notNull(condition);
        Validate.notNull(thenBranch);
        Validate.notNull(elseBranch);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public ListInst getElseBranch(){
        return elseBranch;
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        condition.verifyCondition(compiler, localEnv, currentClass);
        thenBranch.verifyListInst(compiler, localEnv, currentClass, returnType);
        elseBranch.verifyListInst(compiler, localEnv, currentClass, returnType);
    }
    
    
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        // Mon code est une usine à gaz.
        // Il devrait être possible d'utiliser la nature récursive du if/then/else...
        // ou de factoriser un peu
        int depth = compiler.pile.getElseId();
        IfThenElse currentIf = this;

        while (!currentIf.elseBranch.isEmpty() && currentIf.elseBranch.getList().get(0) instanceof IfThenElse) {
            depth++;
            currentIf = (IfThenElse) currentIf.elseBranch.getList().get(0);
        }

        Label endIf = new Label("End_if." + depth);

        compiler.pile.currentEndIf = endIf;
        currentIf = this;

        if (currentIf.elseBranch.isEmpty()) {
            compiler.pile.incElseId();
            currentIf.condition.codeGenCondition(compiler, false, endIf);
            currentIf.thenBranch.codeGenListInst(compiler);

        } else {

            Label tempLabel = new Label("sinon" + compiler.pile.getElseId());
            compiler.pile.incElseId();
            currentIf.condition.codeGenCondition(compiler, false, tempLabel);
            currentIf.thenBranch.codeGenListInst(compiler);
            compiler.addInstruction(new BRA(endIf));


            while ((!currentIf.elseBranch.isEmpty()) && currentIf.elseBranch.getList().get(0) instanceof IfThenElse) {

                currentIf = (IfThenElse) currentIf.elseBranch.getList().get(0);

                compiler.addLabel(tempLabel);

                tempLabel = new Label("sinon" + compiler.pile.getElseId());
                compiler.pile.incElseId();

                currentIf.condition.codeGenCondition(compiler,false, tempLabel);
                currentIf.thenBranch.codeGenListInst(compiler);
                compiler.addInstruction(new BRA(endIf));
            }


            if (currentIf.elseBranch.isEmpty()) {
                compiler.addLabel(tempLabel);
                compiler.pile.incElseId();
                currentIf.condition.codeGenCondition(compiler,false, endIf);
                currentIf.thenBranch.codeGenListInst(compiler);
            }
            else{
                compiler.addLabel(tempLabel);
                currentIf.elseBranch.codeGenListInst(compiler);
                }

        }

        compiler.addLabel(endIf);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("if(");
        condition.decompile(s);
        s.print(")");
        s.println("{");
        s.indent();
        thenBranch.decompile(s);
        s.unindent();
        s.println("}");
        if(!elseBranch.getList().isEmpty()){
            s.print("else ");
            Iterator<AbstractInst> iter = elseBranch.iterator();
            if(iter.next() instanceof IfThenElse && !iter.hasNext()){
                elseBranch.decompile(s);
            }
            else{
                s.println("{");
                s.indent();
                elseBranch.decompile(s);
                s.unindent();
                s.println("}");
            }
        }
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        condition.iter(f);
        thenBranch.iter(f);
        elseBranch.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s, prefix, false);
        thenBranch.prettyPrint(s, prefix, false);
        elseBranch.prettyPrint(s, prefix, true);
    }
}
