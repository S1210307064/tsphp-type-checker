package ch.tsphp.typechecker.test.integration.testutils;

import ch.tsphp.common.IErrorLogger;
import ch.tsphp.common.IParser;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.parser.ParserFacade;
import ch.tsphp.typechecker.error.ErrorMessageProvider;
import ch.tsphp.typechecker.error.ITypeCheckErrorReporter;
import ch.tsphp.typechecker.error.TypeCheckErrorReporter;
import ch.tsphp.typechecker.error.TypeCheckErrorReporterRegistry;
import org.junit.Ignore;

import java.util.ArrayList;
import java.util.List;

@Ignore
public abstract class ATest implements IErrorLogger
{

    protected List<Exception> exceptions = new ArrayList<>();
    protected IParser parser;
    protected ITypeCheckErrorReporter typeCheckErrorReporter;

    public ATest() {
        parser = createParser();
        parser.registerErrorLogger(new WriteExceptionToConsole());

        typeCheckErrorReporter = createTypeCheckErrorReporter();
        typeCheckErrorReporter.registerErrorLogger(this);
        TypeCheckErrorReporterRegistry.set(typeCheckErrorReporter);
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
