package ch.tutteli.tsphp.typechecker.antlr;

import ch.tutteli.tsphp.common.IErrorLogger;
import ch.tutteli.tsphp.common.IErrorReporter;
import ch.tutteli.tsphp.common.exceptions.TSPHPException;
import ch.tutteli.tsphp.typechecker.ITypeCheckerController;
import java.util.ArrayDeque;
import java.util.Collection;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.TreeNodeStream;

public class ErrorReportingTSPHPReferenceWalker extends TSPHPReferenceWalker implements IErrorReporter
{

    private final Collection<IErrorLogger> errorLoggers = new ArrayDeque<>();
    private boolean hasFoundError;

    public ErrorReportingTSPHPReferenceWalker(TreeNodeStream input, ITypeCheckerController theController) {
        super(input, theController);
    }

    @Override
    public boolean hasFoundError() {
        return hasFoundError;
    }

    @Override
    public void reportError(RecognitionException exception) {
        hasFoundError = true;
        String tokenText = exception.token != null
                ? "Unexpected token: " + exception.token.getText()
                : "Unknown token";
        for (IErrorLogger logger : errorLoggers) {
            logger.log(new TSPHPException("Line " + exception.line + "|" + exception.charPositionInLine
                    + " reference phase exception occurred. " + tokenText, exception));
        }
    }

    @Override
    public void registerErrorLogger(IErrorLogger errorLogger) {
        errorLoggers.add(errorLogger);
    }
}
