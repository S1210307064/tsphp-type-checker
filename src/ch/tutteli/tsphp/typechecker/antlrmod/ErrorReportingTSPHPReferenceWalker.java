package ch.tutteli.tsphp.typechecker.antlrmod;

import ch.tutteli.tsphp.common.ErrorReporterHelper;
import ch.tutteli.tsphp.common.IErrorLogger;
import ch.tutteli.tsphp.common.IErrorReporter;
import ch.tutteli.tsphp.typechecker.ITypeCheckerController;
import ch.tutteli.tsphp.typechecker.antlr.TSPHPReferenceWalker;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.TreeNodeStream;

import java.util.ArrayDeque;
import java.util.Collection;

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
        ErrorReporterHelper.reportError(errorLoggers, exception, "reference");
    }

    @Override
    public void registerErrorLogger(IErrorLogger errorLogger) {
        errorLoggers.add(errorLogger);
    }
}
