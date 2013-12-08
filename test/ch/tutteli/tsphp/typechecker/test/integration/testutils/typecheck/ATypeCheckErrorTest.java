package ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck;

import ch.tutteli.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.reference.AReferenceErrorTest;
import org.junit.Ignore;

@Ignore
public abstract class ATypeCheckErrorTest extends ATypeCheckTest
{

    protected ReferenceErrorDto[] errorDtos;

    public ATypeCheckErrorTest(String testString, ReferenceErrorDto[] theErrorDtos) {
        super(testString);
        errorDtos = theErrorDtos;
    }

    @Override
    protected void checkErrors() {
        verifyTypeCheck();
    }

    @Override
    protected void verifyTypeCheck() {
        AReferenceErrorTest.verifyReferences(errorMessagePrefix, exceptions, errorDtos);
    }
}
