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
package ch.tutteli.tsphp.typechecker.test.integration.reference;

import ch.tutteli.tsphp.typechecker.test.integration.testutils.reference.AReferenceTest;
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
public class BreakContinueTest extends AReferenceTest
{

    public BreakContinueTest(String testString) {
        super(testString);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Override
    protected void verifyReferences() {
        //TODO should verify that the check is actually called
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

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

        for (String[] loop : loops) {
            collection.add(new Object[]{loop[0] + "break;" + loop[1]});
            collection.add(new Object[]{loop[0] + "break 1;" + loop[1]});
            collection.add(new Object[]{loop[0] + "continue;" + loop[1]});
            collection.add(new Object[]{loop[0] + "continue 1;" + loop[1]});
            for (String[] loop2 : loops2) {
                collection.add(new Object[]{loop[0] + loop2[0] + "break 2;" + loop2[1] + loop[1]});
                collection.add(new Object[]{loop[0] + loop2[0] + "continue 2;" + loop2[1] + loop[1]});
                for (String[] loop3 : loops3) {
                    collection.add(new Object[]{
                        loop[0] + loop2[0] + loop3[0] + "break 3;" + loop3[1] + loop2[1] + loop[1]
                    });
                    collection.add(new Object[]{
                        loop[0] + loop2[0] + loop3[0] + "continue 3;" + loop3[1] + loop2[1] + loop[1]
                    });
                }
            }

        }

        return collection;
    }

}
