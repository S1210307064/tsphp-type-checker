package ch.tutteli.tsphp.typechecker.test.integration.typecheck;

import ch.tutteli.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.ATypeCheckErrorTest;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.AssignHelper;
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

        //see TSPHP-490 - erroneous symbols should not been investigated further
        collection.addAll(Arrays.asList(new Object[][]{
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
        }));
        return collection;
    }
}

