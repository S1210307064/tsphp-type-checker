package ch.tsphp.typechecker.test.integration.typecheck;

import ch.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.TypeCheckStruct;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ForTest extends AOperatorTypeCheckTest
{

    public ForTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                {"for(int $i=0;$i < 2;++$i);", new TypeCheckStruct[]{struct("<", Bool, 1, 0, 1, 0)}},
                {"for(int $i=0;$i, $i < 2;++$i);", new TypeCheckStruct[]{struct("<", Bool, 1, 0, 1, 1)}},
                {"for(int $i=0;$i, 1+1, true;++$i);", new TypeCheckStruct[]{struct("true", Bool, 1, 0, 1, 2)}},
        });
    }
}
