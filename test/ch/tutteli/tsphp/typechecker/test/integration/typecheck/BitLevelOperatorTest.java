package ch.tutteli.tsphp.typechecker.test.integration.typecheck;

import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.TypeCheckStruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class BitLevelOperatorTest extends AOperatorTypeCheckTest
{

    public BitLevelOperatorTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();
        String[] arithmeticOperators = new String[]{"|", "&", "^", "<<", ">>"};
        for (String operator : arithmeticOperators) {
            collection.addAll(Arrays.asList(new Object[][]{
                {"true " + operator + " false;", new TypeCheckStruct[]{struct(operator, Int, 1, 0, 0)}},
                {"true " + operator + " 1;", new TypeCheckStruct[]{struct(operator, Int, 1, 0, 0)}},
                {"2 " + operator + " true;", new TypeCheckStruct[]{struct(operator, Int, 1, 0, 0)}},
                {"2 " + operator + " 5;", new TypeCheckStruct[]{struct(operator, Int, 1, 0, 0)}}
            }));
        }
        collection.addAll(Arrays.asList(new Object[][]{
            {"~true;", new TypeCheckStruct[]{struct("~", Int, 1, 0, 0)}},
            {"~false;", new TypeCheckStruct[]{struct("~", Int, 1, 0, 0)}},
            {"~23098;", new TypeCheckStruct[]{struct("~", Int, 1, 0, 0)}}
        }));
        return collection;
    }
}
