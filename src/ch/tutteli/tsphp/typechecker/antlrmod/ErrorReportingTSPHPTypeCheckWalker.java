package ch.tutteli.tsphp.typechecker.antlrmod;

import ch.tutteli.tsphp.common.ErrorReporterHelper;
import ch.tutteli.tsphp.common.IErrorLogger;
import ch.tutteli.tsphp.common.IErrorReporter;
import ch.tutteli.tsphp.typechecker.IAccessResolver;
import ch.tutteli.tsphp.typechecker.ITypeCheckPhaseController;
import ch.tutteli.tsphp.typechecker.ITypeSystem;
import ch.tutteli.tsphp.typechecker.antlr.TSPHPTypeCheckWalker;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.TreeNodeStream;

import java.util.ArrayDeque;
import java.util.Collection;

public class ErrorReportingTSPHPTypeCheckWalker extends TSPHPTypeCheckWalker implements IErrorReporter
{

    private final Collection<IErrorLogger> errorLoggers = new ArrayDeque<>();
    private boolean hasFoundError;

    public ErrorReportingTSPHPTypeCheckWalker(
            TreeNodeStream input,
            ITypeCheckPhaseController controller,
            IAccessResolver accessResolver,
            ITypeSystem typeSystem) {
        super(input, controller, accessResolver, typeSystem);
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
