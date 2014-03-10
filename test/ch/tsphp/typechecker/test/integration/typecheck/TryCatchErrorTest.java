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
public class TryCatchErrorTest extends ATypeCheckErrorTest
{


    public TryCatchErrorTest(String testString, ReferenceErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        ReferenceErrorDto[] errorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("catch", 2, 1)};
        return Arrays.asList(new Object[][]{
                {"class A{} try{}\n catch(A $b){};", errorDto},
                {"class A{} try{}catch(Exception $e){}\n catch(A $b){};", errorDto},
                {"class A{} try{}catch(ErrorException $e){}catch(Exception $e2){}\n catch(A $b){};", errorDto},

                {
                        "class A{} class B{} try{}\n catch(A $b){}catch(Exception $e){}\n catch(B $c){};",
                        new ReferenceErrorDto[]{
                                new ReferenceErrorDto("catch", 2, 1),
                                new ReferenceErrorDto("catch", 3, 1),
                        }
                },
        });

    }
}
