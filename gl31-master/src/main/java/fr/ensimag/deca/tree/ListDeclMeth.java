package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

public class ListDeclMeth extends TreeList<AbstractDeclMeth>{
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclMeth m : getList()) {
            m.decompile(s);
            s.println();
        }
    }

    //Pass 2: Verification of legal method declaration

    void verifyListDeclMeth(DecacCompiler compiler, EnvironmentExp localEnv,
                             ClassDefinition currentClass) throws ContextualError {
        for (AbstractDeclMeth decl:getList()){
            decl.verifyDeclMeth(compiler, localEnv, currentClass);
        }
    }

    //Pass 3: Verification of body method for non deca methods
    public void verifyListDeclMethBody(DecacCompiler compiler, EnvironmentExp localEnv,
                                       ClassDefinition currentClass) throws ContextualError{
        for (AbstractDeclMeth decl:getList()){
            decl.verifyDeclMethBody(compiler, localEnv, currentClass);
        }
    }

}
