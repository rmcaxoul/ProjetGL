package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;
import java.io.PrintStream;

/**
 * Declaration of parameter
 *
 * @author gl31
 * @date 15/01/2021
 *
 */

public class DeclParam extends AbstractDeclParam{
    final private AbstractIdentifier type;
    final private AbstractIdentifier paramName;

    public DeclParam(AbstractIdentifier type, AbstractIdentifier paramName) {
        Validate.notNull(type);
        Validate.notNull(paramName);
        this.type = type;
        this.paramName = paramName;
    }

    @Override
    protected Type verifyDeclParam(DecacCompiler compiler,
                                 EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        //Similar to DeclVar, plus declaration of the parameter in the method env
        Type typeParam = type.verifyType(compiler);
        if (typeParam.isVoid()){
            throw new ContextualError("Declaration is of type void [3.17]", getLocation());
        }

        SymbolTable.Symbol name = paramName.getName();
        ExpDefinition paramDef = new ParamDefinition(typeParam, getLocation());
        try {
            localEnv.declare(name, paramDef);
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new ContextualError("Variable " + name + " already defined : " + e + "[3.17] ", getLocation());
        }
        paramName.verifyExpr(compiler, localEnv, currentClass);
        if (typeParam.isString()){
            throw  new ContextualError("Cannot assign string [3.17] ", getLocation());
        }
        return typeParam;
    }
    
    @Override
    protected void codeGenParam(DecacCompiler compiler, int indiceParam){
        ParamDefinition def = (ParamDefinition) paramName.getDefinition();
        def.setOperand(new RegisterOffset(indiceParam, Register.LB));
        compiler.pile.incOffsetLB();
    }

    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        paramName.decompile(s);
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        type.iter(f);
        paramName.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        paramName.prettyPrint(s, prefix, false);
    }
}
