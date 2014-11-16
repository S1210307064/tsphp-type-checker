/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.reference;

import ch.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tsphp.typechecker.test.integration.testutils.reference.AReferenceErrorTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ResolveConstantNotFoundTest extends AReferenceErrorTest
{

    public ResolveConstantNotFoundTest(String testString, ReferenceErrorDto[] theErrorDtos) {
        super(testString, theErrorDtos);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                //See bug TSPHP-900
                {
                        "namespace ab\\b\\c{const int d = 1;} namespace ab{use a\\b; echo \n b\\c\\d;}",
                        new ReferenceErrorDto[]{
                                new ReferenceErrorDto("\\a\\b\\c\\d#", 2, 1),
                        }
                },
        });
    }
}
