package ch.tutteli.tsphp.typechecker.test.integration.definition;

import ch.tutteli.tsphp.typechecker.test.integration.testutils.TypeHelper;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.definition.ADefinitionSymbolTest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class UseTest extends ADefinitionSymbolTest
{

    public UseTest(String testString, String expectedResult) {
        super(testString, expectedResult);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();


        String[] types = TypeHelper.getClassInterfaceTypes();
        for (String type : types) {
            collection.add(new Object[]{
                        "use " + type + " as c;",
                        "\\.\\." + type + " \\.\\.c"
                    });
            collection.add(new Object[]{
                        "namespace b; use " + type + " as b;",
                        "\\b\\.\\b\\." + type + " \\b\\.\\b\\.b"
                    });
        }

        return collection;
    }
}
