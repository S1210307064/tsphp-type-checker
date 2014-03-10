/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.testutils.typecheck;

import ch.tsphp.typechecker.error.DefinitionErrorDto;
import ch.tsphp.typechecker.test.integration.testutils.reference.AReferenceDefinitionErrorTest;
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
        AReferenceDefinitionErrorTest.verifyReferences(
                errorMessagePrefix, exceptions, errorDtos, typeCheckErrorReporter);
    }
}
