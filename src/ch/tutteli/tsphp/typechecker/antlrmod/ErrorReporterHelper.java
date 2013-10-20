package ch.tutteli.tsphp.typechecker.antlrmod;

import ch.tutteli.tsphp.common.IErrorLogger;
import ch.tutteli.tsphp.common.exceptions.TSPHPException;
import org.antlr.runtime.RecognitionException;

import java.util.Collection;

public final class ErrorReporterHelper
{
    private ErrorReporterHelper() {
    }

    public static void reportError(final Collection<IErrorLogger> errorLoggers,
            final RecognitionException exception, final String phase) {
        final String tokenText = exception.token != null
                ? "Unexpected token: " + exception.token.getText()
                : "Unknown token";
        for (IErrorLogger logger : errorLoggers) {
            logger.log(new TSPHPException("Line " + exception.line + "|" + exception.charPositionInLine
                    + " exception during " + phase + " phase occurred. " + tokenText, exception));
        }
    }
}
