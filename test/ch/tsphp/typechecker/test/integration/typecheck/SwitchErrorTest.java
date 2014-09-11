/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.typecheck;

import ch.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.ATypeCheckErrorTest;
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
        ReferenceErrorDto[] errorDto = refErrorDto("switch", 2, 1);

        //wrong switch condition type
        String[][] types = new String[][]{
                {"array", "null"},
                {"array!", "null"},
                {"resource", "null"},
                {"resource!", "null"},
                {"mixed", "null"},
                {"Exception", "null"},
                {"Exception!", "null"},
                {"ErrorException", "null"},
                {"ErrorException!", "null"}
        };
        for (String type[] : types) {
            collection.add(new Object[]{type[0] + " $b=" + type[1] + ";\n switch($b){}", errorDto});
        }

        // case label not same type or sub-type of condition
        errorDto = refErrorDto("$b", 2, 1);

        addToCollection(collection, errorDto, "bool", "false", new String[][]{
                {"bool!", "false"},
                {"bool?", "null"},
                {"bool!?", "null"},
                {"int", "0"},
                {"int!", "false"},
                {"int?", "null"},
                {"int!?", "null"},
                {"float", "0.0"},
                {"float!", "false"},
                {"float?", "null"},
                {"float!?", "null"},
                {"string", "''"},
                {"string!", "false"},
                {"string?", "null"},
                {"string!?", "null"},
                {"array", "null"},
                {"array!", "null"},
                {"resource", "null"},
                {"resource!", "null"},
                {"mixed", "null"},
                {"Exception", "null"},
                {"Exception!", "null"},
                {"ErrorException", "null"},
                {"ErrorException!", "null"}
        });

        addToCollection(collection, errorDto, "bool!", "false", new String[][]{
                {"bool?", "null"},
                {"bool!?", "null"},
                {"int", "0"},
                {"int!", "false"},
                {"int?", "null"},
                {"int!?", "null"},
                {"float", "0.0"},
                {"float!", "false"},
                {"float?", "null"},
                {"float!?", "null"},
                {"string", "''"},
                {"string!", "false"},
                {"string?", "null"},
                {"string!?", "null"},
                {"array", "null"},
                {"array!", "null"},
                {"resource", "null"},
                {"resource!", "null"},
                {"mixed", "null"},
                {"Exception", "null"},
                {"Exception!", "null"},
                {"ErrorException", "null"},
                {"ErrorException!", "null"}
        });

        addToCollection(collection, errorDto, "bool?", "null", new String[][]{
                {"bool!", "false"},
                {"bool!?", "null"},
                {"int", "0"},
                {"int!", "false"},
                {"int?", "null"},
                {"int!?", "null"},
                {"float", "0.0"},
                {"float!", "false"},
                {"float?", "null"},
                {"float!?", "null"},
                {"string", "''"},
                {"string!", "false"},
                {"string?", "null"},
                {"string!?", "null"},
                {"array", "null"},
                {"array!", "null"},
                {"resource", "null"},
                {"resource!", "null"},
                {"mixed", "null"},
                {"Exception", "null"},
                {"Exception!", "null"},
                {"ErrorException", "null"},
                {"ErrorException!", "null"}
        });

        addToCollection(collection, errorDto, "bool!?", "null", new String[][]{
                {"int", "0"},
                {"int!", "false"},
                {"int?", "null"},
                {"int!?", "null"},
                {"float", "0.0"},
                {"float!", "false"},
                {"float?", "null"},
                {"float!?", "null"},
                {"string", "''"},
                {"string!", "false"},
                {"string?", "null"},
                {"string!?", "null"},
                {"array", "null"},
                {"array!", "null"},
                {"resource", "null"},
                {"resource!", "null"},
                {"mixed", "null"},
                {"Exception", "null"},
                {"Exception!", "null"},
                {"ErrorException", "null"},
                {"ErrorException!", "null"}
        });

        addToCollection(collection, errorDto, "int", "1", new String[][]{
                {"bool!", "false"},
                {"bool?", "null"},
                {"bool!?", "null"},
                {"int!", "false"},
                {"int?", "null"},
                {"int!?", "null"},
                {"float", "0.0"},
                {"float!", "false"},
                {"float?", "null"},
                {"float!?", "null"},
                {"string", "''"},
                {"string!", "false"},
                {"string?", "null"},
                {"string!?", "null"},
                {"array", "null"},
                {"array!", "null"},
                {"resource", "null"},
                {"resource!", "null"},
                {"mixed", "null"},
                {"Exception", "null"},
                {"Exception!", "null"},
                {"ErrorException", "null"},
                {"ErrorException!", "null"}
        });

        addToCollection(collection, errorDto, "int!", "false", new String[][]{
                {"bool?", "null"},
                {"bool!?", "null"},
                {"int?", "null"},
                {"int!?", "null"},
                {"float", "0.0"},
                {"float!", "false"},
                {"float?", "null"},
                {"float!?", "null"},
                {"string", "''"},
                {"string!", "false"},
                {"string?", "null"},
                {"string!?", "null"},
                {"array", "null"},
                {"array!", "null"},
                {"resource", "null"},
                {"resource!", "null"},
                {"mixed", "null"},
                {"Exception", "null"},
                {"Exception!", "null"},
                {"ErrorException", "null"},
                {"ErrorException!", "null"}
        });

        addToCollection(collection, errorDto, "int?", "null", new String[][]{
                {"bool!", "false"},
                {"bool!?", "null"},
                {"int!", "false"},
                {"int!?", "null"},
                {"float", "0.0"},
                {"float!", "false"},
                {"float?", "null"},
                {"float!?", "null"},
                {"string", "''"},
                {"string!", "false"},
                {"string?", "null"},
                {"string!?", "null"},
                {"array", "null"},
                {"array!", "null"},
                {"resource", "null"},
                {"resource!", "null"},
                {"mixed", "null"},
                {"Exception", "null"},
                {"Exception!", "null"},
                {"ErrorException", "null"},
                {"ErrorException!", "null"}
        });

        addToCollection(collection, errorDto, "int!?", "null", new String[][]{
                {"float", "0.0"},
                {"float!", "false"},
                {"float?", "null"},
                {"float!?", "null"},
                {"string", "''"},
                {"string!", "false"},
                {"string?", "null"},
                {"string!?", "null"},
                {"array", "null"},
                {"array!", "null"},
                {"resource", "null"},
                {"resource!", "null"},
                {"mixed", "null"},
                {"Exception", "null"},
                {"Exception!", "null"},
                {"ErrorException", "null"},
                {"ErrorException!", "null"}
        });

        addToCollection(collection, errorDto, "float", "1.4", new String[][]{
                {"bool!", "false"},
                {"bool?", "null"},
                {"bool!?", "null"},
                {"int!", "false"},
                {"int?", "null"},
                {"int!?", "null"},
                {"float!", "false"},
                {"float?", "null"},
                {"float!?", "null"},
                {"string", "''"},
                {"string!", "false"},
                {"string?", "null"},
                {"string!?", "null"},
                {"array", "null"},
                {"array!", "null"},
                {"resource", "null"},
                {"resource!", "null"},
                {"mixed", "null"},
                {"Exception", "null"},
                {"Exception!", "null"},
                {"ErrorException", "null"},
                {"ErrorException!", "null"}
        });

        addToCollection(collection, errorDto, "float!", "false", new String[][]{
                {"bool?", "null"},
                {"bool!?", "null"},
                {"int?", "null"},
                {"int!?", "null"},
                {"float?", "null"},
                {"float!?", "null"},
                {"string", "''"},
                {"string!", "false"},
                {"string?", "null"},
                {"string!?", "null"},
                {"array", "null"},
                {"array!", "null"},
                {"resource", "null"},
                {"resource!", "null"},
                {"mixed", "null"},
                {"Exception", "null"},
                {"Exception!", "null"},
                {"ErrorException", "null"},
                {"ErrorException!", "null"}
        });

        addToCollection(collection, errorDto, "float?", "null", new String[][]{
                {"bool!", "false"},
                {"bool!?", "null"},
                {"int!", "false"},
                {"int!?", "null"},
                {"float!", "false"},
                {"float!?", "null"},
                {"string", "''"},
                {"string!", "false"},
                {"string?", "null"},
                {"string!?", "null"},
                {"array", "null"},
                {"array!", "null"},
                {"resource", "null"},
                {"resource!", "null"},
                {"mixed", "null"},
                {"Exception", "null"},
                {"Exception!", "null"},
                {"ErrorException", "null"},
                {"ErrorException!", "null"}
        });

        addToCollection(collection, errorDto, "float!?", "null", new String[][]{
                {"string", "''"},
                {"string!", "false"},
                {"string?", "null"},
                {"string!?", "null"},
                {"array", "null"},
                {"array!", "null"},
                {"resource", "null"},
                {"resource!", "null"},
                {"mixed", "null"},
                {"Exception", "null"},
                {"Exception!", "null"},
                {"ErrorException", "null"},
                {"ErrorException!", "null"}
        });

        addToCollection(collection, errorDto, "string", "'hi'", new String[][]{
                {"bool!", "false"},
                {"bool?", "null"},
                {"bool!?", "null"},
                {"int!", "false"},
                {"int?", "null"},
                {"int!?", "null"},
                {"float!", "false"},
                {"float?", "null"},
                {"float!?", "null"},
                {"string!", "false"},
                {"string?", "null"},
                {"string!?", "null"},
                {"array", "null"},
                {"array!", "null"},
                {"resource", "null"},
                {"resource!", "null"},
                {"mixed", "null"},
                {"Exception", "null"},
                {"Exception!", "null"},
                {"ErrorException", "null"},
                {"ErrorException!", "null"}
        });

        addToCollection(collection, errorDto, "string!", "false", new String[][]{
                {"bool?", "null"},
                {"bool!?", "null"},
                {"int?", "null"},
                {"int!?", "null"},
                {"float?", "null"},
                {"float!?", "null"},
                {"string?", "null"},
                {"string!?", "null"},
                {"array", "null"},
                {"array!", "null"},
                {"resource", "null"},
                {"resource!", "null"},
                {"mixed", "null"},
                {"Exception", "null"},
                {"Exception!", "null"},
                {"ErrorException", "null"},
                {"ErrorException!", "null"}
        });

        addToCollection(collection, errorDto, "string?", "null", new String[][]{
                {"bool!", "false"},
                {"bool!?", "null"},
                {"int!", "false"},
                {"int!?", "null"},
                {"float!", "false"},
                {"float!?", "null"},
                {"string!", "false"},
                {"string!?", "null"},
                {"array", "null"},
                {"array!", "null"},
                {"resource", "null"},
                {"resource!", "null"},
                {"mixed", "null"},
                {"Exception", "null"},
                {"Exception!", "null"},
                {"ErrorException", "null"},
                {"ErrorException!", "null"}
        });

        addToCollection(collection, errorDto, "string!?", "null", new String[][]{
                {"array", "null"},
                {"array!", "null"},
                {"resource", "null"},
                {"resource!", "null"},
                {"mixed", "null"},
                {"Exception", "null"},
                {"Exception!", "null"},
                {"ErrorException", "null"},
                {"ErrorException!", "null"}
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
