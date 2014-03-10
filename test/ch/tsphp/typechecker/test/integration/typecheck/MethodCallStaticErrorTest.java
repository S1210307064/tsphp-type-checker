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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class MethodCallStaticErrorTest extends ATypeCheckErrorTest
{

    public MethodCallStaticErrorTest(String testString, ReferenceErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();
        MethodCallErrorTest.AddGeneralTestStrings(collection, true);

        ReferenceErrorDto[] errorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("foo()", 2, 1)};
        collection.addAll(Arrays.asList(new Object[][]{
                //not static
                {"class A{public function void foo(){}} A::\n foo();", errorDto},
                {"class A{public function void foo(){}} class B extends A{} B::\n foo();", errorDto}
        }));

        return collection;
    }
}
