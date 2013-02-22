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
public class ConstructDoubleDefinitionErrorTest extends AReferenceDefinitionErrorTest
{

    private static List<Object[]> collection;

    public ConstructDoubleDefinitionErrorTest(String testString, DefinitionErrorDto[] expectedLinesAndPositions) {
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

        return collection;
    }

    public static void addVariations(String prefix, String appendix) {
        addModifiers(prefix, appendix);

        final String newPrefix = prefix + "class a{ ";
        final String newAppendix = appendix + "}";

        final DefinitionErrorDto[] errorDto = new DefinitionErrorDto[]{new DefinitionErrorDto("__construct()", 2, 1, "__construct()", 3, 1)};
        final DefinitionErrorDto[] errorDtoTwo = new DefinitionErrorDto[]{
            new DefinitionErrorDto("__construct()", 2, 1, "__construct()", 3, 1),
            new DefinitionErrorDto("__construct()", 2, 1, "__construct()", 4, 1)
        };

       
        List<String> types = TypeHelper.getPrimitiveTypes();
        for (String type : types) {
            
            //since PHP does not support method overloading, also the parameter do not matter
            collection.add(new Object[]{
                        newPrefix + " function\n __construct("+type+" $b){} function\n __construct(){}" + newAppendix,
                        errorDto
                    });
            collection.add(new Object[]{
                        newPrefix + " function\n __construct("+type+" $b){} function\n __construct(){}"
                    + "function\n __construct(int $a){}" + newAppendix,
                        errorDtoTwo
                    });
        }

        collection.addAll(Arrays.asList(new Object[][]{
                    //case insensitive
                    {
                        "class a{function\n __construct(){} function\n __conStruct(){}}",
                        new DefinitionErrorDto[]{new DefinitionErrorDto("__construct()", 2, 1, "__conStruct()", 3, 1)}
                    },
                    {
                        "class a{function\n __Construct(){} function\n __construct(){} "
                            + "function\n __conSTRUCT(){}}",
                        new DefinitionErrorDto[]{
                            new DefinitionErrorDto("__Construct()", 2, 1, "__construct()", 3, 1),
                            new DefinitionErrorDto("__Construct()", 2, 1, "__conSTRUCT()", 4, 1)
                        }
                    }
                }));
    }

    private static void addModifiers(String prefix, String appendix) {

        String[] variations = new String[]{
            "",
            //
            "private",
            "private final",
            //
            "protected",
            "protected final",
            //
            "public",
            "public final",
            //
            "final",
            "final private",
            "final protected",
            "final public",
        };

        final String newPrefix = prefix + "class a{ ";
        final String newAppendix = appendix + "}";

        final DefinitionErrorDto[] errorDto = new DefinitionErrorDto[]{new DefinitionErrorDto("__construct()", 2, 1, "__construct()", 3, 1)};
        final DefinitionErrorDto[] errorDtoTwo = new DefinitionErrorDto[]{
            new DefinitionErrorDto("__construct()", 2, 1, "__construct()", 3, 1),
            new DefinitionErrorDto("__construct()", 2, 1, "__construct()", 4, 1)
        };

        String foo = " function\n __construct(){} ";

        //it does not matter if modifier are different
        for (String modifier : variations) {
            collection.add(new Object[]{
                        newPrefix + modifier + foo + modifier + foo + newAppendix,
                        errorDto
                    });
            collection.add(new Object[]{
                        newPrefix + modifier + foo + modifier + foo + modifier + foo + newAppendix,
                        errorDtoTwo
                    });
        }
        variations = new String[]{
            "abstract",
            "abstract protected",
            "abstract public",
            "protected abstract",
            "public abstract"
        };

        foo = " function\n __construct(); ";
        for (String modifier : variations) {
            collection.add(new Object[]{
                        newPrefix + modifier + foo + modifier + foo + newAppendix,
                        errorDto
                    });
            collection.add(new Object[]{
                        newPrefix + modifier + foo + modifier + foo + modifier + foo + newAppendix,
                        errorDtoTwo
                    });
        }
    }
}
