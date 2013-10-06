package ch.tutteli.tsphp.typechecker.test.integration.typecheck;

import ch.tutteli.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.ATypeCheckErrorTest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

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


        String[] types = new String[]{"bool?", "int", "int?", "float", "float?", "string", "string?",
            "array", "resource", "object", "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{type + " $b;\n if($b);", errorDto});
            collection.add(new Object[]{type + " $b;if(true);else\n if($b);", errorDto});
            collection.add(new Object[]{"if(true){}else{" + type + " $b;\n if($b);}", errorDto});
        }

        return collection;
    }
}
