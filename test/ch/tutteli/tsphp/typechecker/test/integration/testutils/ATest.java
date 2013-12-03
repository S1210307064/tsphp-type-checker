package ch.tutteli.tsphp.typechecker.test.integration.testutils;

import ch.tutteli.tsphp.common.IErrorLogger;
import ch.tutteli.tsphp.common.IParser;
import ch.tutteli.tsphp.common.exceptions.TSPHPException;
import ch.tutteli.tsphp.parser.ParserFacade;
import ch.tutteli.tsphp.typechecker.error.ErrorMessageProvider;
import ch.tutteli.tsphp.typechecker.error.TypeCheckErrorReporter;
import ch.tutteli.tsphp.typechecker.error.TypeCheckErrorReporterRegistry;
import ch.tutteli.tsphp.typechecker.error.ITypeCheckErrorReporter;
import org.junit.Ignore;

import java.util.ArrayList;
import java.util.List;

@Ignore
public abstract class ATest implements IErrorLogger
{

    protected List<Exception> exceptions = new ArrayList<>();
    protected IParser parser;

    public ATest() {
        parser = new ParserFacade();
        ITypeCheckErrorReporter errorReporter = new TypeCheckErrorReporter(new ErrorMessageProvider());
        errorReporter.registerErrorLogger(this);
        TypeCheckErrorReporterRegistry.set(errorReporter);
    }

    public void log(TSPHPException exception) {
        exceptions.add(exception);
    }
}
