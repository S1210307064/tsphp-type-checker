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
public class ThrowTest extends AOperatorTypeCheckTest
{

    public ThrowTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
         return Arrays.asList(new Object[][]{
            {"Exception $a; throw $a;", new TypeCheckStruct[]{struct("$a", Exception, 1, 1, 0)}},
            {"ErrorException $a; throw $a;", new TypeCheckStruct[]{struct("$a", ErrorException, 1, 1, 0)}},
           
        });
    }
}
