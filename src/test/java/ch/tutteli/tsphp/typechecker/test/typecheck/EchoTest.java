/*
 * Copyright 2012 Robert Stoll <rstoll@tutteli.ch>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package ch.tutteli.tsphp.typechecker.test.typecheck;

import ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Bool;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.BoolNullable;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.String;
import ch.tutteli.tsphp.typechecker.test.testutils.typecheck.TypeCheckStruct;
import java.util.Arrays;
import java.util.Collection;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
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
            {"bool? $b; echo $b;", new TypeCheckStruct[]{struct("$b", BoolNullable, 1, 1, 0)}},
            {"echo 1;", new TypeCheckStruct[]{struct("1", Int, 1, 0, 0)}},
            {"const int b=1; echo b;", new TypeCheckStruct[]{struct("b#", Int, 1, 1, 0)}},
            {"int? $b; echo $b;", new TypeCheckStruct[]{struct("$b", IntNullable, 1, 1, 0)}},
            {"echo 1.0;", new TypeCheckStruct[]{struct("1.0", Float, 1, 0, 0)}},
            {"const float b=false; echo b;", new TypeCheckStruct[]{struct("b#", Float, 1, 1, 0)}},
            {"float? $b; echo $b;", new TypeCheckStruct[]{struct("$b", FloatNullable, 1, 1, 0)}},
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
