package ch.tutteli.tsphp.typechecker.test.integration.typecheck;

import ch.tutteli.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.ATypeCheckErrorTest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class InstanceofErrorTest extends ATypeCheckErrorTest
{

    public InstanceofErrorTest(String testString, ReferenceErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        ReferenceErrorDto[] errorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("instanceof", 2, 1)};

        collection.addAll(Arrays.asList(new Object[][]{
            {"class A{} class B{} A $a; $a \n instanceof B;", errorDto},
            {"class A{} class B{} A $a; B $b; $a \n instanceof $b;", errorDto}
        }));

        String[] types = new String[]{"bool", "bool?", "int", "int?", "float", "float?", "string", "string?", "array",
            "resource", "object"};
        String[] types2 = new String[]{"Exception", "ErrorException"};

        ReferenceErrorDto[] left = new ReferenceErrorDto[]{new ReferenceErrorDto("$a", 2, 1)};
        ReferenceErrorDto[] right = new ReferenceErrorDto[]{new ReferenceErrorDto("$b", 3, 1)};
        ReferenceErrorDto[] both = new ReferenceErrorDto[]{
            new ReferenceErrorDto("$a", 2, 1),
            new ReferenceErrorDto("$b", 3, 1)
        };

        for (String type : types) {
            for (String type2 : types2) {
                collection.add(new Object[]{type + " $a;" + type2 + " $b;\n $a instanceof\n $b;", left});
            }
            for (String type2 : types) {
                collection.add(new Object[]{type + " $a;" + type2 + " $b;\n $a instanceof\n $b;", both});
            }
        }

        for (String type : types2) {
            for (String type2 : types) {
                collection.add(new Object[]{type + " $a;" + type2 + " $b;\n $a instanceof\n $b;", right});
            }
        }
        return collection;
    }
}
