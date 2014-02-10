package ch.tsphp.typechecker.test.integration.testutils.reference;

import ch.tsphp.common.IErrorReporter;
import ch.tsphp.common.exceptions.ReferenceException;
import ch.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tsphp.typechecker.error.TypeCheckErrorReporterRegistry;
import org.junit.Assert;
import org.junit.Ignore;

import java.util.List;

@Ignore
public abstract class AReferenceErrorTest extends AReferenceTest
{

    protected ReferenceErrorDto[] errorDtos;

    public AReferenceErrorTest(String testString, ReferenceErrorDto[] theErrorDtos) {
        super(testString);
        errorDtos = theErrorDtos;
    }

    @Override
    protected void checkReferences() {
        verifyReferences();
    }

    @Override
    public void verifyReferences() {
        verifyReferences(errorMessagePrefix, exceptions, errorDtos);
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public static void verifyReferences(String errorMessagePrefix, List<Exception> exceptions,
            ReferenceErrorDto[] errorDtos) {
        IErrorReporter errorReporter = TypeCheckErrorReporterRegistry.get();
        Assert.assertTrue(errorMessagePrefix + " failed. No exception occurred.", errorReporter.hasFoundError());

        Assert.assertEquals(errorMessagePrefix + " failed. More or less exceptions occurred." + exceptions.toString(),
                errorDtos.length, exceptions.size());

        for (int i = 0; i < errorDtos.length; ++i) {
            ReferenceException exception = (ReferenceException) exceptions.get(i);

            Assert.assertEquals(errorMessagePrefix + " failed. wrong identifier.",
                    errorDtos[i].identifier, exception.getDefinition().getText());

            Assert.assertEquals(errorMessagePrefix + " failed. wrong line.",
                    errorDtos[i].line, exception.getDefinition().getLine());

            Assert.assertEquals(errorMessagePrefix + " failed. wrong position.",
                    errorDtos[i].position, exception.getDefinition().getCharPositionInLine());
        }

    }
}
