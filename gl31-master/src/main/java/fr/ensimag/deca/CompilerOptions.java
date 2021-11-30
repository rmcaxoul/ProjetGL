package fr.ensimag.deca;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import org.apache.commons.cli.*;

/**
 * User-specified options influencing the compilation.
 *
 * @author gl31
 * @date 01/01/2021
 */
public class CompilerOptions {
    public static final int QUIET = 0;
    public static final int INFO  = 1;
    public static final int DEBUG = 2;
    public static final int TRACE = 3;
    public int getDebug() {
        return debug;
    }

    public boolean getParallel() {
        return parallel;
    }

    public boolean getPrintBanner() {
        return printBanner;
    }
    
    public boolean getParse() {
        return parse;
    }
    
    public boolean getVerif() {
        return verif;
    }
    
    public boolean getNoCheck() {
        return noCheck;
    }
    
    public boolean getRegisters() {
        return registers;
    }
    
    public int getRegistersNumber() {
        return registersNumber;
    }
    
    public List<File> getSourceFiles() {
        return Collections.unmodifiableList(sourceFiles);
    }

    private int debug = 0;
    private boolean parallel = false;
    private boolean printBanner = false;
    private boolean parse = false;
    private boolean verif = false;
    private boolean noCheck = false;
    private boolean registers = false;
    private int registersNumber = 0;
    private List<File> sourceFiles = new ArrayList<File>();


    // Option outside of parsArgs method so that method displayUsage have access to all the options.
    private Options options = new Options();


    public void parseArgs(String[] args) throws CLIException {
        // A FAIRE : parcourir args pour positionner les options correctement.
        Logger logger = Logger.getRootLogger();

        // map command-line debug option to log4j's level.
        switch (getDebug()) {
            case QUIET: break; // keep default
            case INFO:
                logger.setLevel(Level.INFO); break;
            case DEBUG:
                logger.setLevel(Level.DEBUG); break;
            case TRACE:
                logger.setLevel(Level.TRACE); break;
            default:
                logger.setLevel(Level.ALL); break;
        }
        logger.info("Application-wide trace level set to " + logger.getLevel());

        // Parsing of args
        options.addOption(new Option("b", "banner",
                false, "Affiche une bannière indiquant le nom de l'équipe."));
        options.addOption(new Option("p", "parse",
                false, "Arrête decac après l’étape de construction de l’arbre, et affiche la décompilation de ce dernier."));
        options.addOption(new Option("v", "verification",
                false, "Arrête decac après l’étape de vérifications."));
        options.addOption(new Option("n", "no-check",
                false, "Supprime les tests de débordement à l’exécution."));
        options.addOption(new Option("r", "registers",
                true, "Limite les registres disponibles."));
        options.addOption(new Option("d", "debug",
                false, "Active les traces de debug, option pouvant être repétée."));
        options.addOption(new Option("P", "parallel",
                false, "S'il y a plusieurs fichiers source, lance leur compilation en parallele."));

        try {
            // Parse command line
            CommandLineParser parser = new DefaultParser();
            CommandLine line = parser.parse(options, args);
            printBanner = line.hasOption("b");
            parallel = line.hasOption("P");
            parse = line.hasOption("p");
            verif = line.hasOption("v");
            noCheck = line.hasOption("n");

            // Parse the filnames
            String[] filenames = line.getArgs();
            for(String filename : filenames){
                sourceFiles.add(new File(filename));
            }

            if (line.hasOption("p") && line.hasOption("v")){
                throw new CLIException("Les options '-p' et '-v' sont incompatibles.");
            }

            if (printBanner && (line.getOptions().length != 1)){
                throw new CLIException("L'option '-b' est incompatible avec les autres options.");
            }

            if (line.hasOption("r") && (Integer.parseInt(line.getOptionValue("r") ) < 4 ||
                                        Integer.parseInt(line.getOptionValue("r")) > 16)) {
                throw new CLIException("L'argument de l'option 'r' doit être compris entre 4 et 16.");
            }
            
            if(line.hasOption("r")){
                registers = true;
                registersNumber = Integer.parseInt(line.getOptionValue("r"));
            }
            
            // Set the debug level
            for (Option option: line.getOptions()){
                if (option.getOpt() == "d"){
                    debug++;
                }
            }

        } catch (ParseException e) {
            throw new CLIException("Parsing error: " + e.getMessage());
        }

        boolean assertsEnabled = false;
        assert assertsEnabled = true; // Intentional side effect!!!
        if (assertsEnabled) {
            logger.info("Java assertions enabled");
        } else {
            logger.info("Java assertions disabled");
        }

        /*throw new UnsupportedOperationException("not yet implemented");*/
    }

    protected void displayUsage() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("decac [OPTION] [FILES]", options);
    }
}
