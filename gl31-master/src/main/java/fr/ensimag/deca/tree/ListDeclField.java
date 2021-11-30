package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

public class ListDeclField extends TreeList<AbstractDeclField>{
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclField v : getList()) {
            v.decompile(s);
            s.println();
        }
    }
    void verifyListDeclField(DecacCompiler compiler, EnvironmentExp localEnv,
                                ClassDefinition currentClass) throws ContextualError {
        for (AbstractDeclField decl:getList()){
            decl.verifyDeclField(compiler, localEnv, currentClass);
        }
    }

    void initFields(DecacCompiler compiler){
        for (AbstractDeclField field : getList()){
            field.initField(compiler, false);
        }
    }

    void initFieldsZero(DecacCompiler compiler){
        for (AbstractDeclField field : getList()){
            field.initField(compiler, true);
        }
    }
}
