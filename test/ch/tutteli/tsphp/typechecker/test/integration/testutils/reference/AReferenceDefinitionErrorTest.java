package ch.tutteli.tsphp.typechecker.test.integration.testutils.reference;

import ch.tutteli.tsphp.common.IErrorReporter;
import ch.tutteli.tsphp.common.exceptions.DefinitionException;
import ch.tutteli.tsphp.typechecker.error.DefinitionErrorDto;
import ch.tutteli.tsphp.typechecker.error.ErrorReporterRegistry;
import java.util.List;
import org.junit.Assert;
import org.junit.Ignore;

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
        verifyReferences(errorMessagePrefix, exceptions, errorDtos);
    }
    
    public static void verifyReferences(String errorMessagePrefix, List<Exception> exceptions, DefinitionErrorDto[] errorDtos){
        
        IErrorReporter errorReporter = ErrorReporterRegistry.get();
        Assert.assertTrue(errorMessagePrefix + " failed. No exception occured.", errorReporter.hasFoundError());

        Assert.assertEquals(errorMessagePrefix + " failed. More or less exceptions occured." + exceptions.toString(),
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
