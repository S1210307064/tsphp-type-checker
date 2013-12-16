package ch.tutteli.tsphp.typechecker.test.integration.typecheck;

import ch.tutteli.tsphp.typechecker.error.DefinitionErrorDto;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.ATypeCheckDefinitionErrorTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ClassMemberAccessDefinitionErrorTest extends ATypeCheckDefinitionErrorTest
{

    public ClassMemberAccessDefinitionErrorTest(String testString, DefinitionErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        DefinitionErrorDto[] errorDto = new DefinitionErrorDto[]{new DefinitionErrorDto("A", 2, 1, "a", 3, 1)};
        return Arrays.asList(new Object[][]{
                {"class\n A{} A $a=null; $a->\n a;", errorDto},
                {"class\n A{} class B extends A{function void foo(){}} B $b=null; A $a = $b; $a->\n a;", errorDto},
        });

    }
}
