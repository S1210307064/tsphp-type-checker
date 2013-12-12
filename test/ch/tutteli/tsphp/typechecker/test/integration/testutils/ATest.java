package ch.tutteli.tsphp.typechecker.test.integration.testutils;

import ch.tutteli.tsphp.common.IErrorLogger;
import ch.tutteli.tsphp.common.IParser;
import ch.tutteli.tsphp.common.exceptions.TSPHPException;
import ch.tutteli.tsphp.parser.ParserFacade;
import ch.tutteli.tsphp.typechecker.error.ErrorMessageProvider;
import ch.tutteli.tsphp.typechecker.error.ITypeCheckErrorReporter;
import ch.tutteli.tsphp.typechecker.error.TypeCheckErrorReporter;
import ch.tutteli.tsphp.typechecker.error.TypeCheckErrorReporterRegistry;
import org.junit.Ignore;

import java.util.ArrayList;
import java.util.List;

@Ignore
public abstract class ATest implements IErrorLogger
{

    protected List<Exception> exceptions = new ArrayList<>();
    protected IParser parser;

    public ATest() {
        parser = createParser();
        parser.registerErrorLogger(new WriteExceptionToConsole());

        ITypeCheckErrorReporter errorReporter = createTypeCheckErrorReporter();
        errorReporter.registerErrorLogger(this);
        TypeCheckErrorReporterRegistry.set(errorReporter);
    }

    public void log(TSPHPException exception) {
        exceptions.add(exception);
    }

    protected IParser createParser() {
        return new ParserFacade();
    }

    protected ITypeCheckErrorReporter createTypeCheckErrorReporter() {
        return new TypeCheckErrorReporter(new ErrorMessageProvider());
    }
}
