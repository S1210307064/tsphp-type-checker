/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.testutils.reference;

import ch.tsphp.common.IErrorReporter;
import ch.tsphp.common.exceptions.ReferenceException;
import ch.tsphp.typechecker.error.ITypeCheckerErrorReporter;
import ch.tsphp.typechecker.error.ReferenceErrorDto;
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
        verifyReferences(errorMessagePrefix, exceptions, errorDtos, typeCheckErrorReporter);
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public static void verifyReferences(String errorMessagePrefix, List<Exception> exceptions,
            ReferenceErrorDto[] errorDtos, ITypeCheckerErrorReporter typeCheckerErrorReporter) {
        Assert.assertTrue(errorMessagePrefix + " failed. No exception occurred.", typeCheckerErrorReporter.hasFoundError());

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
