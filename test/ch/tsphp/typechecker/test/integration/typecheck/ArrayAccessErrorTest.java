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
public class ArrayAccessErrorTest extends ATypeCheckErrorTest
{

    public ArrayAccessErrorTest(String testString, ReferenceErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();
        ReferenceErrorDto[] errorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("$a", 2, 1)};

        String[][] types = new String[][]{
                {"bool?", "null"},
                {"int?", "null"},
                {"float?", "null"},
                {"string?", "null"},
                {"array", "null"},
                {"resource", "null"},
                {"object", "null"},
                {"Exception", "null"},
                {"ErrorException", "null"}
        };

        for (String[] type : types) {
            collection.add(new Object[]{"array $a = [1,2];" + type[0] + " $b=" + type[1] + "; \n $a[$b];", errorDto});
        }

        return collection;
    }
}
