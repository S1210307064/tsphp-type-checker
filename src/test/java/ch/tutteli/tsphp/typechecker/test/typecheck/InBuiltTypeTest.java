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

import ch.tutteli.tsphp.typechecker.test.testutils.typecheck.ATypeCheckTest;
import ch.tutteli.tsphp.typechecker.test.testutils.typecheck.EBuiltInType;
import ch.tutteli.tsphp.typechecker.test.testutils.typecheck.TypCheckStruct;
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
public class InBuiltTypeTest extends ATypeCheckTest
{

    public InBuiltTypeTest(String testString, TypCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                    {"bool $a = true;", new TypCheckStruct[]{struct("true", Bool, 1, 0, 1, 0)}},
                    {"bool $a = false;", new TypCheckStruct[]{struct("false", Bool, 1, 0, 1, 0)}},
                    {"int $a = 1;", new TypCheckStruct[]{struct("1", Int, 1, 0, 1, 0)}},
                    {"float $a = 1.0;", new TypCheckStruct[]{struct("1.0", Float, 1, 0, 1, 0)}},
                    {"string $a = 'hello';", new TypCheckStruct[]{struct("'hello'", String, 1, 0, 1, 0)}},
                    {"string $a = \"hello\";", new TypCheckStruct[]{struct("\"hello\"", String, 1, 0, 1, 0)}}
                });
    }

    private static TypCheckStruct struct(String astText, EBuiltInType type, Integer... accessOrder) {
        return new TypCheckStruct(astText, type, Arrays.asList(accessOrder));
    }
}
