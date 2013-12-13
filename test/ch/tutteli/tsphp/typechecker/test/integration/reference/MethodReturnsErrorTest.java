package ch.tutteli.tsphp.typechecker.test.integration.reference;

import ch.tutteli.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.MethodModifierHelper;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.ReturnCheckHelper;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.reference.AReferenceErrorTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class MethodReturnsErrorTest extends AReferenceErrorTest
{

    public MethodReturnsErrorTest(String testString, ReferenceErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        String[] variations = MethodModifierHelper.getVariations();
        for (String modifier : variations) {
            collection.addAll(ReturnCheckHelper.getReferenceErrorPairs(
                    "class a{" + modifier + " function int\n foo(){", "}}",
                    new ReferenceErrorDto[]{new ReferenceErrorDto("foo()", 2, 1)}
            ));
        }
        return collection;
    }
}
