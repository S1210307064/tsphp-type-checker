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
public class InBuiltTypeTest extends AOperatorTypeCheckTest
{

    public InBuiltTypeTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                    {"bool $a = true;", new TypeCheckStruct[]{struct("true", Bool, 1, 0, 1, 0)}},
                    {"bool $a = false;", new TypeCheckStruct[]{struct("false", Bool, 1, 0, 1, 0)}},
                    {"int $a = 1;", new TypeCheckStruct[]{struct("1", Int, 1, 0, 1, 0)}},
                    {"float $a = 1.0;", new TypeCheckStruct[]{struct("1.0", Float, 1, 0, 1, 0)}},
                    {"string $a = 'hello';", new TypeCheckStruct[]{struct("'hello'", String, 1, 0, 1, 0)}},
                    {"string $a = \"hello\";", new TypeCheckStruct[]{struct("\"hello\"", String, 1, 0, 1, 0)}}
                });
    }
}
