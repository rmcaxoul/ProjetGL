package fr.ensimag.deca.syntax;

import fr.ensimag.deca.tree.LocationException;
import org.antlr.v4.runtime.Token;

import java.io.PrintStream;

public class ParsingError extends DecaRecognitionException{

    private final String errorMessage;

    public ParsingError(DecaParser recognizer, Token offendingToken, String errorMessage) {
        super(recognizer, offendingToken);
        this.errorMessage = errorMessage;
    }


    @Override
    public String getMessage() {
        return errorMessage;
    }
}
