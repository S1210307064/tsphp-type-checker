package ch.tutteli.tsphp.typechecker.antlrmod;

import ch.tutteli.tsphp.common.IErrorLogger;
import ch.tutteli.tsphp.common.IErrorReporter;
import ch.tutteli.tsphp.typechecker.ITypeCheckerController;
import ch.tutteli.tsphp.typechecker.antlr.TSPHPTypeCheckWalker;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.TreeNodeStream;

import java.util.ArrayDeque;
import java.util.Collection;

public class ErrorReportingTSPHPTypeCheckWalker extends TSPHPTypeCheckWalker implements IErrorReporter
{

    private final Collection<IErrorLogger> errorLoggers = new ArrayDeque<>();
    private boolean hasFoundError;

    public ErrorReportingTSPHPTypeCheckWalker(TreeNodeStream input, ITypeCheckerController theController) {
        super(input, theController);
    }

    @Override
    public boolean hasFoundError() {
        return hasFoundError;
    }

    @Override
    public void reportError(RecognitionException exception) {
        hasFoundError = true;
        ErrorReporterHelper.reportError(errorLoggers, exception, "type checking");
    }

    @Override
    public void registerErrorLogger(IErrorLogger errorLogger) {
        errorLoggers.add(errorLogger);
    }
}
