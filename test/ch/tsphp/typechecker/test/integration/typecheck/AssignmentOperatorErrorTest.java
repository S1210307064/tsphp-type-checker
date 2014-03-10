/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.typecheck;

import ch.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.ATypeCheckErrorTest;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.AssignHelper;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;


@RunWith(Parameterized.class)
public class AssignmentOperatorErrorTest extends ATypeCheckErrorTest
{

    public AssignmentOperatorErrorTest(String testString, ReferenceErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        Collection<Object[]> collection = AssignHelper.getAssignmentErrorTestStrings(false);


        collection.addAll(Arrays.asList(new Object[][]{
                //see TSPHP-490 - erroneous symbols should not been investigated further
                {"int $a =\n nonExistingFunction();", refErrorDto("nonExistingFunction()", 2, 1)},
                {
                        "int $notAClassSymbol=1; int $a =\n $notAClassSymbol->foo();",
                        refErrorDto("$notAClassSymbol", 2, 1)
                },
                {"int $a; $a =\n nonExistingFunction();", refErrorDto("nonExistingFunction()", 2, 1)},
                {
                        "int $notAClassSymbol=1; int $a; $a =\n $notAClassSymbol->foo();",
                        refErrorDto("$notAClassSymbol", 2, 1)
                },
                //see TSPHP-698 - const assignment is only allowed in the declaration
                {"const int a = 1;\n a = 2;", refErrorDto("a#", 2, 1)}
        }));
        return collection;
    }
}

