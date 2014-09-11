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
        collection.add(new Object[]{"int $b=1; \n return $b;", refErrorDto("return", 2, 1)});
        collection.addAll(getVariations("", ""));
        collection.addAll(getVariations("class A{", "}"));
        //see TSPHP-489 - NullPointerException when function return value void is assigned to a variable
        collection.add(new Object[]{"function void foo(){} int \n $a = foo();", refErrorDto("=", 2, 1)});
        return collection;
    }

    private static Collection<Object[]> getVariations(String prefix, String appendix) {
        List<Object[]> collection = new ArrayList<>();
        ReferenceErrorDto[] errorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("return", 2, 1)};

        String[][] types = TypeHelper.getAllTypesInclDefaultValue();
        for (String[] type : types) {
            collection.add(new Object[]{
                    prefix + "function void foo(){" + type[0] + " $b=" + type[1] + ";\n return $b;}" + appendix,
                    errorDto
            });
        }

        for (int i = 0; i < types.length - 1; ++i) {
            if (types[i][0].equals("mixed") || types[i][0].equals("Exception") || types[i][0].equals("Exception!")) {
                continue;
            }
            collection.add(new Object[]{
                    prefix + "function " + types[i][0] + " foo(){"
                            + types[i + 1][0] + " $b=" + types[i + 1][1] + ";\n return $b;}" + appendix,
                    errorDto
            });
        }

        collection.addAll(Arrays.asList(new Object[][]{
                {prefix + "function \\ErrorException foo(){Exception $b=null;\n " + "return $b;}" + appendix, errorDto},
                //see TSPHP-663 - return without value causes NullPointerException
                {prefix + "function int foo(){\n return;}" + appendix, errorDto}
        }));

        return collection;
    }
}
