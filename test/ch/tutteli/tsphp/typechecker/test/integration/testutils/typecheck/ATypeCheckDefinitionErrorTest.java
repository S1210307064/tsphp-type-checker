package ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck;

import ch.tutteli.tsphp.typechecker.error.DefinitionErrorDto;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.reference.AReferenceDefinitionErrorTest;
import org.junit.Ignore;

@Ignore
public abstract class ATypeCheckDefinitionErrorTest extends ATypeCheckTest
{

    protected DefinitionErrorDto[] errorDtos;

    public ATypeCheckDefinitionErrorTest(String testString, DefinitionErrorDto[] theErrorDtos) {
        super(testString);
        errorDtos = theErrorDtos;

    }

    @Override
    protected void checkErrors() {


        verifyTypeCheck();
    }

    @Override
    protected void verifyTypeCheck() {
        AReferenceDefinitionErrorTest.verifyReferences(errorMessagePrefix, exceptions, errorDtos);
    }
}
