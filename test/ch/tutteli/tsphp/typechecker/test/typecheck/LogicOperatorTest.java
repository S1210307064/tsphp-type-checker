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
public class LogicOperatorTest extends AOperatorTypeCheckTest
{

    public LogicOperatorTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();
        String[] arithmeticOperators = new String[]{"or", "xor", "and", "||", "&&"};
        for (String operator : arithmeticOperators) {
            collection.addAll(Arrays.asList(new Object[][]{
                        {"true " + operator + " true;", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                        {"false " + operator + " false;", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                        {"true " + operator + " false;", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                        {"false " + operator + " true;", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                        {"true " + operator + " false " + operator + " true;", new TypeCheckStruct[]{
                                struct(operator, Bool, 1, 0, 0),
                                struct(operator, Bool, 1, 0, 0, 0)
                            }
                        },}));
        }
        collection.addAll(Arrays.asList(new Object[][]{
                    {"!true;", new TypeCheckStruct[]{struct("!", Bool, 1, 0, 0)}},
                    {"!false;", new TypeCheckStruct[]{struct("!", Bool, 1, 0, 0)}}
                }));
        return collection;
    }
}
