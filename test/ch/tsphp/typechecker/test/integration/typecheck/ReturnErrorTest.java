package ch.tsphp.typechecker.test.integration.typecheck;

import ch.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tsphp.typechecker.test.integration.testutils.TypeHelper;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.ATypeCheckErrorTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

        collection.add(new Object[]{"int $b=1; \n return $b;", errorDto});

        String[][] types = TypeHelper.getTypesInclDefaultValue();

        for (String[] type : types) {
            collection.add(new Object[]{
                    "function void foo(){" + type[0] + " $b=" + type[1] + ";\n return $b;}", errorDto
            });
            collection.add(new Object[]{
                    "class A{function void foo(){" + type[0] + " $b=" + type[1] + ";\n return $b;}}", errorDto
            });
        }

        for (int i = 0; i < types.length - 1; ++i) {
            if (types[i][0].equals("object") || types[i][0].equals("Exception")) {
                continue;
            }
            collection.add(new Object[]{
                    "function " + types[i][0] + " foo(){" + types[i + 1][0] + " $b=" + types[i + 1][1] + ";"
                            + "\n return $b;}",
                    errorDto
            });
        }

        collection.add(new Object[]{
                "function \\ErrorException foo(){Exception $b=null;\n " + "return $b;}",
                errorDto
        });
        return collection;
    }
}
