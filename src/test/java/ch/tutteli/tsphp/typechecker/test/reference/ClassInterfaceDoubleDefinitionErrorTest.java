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
public class ClassInterfaceDoubleDefinitionErrorTest extends AReferenceDefinitionErrorTest
{

    public ClassInterfaceDoubleDefinitionErrorTest(String testString, DefinitionErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        DefinitionErrorDto[] errorDto = new DefinitionErrorDto[]{new DefinitionErrorDto("a", 2, 1, "a", 3, 1)};
        collection.addAll(getVariations("", ""));
        collection.addAll(getVariations("namespace{", "}"));
        collection.addAll(getVariations("namespace a;", ""));
        collection.addAll(getVariations("namespace a{", "}"));
        collection.addAll(getVariations("namespace a\\b;", ""));
        collection.addAll(getVariations("namespace a\\b\\z{", "}"));
        collection.addAll(Arrays.asList(new Object[][]{
                    {"namespace{class \n a{}} namespace{class \n a{}}", errorDto},
                    {"namespace a {class \n a{}} namespace a{class \n a{}}", errorDto},
                    {
                        "namespace a {class \n a{}} namespace a{class \n a{}} namespace a{class \n a{}}",
                        new DefinitionErrorDto[]{
                            new DefinitionErrorDto("a", 2, 1, "a", 3, 1),
                            new DefinitionErrorDto("a", 2, 1, "a", 4, 1)
                        }
                    },
                    {
                        "namespace{class \n A{}} namespace{class \n a{}}",
                        new DefinitionErrorDto[]{
                            new DefinitionErrorDto("A", 2, 1, "a", 3, 1),}
                    },
                    {
                        "namespace a {class \n a{}} namespace a{class \n A{}}",
                        new DefinitionErrorDto[]{
                            new DefinitionErrorDto("a", 2, 1, "A", 3, 1),}
                    },
                    {
                        "namespace a {class \n a{}} namespace a{class \n A{}} namespace a{class \n a{}}",
                        new DefinitionErrorDto[]{
                            new DefinitionErrorDto("a", 2, 1, "A", 3, 1),
                            new DefinitionErrorDto("a", 2, 1, "a", 4, 1)
                        }
                    },
                    {
                        "namespace a {class \n A{}} namespace a{class \n A{}} namespace a{class \n a{}}",
                        new DefinitionErrorDto[]{
                            new DefinitionErrorDto("A", 2, 1, "A", 3, 1),
                            new DefinitionErrorDto("A", 2, 1, "a", 4, 1)
                        }
                    }
                }));
        return collection;
    }

    public static Collection<Object[]> getVariations(String prefix, String appendix) {
        List<Object[]> collection = new ArrayList<>();
        collection.addAll(getVariations(prefix, appendix, true));
        collection.addAll(getVariations(prefix, appendix, false));
        return collection;
    }

    public static Collection<Object[]> getVariations(String prefix, String appendix, boolean testClass) {
        String kind = testClass ? "class" : "interface";
        DefinitionErrorDto[] errorDto = new DefinitionErrorDto[]{new DefinitionErrorDto("a", 2, 1, "a", 3, 1)};
        DefinitionErrorDto[] errorDtoTwo = new DefinitionErrorDto[]{
            new DefinitionErrorDto("a", 2, 1, "a", 3, 1),
            new DefinitionErrorDto("a", 2, 1, "a", 4, 1)
        };
        return Arrays.asList(new Object[][]{
                    {prefix + kind + " \n a{} class \n a{}" + appendix, errorDto},
                    {prefix + "class b{} " + kind + " \n a{}class \n a extends b{}" + appendix, errorDto},
                    {prefix + "interface b{} " + kind + " \n a{} class \n a implements b{}" + appendix, errorDto},
                    {prefix + "class b{} class \n a extends b{} " + kind + " \n a{}" + appendix, errorDto},
                    {prefix + "interface b{} class \n a implements b{} " + kind + " \n a{}" + appendix, errorDto},
                    {prefix + "class b{} class c{} " + kind + " \n a{} class \n a extends b{}" + appendix, errorDto},
                    {prefix + "interface b{} interface c{} " + kind + " \n a{}class \n a implements b,c{}" + appendix, errorDto},
                    {prefix + kind + "  b{} " + kind + "  c{}  " + kind + " \n a extends c{} class \n a{}" + appendix, errorDto},
                    {prefix + "interface b{} interface c{} class \n a implements b,c{} " + kind + " \n a{}" + appendix, errorDto},
                    {prefix + kind + "  b{} " + kind + " c{}  " + kind + " \n a extends c{} class \n a{}" + appendix, errorDto},
                    {prefix + "interface b{} " + kind + " c{} " + kind + " \n a extends c{}  class \n a implements b{}" + appendix, errorDto},
                    {prefix + "interface b{} " + kind + "  c{} class \n a implements b{} " + kind + " \n a extends c{}" + appendix, errorDto},
                    //
                    {prefix + kind + " \n a{}class \n a{} class \n a{}" + appendix, errorDtoTwo},
                    {prefix + "class b{} interface c{}" + kind + " \n a{}class \n a extends b{} class \n a implements c{}" + appendix,
                        errorDtoTwo
                    },
                    //
                    {prefix + kind + " \n a{} interface \n a{}" + appendix, errorDto},
                    {prefix + "interface b{} " + kind + " \n a{}interface \n a extends b{}" + appendix, errorDto},
                    {prefix + kind + " b{} " + kind + " \n a extends b{} interface \n a{}" + appendix, errorDto},
                    {prefix + "interface b{} interface c{} " + kind + " \n a{} interface \n a extends b,c{}" + appendix, errorDto},
                    {prefix + "interface b{} interface c{} interface \n a extends b,c{} " + kind + " \n a{} " + appendix, errorDto},
                    //
                    {prefix + kind + " \n a{}interface \n a{} interface \n a{}" + appendix,
                        errorDtoTwo
                    },
                    {prefix + "interface b{} " + kind + " \n a{}interface \n a extends b{} interface \n a extends b{}" + appendix,
                        errorDtoTwo
                    },
                    //case insensitive
                    {
                        prefix + "class \n A{} " + kind + " \n a{}" + appendix,
                        new DefinitionErrorDto[]{new DefinitionErrorDto("A", 2, 1, "a", 3, 1)}
                    },
                    {
                        prefix + "class \n A{} " + kind + " \n A{}" + appendix,
                        new DefinitionErrorDto[]{new DefinitionErrorDto("A", 2, 1, "A", 3, 1)}
                    },
                    {
                        prefix + "class \n a{} " + kind + " \n A{}" + appendix,
                        new DefinitionErrorDto[]{new DefinitionErrorDto("a", 2, 1, "A", 3, 1)}
                    },
                    {
                        prefix + "interface \n A{} " + kind + " \n a{}" + appendix,
                        new DefinitionErrorDto[]{new DefinitionErrorDto("A", 2, 1, "a", 3, 1)}
                    },
                    {
                        prefix + "interface \n A{} " + kind + " \n A{}" + appendix,
                        new DefinitionErrorDto[]{new DefinitionErrorDto("A", 2, 1, "A", 3, 1)}
                    },
                    {
                        prefix + "interface \n a{} " + kind + " \n A{}" + appendix,
                        new DefinitionErrorDto[]{new DefinitionErrorDto("a", 2, 1, "A", 3, 1)}
                    },});

    }
}
