package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.log4j.Logger;
import fr.ensimag.deca.tools.SymbolTable;
/**
 *
 * @author gl31
 * @date 01/01/2021
 */
public class ListDeclClass extends TreeList<AbstractDeclClass> {
    private static final Logger LOG = Logger.getLogger(ListDeclClass.class);
    
    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclClass c : getList()) {
            c.decompile(s);
            s.println();
        }
    }

    /**
     * Pass 1 of [SyntaxeContextuelle]
     */
    void verifyListClass(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify listClass: start");
        DeclClass.declObject(compiler);
        for (AbstractDeclClass c:getList()){
            c.verifyClass(compiler);
        }
        LOG.debug("verify listClass: end");
    }

    /**
     * Pass 2 of [SyntaxeContextuelle]
     */
    public void verifyListClassMembers(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify listClass: start");
        for (AbstractDeclClass c:getList()){
            c.verifyClassMembers(compiler);
        }
        LOG.debug("verify listClass: end");    }
    
    /**
     * Pass 3 of [SyntaxeContextuelle]
     */
    public void verifyListClassBody(DecacCompiler compiler) throws ContextualError {
        for (AbstractDeclClass c: getList()){
            c.verifyClassBody(compiler);
        }
    }
    
    /**
     * Pass 1 of [CodeGen]
     */
    protected void codeGenTab(DecacCompiler compiler){
        // Dans un premier temps on créer la table de la class Objet
        // Si on a juste un main ça ne sert pas.
        if (!getList().isEmpty()) {
            DeclClass.codeGenObject(compiler, (ClassDefinition) compiler.envType.get(compiler.symbolTable.get("Object")));
        }
        for (AbstractDeclClass c: getList()){
            c.codeGenTab(compiler);
        }
    }
    
    /**
     * Codage des méthodes
     */
    protected void codeGenMeth(DecacCompiler compiler){
        //On traite le cas de la méthode equals de Object à part
        // Si on a juste un main ça ne sert pas.

        if (!getList().isEmpty()) {
            ClassDefinition object = (ClassDefinition) compiler.envType.get(compiler.symbolTable.get("Object"));
            object.listOfMethods.get(0).codeGenMeth(compiler, object);
            compiler.putIsCoded("Code.B.equals", true);
        }
        //On itère sur toutes les classes
        for (AbstractDeclClass c: getList()){
            c.codeGenMeth(compiler);
        }
    }

    /**
     * Pass 1 of [CodeGen]
     */
    protected void codeInitFields(DecacCompiler compiler){
        for (AbstractDeclClass c: getList()){
            c.initFields(compiler);
        }
    }
}
