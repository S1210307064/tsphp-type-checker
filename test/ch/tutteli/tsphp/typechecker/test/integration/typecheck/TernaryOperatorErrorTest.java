package ch.tutteli.tsphp.typechecker.test.integration.typecheck;

import ch.tutteli.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.ATypeCheckErrorTest;
import java.util.Arrays;
import java.util.Collection;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

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
            {"bool? $a; bool? $b; $a\n ? $a : $b;", new ReferenceErrorDto[]{new ReferenceErrorDto("?", 2, 1)}},
            //not same type hierarchy
            {"bool? $a; int $b; true ? $a : \n $b;", new ReferenceErrorDto[]{new ReferenceErrorDto("$b", 2, 1)}},
            {"bool? $a; array $b; true ? $a : \n $b;", new ReferenceErrorDto[]{new ReferenceErrorDto("$b", 2, 1)}}
        });
    }
}
