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
import org.mockito.exceptions.base.MockitoAssertionError;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;


public class AssignmentOperatorWithReferenceErrorTest extends ATypeCheckWithReferenceErrorTest
{

    public AssignmentOperatorWithReferenceErrorTest() {
        super("");
    }

    @Override
    protected void verifyTypeCheck() {
        // nothing to check in addition.
        // No error other than the reference error should have occurred.
        // That's already checked in ATypeCheckWithReferenceErrorTest
    }

    //see TSPHP-785 - forward reference should not cause type checker exception (only reference exception)
    @Test
    public void checkAssignment_ForwardReference_OnlyReferenceException() throws RecognitionException {
        testString = "$s = 'hello'; string $s;";
        check();
        try {
            verify(typeCheckErrorReporter).forwardReference(any(ITSPHPAst.class), any(ITSPHPAst.class));
        } catch (MockitoAssertionError e) {
            System.err.println(testString + " failed.");
            throw e;
        }
    }

    //see TSPHP-785 - not defined variable should not cause type checker exception (only reference exception)
    @Test
    public void checkAssignment_NotDefined_OnlyReferenceException() throws RecognitionException {
        testString = "$s = 'hello';";
        check();
        try {
            verify(typeCheckErrorReporter).notDefined(any(ITSPHPAst.class));
        } catch (MockitoAssertionError e) {
            System.err.println(testString + " failed.");
            throw e;
        }
    }

    @Override
    protected ITypeCheckerErrorReporter createTypeCheckerErrorReporter() {
        return spy(super.createTypeCheckerErrorReporter());
    }
}

