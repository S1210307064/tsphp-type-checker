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
public class WhileErrorTest extends ATypeCheckErrorTest
{

    public WhileErrorTest(String testString, ReferenceErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        String[] types = new String[]{"bool?", "int", "int?", "float", "float?", "string", "string?",
            "array", "resource", "object"};
        for (String type : types) {
            collection.add(new Object[]{
                    type + " $b;\n while($b);",
                    new ReferenceErrorDto[]{new ReferenceErrorDto("while", 2, 1)}
            });
            collection.add(new Object[]{
                    type + " $b;\n do;while($b);",
                    new ReferenceErrorDto[]{new ReferenceErrorDto("do", 2, 1)}
            });
        }

        return collection;
    }
}
