/*
 * Copyright 2013 Robert Stoll <rstoll@tutteli.ch>
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
package ch.tutteli.tsphp.typechecker.test.integration.reference;

import ch.tutteli.tsphp.typechecker.error.DefinitionErrorDto;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.reference.AReferenceDefinitionErrorTest;
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
public class UseDoubleDefinitionErrorTest extends AReferenceDefinitionErrorTest
{

    public UseDoubleDefinitionErrorTest(String testString, DefinitionErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();
        //default namespace;
        DefinitionErrorDto[] errorDtos = new DefinitionErrorDto[]{new DefinitionErrorDto("z", 2, 1, "z", 3, 1)};
        collection.addAll(getVariations("", ""));
        collection.addAll(getVariations("namespace{", "}"));
        collection.addAll(getVariations("namespace a;", ""));
        collection.addAll(getVariations("namespace a{", "}"));
        collection.addAll(getVariations("namespace a\\b;", ""));
        collection.addAll(getVariations("namespace a\\b\\z{", "}"));
        collection.addAll(Arrays.asList(new Object[][]{
                    {
                        "namespace b; use \n b\\z; class \n z{} z $b;",
                        errorDtos
                    },
                    {
                        "namespace b {use\n b\\z; class \n z{} z $b;}",
                        errorDtos
                    },
                    {
                        "namespace b\\c {use\n b\\c\\z; class \n z{} z $b;}",
                        errorDtos
                    },
                    {
                        "namespace b; use \n b\\z; interface \n z{} z $b;",
                        errorDtos
                    },
                    {
                        "namespace b {use\n b\\z; interface \n z{} z $b;}",
                        errorDtos
                    },
                    {
                        "namespace b\\c {use\n b\\c\\z; interface \n z{} z $b;}",
                        errorDtos
                    }
                }));
        return collection;
    }

    public static Collection<Object[]> getVariations(String prefix, String appendix) {
        DefinitionErrorDto[] errorDtos = new DefinitionErrorDto[]{new DefinitionErrorDto("B", 2, 1, "B", 3, 1)};
        return Arrays.asList(new Object[][]{
                    {prefix + "use \\A as \n B; use \\C as \n B;" + appendix, errorDtos},
                    {prefix + "use \\A as \n B, \\C as \n B;" + appendix, errorDtos},
                    {prefix + "use \n \\A\\B; use \\C as \n B;" + appendix, errorDtos},
                    {prefix + "use \n \\A\\B, \\C as \n B;" + appendix, errorDtos},
                    {prefix + "use \\A as \n B; use \n \\C\\B;" + appendix, errorDtos},
                    {prefix + "use \\A as \n B, \n \\C\\B;" + appendix, errorDtos},
                    {prefix + "use \n \\A\\C\\B; use \n \\C\\B;" + appendix, errorDtos},
                    {prefix + "use \n \\A\\C\\B, \n \\C\\B;" + appendix, errorDtos},
                    {prefix + "use \n \\A\\B; use \\A; use \n \\C\\B;" + appendix, errorDtos},
                    {prefix + "use \\A as \n B; use \\A; use \n \\C\\B;" + appendix, errorDtos},
                    //More than one
                    {prefix + "use \\A as \n B; use \\A; use \n \\C\\B, \\C as \n B;" + appendix,
                        new DefinitionErrorDto[]{
                            new DefinitionErrorDto("B", 2, 1, "B", 3, 1),
                            new DefinitionErrorDto("B", 2, 1, "B", 4, 1)
                        }
                    },
                    {prefix + "use \\A, \\A as \n B; use \\C; use \n \\C\\B, \\C as \n B;" + appendix,
                        new DefinitionErrorDto[]{
                            new DefinitionErrorDto("B", 2, 1, "B", 3, 1),
                            new DefinitionErrorDto("B", 2, 1, "B", 4, 1)
                        }
                    },});
    }
}
