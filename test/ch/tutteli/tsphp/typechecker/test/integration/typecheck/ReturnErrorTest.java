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
public class ReturnErrorTest extends ATypeCheckErrorTest
{

    public ReturnErrorTest(String testString, ReferenceErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();
        ReferenceErrorDto[] errorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("return", 2, 1)};

        collection.add(new Object[]{
            "int $b; \n return $b;",
            errorDto
        });

        String[] types = new String[]{"bool", "bool?", "int", "int?", "float", "float?", "string", "string?",
            "array", "resource", "ErrorException", "Exception"};

        for (String type : types) {
            collection.add(new Object[]{"function void foo(){" + type + " $b;\n return $b;}", errorDto});
            collection.add(new Object[]{"class A{function void foo(){" + type + " $b;\n return $b;}}", errorDto});
        }

        for (int i = 0; i < types.length - 1; ++i) {
            collection.add(new Object[]{
                "function " + types[i] + " foo(){" + types[i + 1] + " $b;\n return $b;}",
                errorDto
            });
        }
        collection.add(new Object[]{
            "function " + types[types.length - 1] + " foo(){" + types[0] + " $b;\n return $b;}",
            errorDto
        });
        return collection;
    }
}
