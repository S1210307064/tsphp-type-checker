/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.typecheck;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.error.ITypeCheckerErrorReporter;
import ch.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tsphp.typechecker.test.integration.testutils.TypeHelper;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.ATypeCheckErrorTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.exceptions.base.MockitoAssertionError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class ClassMemberInitialValueSingleOnlyErrorTest extends ATypeCheckErrorTest
{

    public ClassMemberInitialValueSingleOnlyErrorTest(String testString,
            ReferenceErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void test() throws RecognitionException {
        check();
        try {
            verify(typeCheckErrorReporter).onlySingleValue(any(ITSPHPAst.class), any(ITSPHPAst.class));
        } catch (MockitoAssertionError e) {
            System.err.println(testString + " failed.");
            throw e;
        }
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();
        String[][] types = TypeHelper.getAllTypesInclDefaultValueWithoutExceptions();
        for (String[] type : types) {
            collection.add(new Object[]{
                    "class A{" + type[0] + " $a; " + type[0] + "\n $b = $this->a;}",
                    refErrorDto("$b", 2, 1)
            });
        }

        collection.addAll(Arrays.asList(new Object[][]{
                {"class A{bool   \n $b = true & false; }", refErrorDto("$b", 2, 1)},
                {"class A{bool!  \n $b = true & false; }", refErrorDto("$b", 2, 1)},
                {"class A{bool?  \n $b = true & false; }", refErrorDto("$b", 2, 1)},
                {"class A{bool!? \n $b = true & false; }", refErrorDto("$b", 2, 1)},
                {"class A{int   \n $b = 1 + 1; }", refErrorDto("$b", 2, 1)},
                {"class A{int!  \n $b = 1 + 4; }", refErrorDto("$b", 2, 1)},
                {"class A{int?  \n $b = 1 + 3; }", refErrorDto("$b", 2, 1)},
                {"class A{int!? \n $b = 1 + 2; }", refErrorDto("$b", 2, 1)},
                {"class A{float   \n $b = 1.0 + 1; }", refErrorDto("$b", 2, 1)},
                {"class A{float!  \n $b = 1.3 + 4; }", refErrorDto("$b", 2, 1)},
                {"class A{float?  \n $b = 1.4 + 3; }", refErrorDto("$b", 2, 1)},
                {"class A{float!? \n $b = 1.5 + 2; }", refErrorDto("$b", 2, 1)},
        }));

        return collection;
    }

    @Override
    protected ITypeCheckerErrorReporter createTypeCheckerErrorReporter() {
        return spy(super.createTypeCheckerErrorReporter());
    }
}

