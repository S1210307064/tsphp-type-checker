/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.typecheck;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.typechecker.error.ITypeCheckerErrorReporter;
import ch.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.ATypeCheckErrorTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.exceptions.base.MockitoAssertionError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class ClassMemberInitialValueWrongTypeErrorTest extends ATypeCheckErrorTest
{

    public ClassMemberInitialValueWrongTypeErrorTest(String testString, ReferenceErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void test() throws RecognitionException {
        check();
        try {
            verify(typeCheckErrorReporter).wrongClassMemberInitialValue(
                    any(ITSPHPAst.class), any(ITSPHPAst.class), any(ITypeSymbol.class));
        } catch (MockitoAssertionError e) {
            System.err.println(testString + " failed.");
            throw e;
        }
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();
        Object[][] typeAndInitialValues = new Object[][]{
                {"bool", new String[]{"1", "1.2", "'hello'", "null", "[]"}},
                {"bool!", new String[]{"1", "1.2", "'hello'", "null", "[]"}},
                {"bool?", new String[]{"1", "1.2", "'hello'", "[]"}},
                {"bool!?", new String[]{"1", "1.2", "'hello'", "[]"}},
                {"int", new String[]{"false", "1.2", "'hello'", "null", "[]"}},
                {"int!", new String[]{"1.2", "'hello'", "null", "[]"}},
                {"int?", new String[]{"false", "1.2", "'hello'", "[]"}},
                {"int!?", new String[]{"1.2", "'hello'", "[]"}},
                {"float", new String[]{"false", "1", "'hello'", "null", "[]"}},
                {"float!", new String[]{"1", "'hello'", "null", "[]"}},
                {"float?", new String[]{"false", "1", "'hello'", "[]"}},
                {"float!?", new String[]{"1", "'hello'", "[]"}},
                {"string", new String[]{"false", "1", "1.2", "null", "[]"}},
                {"string!", new String[]{"1", "1.2", "null", "[]"}},
                {"string?", new String[]{"false", "1", "1.2", "[]"}},
                {"string!?", new String[]{"1", "1.2", "[]"}},
                {"array", new String[]{"false", "1", "'1.2'", "'hello'"}},
                {"array!", new String[]{"1", "'1.2'", "'hello'"}},
                {"resource", new String[]{"false", "1", "'1.2'", "'hello'", "[]"}},
                {"resource!", new String[]{"1", "'1.2'", "'hello'", "[]"}},
                {"Exception", new String[]{"false", "1", "'1.2'", "'hello'", "[]"}},
                {"Exception!", new String[]{"1", "'1.2'", "'hello'", "[]"}},
                {"ErrorException", new String[]{"false", "1", "'1.2'", "'hello'", "[]"}},
                {"ErrorException!", new String[]{"1", "'1.2'", "'hello'", "[]"}},
        };

        for (Object[] tuple : typeAndInitialValues) {
            String type = (String) tuple[0];
            for (String value : (String[]) tuple[1]) {
                collection.add(new Object[]{
                        "class A{ cast " + type + "\n $a = " + value + "; }", refErrorDto("$a", 2, 1)
                });
            }
        }
        return collection;
    }

    @Override
    protected ITypeCheckerErrorReporter createTypeCheckerErrorReporter() {
        return spy(super.createTypeCheckerErrorReporter());
    }
}

