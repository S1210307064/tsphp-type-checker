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

import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.typechecker.test.testutils.typecheck.ATypeCheckTest;
import ch.tutteli.tsphp.typechecker.test.testutils.typecheck.EBuiltInType;
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
public class OperatorTest extends ATypeCheckTest
{

    private static List<Object[]> collection;

    public OperatorTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        collection = new ArrayList<>();
        addLogicOperators();
        addBitLevelOperators();
        addArithmeticBinaryOperators();
        addArithmeticUnaryOperators();
        addErrorHandlerOperator();
        return collection;
    }

    private static void addLogicOperators() {
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
                    {"!false;", new TypeCheckStruct[]{struct("!", Bool, 1, 0, 0)}},}));
    }

    private static void addBitLevelOperators() {
        String[] arithmeticOperators = new String[]{"|", "&", "^", "<<", ">>"};
        for (String operator : arithmeticOperators) {
            collection.addAll(Arrays.asList(new Object[][]{
                        {"true " + operator + " false;", new TypeCheckStruct[]{struct(operator, Int, 1, 0, 0)}},
                        {"true " + operator + " 1;", new TypeCheckStruct[]{struct(operator, Int, 1, 0, 0)}},
                        {"2 " + operator + " true;", new TypeCheckStruct[]{struct(operator, Int, 1, 0, 0)}},
                        {"2 " + operator + " 5;", new TypeCheckStruct[]{struct(operator, Int, 1, 0, 0)}}
                    }));
        }
        collection.addAll(Arrays.asList(new Object[][]{
                    {"~true;", new TypeCheckStruct[]{struct("~", Int, 1, 0, 0)}},
                    {"~false;", new TypeCheckStruct[]{struct("~", Int, 1, 0, 0)}},
                    {"~23098;", new TypeCheckStruct[]{struct("~", Int, 1, 0, 0)}}
                }));
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
                                struct("casting", Int, 1, 0, 0, 0, 0)
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
                        },}));

        }

    }

    private static void addErrorHandlerOperator() {

        collection.addAll(Arrays.asList(new Object[][]{
                    //                    {"@true;", new TypCheckStruct[]{struct("@", Bool, 1, 0, 0)}},
                    //                    {"@1;", new TypCheckStruct[]{struct("@", Int, 1, 0, 0)}},
                    //                    {"@1.2;", new TypCheckStruct[]{struct("@", Float, 1, 0, 0)}},
                    //                    {"@'hello';", new TypCheckStruct[]{struct("@", String, 1, 0, 0)}},
                    {"@[1];", new TypeCheckStruct[]{struct("@", Array, 1, 0, 0)}}
                }));
    }

    private static TypeCheckStruct struct(String astText, EBuiltInType type, Integer... accessOrder) {
        return new TypeCheckStruct(astText, type, Arrays.asList(accessOrder));
    }
}
