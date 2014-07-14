/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.typecheck;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.error.ITypeCheckerErrorReporter;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.ATypeCheckWithReferenceErrorTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.exceptions.base.MockitoAssertionError;

import java.util.Arrays;
import java.util.Collection;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class BinaryOperatorWithReferenceErrorTest extends ATypeCheckWithReferenceErrorTest
{

    public BinaryOperatorWithReferenceErrorTest(String testString) {
        super(testString);
    }

    @Override
    protected void verifyTypeCheck() {
        // nothing to check in addition.
        // No error other than the reference error should have occurred.
        // That's already checked in ATypeCheckWithReferenceErrorTest
    }

    @Test
    public void test() throws RecognitionException {
        check();
        try {
            verify(typeCheckErrorReporter).notDefined(any(ITSPHPAst.class));
        } catch (MockitoAssertionError e) {
            System.err.println(testString + " failed.");
            throw e;
        }
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                //see TSPHP-735 - operation with undefined variable/constant results in NullPointerException
                {"if(NOT_DEFINED_CONSTANT < 10){}"},
                {"if(10 < NOT_DEFINED_CONSTANT){}"},
                {"if($notDefinedVariable < 10){}"},
                {"if(10 < $notDefinedVariable){}"}
        });
    }

    @Override
    protected ITypeCheckerErrorReporter createTypeCheckerErrorReporter() {
        return spy(super.createTypeCheckerErrorReporter());
    }
}


