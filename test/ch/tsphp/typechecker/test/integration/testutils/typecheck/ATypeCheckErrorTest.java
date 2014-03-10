/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

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
        AReferenceErrorTest.verifyReferences(errorMessagePrefix, exceptions, errorDtos, typeCheckErrorReporter);
    }

    public static ReferenceErrorDto[] refErrorDto(String identifier, int line, int position) {
        return new ReferenceErrorDto[]{new ReferenceErrorDto(identifier, line, position)};
    }
}
