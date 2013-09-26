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
public class TryCatchTest extends AOperatorTypeCheckTest
{

    public TryCatchTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
            {"try{}catch(Exception $a){}", new TypeCheckStruct[]{struct("$a", Exception, 1, 0, 1, 0, 1)}},
            {"try{}catch(ErrorException $a){}", new TypeCheckStruct[]{struct("$a", ErrorException, 1, 0, 1, 0, 1)}}
        });
    }
}
