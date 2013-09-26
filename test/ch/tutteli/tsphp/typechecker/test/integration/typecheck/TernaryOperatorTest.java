
package ch.tutteli.tsphp.typechecker.test.integration.typecheck;

import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.TypeCheckStruct;

import java.util.Arrays;
import java.util.Collection;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TernaryOperatorTest extends AOperatorTypeCheckTest
{

    public TernaryOperatorTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        //I think there are enough test which cover the identity check. Thus here only a few tests
        return Arrays.asList(new Object[][]{
            //same types
            {"float $a; float $b; true ? $a : $b;", new TypeCheckStruct[]{
                    AOperatorTypeCheckTest.struct("?", Float, 1, 2, 0),
                    AOperatorTypeCheckTest.struct("true", Bool, 1, 2, 0, 0),
                    AOperatorTypeCheckTest.struct("$a", Float, 1, 2, 0, 1),
                    AOperatorTypeCheckTest.struct("$b", Float, 1, 2, 0, 2)}
            },
            //parent type left
            {"string $a; int $b; true ? $a : $b;", new TypeCheckStruct[]{
                    AOperatorTypeCheckTest.struct("?", String, 1, 2, 0),
                    AOperatorTypeCheckTest.struct("true", Bool, 1, 2, 0, 0),
                    AOperatorTypeCheckTest.struct("$a", String, 1, 2, 0, 1),
                    AOperatorTypeCheckTest.struct("$b", Int, 1, 2, 0, 2)}
            },
            //parent type right
            {"bool $a; int $b; true ? $a : $b;", new TypeCheckStruct[]{
                    AOperatorTypeCheckTest.struct("?", Int, 1, 2, 0),
                    AOperatorTypeCheckTest.struct("true", Bool, 1, 2, 0, 0),
                    AOperatorTypeCheckTest.struct("$a", Bool, 1, 2, 0, 1),
                    AOperatorTypeCheckTest.struct("$b", Int, 1, 2, 0, 2)}
            },
        });
    }
}
