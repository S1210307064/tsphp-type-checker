package ch.tutteli.tsphp.typechecker.test.integration.typecheck;

import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.TypeCheckStruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class ReturnTest extends AOperatorTypeCheckTest
{

    public ReturnTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        collection.add(new Object[]{"function void a(){return;}", new TypeCheckStruct[]{}});
        collection.add(new Object[]{"class A{function void a(){return;}}", new TypeCheckStruct[]{}});


        Object[][] types = new Object[][]{
            {"bool", Bool},
            {"bool?", BoolNullable},
            {"int", Int},
            {"int?", IntNullable},
            {"float", Float},
            {"float?", FloatNullable},
            {"string", String},
            {"string?", StringNullable},
            {"array", Array},
            {"resource", Resource},
            {"Exception", Exception},
            {"ErrorException", ErrorException}
        };

        for (Object[] type : types) {
            collection.add(new Object[]{
                "function " + type[0] + " a(){" + type[0] + " $b; return $b;}",
                new TypeCheckStruct[]{struct("return", (EBuiltInType) type[1], 1, 0, 4, 1)}
            });
            collection.add(new Object[]{
                "class A{function " + type[0] + " a(){" + type[0] + " $b; return $b;}}",
                new TypeCheckStruct[]{struct("return", (EBuiltInType) type[1], 1, 0, 4, 0, 4, 1)}
            });
        }
        
        return collection;
    }
}
