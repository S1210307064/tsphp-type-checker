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
public class FunctionDoubleDefinitionErrorTest extends AReferenceDefinitionErrorTest
{
    public FunctionDoubleDefinitionErrorTest(String testString, DefinitionErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        Collection<Object[]> collection = new ArrayList<>();


        collection.addAll(getNamespaceWithoutBracketVariations("", ""));
        collection.addAll(getNamespaceWithoutBracketVariations("namespace a;", ""));
        collection.addAll(getNamespaceWithoutBracketVariations("namespace a\\b;", ""));

        collection.addAll(getNamespaceBracketVariations("namespace{", "}"));
        collection.addAll(getNamespaceBracketVariations("namespace a{", "}"));
        collection.addAll(getNamespaceBracketVariations("namespace a\\b\\z{", "}"));

        return collection;
    }

    public static Collection<Object[]> getNamespaceWithoutBracketVariations(
            final String prefix, final String appendix) {

        Collection<Object[]> collection = new ArrayList<>();
        DefinitionErrorDto[] errorDto = new DefinitionErrorDto[]{new DefinitionErrorDto("foo()", 2, 1, "foo()", 3, 1)};
        DefinitionErrorDto[] errorDtoTwo = new DefinitionErrorDto[]{
                new DefinitionErrorDto("foo()", 2, 1, "foo()", 3, 1),
                new DefinitionErrorDto("foo()", 2, 1, "foo()", 4, 1)
        };

        collection.addAll(Arrays.asList(new Object[][]{
                {prefix + "function void \n foo(){} function void \n foo(){}" + appendix, errorDto},
                {
                        prefix + "function void \n foo(){}"
                                + "function void \n foo(){}"
                                + "function void \n foo(){}" + appendix,
                        errorDtoTwo
                },
                {
                        prefix + "function void \n foO(){} function void \n foo(){}" + appendix,
                        new DefinitionErrorDto[]{
                                new DefinitionErrorDto("foO()", 2, 1, "foo()", 3, 1)
                        }
                },
                {
                        prefix + "function void \n foO(){} "
                                + "function void \n foo(){}"
                                + "function void \n fOO(){}" + appendix,
                        new DefinitionErrorDto[]{
                                new DefinitionErrorDto("foO()", 2, 1, "foo()", 3, 1),
                                new DefinitionErrorDto("foO()", 2, 1, "fOO()", 4, 1)
                        }
                },
                //parameter name does not matter
                {
                        prefix + "function void \n foo(int $a){} function void \n foo(int $b){}" + appendix,
                        errorDto
                },
                {
                        prefix + "function void \n foo(int $a){}"
                                + "function void \n foo(int $b){}"
                                + "function void \n foo(int $c){}" + appendix,
                        errorDtoTwo
                },
                //number of parameters does not matter
                {
                        prefix + "function void \n foo(int $a){} function void \n foo(int $a, int $b){}" + appendix,
                        errorDto
                },
                {
                        prefix + "function void \n foo(int $a){}"
                                + "function void \n foo(int $a, int $b){}"
                                + "function void \n foo(int $a, int $b, int $c){}" + appendix,
                        errorDtoTwo
                },
        }));

        List<String> types = TypeHelper.getPrimitiveTypes();
        for (String type : types) {
            collection.addAll(Arrays.asList(new Object[][]{
                    //it does not matter if return values are different
                    {
                            prefix + "function " + type + " \n foo(){return 1;} function void \n foo(){}" + appendix,
                            errorDto
                    },
                    {
                            prefix + "function " + type + " \n foo(){return 1;}"
                                    + "function int \n foo(){return 1;}"
                                    + "function void \n foo(){}" + appendix,
                            errorDtoTwo
                    },
                    //parameter type does not matter
                    {
                            prefix + "function void \n foo(" + type + " $a){} "
                                    + "function void \n foo(\\Exception $a){}" + appendix,
                            errorDto
                    },
                    {
                            prefix + "function void \n foo(" + type + " $a){} "
                                    + "function void \n foo(\\ErrorException $a){} "
                                    + "function void \n foo(\\Exception $a){}" + appendix,
                            errorDtoTwo
                    },
            }));
        }
        return collection;
    }

