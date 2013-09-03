/*
 * Copyright 2013 Robert Stoll <rstoll@tutteli.ch>
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
package ch.tutteli.tsphp.typechecker.test.integration.typecheck;

import ch.tutteli.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.ATypeCheckErrorTest;
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
public class TryCatchErrorTest extends ATypeCheckErrorTest
{


    public TryCatchErrorTest(String testString, ReferenceErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        ReferenceErrorDto[] errorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("catch", 2, 1)};
        return Arrays.asList(new Object[][]{
            {"class A{} try{}\n catch(A $b){};", errorDto},
            {"class A{} try{}catch(Exception $e){}\n catch(A $b){};", errorDto},
            {"class A{} try{}catch(ErrorException $e){}catch(Exception $e2){}\n catch(A $b){};", errorDto},
            
            {
                "class A{} class B{} try{}\n catch(A $b){}catch(Exception $e){}\n catch(B $c){};",
                new ReferenceErrorDto[]{
                    new ReferenceErrorDto("catch", 2, 1),
                    new ReferenceErrorDto("catch", 3, 1),
                }
            },
        });

    }
}
