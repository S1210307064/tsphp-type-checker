package ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck;

import org.junit.Ignore;

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
}
