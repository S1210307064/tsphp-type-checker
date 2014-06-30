/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.typecheck;

import ch.tsphp.typechecker.error.ITypeCheckerErrorReporter;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.ATypeCheckWithParserErrorTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.mockito.Mockito.spy;


@RunWith(Parameterized.class)
public class AssignmentOperatorWithParserErrorTest extends ATypeCheckWithParserErrorTest
{

    public AssignmentOperatorWithParserErrorTest(String testString) {
        super(testString);
    }

    @Override
    protected void verifyTypeCheck() {
        // nothing to check in addition.
        // No error other than the parsing error and reference parsing error should have occurred.
        // That's already checked in ATypeCheckWithParserErrorTest
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                //see TSPHP-535 - const type missing, should cause parser error but no type check error
                {"const a = 1;"},
        });
    }

    @Override
    protected ITypeCheckerErrorReporter createTypeCheckErrorReporter() {
        return spy(super.createTypeCheckErrorReporter());
    }
}

