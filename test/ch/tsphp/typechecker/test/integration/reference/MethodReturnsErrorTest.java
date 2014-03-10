/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.reference;

import ch.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tsphp.typechecker.test.integration.testutils.MethodModifierHelper;
import ch.tsphp.typechecker.test.integration.testutils.ReturnCheckHelper;
import ch.tsphp.typechecker.test.integration.testutils.reference.AReferenceErrorTest;
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
