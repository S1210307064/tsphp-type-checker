/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.testutils.typecheck;

import org.junit.Ignore;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Ignore
public abstract class ATypeCheckWithReferenceErrorTest extends ATypeCheckTest
{
    public ATypeCheckWithReferenceErrorTest(String testString) {
        super(testString);
    }

    @Override
    protected void checkErrors() {
        verifyTypeCheck();
    }

    @Override
    protected void registerReferenceErrorLogger() {
        // no need to write the errors to the console
    }

    @Override
    protected void checkReferences() {
        assertTrue(testString + " failed. Exceptions expected but nothing was thrown." + exceptions,
                typeCheckErrorReporter.hasFoundError());
        assertFalse(testString + " failed. reference walker exceptions occurred", reference.hasFoundError());

        verifyReferences();
    }
}
