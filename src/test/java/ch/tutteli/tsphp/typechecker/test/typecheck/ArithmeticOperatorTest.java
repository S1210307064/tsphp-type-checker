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
public class ArithmeticOperatorTest extends AOperatorTypeCheckTest
{

    private static List<Object[]> collection;

    public ArithmeticOperatorTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        collection = new ArrayList<>();
        addArithmeticBinaryOperators();
        addArithmeticUnaryOperators();
        return collection;
    }

    private static void addArithmeticUnaryOperators() {
        String[][] arithmeticOperators = new String[][]{
            {"++", "preIncr"},
            {"--", "preDecr"},
            {"-", "uMinus"},};
        for (String[] operator : arithmeticOperators) {
            collection.addAll(Arrays.asList(new Object[][]{
                        {"bool $a; " + operator[0] + "$a;", new TypeCheckStruct[]{struct(operator[1], Int, 1, 1, 0)}},
                        {"int $a;" + operator[0] + "$a;", new TypeCheckStruct[]{struct(operator[1], Int, 1, 1, 0)}},
                        {"float $a;" + operator[0] + "$a;", new TypeCheckStruct[]{struct(operator[1], Float, 1, 1, 0)}}
                    }));
        }
        arithmeticOperators = new String[][]{
            {"++", "postIncr"},
            {"--", "postDecr"}
        };
        for (String[] operator : arithmeticOperators) {
            collection.addAll(Arrays.asList(new Object[][]{
                        {"bool $a; $a" + operator[0] + ";", new TypeCheckStruct[]{struct(operator[1], Int, 1, 1, 0)}},
                        {"int $a; $a" + operator[0] + ";", new TypeCheckStruct[]{struct(operator[1], Int, 1, 1, 0)}},
                        {"float $a; $a" + operator[0] + ";", new TypeCheckStruct[]{struct(operator[1], Float, 1, 1, 0)}}
                    }));
        }
    }

    private static void addArithmeticBinaryOperators() {
        String[] arithmeticOperators = new String[]{"+", "-", "*", "/", "%"};
        for (String operator : arithmeticOperators) {
            collection.addAll(Arrays.asList(new Object[][]{
                        {"true " + operator + " false;", new TypeCheckStruct[]{struct(operator, Int, 1, 0, 0)}},
                        {"true " + operator + " 2;", new TypeCheckStruct[]{struct(operator, Int, 1, 0, 0)}},
                        {"1 " + operator + " false;", new TypeCheckStruct[]{struct(operator, Int, 1, 0, 0)}},
                        {"1 " + operator + " 2;", new TypeCheckStruct[]{struct(operator, Int, 1, 0, 0)}},
                        {"1 " + operator + " 2.0;", new TypeCheckStruct[]{struct(operator, Float, 1, 0, 0)}},
                        {"1.0 " + operator + " 2;", new TypeCheckStruct[]{struct(operator, Float, 1, 0, 0)}},
                        {"true " + operator + " 2.0;", new TypeCheckStruct[]{struct(operator, Float, 1, 0, 0)}},
                        {"1.0 " + operator + " false;", new TypeCheckStruct[]{struct(operator, Float, 1, 0, 0)}},
                        {"1.0 " + operator + " 2.0;", new TypeCheckStruct[]{struct(operator, Float, 1, 0, 0)}},
                        {"true " + operator + " 1 " + operator + " 10;", new TypeCheckStruct[]{
                                struct(operator, Int, 1, 0, 0),
                                struct(operator, Int, 1, 0, 0, 0),
                            }
                        },
                        {"1 " + operator + " 1 " + operator + " false;", new TypeCheckStruct[]{
                                struct(operator, Int, 1, 0, 0),
                                struct(operator, Int, 1, 0, 0, 0),
                                struct(operator, Int, 1, 0, 0, 0)
                            }
                        },
                        {"1 " + operator + " 1 " + operator + " 10;", new TypeCheckStruct[]{
                                struct(operator, Int, 1, 0, 0),
                                struct(operator, Int, 1, 0, 0, 0)
                            }
                        },
                        {"1 " + operator + " 5.0 " + operator + " 10;", new TypeCheckStruct[]{
                                struct(operator, Float, 1, 0, 0),
                                struct(operator, Float, 1, 0, 0, 0)
                            }
                        },
                        {"1 " + operator + " 5.0 " + operator + " true;", new TypeCheckStruct[]{
                                struct(operator, Float, 1, 0, 0),
                                struct(operator, Float, 1, 0, 0, 0)
                            }
                        },
                        {"2.0 " + operator + " 5.0 " + operator + " 10.3;", new TypeCheckStruct[]{
                                struct(operator, Float, 1, 0, 0),
                                struct(operator, Float, 1, 0, 0, 0)
                            }
                        }
                    }));

        }
        collection.addAll(Arrays.asList(new Object[][]{
                    {"[1,2] + [3];", new TypeCheckStruct[]{struct("+", Array, 1, 0, 0)}},
                    {"[1,'b'=>2] + [];", new TypeCheckStruct[]{struct("+", Array, 1, 0, 0)}},
                    {"array() + [3];", new TypeCheckStruct[]{struct("+", Array, 1, 0, 0)}},
                    {"[1] + ['hello'] + ['a' => 2.0];", new TypeCheckStruct[]{
                            struct("+", Array, 1, 0, 0),
                            struct("+", Array, 1, 0, 0, 0)
                        }
                    }
                }));

    }
}
