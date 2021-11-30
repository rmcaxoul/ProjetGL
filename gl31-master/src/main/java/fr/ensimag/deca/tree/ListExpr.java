package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * List of expressions (eg list of parameters).
 *
 * @author gl31
 * @date 01/01/2021
 */
public class ListExpr extends TreeList<AbstractExpr> {


    
//Pas sûr du tout de cette méthode -Antoine (inspirée de ListInst)
    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractExpr e : getList()) {
            e.decompile(s);
            s.println();
        }
    }
}
