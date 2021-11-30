package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.FloatType;
import fr.ensimag.deca.context.IntType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import org.apache.commons.lang.Validate;

/**
 * Print statement (print, println, ...).
 *
 * @author gl31
 * @date 01/01/2021
 */
public abstract class AbstractPrint extends AbstractInst {

    private boolean printHex;
    private ListExpr arguments = new ListExpr();
    
    abstract String getSuffix();

    public AbstractPrint(boolean printHex, ListExpr arguments) {
        Validate.notNull(arguments);
        this.arguments = arguments;
        this.printHex = printHex;
    }

    public ListExpr getArguments() {
        return arguments;
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        for (AbstractExpr a: getArguments().getList()){
            a.verifyInst(compiler, localEnv, currentClass, returnType);
            Type t = a.getType();
            if (!t.isInt()&&!t.isFloat()&&!t.isString()){
                throw new ContextualError("Incorrect print type, given " + t + " [3.31]", a.getLocation());
            }
        }
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        for (AbstractExpr a : getArguments().getList()) {
            a.codeGenPrint(compiler);

            // if because if it's a stringLiteral, then it as no Type, and we get a nullPointer error.
            // StrinLiteral as no Type because String is not a type in deca
            if (!(a instanceof StringLiteral)) {
                if (a.getType().isFloat() && printHex) {
                    compiler.addInstruction(new WFLOATX());
                } else if (a.getType().isFloat()) {
                    compiler.addInstruction(new WFLOAT());
                } else if (a.getType().isInt()) {
                    compiler.addInstruction(new WINT());
                }
            }
        }
    }

    private boolean getPrintHex() {
        return printHex;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        int i = 0;
        s.print("print" + getSuffix() + "(");
        for (AbstractExpr a: getArguments().getList()){
            a.decompile(s);
            i++;
            if(getArguments().size() > i){
                s.print(",");
            }
        }
        s.print(");");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        arguments.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        arguments.prettyPrint(s, prefix, true);
    }

}
