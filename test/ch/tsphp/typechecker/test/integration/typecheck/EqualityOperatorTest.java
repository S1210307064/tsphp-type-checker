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
public class EqualityOperatorTest extends AOperatorTypeCheckTest
{

    public EqualityOperatorTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();
        String[] operators = new String[]{"==", "!="};
        for (String operator : operators) {
            collection.addAll(Arrays.asList(new Object[][]{
                    {"true " + operator + " false;", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                    {"false " + operator + " 1;", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                    {"1 " + operator + " 1;", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                    {"1 " + operator + " 1;", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                    {"true " + operator + " 1.0;", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                    {"1.0 " + operator + " false;", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                    {"1 " + operator + " 2.0;", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                    {"2.0 " + operator + " 1;", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                    {"true " + operator + " 'hello';", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                    {"'hello' " + operator + " false;", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                    {"1 " + operator + " 'hello';", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                    {"'hello' " + operator + " 1;", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                    {"4.5 " + operator + " 'hello';", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                    {"'hello' " + operator + " .1e1;", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                    {"[1,2] " + operator + " array(7,8);", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                    {
                            "resource $a=null; resource $b=null; $a " + operator + " $b;",
                            new TypeCheckStruct[]{struct(operator, Bool, 1, 2, 0)}
                    },}));
        }
        return collection;
    }
}
