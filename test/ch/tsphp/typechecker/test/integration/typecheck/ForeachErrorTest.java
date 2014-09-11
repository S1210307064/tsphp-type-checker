/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

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
public class ForeachErrorTest extends ATypeCheckErrorTest
{

    public ForeachErrorTest(String testString, ReferenceErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();
        String[][] types = TypeHelper.getAllTypesInclDefaultValue();
        for (String[] type : types) {
            //check that type is array
            if (!type[0].equals("array")) {
                collection.add(new Object[]{
                        type[0] + " $b=" + type[1] + ";\n foreach($b as mixed $v);",
                        refErrorDto("foreach", 2, 1)
                });
            }

            //only mixed is supported as type of the values at the moment
            if (!type[0].equals("mixed")) {
                collection.add(new Object[]{"foreach([1,2] as " + type[0] + "\n $v);", refErrorDto("$v", 2, 1)});
            }
        }

        //only string is supported as type of the keys as the moment
        collection.addAll(Arrays.asList(new Object[][]{
                {"foreach([1] as bool \n $k => mixed $v);", refErrorDto("$k", 2, 1)},
                {"foreach([1] as int \n $k => mixed $v);", refErrorDto("$k", 2, 1)},
                {"foreach([1] as float \n $k => mixed $v);", refErrorDto("$k", 2, 1)}
        }));

        return collection;
    }
}
