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
public class IfErrorTest extends ATypeCheckErrorTest
{

    public IfErrorTest(String testString, ReferenceErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();
        ReferenceErrorDto[] errorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("if", 2, 1)};


        String[][] types = TypeHelper.getTypesInclDefaultValue();
        for (String[] type : types) {
            if (type[0].equals("bool")) {
                continue;
            }
            collection.add(new Object[]{type[0] + " $b=" + type[1] + ";\n if($b);", errorDto});
            collection.add(new Object[]{type[0] + " $b=" + type[1] + ";if(true);else\n if($b);", errorDto});
            collection.add(new Object[]{"if(true){}else{" + type[0] + " $b=" + type[1] + ";\n if($b);}", errorDto});
        }

        return collection;
    }
}
