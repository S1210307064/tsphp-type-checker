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
import ch.tutteli.tsphp.typechecker.test.testutils.AReferenceDefinitionErrorTest;
import ch.tutteli.tsphp.typechecker.test.testutils.TypeHelper;
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
public class ConstantDoubleDefinitionErrorTest extends AReferenceDefinitionErrorTest
{

    private static List<Object[]> collection;

    public ConstantDoubleDefinitionErrorTest(String testString, DefinitionErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        collection = new ArrayList<>();

        //global constants
        addVariations("", "");
        addVariations("namespace{", "}");
        addVariations("namespace a;", "");
        addVariations("namespace a{", "}");
        addVariations("namespace a\\b;", "");
        addVariations("namespace a\\b\\z{", "}");
        //class constants
        addVariations("class a{ ", "}");
        addVariations("namespace{ class a{", "}}");
        addVariations("namespace a; class a{", "}");
        addVariations("namespace a{ class a{", "}}");
        addVariations("namespace a\\b; class a{", "}");
        addVariations("namespace a\\b\\z{ class a{", "}}");
        
        //does not matter if it is a comma initialisation
        collection.add(new Object[]{
                    "class a{ const int\n a=1,\n a=1;}",
                    new DefinitionErrorDto[]{new DefinitionErrorDto("#a", 2, 1, "#a", 3, 1)}
                });
        collection.add(new Object[]{
                    "class a{ const int\n a=1,\n a=1,\n a=2;}",
                    new DefinitionErrorDto[]{
                        new DefinitionErrorDto("#a", 2, 1, "#a", 3, 1),
                        new DefinitionErrorDto("#a", 2, 1, "#a", 4, 1)
                    }
                });
        return collection;
    }

    public static void addVariations(String prefix, String appendix) {

        DefinitionErrorDto[] errorDto = new DefinitionErrorDto[]{new DefinitionErrorDto("#a", 2, 1, "#a", 3, 1)};
        DefinitionErrorDto[] errorDtoTwo = new DefinitionErrorDto[]{
            new DefinitionErrorDto("#a", 2, 1, "#a", 3, 1),
            new DefinitionErrorDto("#a", 2, 1, "#a", 4, 1)
        };


        String[] types = TypeHelper.getScalarTypes();
        for (String type : types) {
            //it does not matter if type differs
            collection.add(new Object[]{
                        prefix + "const " + type + "\n a=1; const int\n a=1;" + appendix,
                        errorDto
                    });
            collection.add(new Object[]{
                        prefix + "const " + type + "\n a=1; const int\n a=1; const float\n a=3;" + appendix,
                        errorDtoTwo
                    });

        }
    }
}
