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
public class VariableForwardReferenceErrorTest extends AReferenceDefinitionErrorTest
{

    private static List<Object[]> collection;

    public VariableForwardReferenceErrorTest(String testString, DefinitionErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        collection = new ArrayList<>();

        //global
        addVariations("", "");
        addVariations("namespace{", "}");
        addVariations("namespace a;", "");
        addVariations("namespace a{", "}");
        addVariations("namespace a\\b;", "");
        addVariations("namespace a\\b\\z{", "}");

        //functions
        addVariations("function void foo(){", "}");
        addVariations("namespace{function void foo(){", "}}");
        addVariations("namespace a;function void foo(){", "}");
        addVariations("namespace a{function void foo(){", "}}");
        addVariations("namespace a\\b;function void foo(){", "}");
        addVariations("namespace a\\b\\z{function void foo(){", "}}");

        //methods
        addVariations("class a{ function void foo(){", "}}");
        addVariations("namespace{ class a{ function void foo(){", "}}}");
        addVariations("namespace a; class a{ function void foo(){", "}}");
        addVariations("namespace a{ class a { function void foo(){", "}}}");
        addVariations("namespace a\\b; class a{ function void foo(){", "}}");
        addVariations("namespace a\\b\\z{ class a{ function void foo(){", "}}}");
        collection.add(new Object[]{
                "class a{function void foo(){}} \n $a->foo(); a\n $a;",
                new DefinitionErrorDto[]{new DefinitionErrorDto("$a", 2, 1, "$a", 3, 1)}
        });
        return collection;
    }

    private static void addVariations(String prefix, String appendix) {
        DefinitionErrorDto[] errorDto = new DefinitionErrorDto[]{new DefinitionErrorDto("$a", 2, 1, "$a", 3, 1)};
        DefinitionErrorDto[] twoErrorDto = new DefinitionErrorDto[]{
                new DefinitionErrorDto("$a", 2, 1, "$a", 4, 1),
                new DefinitionErrorDto("$a", 3, 1, "$a", 4, 1)
        };
        collection.addAll(Arrays.asList(new Object[][]{
                {prefix + "\n $a; int\n $a;" + appendix, errorDto},
                {prefix + "\n $a; int\n $a=1;" + appendix, errorDto},
                {prefix + "\n $a; int $b,\n $a;" + appendix, errorDto},
                {prefix + "\n $a; int $b,\n $a=1;" + appendix, errorDto},
                //More than one
                {prefix + "\n $a; \n $a; int\n $a;" + appendix, twoErrorDto},
                {prefix + "\n $a; \n $a; int\n $a=1;" + appendix, twoErrorDto},
                {prefix + "\n $a; \n $a; int $b,\n $a;" + appendix, twoErrorDto},
                {prefix + "\n $a; \n $a; int $b,\n $a=1;" + appendix, twoErrorDto},}));
    }
}
