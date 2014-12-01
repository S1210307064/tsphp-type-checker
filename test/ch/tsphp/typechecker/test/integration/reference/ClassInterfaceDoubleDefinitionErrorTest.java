/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.reference;

import ch.tsphp.typechecker.error.DefinitionErrorDto;
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
        collection.addAll(getVariations("namespace a;", ""));
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
                {prefix + "interface b{} interface c{} " + kind + " \n a{}class \n a implements b,c{}" + appendix,
                        errorDto},
                {prefix + kind + "  b{} " + kind + "  c{}  " + kind + " \n a extends c{} class \n a{}" + appendix,
                        errorDto},
                {prefix + "interface b{} interface c{} class \n a implements b,c{} " + kind + " \n a{}" + appendix,
                        errorDto},
                {prefix + kind + "  b{} " + kind + " c{}  " + kind + " \n a extends c{} class \n a{}" + appendix,
                        errorDto},
                {prefix + "interface b{} " + kind + " c{} " + kind + " \n a extends c{}  class \n a implements b{}" +
                        appendix, errorDto},
                {prefix + "interface b{} " + kind + "  c{} class \n a implements b{} " + kind + " \n a extends c{}" +
                        appendix, errorDto},
                //
                {prefix + kind + " \n a{}class \n a{} class \n a{}" + appendix, errorDtoTwo},
                {prefix + "class b{} interface c{}" + kind + " \n a{}class \n a extends b{} class \n a implements " +
                        "c{}" + appendix,
                        errorDtoTwo
                },
                //
                {prefix + kind + " \n a{} interface \n a{}" + appendix, errorDto},
                {prefix + "interface b{} " + kind + " \n a{}interface \n a extends b{}" + appendix, errorDto},
                {prefix + kind + " b{} " + kind + " \n a extends b{} interface \n a{}" + appendix, errorDto},
                {prefix + "interface b{} interface c{} " + kind + " \n a{} interface \n a extends b,c{}" + appendix,
                        errorDto},
                {prefix + "interface b{} interface c{} interface \n a extends b,c{} " + kind + " \n a{} " + appendix,
                        errorDto},
                //
                {prefix + kind + " \n a{}interface \n a{} interface \n a{}" + appendix,
                        errorDtoTwo
                },
                {prefix + "interface b{} " + kind + " \n a{}interface \n a extends b{} interface \n a extends b{}" +
                        appendix,
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
