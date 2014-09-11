/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.typecheck;

import ch.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.TypeCheckStruct;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
        addArithmeticUnaryOperators();
        addArithmeticBinaryOperators();
        return collection;
    }

    private static void addArithmeticUnaryOperators() {
        String[][] arithmeticOperators = new String[][]{
                {"++", "preIncr"},
                {"--", "preDecr"},
                {"-", "uMinus"},
                {"+", "uPlus"}
        };

        for (String[] operator : arithmeticOperators) {
            collection.addAll(Arrays.asList(new Object[][]{
                    {"int    $a=0;" + operator[0] + "$a;", typeStruct(operator[1], Int, 1, 1, 0)},
                    {"int!   $a=false;" + operator[0] + "$a;", typeStruct(operator[1], Int, 1, 1, 0)},
                    {"int?   $a=null;" + operator[0] + "$a;", typeStruct(operator[1], Int, 1, 1, 0)},
                    {"int!?   $a=null;" + operator[0] + "$a;", typeStruct(operator[1], Int, 1, 1, 0)},
                    {"float  $a=0.14;" + operator[0] + "$a;", typeStruct(operator[1], Float, 1, 1, 0)},
                    {"float! $a=false;" + operator[0] + "$a;", typeStruct(operator[1], Float, 1, 1, 0)},
                    {"float? $a=null;" + operator[0] + "$a;", typeStruct(operator[1], Float, 1, 1, 0)},
                    {"float!? $a=null;" + operator[0] + "$a;", typeStruct(operator[1], Float, 1, 1, 0)}

            }));
        }

        arithmeticOperators = new String[][]{
                {"++", "preIncr"},
                {"--", "preDecr"},
        };
        for (String[] operator : arithmeticOperators) {
            collection.addAll(Arrays.asList(new Object[][]{
                    {"bool   $a=false; " + operator[0] + "$a;", typeStruct(operator[1], Bool, 1, 1, 0)},
                    {"bool!  $a=false; " + operator[0] + "$a;", typeStruct(operator[1], Bool, 1, 1, 0)},
                    {"bool?  $a=null; " + operator[0] + "$a;", typeStruct(operator[1], Bool, 1, 1, 0)},
                    {"bool!?  $a=null; " + operator[0] + "$a;", typeStruct(operator[1], Bool, 1, 1, 0)},
            }));
        }

        arithmeticOperators = new String[][]{
                {"-", "uMinus"},
                {"+", "uPlus"}
        };
        for (String[] operator : arithmeticOperators) {
            collection.addAll(Arrays.asList(new Object[][]{
                    {"bool   $a=false; " + operator[0] + "$a;", typeStruct(operator[1], Int, 1, 1, 0)},
                    {"bool!  $a=false; " + operator[0] + "$a;", typeStruct(operator[1], Int, 1, 1, 0)},
                    {"bool?  $a=null; " + operator[0] + "$a;", typeStruct(operator[1], Int, 1, 1, 0)},
                    {"bool!?  $a=null; " + operator[0] + "$a;", typeStruct(operator[1], Int, 1, 1, 0)},
            }));
        }

        arithmeticOperators = new String[][]{
                {"++", "postIncr"},
                {"--", "postDecr"}
        };
        for (String[] operator : arithmeticOperators) {
            collection.addAll(Arrays.asList(new Object[][]{
                    {"bool   $a=false; $a" + operator[0] + ";", typeStruct(operator[1], Bool, 1, 1, 0)},
                    {"bool!  $a=false; $a" + operator[0] + ";", typeStruct(operator[1], Bool, 1, 1, 0)},
                    {"bool?  $a=false; $a" + operator[0] + ";", typeStruct(operator[1], Bool, 1, 1, 0)},
                    {"bool!?  $a=false; $a" + operator[0] + ";", typeStruct(operator[1], Bool, 1, 1, 0)},
                    {"int    $a=0; $a" + operator[0] + ";", typeStruct(operator[1], Int, 1, 1, 0)},
                    {"int!   $a=0; $a" + operator[0] + ";", typeStruct(operator[1], Int, 1, 1, 0)},
                    {"int?   $a=0; $a" + operator[0] + ";", typeStruct(operator[1], Int, 1, 1, 0)},
                    {"int!?   $a=0; $a" + operator[0] + ";", typeStruct(operator[1], Int, 1, 1, 0)},
                    {"float  $a=0; $a" + operator[0] + ";", typeStruct(operator[1], Float, 1, 1, 0)},
                    {"float! $a=1; $a" + operator[0] + ";", typeStruct(operator[1], Float, 1, 1, 0)},
                    {"float? $a=1; $a" + operator[0] + ";", typeStruct(operator[1], Float, 1, 1, 0)},
                    {"float!? $a=1; $a" + operator[0] + ";", typeStruct(operator[1], Float, 1, 1, 0)},
            }));
        }
    }

    private static void addArithmeticBinaryOperators() {
        String[] arithmeticOperators = new String[]{"+", "-", "*", "/"};
        Object[][] typesInclReturnType = new Object[][]{{"bool", Int}, {"int", Int}, {"float", Float}};
        for (String op : arithmeticOperators) {
            collection.addAll(Arrays.asList(new Object[][]{
                    {"true " + op + " false;", typeStruct(op, Int, 1, 0, 0)},
                    {"true " + op + " 2;", typeStruct(op, Int, 1, 0, 0)},
                    {"true " + op + " 2.0;", typeStruct(op, Float, 1, 0, 0)},
                    {"1 " + op + " false;", typeStruct(op, Int, 1, 0, 0)},
                    {"1 " + op + " 2;", typeStruct(op, Int, 1, 0, 0)},
                    {"1 " + op + " 2.0;", typeStruct(op, Float, 1, 0, 0)},
                    {"1.0 " + op + " false;", typeStruct(op, Float, 1, 0, 0)},
                    {"1.0 " + op + " 2;", typeStruct(op, Float, 1, 0, 0)},
                    {"1.0 " + op + " 2.0;", typeStruct(op, Float, 1, 0, 0)},
                    {"true " + op + " 1 " + op + " 10;", new TypeCheckStruct[]{
                            struct(op, Int, 1, 0, 0),
                            struct(op, Int, 1, 0, 0, 0)}
                    },
                    {"1 " + op + " 1 " + op + " false;", new TypeCheckStruct[]{
                            struct(op, Int, 1, 0, 0),
                            struct(op, Int, 1, 0, 0, 0),
                            struct(op, Int, 1, 0, 0, 0)}
                    },
                    {"1 " + op + " 1 " + op + " 10;", new TypeCheckStruct[]{
                            struct(op, Int, 1, 0, 0),
                            struct(op, Int, 1, 0, 0, 0)}
                    },
                    {"1 " + op + " 5.0 " + op + " 10;", new TypeCheckStruct[]{
                            struct(op, Float, 1, 0, 0),
                            struct(op, Float, 1, 0, 0, 0)}
                    },
                    {"1 " + op + " 5.0 " + op + " true;", new TypeCheckStruct[]{
                            struct(op, Float, 1, 0, 0),
                            struct(op, Float, 1, 0, 0, 0)}
                    },
                    {"2.0 " + op + " 5.0 " + op + " 10.3;", new TypeCheckStruct[]{
                            struct(op, Float, 1, 0, 0),
                            struct(op, Float, 1, 0, 0, 0)}
                    }
            }));

            for (Object[] typeAndReturnType : typesInclReturnType) {
                String type = (String) typeAndReturnType[0];
                EBuiltInType rType = (EBuiltInType) typeAndReturnType[1];
                collection.addAll(Arrays.asList(new Object[][]{
                        {type + "! $a=false; bool $b=true; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "! $a=false; bool! $b=false; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "! $a=false; bool? $b=null; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "! $a=false; bool!? $b=false; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "! $a=false; int $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "! $a=false; int! $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "! $a=false; int? $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "! $a=false; int!? $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "! $a=false; float $b=1.0; $a " + op + " $b;", typeStruct(op, Float, 1, 2, 0)},
                        {type + "! $a=false; float! $b=1.0; $a " + op + " $b;", typeStruct(op, Float, 1, 2, 0)},
                        {type + "! $a=false; float? $b=1.0; $a " + op + " $b;", typeStruct(op, Float, 1, 2, 0)},
                        {type + "! $a=false; float!? $b=1.0; $a " + op + " $b;", typeStruct(op, Float, 1, 2, 0)},
                        {type + "? $a=null; bool $b=true; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "? $a=null; bool! $b=false; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "? $a=null; bool? $b=null; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "? $a=null; bool!? $b=false; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "? $a=null; int $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "? $a=null; int! $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "? $a=null; int? $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "? $a=null; int!? $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "? $a=null; float $b=1.0; $a " + op + " $b;", typeStruct(op, Float, 1, 2, 0)},
                        {type + "? $a=null; float! $b=1.0; $a " + op + " $b;", typeStruct(op, Float, 1, 2, 0)},
                        {type + "? $a=null; float? $b=1.0; $a " + op + " $b;", typeStruct(op, Float, 1, 2, 0)},
                        {type + "? $a=null; float!? $b=1.0; $a " + op + " $b;", typeStruct(op, Float, 1, 2, 0)},
                        {type + "!? $a=null; bool $b=true; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "!? $a=null; bool! $b=false; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "!? $a=null; bool? $b=null; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "!? $a=null; bool!? $b=false; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "!? $a=null; int $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "!? $a=null; int! $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "!? $a=null; int? $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "!? $a=null; int!? $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "!? $a=null; float $b=1.0; $a " + op + " $b;", typeStruct(op, Float, 1, 2, 0)},
                        {type + "!? $a=null; float! $b=1.0; $a " + op + " $b;", typeStruct(op, Float, 1, 2, 0)},
                        {type + "!? $a=null; float? $b=1.0; $a " + op + " $b;", typeStruct(op, Float, 1, 2, 0)},
                        {type + "!? $a=null; float!? $b=1.0; $a " + op + " $b;", typeStruct(op, Float, 1, 2, 0)},
                }));
            }
            collection.addAll(Arrays.asList(new Object[][]{
                    {"[1,2] + [3];", typeStruct("+", Array, 1, 0, 0)},
                    {"[1,'b'=>2] + [];", typeStruct("+", Array, 1, 0, 0)},
                    {"array() + [3];", typeStruct("+", Array, 1, 0, 0)},
                    {"[1] + ['hello'] + ['a' => 2.0];", new TypeCheckStruct[]{
                            struct("+", Array, 1, 0, 0),
                            struct("+", Array, 1, 0, 0, 0)}
                    }
            }));


        }
    }
}
