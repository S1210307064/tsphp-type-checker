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
import ch.tutteli.tsphp.typechecker.test.testutils.typecheck.TypeCheckStruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
@RunWith(Parameterized.class)
public class DotOperatorTest extends AOperatorTypeCheckTest
{

    public DotOperatorTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        collection.addAll(Arrays.asList(new Object[][]{
            {"true . false;", new TypeCheckStruct[]{struct(".", String, 1, 0, 0)}},
            {"bool $a; bool? $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"bool? $a; bool $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"bool? $a; bool? $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            //
            {"true . 1;", new TypeCheckStruct[]{struct(".", String, 1, 0, 0)}},
            {"1 . true;", new TypeCheckStruct[]{struct(".", String, 1, 0, 0)}},
            {"int $a; bool? $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"bool? $a; int $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"1 . 1;", new TypeCheckStruct[]{struct(".", String, 1, 0, 0)}},
            //
            {"int? $a; bool $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"bool $a; int? $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"int? $a; bool? $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"bool? $a; int? $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"int $a; int? $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"int? $a; int $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"int? $a; int? $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            //
            {"true . 1.0;", new TypeCheckStruct[]{struct(".", String, 1, 0, 0)}},
            {"1.0 . true;", new TypeCheckStruct[]{struct(".", String, 1, 0, 0)}},
            {"float $a; bool? $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"bool? $a; float $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"1 . 1.0;", new TypeCheckStruct[]{struct(".", String, 1, 0, 0)}},
            {"1.0 . 1;", new TypeCheckStruct[]{struct(".", String, 1, 0, 0)}},
            {"float $a; int? $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"int? $a; float $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"1.0 . 1.0;", new TypeCheckStruct[]{struct(".", String, 1, 0, 0)}},
            //
            {"float? $a; bool $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"bool $a; float? $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"float? $a; bool? $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"bool? $a; float? $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"int $a; float? $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"float? $a; int $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"int? $a; float? $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"float? $a; int? $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"float $a; float? $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"float? $a; float $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"float? $a; float? $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            //
            {"true . 'hello';", new TypeCheckStruct[]{struct(".", String, 1, 0, 0)}},
            {"'hello' . true;", new TypeCheckStruct[]{struct(".", String, 1, 0, 0)}},
            {"string $a; bool? $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"bool? $a; string $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"1 . 'hello';", new TypeCheckStruct[]{struct(".", String, 1, 0, 0)}},
            {"'hello' . 1;", new TypeCheckStruct[]{struct(".", String, 1, 0, 0)}},
            {"string $a; int? $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"int? $a; string $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"1.0 . 'hello';", new TypeCheckStruct[]{struct(".", String, 1, 0, 0)}},
            {"'hello' . 1.0;", new TypeCheckStruct[]{struct(".", String, 1, 0, 0)}},
            {"string $a; float? $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"float? $a; string $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"'hello' . 'hello';", new TypeCheckStruct[]{struct(".", String, 1, 0, 0)}},
            //
            {"string? $a; bool $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"bool $a; string? $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"string? $a; string? $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"bool? $a; string? $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"int $a; string? $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"string? $a; int $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"int? $a; string? $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"string? $a; int? $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"float $a; string? $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"string? $a; float $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"float? $a; string? $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"string? $a; float? $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"string $a; string? $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"string? $a; string $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
            {"string? $a; string? $b; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}}
        }));


        return collection;

    }
}
