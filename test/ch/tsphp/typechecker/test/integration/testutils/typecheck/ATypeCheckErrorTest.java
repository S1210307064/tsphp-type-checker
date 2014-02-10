package ch.tsphp.typechecker.test.integration.testutils.typecheck;

import ch.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tsphp.typechecker.test.integration.testutils.reference.AReferenceErrorTest;
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

    public static ReferenceErrorDto[] refErrorDto(String identifier, int line, int position) {
        return new ReferenceErrorDto[]{new ReferenceErrorDto(identifier, line, position)};
    }
}
