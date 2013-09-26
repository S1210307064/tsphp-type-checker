package ch.tutteli.tsphp.typechecker.test.integration.reference;

import ch.tutteli.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.reference.AReferenceErrorTest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

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

        collection.addAll(getVariations("", ""));
        collection.addAll(getVariations("namespace{", "}"));
        collection.addAll(getVariations("namespace a;", ""));
        collection.addAll(getVariations("namespace a{", "}"));
        collection.addAll(getVariations("namespace a\\b;", ""));
        collection.addAll(getVariations("namespace a\\b\\z{", "}"));
        return collection;
    }

    public static Collection<Object[]> getVariations(String prefix, String appendix) {
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
                    }
                }));
        return collection;
    }
}
