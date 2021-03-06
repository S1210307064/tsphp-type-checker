/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.typecheck;

import ch.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.TypeCheckStruct;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class DoubleExplicitCastingTest extends AOperatorTypeCheckTest
{

    public DoubleExplicitCastingTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        typeSystem.getExplicitCastings()
                .get(typeSystem.getObjectTypeSymbol())
                .remove(typeSystem.getBoolTypeSymbol());

        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        collection.addAll(Arrays.asList(new Object[][]{
                {"cast int $a; $a = [1,2];", new TypeCheckStruct[]{struct("=", Int, 1, 1, 0)}},
                {"cast float $a; $a = [1,2];", new TypeCheckStruct[]{struct("=", Float, 1, 1, 0)}},
                {"cast string $a; $a = [1,2];", new TypeCheckStruct[]{struct("=", String, 1, 1, 0)}}
        }));

        return collection;
    }
}
