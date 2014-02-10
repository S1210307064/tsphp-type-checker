package ch.tsphp.typechecker.test.integration.typecheck;

import ch.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.TypeCheckStruct;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class LogicOperatorTest extends AOperatorTypeCheckTest
{

    public LogicOperatorTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();
        String[] arithmeticOperators = new String[]{"or", "xor", "and", "||", "&&"};
        for (String operator : arithmeticOperators) {
            collection.addAll(Arrays.asList(new Object[][]{
                    {"true " + operator + " true;", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                    {"false " + operator + " false;", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                    {"true " + operator + " false;", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                    {"false " + operator + " true;", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                    {"true " + operator + " false " + operator + " true;", new TypeCheckStruct[]{
                            struct(operator, Bool, 1, 0, 0),
                            struct(operator, Bool, 1, 0, 0, 0)
                    }
                    },}));
        }
        collection.addAll(Arrays.asList(new Object[][]{
                {"!true;", new TypeCheckStruct[]{struct("!", Bool, 1, 0, 0)}},
                {"!false;", new TypeCheckStruct[]{struct("!", Bool, 1, 0, 0)}}
        }));
        return collection;
    }
}
