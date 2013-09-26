package ch.tutteli.tsphp.typechecker.test.integration.typecheck;

import ch.tutteli.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.ATypeCheckErrorTest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class ForeachErrorTest extends ATypeCheckErrorTest
{

    private static List<Object[]> collection;

    public ForeachErrorTest(String testString, ReferenceErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        collection = new ArrayList<>();
        ReferenceErrorDto[] errorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("foreach", 2, 1)};
        ReferenceErrorDto[] twoErrorDto = new ReferenceErrorDto[]{
            new ReferenceErrorDto("foreach", 2, 1),
            new ReferenceErrorDto("$k", 3, 1)
        };
        String[] types = new String[]{"bool", "bool?", "int", "int?", "float", "float?", "string", "string?",
            "resource", "object", "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{type + " $b;\n foreach($b as object $v);", errorDto});
            collection.add(new Object[]{type + " $b;\n foreach($b as string $k => object $v);", errorDto});
            collection.add(new Object[]{type + " $b;\n foreach($b as float\n $k => object $v);", twoErrorDto});
            collection.add(new Object[]{type + " $b;\n foreach($b as int\n $k => object $v);", twoErrorDto});
            collection.add(new Object[]{type + " $b;\n foreach($b as bool\n $k => object $v);", twoErrorDto});
        }
        types = new String[]{"bool", "bool?", "int", "int?", "float", "float?", "string", "string?", "array",
            "resource", "Exception", "ErrorException"};
        for (String type : types) {
            //only object is supported as type of the values at the moment
            collection.add(new Object[]{
                "foreach([1,2] as " + type + "\n $v);",
                new ReferenceErrorDto[]{new ReferenceErrorDto("$v", 2, 1)}
            });
        }
        return collection;
    }
}
