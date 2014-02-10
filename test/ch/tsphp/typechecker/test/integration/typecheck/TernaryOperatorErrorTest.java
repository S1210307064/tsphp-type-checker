package ch.tsphp.typechecker.test.integration.typecheck;

import ch.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.ATypeCheckErrorTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class TernaryOperatorErrorTest extends ATypeCheckErrorTest
{

    public TernaryOperatorErrorTest(String testString, ReferenceErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        //I think there are enough test which cover the identity check. Thus here only a few tests
        return Arrays.asList(new Object[][]{
                //condition not boolean
                {"bool? $a=null; bool? $b=null; $a\n ? $a : $b;", refErrorDto("?", 2, 1)},
                //not same type hierarchy
                {"bool? $a=null; int   $b=1;  true ? $a : \n $b;", refErrorDto("$b", 2, 1)},
                {"bool? $a=null; array $b=[]; true ? $a : \n $b;", refErrorDto("$b", 2, 1)}
        });
    }
}
