package ch.tsphp.typechecker.test.integration.reference;

import ch.tsphp.typechecker.error.DefinitionErrorDto;
import ch.tsphp.typechecker.test.integration.testutils.TypeHelper;
import ch.tsphp.typechecker.test.integration.testutils.reference.AReferenceDefinitionErrorTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class ConstantDoubleDefinitionErrorTest extends AReferenceDefinitionErrorTest
{

    private static List<Object[]> collection;

    public ConstantDoubleDefinitionErrorTest(String testString, DefinitionErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        collection = new ArrayList<>();

        //global constants
        addVariations("", "");
        addVariations("namespace{", "}");
        addVariations("namespace a;", "");
        addVariations("namespace a{", "}");
        addVariations("namespace a\\b;", "");
        addVariations("namespace a\\b\\z{", "}");
        //class constants
        addVariations("class a{ ", "}");
        addVariations("namespace{ class a{", "}}");
        addVariations("namespace a; class a{", "}");
        addVariations("namespace a{ class a{", "}}");
        addVariations("namespace a\\b; class a{", "}");
        addVariations("namespace a\\b\\z{ class a{", "}}");

        //does not matter if it is a comma initialisation
        collection.add(new Object[]{
                "class a{ const int\n a=1,\n a=1;}",
                new DefinitionErrorDto[]{new DefinitionErrorDto("a#", 2, 1, "a#", 3, 1)}
        });
        collection.add(new Object[]{
                "class a{ const int\n a=1,\n a=1,\n a=2;}",
                new DefinitionErrorDto[]{
                        new DefinitionErrorDto("a#", 2, 1, "a#", 3, 1),
                        new DefinitionErrorDto("a#", 2, 1, "a#", 4, 1)
                }
        });
        return collection;
    }

    public static void addVariations(String prefix, String appendix) {

        DefinitionErrorDto[] errorDto = new DefinitionErrorDto[]{new DefinitionErrorDto("a#", 2, 1, "a#", 3, 1)};
        DefinitionErrorDto[] errorDtoTwo = new DefinitionErrorDto[]{
                new DefinitionErrorDto("a#", 2, 1, "a#", 3, 1),
                new DefinitionErrorDto("a#", 2, 1, "a#", 4, 1)
        };


        String[] types = TypeHelper.getScalarTypes();
        for (String type : types) {
            //it does not matter if type differs
            collection.add(new Object[]{
                    prefix + "const " + type + "\n a=1; const int\n a=1;" + appendix,
                    errorDto
            });
            collection.add(new Object[]{
                    prefix + "const " + type + "\n a=1; const int\n a=1; const float\n a=3;" + appendix,
                    errorDtoTwo
            });

        }
    }
}
