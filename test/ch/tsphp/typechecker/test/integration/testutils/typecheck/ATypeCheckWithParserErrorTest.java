package ch.tsphp.typechecker.test.integration.testutils.typecheck;

import ch.tsphp.common.IErrorReporter;
import ch.tsphp.typechecker.error.TypeCheckErrorReporterRegistry;
import org.junit.Ignore;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Ignore
public abstract class ATypeCheckWithParserErrorTest extends ATypeCheckTest
{
    public ATypeCheckWithParserErrorTest(String testString) {
        super(testString);
    }

    @Override
    protected void checkErrors() {
        verifyTypeCheck();
    }

    @Override
    protected void verifyParser() {
        assertTrue(
                testString.replaceAll("\n", " ") + " failed - parser exception was expected but nothing was thrown",
                parser.hasFoundError()
        );
    }

    @Override
    protected void checkReferences() {
        IErrorReporter errorHelper = TypeCheckErrorReporterRegistry.get();
        assertFalse(testString + " failed. Exceptions occurred." + exceptions, errorHelper.hasFoundError());
        assertTrue(testString + " failed. reference walker exceptions expected but nothing was thrown.",
                reference.hasFoundError());
    }
}
