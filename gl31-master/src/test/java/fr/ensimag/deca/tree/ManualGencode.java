/* A manual test for the initial sketch of code generation included in
 * students skeleton.
 *
 * It is not intended to still work when code generation has been updated.
 */
package fr.ensimag.deca.tree;

        import fr.ensimag.deca.CompilerOptions;
        import fr.ensimag.deca.DecacCompiler;
        import fr.ensimag.deca.context.ContextualError;
        import fr.ensimag.deca.syntax.AbstractDecaLexer;
        import fr.ensimag.deca.syntax.DecaLexer;
        import fr.ensimag.deca.syntax.DecaParser;
        import org.antlr.v4.runtime.CommonTokenStream;

        import java.io.File;
        import java.io.IOException;

/**
 *
 * @author Ensimag
 * @date 01/01/2021
 */
public class ManualGencode {


    public static String gencodeSource(AbstractProgram source) {
        DecacCompiler compiler = new DecacCompiler(null,null);
        source.codeGenProgram(compiler);
        return compiler.displayIMAProgram();
    }

    public static void main(String[] args) throws IOException, ContextualError {
        // Uncomment the following line to activate debug traces
        // unconditionally for test_synt
        // Logger.getRootLogger().setLevel(Level.DEBUG);
        DecaLexer lex = AbstractDecaLexer.createLexerFromArgs(args);
        CommonTokenStream tokens = new CommonTokenStream(lex);
        DecaParser parser = new DecaParser(tokens);
        File file = null;
        if (lex.getSourceName() != null) {
            file = new File(lex.getSourceName());
        }
        final DecacCompiler decacCompiler = new DecacCompiler(new CompilerOptions(), file);
        parser.setDecacCompiler(decacCompiler);
        AbstractProgram prog = parser.parseProgramAndManageErrors(System.err);
        prog.verifyProgram(decacCompiler);

        prog.codeGenProgram(decacCompiler);
        System.out.println("--- Abstract syntax tree --- ");
        prog.prettyPrint(System.out);
        String codeSource = decacCompiler.displayIMAProgram();
        System.out.println("--- Generated assembly code --- ");
        System.out.println(codeSource);
    }
}
