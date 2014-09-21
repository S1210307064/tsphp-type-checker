/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.typecheck;

import ch.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.ATypeCheckErrorTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class FieldAccessErrorTest extends ATypeCheckErrorTest
{

    public FieldAccessErrorTest(String testString, ReferenceErrorDto[] expectedLinesAndPositions) {
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