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
import ch.tutteli.tsphp.typechecker.test.testutils.reference.AReferenceDefinitionErrorTest;
import java.util.ArrayList;
import java.util.Arrays;
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
public class VariableOutsideConditionalScopeErrorTest extends AReferenceDefinitionErrorTest
{

    public VariableOutsideConditionalScopeErrorTest(String testString, DefinitionErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        //global variables
        collection.addAll(getVariations("", ""));
        collection.addAll(getVariations("namespace{", "}"));
        collection.addAll(getVariations("namespace a;", ""));
        collection.addAll(getVariations("namespace a{", "}"));
        collection.addAll(getVariations("namespace a\\b;", ""));
        collection.addAll(getVariations("namespace a\\b\\z{", "}"));

        //functions
        collection.addAll(getVariations("function void foo(){", "}"));
        collection.addAll(getVariations("namespace{function void foo(){", "}}"));
        collection.addAll(getVariations("namespace a;function void foo(){", "}"));
        collection.addAll(getVariations("namespace a{function void foo(){", "}}"));
        collection.addAll(getVariations("namespace a\\b;function void foo(){", "}"));
        collection.addAll(getVariations("namespace a\\b\\z{function void foo(){", "}}"));

        //methods
        collection.addAll(getVariations("class a{ function void foo(){", "}}"));
        collection.addAll(getVariations("namespace{class a{ function void foo(){", "}}}"));
        collection.addAll(getVariations("namespace a;class a{ function void foo(){", "}}"));
        collection.addAll(getVariations("namespace a{class a{ function void foo(){", "}}}"));
        collection.addAll(getVariations("namespace a\\b;class a{ function void foo(){", "}}"));
        collection.addAll(getVariations("namespace a\\b\\z{class a{ function void foo(){", "}}}"));

        return collection;
    }

    public static Collection<Object[]> getVariations(String prefix, String appendix) {
        List<Object[]> collection = new ArrayList<>();

        DefinitionErrorDto[] errorDto = new DefinitionErrorDto[]{new DefinitionErrorDto("$a", 2, 1, "$a", 3, 1)};
        collection.addAll(Arrays.asList(new Object[][]{
            {prefix + "if(true){int\n $a=1;}\n $a;" + appendix, errorDto},
            {prefix + "if(true);else int\n $a;\n $a;" + appendix, errorDto},
            {prefix + "if(true){}else{ int\n $a=1;} \n $a; " + appendix, errorDto},
            {prefix + "int $b; switch($b){case 1: int\n $a=1;} \n $a;" + appendix, errorDto},
            {prefix + "for(;;)int\n $a; \n $a; " + appendix, errorDto},
            {prefix + "foreach([1,2] as object $b){int\n $a;} \n $a; " + appendix, errorDto},
            {prefix + "foreach([1,2] as object\n $a); \n $a; " + appendix, errorDto},
            {prefix + "foreach([1,2] as string\n $a => object $v); \n $a; " + appendix, errorDto},
            {prefix + "while(true)int\n $a=1; \n $a;" + appendix, errorDto},
            {prefix + "if(true){ do{int\n $a;}while(true);} \n $a;" + appendix, errorDto},
            //
            {prefix + "if(true){ int\n $a;}while(true); \n $a=1; if(true){\n $a;}" + appendix,
                new DefinitionErrorDto[]{
                    new DefinitionErrorDto("$a", 2, 1, "$a", 3, 1),
                    new DefinitionErrorDto("$a", 2, 1, "$a", 4, 1)
                }
            },
            {prefix + "while(true){ if(true){int\n $a;} if(true){\n $a;} \n $a;} " + appendix,
                new DefinitionErrorDto[]{
                    new DefinitionErrorDto("$a", 2, 1, "$a", 3, 1),
                    new DefinitionErrorDto("$a", 2, 1, "$a", 4, 1)
                }
            }
        }));
        return collection;
    }
}
