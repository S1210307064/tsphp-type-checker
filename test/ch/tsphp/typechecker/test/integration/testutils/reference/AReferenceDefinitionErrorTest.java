/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.testutils.reference;

import ch.tsphp.common.exceptions.DefinitionException;
import ch.tsphp.typechecker.error.DefinitionErrorDto;
import ch.tsphp.typechecker.error.ITypeCheckerErrorReporter;
import org.junit.Assert;
import org.junit.Ignore;

import java.util.List;

@Ignore
public abstract class AReferenceDefinitionErrorTest extends AReferenceTest
{

    protected DefinitionErrorDto[] errorDtos;

    public AReferenceDefinitionErrorTest(String testString, DefinitionErrorDto[] theErrorDtos) {
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
            DefinitionErrorDto[] errorDtos, ITypeCheckerErrorReporter theTypeCheckerErrorReporter) {

        Assert.assertTrue(errorMessagePrefix + " failed. No reference exception occurred.",
                theTypeCheckerErrorReporter.hasFoundError());

        Assert.assertEquals(errorMessagePrefix + " failed. More or less reference exceptions occurred." + exceptions
                .toString(),
                errorDtos.length, exceptions.size());

        for (int i = 0; i < errorDtos.length; ++i) {
            DefinitionException exception = (DefinitionException) exceptions.get(i);

            Assert.assertEquals(errorMessagePrefix + " failed. wrong existing identifier.",
                    errorDtos[i].identifier, exception.getExistingDefinition().getText());
            Assert.assertEquals(errorMessagePrefix + " failed. wrong existing line.",
                    errorDtos[i].line, exception.getExistingDefinition().getLine());
            Assert.assertEquals(errorMessagePrefix + " failed. wrong existing position.",
                    errorDtos[i].position, exception.getExistingDefinition().getCharPositionInLine());

            Assert.assertEquals(errorMessagePrefix + " failed. wrong new identifier.",
                    errorDtos[i].identifierNewDefinition, exception.getNewDefinition().getText());
            Assert.assertEquals(errorMessagePrefix + " failed. wrong new line. ",
                    errorDtos[i].lineNewDefinition, exception.getNewDefinition().getLine());
            Assert.assertEquals(errorMessagePrefix + " failed. wrong new position. ",
                    errorDtos[i].positionNewDefinition, exception.getNewDefinition().getCharPositionInLine());
        }

    }
}
