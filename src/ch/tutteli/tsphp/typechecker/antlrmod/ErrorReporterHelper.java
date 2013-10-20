package ch.tutteli.tsphp.typechecker.antlrmod;

import ch.tutteli.tsphp.common.IErrorLogger;
import ch.tutteli.tsphp.common.exceptions.TSPHPException;
import org.antlr.runtime.RecognitionException;

import java.util.Collection;

public class ErrorReporterHelper
{
    private ErrorReporterHelper() {
    }

    public static void reportError(Collection<IErrorLogger> errorLoggers,
            RecognitionException exception, String phase) {
        String tokenText = exception.token != null
                ? "Unexpected token: " + exception.token.getText()
                : "Unknown token";
        for (IErrorLogger logger : errorLoggers) {
            logger.log(new TSPHPException("Line " + exception.line + "|" + exception.charPositionInLine
                    + " exception during " + phase + " phase occurred. " + tokenText, exception));
        }
    }
}
