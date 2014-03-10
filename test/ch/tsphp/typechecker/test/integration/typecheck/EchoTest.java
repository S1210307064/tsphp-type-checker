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
public class EchoTest extends AOperatorTypeCheckTest
{

    public EchoTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                {"echo true;", new TypeCheckStruct[]{struct("true", Bool, 1, 0, 0)}},
                {"echo false;", new TypeCheckStruct[]{struct("false", Bool, 1, 0, 0)}},
                {"const bool b=false; echo b;", new TypeCheckStruct[]{struct("b#", Bool, 1, 1, 0)}},
                {"bool? $b=false; echo $b;", new TypeCheckStruct[]{struct("$b", BoolNullable, 1, 1, 0)}},
                {"echo 1;", new TypeCheckStruct[]{struct("1", Int, 1, 0, 0)}},
                {"const int b=1; echo b;", new TypeCheckStruct[]{struct("b#", Int, 1, 1, 0)}},
                {"int? $b=1; echo $b;", new TypeCheckStruct[]{struct("$b", IntNullable, 1, 1, 0)}},
                {"echo 1.0;", new TypeCheckStruct[]{struct("1.0", Float, 1, 0, 0)}},
                {"const float b=false; echo b;", new TypeCheckStruct[]{struct("b#", Float, 1, 1, 0)}},
                {"float? $b=null; echo $b;", new TypeCheckStruct[]{struct("$b", FloatNullable, 1, 1, 0)}},
                {"echo 'hello';", new TypeCheckStruct[]{struct("'hello'", String, 1, 0, 0)}},
                {"echo \"hello\";", new TypeCheckStruct[]{struct("\"hello\"", String, 1, 0, 0)}},
                {"echo true, false, 1, 1.0, \"hello\";", new TypeCheckStruct[]{
                        struct("true", Bool, 1, 0, 0),
                        struct("false", Bool, 1, 0, 1),
                        struct("1", Int, 1, 0, 2),
                        struct("1.0", Float, 1, 0, 3),
                        struct("\"hello\"", String, 1, 0, 4),
                }
                }
        });
    }
}
