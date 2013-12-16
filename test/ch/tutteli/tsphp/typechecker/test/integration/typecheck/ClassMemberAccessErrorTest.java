package ch.tutteli.tsphp.typechecker.test.integration.typecheck;

import ch.tutteli.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.ATypeCheckErrorTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ClassMemberAccessErrorTest extends ATypeCheckErrorTest
{

    public ClassMemberAccessErrorTest(String testString, ReferenceErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                {
                        "class A{protected int $a;} A $a=null; $a->\n a;",
                        new ReferenceErrorDto[]{new ReferenceErrorDto("a", 2, 1)}
                },
                {
                        "class A{private int $a;} A $a=null; $a->\n a;",
                        new ReferenceErrorDto[]{new ReferenceErrorDto("a", 2, 1)}
                },
                {
                        "class A{private int $a;} class B extends A{function void foo(){$this->\n a;}}",
                        new ReferenceErrorDto[]{new ReferenceErrorDto("a", 2, 1)}
                }
        });

    }
}
