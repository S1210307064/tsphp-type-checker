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
public class ForErrorTest extends ATypeCheckErrorTest
{

    private static List<Object[]> collection;

    public ForErrorTest(String testString, ReferenceErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        collection = new ArrayList<>();
        ReferenceErrorDto[] errorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("for", 2, 1)};
        
        
        String[] types = new String[]{"bool?", "int", "int?", "float", "float?", "string", "string?",
            "array", "resource", "object"};
        for (String type : types) {
            collection.add(new Object[]{type + " $b;\n for(;$b;);", errorDto});
            collection.add(new Object[]{type + " $b;\n for(;true, $b;);", errorDto});
            collection.add(new Object[]{type + " $b;\n for(;true, false,$b;);", errorDto});
        }
        
        return collection;
    }
}
