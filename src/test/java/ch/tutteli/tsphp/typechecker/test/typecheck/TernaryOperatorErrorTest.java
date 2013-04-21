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

import ch.tutteli.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tutteli.tsphp.typechecker.test.testutils.typecheck.ATypeCheckErrorTest;
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
public class TernaryOperatorErrorTest extends ATypeCheckErrorTest
{

    public TernaryOperatorErrorTest(String testString, ReferenceErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        //I think there are enought test which cover the identity check. Thus here only a few tests
        return Arrays.asList(new Object[][]{
            //condition not boolean
            {"bool? $a; bool? $b; $a\n ? $a : $b;", new ReferenceErrorDto[]{new ReferenceErrorDto("?", 2, 1)}},
            //not same type hierarchy
            {"bool? $a; int $b; true ? $a : \n $b;", new ReferenceErrorDto[]{new ReferenceErrorDto("$b", 2, 1)}},
            {"bool? $a; array $b; true ? $a : \n $b;", new ReferenceErrorDto[]{new ReferenceErrorDto("$b", 2, 1)}}
        });
    }
}