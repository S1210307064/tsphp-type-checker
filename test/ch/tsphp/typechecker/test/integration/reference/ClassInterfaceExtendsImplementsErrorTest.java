/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.reference;

import ch.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tsphp.typechecker.test.integration.testutils.reference.AReferenceErrorTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class ClassInterfaceExtendsImplementsErrorTest extends AReferenceErrorTest
{

    public ClassInterfaceExtendsImplementsErrorTest(String testString, ReferenceErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        collection.addAll(getVariations("", "", "\\"));
        collection.addAll(getVariations("namespace a;", "", "\\a\\"));
        collection.addAll(getVariations("namespace a\\b\\z{", "}", "\\a\\b\\z\\"));
        return collection;
    }

    public static Collection<Object[]> getVariations(String prefix, String appendix, String namespaceId) {
        List<Object[]> collection = new ArrayList<>();
        ReferenceErrorDto[] errorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("a", 2, 1)};
        ReferenceErrorDto[] errorDtoTwo = new ReferenceErrorDto[]{
                new ReferenceErrorDto("a", 2, 1),
                new ReferenceErrorDto("b", 3, 1)
        };
        ReferenceErrorDto[] errorDtoThree = new ReferenceErrorDto[]{
                new ReferenceErrorDto("a", 2, 1),
                new ReferenceErrorDto("b", 3, 1),
                new ReferenceErrorDto("c", 4, 1)
        };

        ReferenceErrorDto[] onlyOneError = new ReferenceErrorDto[]{new ReferenceErrorDto(namespaceId + "b", 2, 1)};

        String kind = "interface";
        String falseKind = "class";
        collection.addAll(Arrays.asList(new Object[][]{
                {prefix + "class a{} class b implements\n a{}" + appendix, errorDto},
                {prefix + " class b implements\n a{} class a{}" + appendix, errorDto},
                {prefix + "interface a{} class b extends\n a{}" + appendix, errorDto},
                {prefix + "class b extends\n a{} interface \n a{}" + appendix, errorDto},
                {prefix + falseKind + " a{} " + kind + " b extends\n a{}" + appendix, errorDto},
                {prefix + kind + " b extends\n a{} " + falseKind + " \n a{}" + appendix, errorDto},
                {
                        prefix + falseKind + " a{}{} " + falseKind + " b{} "
                                + kind + " c extends\n a,\n b{}" + appendix, errorDtoTwo
                },
                {
                        prefix + falseKind + " a{}{} " + kind + " c extends\n a,\n b{}"
                                + falseKind + " b{} " + appendix, errorDtoTwo
                },
                {
                        prefix + kind + " d extends\n a,\n b,\n c{}" + falseKind + " a{}{} "
                                + falseKind + " c{} " + falseKind + " b{} " + appendix, errorDtoThree
                },
                {
                        prefix + falseKind + " a{}{} " + kind + " d extends\n a,\n b,\n c{}"
                                + falseKind + " c{} " + falseKind + " b{} " + appendix, errorDtoThree
                },
                //verify that only one error is thrown (no ClassCastExceptions and such see TSPHP-648)
                {prefix + "class a implements\n b{}" + appendix, onlyOneError},
                {prefix + "class a extends\n b{}" + appendix, onlyOneError},
                {prefix + "interface a extends\n b{}" + appendix, onlyOneError}
        }));

        return collection;
    }
}
