package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.tools.IndentPrintStream;

public class ListDeclParam extends TreeList<AbstractDeclParam>{

    private Signature signature = new Signature();
    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclParam v : getList()) {
            v.decompile(s);
            if(getList().lastIndexOf(v) != getList().size() - 1){
                s.print(",");
            }
        }
    }

    protected void verifyListDeclParam(DecacCompiler compiler, EnvironmentExp localEnv,
                                ClassDefinition currentClass) throws ContextualError {
        for (AbstractDeclParam decl:getList()){
            signature.add(decl.verifyDeclParam(compiler, localEnv, currentClass));
        }
    }
    
    protected void codeGenParam(DecacCompiler compile){
        int indiceParam = -3;
        for (AbstractDeclParam v : getList()) {
            v.codeGenParam(compile, indiceParam);
            indiceParam --;
            }
        }

    protected Signature getSignature(){
        return signature;
    }
}
