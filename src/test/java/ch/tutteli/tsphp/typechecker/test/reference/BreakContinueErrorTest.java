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
package ch.tutteli.tsphp.typechecker.test.reference;

import ch.tutteli.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tutteli.tsphp.typechecker.test.testutils.reference.AReferenceErrorTest;
import ch.tutteli.tsphp.typechecker.test.testutils.typecheck.ATypeCheckErrorTest;
import java.util.ArrayList;
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
public class BreakContinueErrorTest extends AReferenceErrorTest
{

    public BreakContinueErrorTest(String testString, ReferenceErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        ReferenceErrorDto[] errorBreakDto = new ReferenceErrorDto[]{new ReferenceErrorDto("break", 2, 1)};
        ReferenceErrorDto[] errorContinueDto = new ReferenceErrorDto[]{new ReferenceErrorDto("continue", 2, 1)};


        String[][] loops = new String[][]{
            //switch is treated like a loop in PHP
            {"int $a; switch($a){case 1:", "}"},
            {"for(;;){", "}"},
            {"foreach([1,2] as object $v){", "}"},
            {"while(true){", "}"},
            {"do{", "}while(true);"}
        };
        String[][] loops2 = new String[][]{
            //switch is treated like a loop in PHP
            {"int $b; switch($b){case 1:", "}"},
            {"for(;;){", "}"},
            {"foreach([1,2] as object $v2){", "}"},
            {"while(true){", "}"},
            {"do{", "}while(true);"}
        };
        String[][] loops3 = new String[][]{
            //switch is treated like a loop in PHP
            {"int $c; switch($c){case 1:", "}"},
            {"for(;;){", "}"},
            {"foreach([1,2] as object $v3){", "}"},
            {"while(true){", "}"},
            {"do{", "}while(true);"}
        };

        collection.add(new Object[]{"if(true){\n break;}", errorBreakDto});
        collection.add(new Object[]{"if(true){\n break 1;}", errorBreakDto});
        collection.add(new Object[]{"if(true){\n break 2;}", errorBreakDto});
        collection.add(new Object[]{"if(true){\n break 3;}", errorBreakDto});
        collection.add(new Object[]{"if(true){\n continue;}", errorContinueDto});
        collection.add(new Object[]{"if(true){\n continue 1;}", errorContinueDto});
        collection.add(new Object[]{"if(true){\n continue 2;}", errorContinueDto});
        collection.add(new Object[]{"if(true){\n continue 3;}", errorContinueDto});

        for (String[] loop : loops) {

            collection.add(new Object[]{loop[0] + "\n break 2;" + loop[1], errorBreakDto});
            collection.add(new Object[]{loop[0] + "\n continue 2;" + loop[1], errorContinueDto});
            for (String[] loop2 : loops2) {
                collection.add(new Object[]{loop[0] + loop2[0] + "\n break 4;" + loop2[1] + loop[1], errorBreakDto});
                collection.add(new Object[]{
                    loop[0] + loop2[0] + "\n continue 4;" + loop2[1] + loop[1],
                    errorContinueDto
                });
                for (String[] loop3 : loops3) {
                    collection.add(new Object[]{
                        loop[0] + loop2[0] + loop3[0] + "\n break 30;" + loop3[1] + loop2[1] + loop[1], errorBreakDto
                    });
                    collection.add(new Object[]{
                        loop[0] + loop2[0] + loop3[0] + "\n continue 30;" + loop3[1] + loop2[1] + loop[1],
                        errorContinueDto
                    });
                }
            }
        }

        return collection;
    }
}
