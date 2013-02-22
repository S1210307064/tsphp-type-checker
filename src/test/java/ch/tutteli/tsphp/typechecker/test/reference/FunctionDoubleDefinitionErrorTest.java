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
import ch.tutteli.tsphp.typechecker.test.testutils.TypeHelper;
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
public class FunctionDoubleDefinitionErrorTest extends AReferenceDefinitionErrorTest
{

    private static List<Object[]> collection;

    public FunctionDoubleDefinitionErrorTest(String testString, DefinitionErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        collection = new ArrayList<>();

        addVariations("", "");
        addVariations("namespace{", "}");
        addVariations("namespace a;", "");
        addVariations("namespace a{", "}");
        addVariations("namespace a\\b;", "");
        addVariations("namespace a\\b\\z{", "}");

        DefinitionErrorDto[] errorDto = new DefinitionErrorDto[]{new DefinitionErrorDto("foo()", 2, 1, "foo()", 3, 1)};
        
        collection.addAll(Arrays.asList(new Object[][]{
                    {
                        "namespace{function void\n foo(){}} namespace{function void\n foo(){}}",
                        errorDto
                    },
                    {
                        "namespace a{function void\n foo(){}} namespace a{function void\n foo(){}}",
                        errorDto
                    },
                    {
                        "namespace {function void\n foO(){}} namespace{function void\n foo(){}}",
                        new DefinitionErrorDto[]{
                            new DefinitionErrorDto("foO()", 2, 1, "foo()", 3, 1)
                        }
                    },
                    {
                        "namespace a{function void\n foo(){}} namespace a{function void\n FOO(){}} "
                        + "namespace a{function void\n foO(){}} namespace a{function void\n foo(){}}",
                        new DefinitionErrorDto[]{
                            new DefinitionErrorDto("foo()", 2, 1, "FOO()", 3, 1),
                            new DefinitionErrorDto("foo()", 2, 1, "foO()", 4, 1),
                            new DefinitionErrorDto("foo()", 2, 1, "foo()", 5, 1)
                        }
                    }
                }));

        return collection;
    }

    public static void addVariations(final String prefix, final String appendix) {

        final DefinitionErrorDto[] errorDto = new DefinitionErrorDto[]{new DefinitionErrorDto("foo()", 2, 1, "foo()", 3, 1)};
        final DefinitionErrorDto[] errorDtoTwo = new DefinitionErrorDto[]{
            new DefinitionErrorDto("foo()", 2, 1, "foo()", 3, 1),
            new DefinitionErrorDto("foo()", 2, 1, "foo()", 4, 1)
        };

         List<String> types = TypeHelper.getPrimitiveTypes();
        for (String type : types) {
             //it does not matter if return values are different
            collection.add(new Object[]{
                        prefix + "function " + type + "\n foo(){} function void \n foo(){}" + appendix,
                        errorDto
                    });
            collection.add(new Object[]{
                        prefix + "function " + type + "\n foo(){} function void \n foo(){} "
                        + "function int \n foo(){}" + appendix,
                        errorDtoTwo
                    });
            
            //And since PHP does not support method overloading, also the parameter do not matter
            collection.add(new Object[]{
                        prefix + " function void \n foo("+type+" $b){} function void \n foo(){}" + appendix,
                        errorDto
                    });
            collection.add(new Object[]{
                        prefix + " function void \n foo("+type+" $b){} function void \n foo(){}"
                    + "function void \n foo(int $a){}" + appendix,
                        errorDtoTwo
                    });
        }

        //case insensitive
        collection.addAll(Arrays.asList(new Object[][]{
                    {
                        "function void\n foo(){} function void\n Foo(){}",
                        new DefinitionErrorDto[]{new DefinitionErrorDto("foo()", 2, 1, "Foo()", 3, 1)}
                    },
                    {
                        "class a{function void\n foo(){} function void\n Foo(){} function void\n fOo(){}}",
                        new DefinitionErrorDto[]{
                            new DefinitionErrorDto("foo()", 2, 1, "Foo()", 3, 1),
                            new DefinitionErrorDto("foo()", 2, 1, "fOo()", 4, 1)
                        }
                    }
                }));
    }
}
