/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.typecheck;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.IAccessResolver;
import ch.tsphp.typechecker.IOverloadResolver;
import ch.tsphp.typechecker.ISymbolResolver;
import ch.tsphp.typechecker.ITypeCheckPhaseController;
import ch.tsphp.typechecker.ITypeSystem;
import ch.tsphp.typechecker.error.ITypeCheckerErrorReporter;
import ch.tsphp.typechecker.test.integration.testutils.TestSymbolFactory;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.ATypeCheckWithParserErrorTest;
import ch.tsphp.typechecker.utils.ITypeCheckerAstHelper;
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
public class UnaryOperatorWithParserErrorTest extends ATypeCheckWithParserErrorTest
{

    public UnaryOperatorWithParserErrorTest(String testString) {
        super(testString);
    }

    @Override
    protected void verifyTypeCheck() {
        // nothing to check in addition.
        // No error other than the reference error should have occurred.
        // That's already checked in ATypeCheckWithReferenceErrorTest
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void test() throws RecognitionException {
        check();
        try {
            verify(typeCheckPhaseController).createErroneousTypeForMissingSymbol(any(ITSPHPAst.class));
        } catch (MockitoAssertionError e) {
            System.err.println(testString + " failed.");
            throw e;
        }
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                //see TSPHP-734 increment constant causes NullPointerException
                {"const int foo = 1; ++foo;"},
                {"const int foo = 1; --foo;"},
        });
    }

    @Override
    protected ITypeCheckPhaseController createTypeCheckerController(
            TestSymbolFactory theSymbolFactory,
            ISymbolResolver theSymbolResolver,
            ITypeCheckerErrorReporter theTypeCheckerErrorReporter,
            ITypeSystem theTypeSystem,
            IOverloadResolver theOverloadResolver,
            IAccessResolver theVisibilityChecker,
            ITypeCheckerAstHelper theAstHelper) {

        return spy(super.createTypeCheckerController(
                theSymbolFactory,
                theSymbolResolver,
                theTypeCheckerErrorReporter,
                theTypeSystem,
                theOverloadResolver,
                theVisibilityChecker,
                theAstHelper));
    }
}


