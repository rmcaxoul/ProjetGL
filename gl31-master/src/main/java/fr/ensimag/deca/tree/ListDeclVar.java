package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * List of declarations (e.g. int x; float y,z).
 * 
 * @author gl31
 * @date 01/01/2021
 */
public class ListDeclVar extends TreeList<AbstractDeclVar> {

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclVar v : getList()) {
            v.decompile(s);
            s.print(";");
            s.println();
        }
    }

    /**
     * Implements non-terminal "list_decl_var" of [SyntaxeContextuelle] in pass 3
     * @param compiler contains the "env_types" attribute
     * @param localEnv 
     *   its "parentEnvironment" corresponds to "env_exp_sup" attribute
     *   in precondition, its "current" dictionary corresponds to 
     *      the "env_exp" attribute
     *   in postcondition, its "current" dictionary corresponds to 
     *      the "env_exp_r" attribute
     * @param currentClass 
     *          corresponds to "class" attribute (null in the main bloc).
     */    
    void verifyListDeclVariable(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        for (AbstractDeclVar decl:getList()){
            decl.verifyDeclVar(compiler, localEnv, currentClass);
        }
    }

    public void codeGenListDeclVar(DecacCompiler compiler) {
        for (AbstractDeclVar var : getList()) {
            var.codeGenDeclVar(compiler);
        }
    }

    public void codeGenListDeclVarMeth(DecacCompiler compiler) {
        for (AbstractDeclVar var : getList()) {
            var.codeGenDeclVarMeth(compiler);
        }
    }


}
