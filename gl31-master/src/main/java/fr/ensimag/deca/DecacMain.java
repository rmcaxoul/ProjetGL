package fr.ensimag.deca;

import java.io.File;
import org.apache.log4j.Logger;
import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Main class for the command-line Deca compiler.
 *
 * @author gl31
 * @date 01/01/2021
 */
public class DecacMain {
    private static Logger LOG = Logger.getLogger(DecacMain.class);

    public static void main(String[] args) {
        // example log4j message.
        LOG.info("Decac compiler started");
        boolean error = false;
        final CompilerOptions options = new CompilerOptions();
        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            System.err.println("Error during option parsing:\n"
                    + e.getMessage());
            System.exit(1);
        }
        if (options.getPrintBanner()) {
            System.out.println("Equipe n°31");
            System.exit(0);
        }
        if (options.getSourceFiles().isEmpty()) {
            options.displayUsage();
            System.exit(0);
        }
        if (options.getParallel()) {
            ExecutorService executor = Executors.newFixedThreadPool(options.getSourceFiles().size());
            List<Future<Boolean>> futures = new ArrayList<>(options.getSourceFiles().size());
            for (File source : options.getSourceFiles()) {
                futures.add(executor.submit(new DecacThread(options,source)));
            }
            for(Future<Boolean> future : futures){
                try{
                    if(future.get()){
                        error = true;
                    }
                } catch (ExecutionException | InterruptedException e){
                    e.printStackTrace();
                }
            }
            executor.shutdown();
        } else {
            for (File source : options.getSourceFiles()) {
                DecacCompiler compiler = new DecacCompiler(options, source);
                if (compiler.compile()) {
                    error = true;
                }
            }
        }
        System.exit(error ? 1 : 0);
    }
}
