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
public class NewOperatorWithoutParameterTest extends AOperatorTypeCheckTest
{

    public NewOperatorWithoutParameterTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
         return Arrays.asList(new Object[][]{
            {"new Exception;", new TypeCheckStruct[]{struct("new", Exception, 1, 0, 0)}},
            {"new Exception();", new TypeCheckStruct[]{struct("new", Exception, 1,0, 0)}},
            {"new ErrorException;", new TypeCheckStruct[]{struct("new", ErrorException, 1, 0, 0)}},
            {"new ErrorException();", new TypeCheckStruct[]{struct("new", ErrorException, 1, 0, 0)}},
        }); 
    }
}
