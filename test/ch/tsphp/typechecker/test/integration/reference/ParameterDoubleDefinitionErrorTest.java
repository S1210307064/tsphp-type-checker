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
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class ParameterDoubleDefinitionErrorTest extends AReferenceDefinitionErrorTest
{

    private static List<Object[]> collection;

    public ParameterDoubleDefinitionErrorTest(String testString, DefinitionErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        collection = new ArrayList<>();

        addVariations("function void foo(", "){}");
        addVariations("namespace{ function void foo(", "){}}");
        addVariations("namespace a; function void foo(", "){}");
        addVariations("namespace a{ function void foo(", "){}}");
        addVariations("namespace a\\b; function void foo(", "){}");
        addVariations("namespace a\\b\\z{ function void foo(", "){}}");
        addVariations("class a{ function void foo(", "){}}");
        addVariations("namespace{ class a{function void foo(", "){}}}");
        addVariations("namespace a; class b{function void foo(", "){}}");
        addVariations("namespace a{ class c{function void foo(", "){}}}");
        addVariations("namespace a\\b; class e{function void foo(", "){}}");
        addVariations("namespace a\\b\\z{ class m{function void foo(", "){}}}");
        return collection;
    }

    public static void addVariations(String prefix, String appendix) {
        DefinitionErrorDto[] errorDto = new DefinitionErrorDto[]{new DefinitionErrorDto("$a", 2, 1, "$a", 3, 1)};
        DefinitionErrorDto[] errorDtoTwo = new DefinitionErrorDto[]{
                new DefinitionErrorDto("$a", 2, 1, "$a", 3, 1),
                new DefinitionErrorDto("$a", 2, 1, "$a", 4, 1)
        };
        List<String> types = TypeHelper.getPrimitiveTypes();
        for (String type : types) {

            //And since PHP does not support method overloading, also the parameter do not matter
            collection.add(new Object[]{
                    prefix + type + "\n $a, int\n $a" + appendix,
                    errorDto
            });
            collection.add(new Object[]{
                    prefix + type + "\n $a, int?\n $a=1, cast float\n $a=3" + appendix,
                    errorDtoTwo
            });
        }

    }
}
