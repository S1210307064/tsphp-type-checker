package ch.tutteli.tsphp.typechecker.test.integration.typecheck;

import ch.tutteli.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.ATypeCheckErrorTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class SwitchErrorTest extends ATypeCheckErrorTest
{

    public SwitchErrorTest(String testString, ReferenceErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();
        ReferenceErrorDto[] errorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("switch", 2, 1)};

        //wrong switch condition type
        String[][] types = new String[][]{
                {"array", "null"},
                {"resource", "null"},
                {"object", "null"},
                {"Exception", "null"},
                {"ErrorException", "null"}
        };
        for (String type[] : types) {
            collection.add(new Object[]{type[0] + " $b=" + type[1] + ";\n switch($b){}", errorDto});
        }

        // case label not same type or sub-type of condition
        errorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("$b", 2, 1)};

        addToCollection(collection, errorDto, "bool", "false", new String[][]{
                {"bool?", "null"},
                {"int", "0"},
                {"int?", "null"},
                {"float", "0.0"},
                {"float?", "null"},
                {"string", "''"},
                {"string?", "null"},
                {"array", "null"},
                {"resource", "null"},
                {"object", "null"},
                {"Exception", "null"},
                {"ErrorException", "null"}
        });

        addToCollection(collection, errorDto, "bool?", "null", new String[][]{
                {"int", "0"},
                {"int?", "null"},
                {"float", "0.0"},
                {"float?", "null"},
                {"string", "''"},
                {"string?", "null"},
                {"array", "null"},
                {"resource", "null"},
                {"object", "null"},
                {"Exception", "null"},
                {"ErrorException", "null"}
        });

        addToCollection(collection, errorDto, "int", "1", new String[][]{
                {"bool?", "null"},
                {"int?", "null"},
                {"float", "0.0"},
                {"float?", "null"},
                {"string", "''"},
                {"string?", "null"},
                {"array", "null"},
                {"resource", "null"},
                {"object", "null"},
                {"Exception", "null"},
                {"ErrorException", "null"}
        });

        addToCollection(collection, errorDto, "int?", "null", new String[][]{
                {"float", "0.0"},
                {"float?", "null"},
                {"string", "''"},
                {"string?", "null"},
                {"array", "null"},
                {"resource", "null"},
                {"object", "null"},
                {"Exception", "null"},
                {"ErrorException", "null"}
        });

        addToCollection(collection, errorDto, "float", "1.2", new String[][]{
                {"bool?", "null"},
                {"int?", "null"},
                {"float?", "null"},
                {"string", "''"},
                {"string?", "null"},
                {"array", "null"},
                {"resource", "null"},
                {"object", "null"},
                {"Exception", "null"},
                {"ErrorException", "null"}
        });

        addToCollection(collection, errorDto, "float?", "null", new String[][]{
                {"string", "''"},
                {"string?", "null"},
                {"array", "null"},
                {"resource", "null"},
                {"object", "null"},
                {"Exception", "null"},
                {"ErrorException", "null"}
        });

        addToCollection(collection, errorDto, "string", "''", new String[][]{
                {"bool?", "null"},
                {"int?", "null"},
                {"float?", "null"},
                {"string?", "null"},
                {"array", "null"},
                {"resource", "null"},
                {"object", "null"},
                {"Exception", "null"},
                {"ErrorException", "null"}
        });

        addToCollection(collection, errorDto, "string?", "null", new String[][]{
                {"array", "null"},
                {"resource", "null"},
                {"object", "null"},
                {"Exception", "null"},
                {"ErrorException", "null"}
        });


        return collection;
    }

    private static void addToCollection(List<Object[]> collection, ReferenceErrorDto[] errorDto,
            String variableType, String initialValue, String[][] types) {
        for (String type[] : types) {
            collection.add(new Object[]{
                    variableType + " $a=" + initialValue + "; "
                            + type[0] + " $b=" + type[1] + ";switch($a){case\n $b:}",
                    errorDto
            });
        }

    }
}
