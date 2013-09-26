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
public class WhileTest extends AOperatorTypeCheckTest
{

    public WhileTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
            {"while(true);", new TypeCheckStruct[]{struct("true", Bool, 1, 0, 0)}},
            {"while(false);", new TypeCheckStruct[]{struct("false", Bool, 1, 0, 0)}},
            {"do;while(true);", new TypeCheckStruct[]{struct("true", Bool, 1, 0, 1)}},
            {"do;while(false);", new TypeCheckStruct[]{struct("false", Bool, 1, 0, 1)}}
        });
    }
}
