package ch.tsphp.typechecker.test.integration.typecheck;

import ch.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tsphp.typechecker.test.integration.testutils.TypeHelper;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.ATypeCheckErrorTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
                {"class A{} class B{} A $a=null; $a \n instanceof B;", errorDto},
                {"class A{} class B{} A $a=null; B $b=null; $a \n instanceof $b;", errorDto}
        }));


        String[][] types = TypeHelper.getTypesInclDefaultValueWithoutExceptions();
        String[][] types2 = new String[][]{{"Exception", "null"}, {"ErrorException", "null"}};

        ReferenceErrorDto[] left = new ReferenceErrorDto[]{new ReferenceErrorDto("$a", 2, 1)};
        ReferenceErrorDto[] right = new ReferenceErrorDto[]{new ReferenceErrorDto("$b", 3, 1)};
        ReferenceErrorDto[] both = new ReferenceErrorDto[]{
                new ReferenceErrorDto("$a", 2, 1),
                new ReferenceErrorDto("$b", 3, 1)
        };

        for (String[] type : types) {
            for (String[] type2 : types2) {
                collection.add(new Object[]{
                        type[0] + " $a=" + type[1] + ";" + type2[0] + " $b=" + type2[1] + ";\n $a instanceof\n $b;",
                        left
                });
            }
            for (String[] type2 : types) {
                collection.add(new Object[]{
                        type[0] + " $a=" + type[1] + ";" + type2[0] + " $b=" + type2[1] + ";\n $a instanceof\n $b;",
                        both
                });
            }
        }

        for (String[] type : types2) {
            for (String[] type2 : types) {
                collection.add(new Object[]{
                        type[0] + " $a=" + type[1] + ";" + type2[0] + " $b=" + type2[1] + ";\n $a instanceof\n $b;",
                        right
                });
            }
        }
        return collection;
    }
}
