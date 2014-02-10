package ch.tsphp.typechecker.test.integration.definition;

import ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tsphp.typechecker.test.integration.testutils.TypeHelper;
import ch.tsphp.typechecker.test.integration.testutils.definition.ADefinitionSymbolTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class InterfaceTest extends ADefinitionSymbolTest
{

    public InterfaceTest(String testString, String expectedResult) {
        super(testString, expectedResult);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        int abstr = TSPHPDefinitionWalker.Abstract;

        collection.addAll(Arrays.asList(new Object[][]{
                {"interface a{}", "\\.\\.a|" + abstr},
                {
                        "interface a{} interface b{} interface c extends a\\b{}",
                        "\\.\\.a|" + abstr + " " + "\\.\\.b|" + abstr + " "
                                + "\\.\\.a\\b " + "\\.\\.c|" + abstr
                },
                {
                        "namespace x{interface a{}} namespace y{interface b{}} "
                                + "namespace z{interface c extends a\\b{}} namespace{interface d{}}",
                        "\\x\\.\\x\\.a|" + abstr + " " + "\\y\\.\\y\\.b|" + abstr + " "
                                + "\\z\\.\\z\\.a\\b " + "\\z\\.\\z\\.c|" + abstr + " " + "\\.\\.d|" + abstr
                }
        }));


        String[] types = TypeHelper.getClassInterfaceTypes();
        for (String type : types) {
            collection.add(new Object[]{
                    "namespace b; interface a extends " + type + "{}",
                    "\\b\\.\\b\\." + type + " " + "\\b\\.\\b\\.a|" + abstr
            });
            collection.add(new Object[]{
                    "interface a extends " + type + "," + type + "{}",
                    "\\.\\." + type + " " + "\\.\\." + type + " "
                            + "\\.\\.a|" + abstr
            });
            collection.add(new Object[]{
                    "interface a extends " + type + "," + type + "," + type + "{}",
                    "\\.\\." + type + " " + "\\.\\." + type + " " + "\\.\\." + type + " "
                            + "\\.\\.a|" + abstr
            });
        }


        return collection;
    }
}
