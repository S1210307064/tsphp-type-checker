package ch.tsphp.typechecker.test.integration.typecheck;

import ch.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tsphp.typechecker.test.integration.testutils.TypeHelper;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.ATypeCheckErrorTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class WhileErrorTest extends ATypeCheckErrorTest
{

    public WhileErrorTest(String testString, ReferenceErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        String[][] types = TypeHelper.getTypesInclDefaultValue();
        for (String[] type : types) {
            if (type[0].equals("bool")) {
                continue;
            }
            collection.add(new Object[]{type[0] + " $b=" + type[1] + ";\n while($b);", refErrorDto("while", 2, 1)});
            collection.add(new Object[]{type[0] + " $b=" + type[1] + ";\n do;while($b);", refErrorDto("do", 2, 1)});
        }

        return collection;
    }
}
