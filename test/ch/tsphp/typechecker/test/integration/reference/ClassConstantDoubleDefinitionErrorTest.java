/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.reference;

import ch.tsphp.typechecker.error.DefinitionErrorDto;
import ch.tsphp.typechecker.test.integration.testutils.TypeHelper;
import ch.tsphp.typechecker.test.integration.testutils.reference.AReferenceDefinitionErrorTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class ClassConstantDoubleDefinitionErrorTest extends AReferenceDefinitionErrorTest
{

    public ClassConstantDoubleDefinitionErrorTest(String testString, DefinitionErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();
        DefinitionErrorDto[] errorDto = new DefinitionErrorDto[]{new DefinitionErrorDto("a#", 2, 1, "a#", 3, 1)};
        DefinitionErrorDto[] errorDtoTwo = new DefinitionErrorDto[]{
                new DefinitionErrorDto("a#", 2, 1, "a#", 3, 1),
                new DefinitionErrorDto("a#", 2, 1, "a#", 4, 1)
        };

        collection.addAll(Arrays.asList(new Object[][]{
                {"class A{const int \n a=1; const int \n a=1;}", errorDto},
                {"class A{const int \n a=1; const int \n a=1; const int \n a=1;}", errorDtoTwo},
                {"class A{ const int \n a=1,\n a=1;}", errorDto},
                {"class A{ const int \n a=1,\n a=1,\n a=2;}", errorDtoTwo}
        }));

        String[] types = TypeHelper.getScalarTypes();
        for (String type : types) {
            //it does not matter if type differs
            collection.addAll(Arrays.asList(new Object[][]{
                    {"class A{const " + type + "\n a=1; const int\n a=1;}", errorDto},
                    {"class A{const " + type + "\n a=1; const int\n a=1; const float\n a=3;}", errorDtoTwo},
            }));
        }

        return collection;
    }
}
