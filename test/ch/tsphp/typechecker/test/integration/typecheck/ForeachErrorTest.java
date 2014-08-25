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
        ReferenceErrorDto[] errorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("foreach", 2, 1)};
        ReferenceErrorDto[] twoErrorDto = new ReferenceErrorDto[]{
                new ReferenceErrorDto("foreach", 2, 1),
                new ReferenceErrorDto("$k", 3, 1)
        };
        String[][] types = TypeHelper.getTypesInclDefaultValue();
        for (String[] type : types) {
            if (type[0].equals("array")) {
                continue;
            }
            collection.addAll(Arrays.asList(new Object[][]{
                    {type[0] + " $b=" + type[1] + ";\n foreach($b as mixed $v);", errorDto},
                    {type[0] + " $b=" + type[1] + ";\n foreach($b as string $k => mixed $v);", errorDto},
                    {type[0] + " $b=" + type[1] + ";\n foreach($b as float\n $k => mixed $v);", twoErrorDto},
                    {type[0] + " $b=" + type[1] + ";\n foreach($b as int\n $k => mixed $v);", twoErrorDto},
                    {type[0] + " $b=" + type[1] + ";\n foreach($b as bool\n $k => mixed $v);", twoErrorDto}
            }));
        }


        for (String[] type : types) {
            //only object is supported as type of the values at the moment
            if (type[0].equals("mixed")) {
                continue;
            }
            collection.add(new Object[]{
                    "foreach([1,2] as " + type[0] + "\n $v);",
                    new ReferenceErrorDto[]{new ReferenceErrorDto("$v", 2, 1)}
            });
        }
        return collection;
    }
}
