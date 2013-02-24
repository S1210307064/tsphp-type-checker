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
public class OperatorTest extends ATypeCheckTest
{

    private static List<Object[]> collection;

    public OperatorTest(String testString, TypCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        collection = new ArrayList<>();
        String[] arithmeticOperators = new String[]{"+", "-", "*", "/", "%"};
        for (String operator : arithmeticOperators) {
            collection.addAll(Arrays.asList(new Object[][]{
                        {"true " + operator + " false;", new TypCheckStruct[]{struct(operator, Int, 1, 0, 0)}},
                        {"true " + operator + " 2;", new TypCheckStruct[]{struct(operator, Int, 1, 0, 0)}},
                        {"1 " + operator + " false;", new TypCheckStruct[]{struct(operator, Int, 1, 0, 0)}},
                        {"1 " + operator + " 2;", new TypCheckStruct[]{struct(operator, Int, 1, 0, 0)}},
                        {"1 " + operator + " 2.0;", new TypCheckStruct[]{struct(operator, Float, 1, 0, 0)}},
                        {"1.0 " + operator + " 2;", new TypCheckStruct[]{struct(operator, Float, 1, 0, 0)}},
                        {"true " + operator + " 2.0;", new TypCheckStruct[]{struct(operator, Float, 1, 0, 0)}},
                        {"1.0 " + operator + " false;", new TypCheckStruct[]{struct(operator, Float, 1, 0, 0)}},
                        {"1.0 " + operator + " 2.0;", new TypCheckStruct[]{struct(operator, Float, 1, 0, 0)}},
                        {"true " + operator + " 1 " + operator + " 10;", new TypCheckStruct[]{
                                struct(operator, Int, 1, 0, 0),
                                struct(operator, Int, 1, 0, 0, 0),
                                struct("casting", Int, 1, 0, 0, 0, 0)
                            }
                        },
                        {"1 " + operator + " 1 " + operator + " false;", new TypCheckStruct[]{
                                struct(operator, Int, 1, 0, 0),
                                struct(operator, Int, 1, 0, 0, 0),
                                struct(operator, Int, 1, 0, 0, 0)
                            }
                        },
                        {"1 " + operator + " 1 " + operator + " 10;", new TypCheckStruct[]{
                                struct(operator, Int, 1, 0, 0),
                                struct(operator, Int, 1, 0, 0, 0)
                            }
                        },
                        {"1 " + operator + " 5.0 " + operator + " 10;", new TypCheckStruct[]{
                                struct(operator, Float, 1, 0, 0),
                                struct(operator, Float, 1, 0, 0, 0)
                            }
                        },
                        {"1 " + operator + " 5.0 " + operator + " true;", new TypCheckStruct[]{
                                struct(operator, Float, 1, 0, 0),
                                struct(operator, Float, 1, 0, 0, 0)
                            }
                        },
                        {"2.0 " + operator + " 5.0 " + operator + " 10.3;", new TypCheckStruct[]{
                                struct(operator, Float, 1, 0, 0),
                                struct(operator, Float, 1, 0, 0, 0)
                            }
                        }
                    }));

        }
        return collection;
    }

    private static TypCheckStruct struct(String astText, EBuiltInType type, Integer... accessOrder) {
        return new TypCheckStruct(astText, type, Arrays.asList(accessOrder));
    }
}
