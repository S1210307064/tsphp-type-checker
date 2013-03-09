/*
 * Copyright 2012 Robert Stoll <rstoll@tutteli.ch>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package ch.tutteli.tsphp.typechecker.test.reference;

import ch.tutteli.tsphp.typechecker.error.DefinitionErrorDto;
import ch.tutteli.tsphp.typechecker.test.testutils.TypeHelper;
import ch.tutteli.tsphp.typechecker.test.testutils.reference.AReferenceDefinitionErrorTest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
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
