package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.instructions.*;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import fr.ensimag.deca.codegen.GenUtils;

/**
 * Deca complete program (class definition plus main block)
 *
 * @author gl31
 * @date 01/01/2021
 */
public class Program extends AbstractProgram {
    private static final Logger LOG = Logger.getLogger(Program.class);
    
    public Program(ListDeclClass classes, AbstractMain main) {
        Validate.notNull(classes);
        Validate.notNull(main);
        this.classes = classes;
        this.main = main;
    }
    public ListDeclClass getClasses() {
        return classes;
    }
    public AbstractMain getMain() {
        return main;
    }
    private ListDeclClass classes;
    private AbstractMain main;


    @Override
    public void verifyProgram(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify program: start");
        classes.verifyListClass(compiler);
        classes.verifyListClassMembers(compiler);
        classes.verifyListClassBody(compiler);
        main.verifyMain(compiler);
        LOG.debug("verify program: end");
    }

    @Override
    public void codeGenProgram(DecacCompiler compiler) {
        
        compiler.pile.resetValTsto();
        
        //Code de la table des méthodes
        if(classes != null) {
            compiler.clearProg();
            compiler.addComment("Table des méthodes");
            classes.codeGenTab(compiler);
            compiler.addComment("Fin de la table des méthodes");
            // On déplace le pointeur de pile
            compiler.mergeProgs();
            compiler.clearProg();
        }
        
        //Code du programme principal
        compiler.clearProg();
        compiler.addComment("Début du main program");
        main.codeGenMain(compiler);
        compiler.addInstruction(new HALT());
        compiler.addComment("Fin du main program");
        compiler.addFirstFinal(new ADDSP(compiler.pile.getOffset() - 1));
        if (!compiler.getCompilerOptions().getNoCheck()) {
            //On ajoute les instructions liées à TSTO
            compiler.addFirstFinal(new BOV(GenUtils.getErrorLabel("stack_overflow_error")));
            compiler.addFirstFinal(new TSTO(compiler.pile.getValTsto()));
        }

        compiler.mergeProgs();
        compiler.clearProg();
        

        //Code d'initialisation des champs
        compiler.addComment("Initialisation des champs de classes");
        classes.codeInitFields(compiler);
        compiler.addComment("Fin d'initialisation des champs de classes");
        
        //On est sûr qu'aucune méthode n'est déjà codée
        compiler.initCodedLabels();

        //Code des méthodes de classe
        compiler.addComment("Code des méthodes");
        classes.codeGenMeth(compiler);
        compiler.addComment("Fin du code des méthodes");

        //Code des messages d'erreur
        compiler.clearProg();
        compiler.addComment("Messages d'erreur");
        GenUtils.generateErrorCode(compiler);
        compiler.addComment("Fin des messages d'erreur");
        compiler.mergeProgs();
        compiler.clearProg();


    }

    @Override
    public void decompile(IndentPrintStream s) {
        getClasses().decompile(s);
        getMain().decompile(s);
    }
    
    @Override
    protected void iterChildren(TreeFunction f) {
        classes.iter(f);
        main.iter(f);
    }
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        classes.prettyPrint(s, prefix, false);
        main.prettyPrint(s, prefix, true);
    }
}
