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
public class ClassMemberWithoutInitValueTest extends AOperatorTypeCheckTest
{

    public ClassMemberWithoutInitValueTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                {"class a{bool $a;}", new TypeCheckStruct[]{struct("false", Bool, 1, 0, 4, 0, 0, 1, 0)}},
                {"class a{int $a;}", new TypeCheckStruct[]{struct("0", Int, 1, 0, 4, 0, 0, 1, 0)}},
                {"class a{float $a;}", new TypeCheckStruct[]{struct("0.0", Float, 1, 0, 4, 0, 0, 1, 0)}},
                {"class a{string $a;}", new TypeCheckStruct[]{struct("''", String, 1, 0, 4, 0, 0, 1, 0)}},
                {"class a{array $a;}", new TypeCheckStruct[]{struct("null", Null, 1, 0, 4, 0, 0, 1, 0)}},
                {"class a{resource $a;}", new TypeCheckStruct[]{struct("null", Null, 1, 0, 4, 0, 0, 1, 0)}},
                {"class a{object $a;}", new TypeCheckStruct[]{struct("null", Null, 1, 0, 4, 0, 0, 1, 0)}},
                {"class a{bool? $a;}", new TypeCheckStruct[]{struct("null", Null, 1, 0, 4, 0, 0, 1, 0)}},
                {"class a{int? $a;}", new TypeCheckStruct[]{struct("null", Null, 1, 0, 4, 0, 0, 1, 0)}},
                {"class a{float? $a;}", new TypeCheckStruct[]{struct("null", Null, 1, 0, 4, 0, 0, 1, 0)}},
                {"class a{string? $a;}", new TypeCheckStruct[]{struct("null", Null, 1, 0, 4, 0, 0, 1, 0)}},
                {"class a{Exception $a;}", new TypeCheckStruct[]{struct("null", Null, 1, 0, 4, 0, 0, 1, 0)}},
                {"class a{ErrorException $a;}", new TypeCheckStruct[]{struct("null", Null, 1, 0, 4, 0, 0, 1, 0)}}
        });
    }
}
