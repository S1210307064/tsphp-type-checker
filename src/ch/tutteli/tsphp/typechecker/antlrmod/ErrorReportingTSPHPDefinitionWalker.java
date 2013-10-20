package ch.tutteli.tsphp.typechecker.antlrmod;

import ch.tutteli.tsphp.common.IErrorLogger;
import ch.tutteli.tsphp.common.IErrorReporter;
import ch.tutteli.tsphp.typechecker.IDefiner;
import ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.TreeNodeStream;

import java.util.ArrayDeque;
import java.util.Collection;

public class ErrorReportingTSPHPDefinitionWalker extends TSPHPDefinitionWalker implements IErrorReporter
{

    private final Collection<IErrorLogger> errorLoggers = new ArrayDeque<>();
    private boolean hasFoundError;

    public ErrorReportingTSPHPDefinitionWalker(TreeNodeStream input, IDefiner theDefiner) {
        super(input, theDefiner);
    }

    @Override
    public boolean hasFoundError() {
        return hasFoundError;
    }

    @Override
    public void reportError(RecognitionException exception) {
        hasFoundError = true;
        ErrorReporterHelper.reportError(errorLoggers, exception, "definition");
    }

    @Override
    public void registerErrorLogger(IErrorLogger errorLogger) {
        errorLoggers.add(errorLogger);
    }
}