    public static Collection<Object[]> getNamespaceBracketVariations(final String prefix, final String appendix) {
        Collection<Object[]> collection = new ArrayList<>();

        DefinitionErrorDto[] errorDto = new DefinitionErrorDto[]{new DefinitionErrorDto("foo()", 2, 1, "foo()", 3, 1)};
        DefinitionErrorDto[] errorDtoTwo = new DefinitionErrorDto[]{
                new DefinitionErrorDto("foo()", 2, 1, "foo()", 3, 1),
                new DefinitionErrorDto("foo()", 2, 1, "foo()", 4, 1)
        };

        collection.addAll(Arrays.asList(new Object[][]{
                {
                        prefix + "function void \n foo(){}" + appendix + " "
                                + prefix + "function void \n foo(){}" + appendix,
                        errorDto
                },
                {
                        prefix + "function void \n foo(){}" + appendix + " "
                                + prefix + "function void \n foo(){}" + appendix
                                + prefix + "function void \n foo(){}" + appendix,
                        errorDtoTwo
                },
                //case insensitive check
                {
                        prefix + "function void \n foO(){}" + appendix + " "
                                + prefix + "function void \n foo(){}" + appendix,
                        new DefinitionErrorDto[]{
                                new DefinitionErrorDto("foO()", 2, 1, "foo()", 3, 1)
                        }
                },
                {
                        prefix + "function void \n foo(){}" + appendix + " "
                                + prefix + "function void \n FOO(){}" + appendix + " "
                                + prefix + "function void \n foO(){}" + appendix + " "
                                + prefix + "function void \n foo(){}" + appendix,
                        new DefinitionErrorDto[]{
                                new DefinitionErrorDto("foo()", 2, 1, "FOO()", 3, 1),
                                new DefinitionErrorDto("foo()", 2, 1, "foO()", 4, 1),
                                new DefinitionErrorDto("foo()", 2, 1, "foo()", 5, 1)
                        }
                },
                //parameter name does not matter
                {
                        prefix + "function void \n foo(int $a){}" + appendix + " "
                                + prefix + "function void \n foo(int $b){}" + appendix,
                        errorDto
                },
                {
                        prefix + "function void \n foo(int $a){}" + appendix + " "
                                + prefix + "function void \n foo(int $b){}" + appendix + " "
                                + prefix + "function void \n foo(int $c){}" + appendix,
                        errorDtoTwo
                },
                //number of parameters does not matter
                {
                        prefix + "function void \n foo(int $a){}" + appendix + " "
                                + prefix + "function void \n foo(int $a, int $b){}" + appendix,
                        errorDto
                },
                {
                        prefix + "function void \n foo(int $a){}" + appendix + " "
                                + prefix + "function void \n foo(int $a, int $b){}" + appendix + " "
                                + prefix + "function void \n foo(int $a, int $b, int $c){}" + appendix,
                        errorDtoTwo
                },

        }));

        List<String> types = TypeHelper.getPrimitiveTypes();
        for (String type : types) {
            collection.addAll(Arrays.asList(new Object[][]{
                    //it does not matter if return values are different
                    {
                            prefix + "function " + type + " \n foo(){return 1;}" + appendix + " "
                                    + prefix + "function void \n foo(){}" + appendix,
                            errorDto
                    },
                    {
                            prefix + "function " + type + " \n foo(){return 1;}" + appendix + " "
                                    + prefix + "function int \n foo(){return 1;}" + appendix + " "
                                    + prefix + "function void \n foo(){}" + appendix,
                            errorDtoTwo
                    },
                    //parameter type does not matter
                    {
                            prefix + "function void \n foo(" + type + " $a){}" + appendix + " "
                                    + prefix + "function void \n foo(\\Exception $a){}" + appendix,
                            errorDto
                    },
                    {
                            prefix + "function void \n foo(" + type + " $a){}" + appendix + " "
                                    + prefix + "function void \n foo(\\ErrorException $a){}" + appendix + " "
                                    + prefix + "function void \n foo(\\Exception $a){}" + appendix,
                            errorDtoTwo
                    },
            }));
        }
        return collection;
    }
}
