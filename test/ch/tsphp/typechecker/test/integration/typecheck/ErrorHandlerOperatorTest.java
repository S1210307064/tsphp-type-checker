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
public class ErrorHandlerOperatorTest extends AOperatorTypeCheckTest
{

    public ErrorHandlerOperatorTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {

        return Arrays.asList(new Object[][]{
                {"@true;", new TypeCheckStruct[]{struct("@", Bool, 1, 0, 0)}},
                {"@1;", new TypeCheckStruct[]{struct("@", Int, 1, 0, 0)}},
                {"@1.2;", new TypeCheckStruct[]{struct("@", Float, 1, 0, 0)}},
                {"@'hello';", new TypeCheckStruct[]{struct("@", String, 1, 0, 0)}},
                {"@[1];", new TypeCheckStruct[]{struct("@", Array, 1, 0, 0)}}
        });
    }
}
