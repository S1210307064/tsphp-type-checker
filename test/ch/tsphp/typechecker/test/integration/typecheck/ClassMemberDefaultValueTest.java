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

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ClassMemberDefaultValueTest extends AOperatorTypeCheckTest
{

    public ClassMemberDefaultValueTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        Integer[] accessOrder = new Integer[]{1, 0, 4, 0, 0, 1, 0};

        return Arrays.asList(new Object[][]{
                {"class a{bool $a;}", typeStruct("false", Bool, accessOrder)},
                {"class a{bool! $a;}", typeStruct("false", Bool, accessOrder)},
                {"class a{bool? $a;}", typeStruct("null", Null, accessOrder)},
                {"class a{bool!? $a;}", typeStruct("null", Null, accessOrder)},
                {"class a{int $a;}", typeStruct("0", Int, accessOrder)},
                {"class a{int! $a;}", typeStruct("false", Bool, accessOrder)},
                {"class a{int? $a;}", typeStruct("null", Null, accessOrder)},
                {"class a{int!? $a;}", typeStruct("null", Null, accessOrder)},
                {"class a{float $a;}", typeStruct("0.0", Float, accessOrder)},
                {"class a{float! $a;}", typeStruct("false", Bool, accessOrder)},
                {"class a{float? $a;}", typeStruct("null", Null, accessOrder)},
                {"class a{float!? $a;}", typeStruct("null", Null, accessOrder)},
                {"class a{string $a;}", typeStruct("''", String, accessOrder)},
                {"class a{string! $a;}", typeStruct("false", Bool, accessOrder)},
                {"class a{string? $a;}", typeStruct("null", Null, accessOrder)},
                {"class a{string!? $a;}", typeStruct("null", Null, accessOrder)},
                {"class a{array $a;}", typeStruct("null", Null, accessOrder)},
                {"class a{array! $a;}", typeStruct("null", Null, accessOrder)},
                {"class a{resource $a;}", typeStruct("null", Null, accessOrder)},
                {"class a{resource! $a;}", typeStruct("null", Null, accessOrder)},
                {"class a{mixed $a;}", typeStruct("null", Null, accessOrder)},
                {"class a{Exception $a;}", typeStruct("null", Null, accessOrder)},
                {"class a{Exception! $a;}", typeStruct("null", Null, accessOrder)},
                {"class a{ErrorException $a;}", typeStruct("null", Null, accessOrder)},
                {"class a{ErrorException! $a;}", typeStruct("null", Null, accessOrder)}
        });
    }
}
