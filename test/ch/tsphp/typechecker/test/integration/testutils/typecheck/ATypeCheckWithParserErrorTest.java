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
public abstract class ATypeCheckWithParserErrorTest extends ATypeCheckTest
{
    public ATypeCheckWithParserErrorTest(String testString) {
        super(testString);
    }

    @Override
    protected void checkErrors() {
        verifyTypeCheck();
    }

    @Override
    protected void verifyParser() {
        assertTrue(
                testString.replaceAll("\n", " ") + " failed - parser exception was expected but nothing was thrown",
                parser.hasFoundError()
        );
    }

    @Override
    protected void checkReferences() {
        assertFalse(testString + " failed. Exceptions occurred." + exceptions, typeCheckErrorReporter.hasFoundError());
        assertTrue(testString + " failed. reference walker exceptions expected but nothing was thrown.",
                reference.hasFoundError());

        verifyReferences();
    }
}
