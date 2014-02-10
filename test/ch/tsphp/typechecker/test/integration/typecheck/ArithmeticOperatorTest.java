package ch.tsphp.typechecker.test.integration.typecheck;

import ch.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest;
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
                    {"bool   $a=false; " + operator[0] + "$a;", typeStruct(operator[1], Int, 1, 1, 0)},
                    {"int    $a=0;" + operator[0] + "$a;", typeStruct(operator[1], Int, 1, 1, 0)},
                    {"float  $a=0.14;" + operator[0] + "$a;", typeStruct(operator[1], Float, 1, 1, 0)},
                    {"bool?  $a=null; " + operator[0] + "$a;", typeStruct(operator[1], Int, 1, 1, 0)},
                    {"int?   $a=null;" + operator[0] + "$a;", typeStruct(operator[1], Int, 1, 1, 0)},
                    {"float? $a=null;" + operator[0] + "$a;", typeStruct(operator[1], Float, 1, 1, 0)}
            }));
        }
        arithmeticOperators = new String[][]{
                {"++", "postIncr"},
                {"--", "postDecr"}
        };
        for (String[] operator : arithmeticOperators) {
            collection.addAll(Arrays.asList(new Object[][]{
                    {"bool   $a=false; $a" + operator[0] + ";", typeStruct(operator[1], Int, 1, 1, 0)},
                    {"int    $a=0; $a" + operator[0] + ";", typeStruct(operator[1], Int, 1, 1, 0)},
                    {"float  $a=0; $a" + operator[0] + ";", typeStruct(operator[1], Float, 1, 1, 0)},
                    {"bool?  $a=false; $a" + operator[0] + ";", typeStruct(operator[1], Int, 1, 1, 0)},
                    {"int?   $a=0; $a" + operator[0] + ";", typeStruct(operator[1], Int, 1, 1, 0)},
                    {"float? $a=1; $a" + operator[0] + ";", typeStruct(operator[1], Float, 1, 1, 0)}
            }));
        }
    }

    private static void addArithmeticBinaryOperators() {
        String[] arithmeticOperators = new String[]{"+", "-", "*", "/", "%"};
        for (String operator : arithmeticOperators) {
            collection.addAll(Arrays.asList(new Object[][]{
                    {"true " + operator + " false;", typeStruct(operator, Int, 1, 0, 0)},
                    {"true " + operator + " 2;", typeStruct(operator, Int, 1, 0, 0)},
                    {"1 " + operator + " false;", typeStruct(operator, Int, 1, 0, 0)},
                    {"1 " + operator + " 2;", typeStruct(operator, Int, 1, 0, 0)},
                    {"1 " + operator + " 2.0;", typeStruct(operator, Float, 1, 0, 0)},
                    {"1.0 " + operator + " 2;", typeStruct(operator, Float, 1, 0, 0)},
                    {"true " + operator + " 2.0;", typeStruct(operator, Float, 1, 0, 0)},
                    {"1.0 " + operator + " false;", typeStruct(operator, Float, 1, 0, 0)},
                    {"1.0 " + operator + " 2.0;", typeStruct(operator, Float, 1, 0, 0)},
                    {"true " + operator + " 1 " + operator + " 10;", new TypeCheckStruct[]{
                            struct(operator, Int, 1, 0, 0),
                            struct(operator, Int, 1, 0, 0, 0)}
                    },
                    {"1 " + operator + " 1 " + operator + " false;", new TypeCheckStruct[]{
                            struct(operator, Int, 1, 0, 0),
                            struct(operator, Int, 1, 0, 0, 0),
                            struct(operator, Int, 1, 0, 0, 0)}
                    },
                    {"1 " + operator + " 1 " + operator + " 10;", new TypeCheckStruct[]{
                            struct(operator, Int, 1, 0, 0),
                            struct(operator, Int, 1, 0, 0, 0)}
                    },
                    {"1 " + operator + " 5.0 " + operator + " 10;", new TypeCheckStruct[]{
                            struct(operator, Float, 1, 0, 0),
                            struct(operator, Float, 1, 0, 0, 0)}
                    },
                    {"1 " + operator + " 5.0 " + operator + " true;", new TypeCheckStruct[]{
                            struct(operator, Float, 1, 0, 0),
                            struct(operator, Float, 1, 0, 0, 0)}
                    },
                    {"2.0 " + operator + " 5.0 " + operator + " 10.3;", new TypeCheckStruct[]{
                            struct(operator, Float, 1, 0, 0),
                            struct(operator, Float, 1, 0, 0, 0)}
                    },
                    {"bool?  $a=false; $a " + operator + " $a;", typeStruct(operator, Int, 1, 1, 0)},
                    {"bool?  $a=false; $a " + operator + " 1;", typeStruct(operator, Int, 1, 1, 0)},
                    {"bool?  $a=null; int? $b=1; $a " + operator + " $b;", typeStruct(operator, Int, 1, 2, 0)},
                    {"int?   $a=null; $a " + operator + " $a;", typeStruct(operator, Int, 1, 1, 0)},
                    {"int?   $a=0; $a " + operator + " 1;", typeStruct(operator, Int, 1, 1, 0)},
                    {"bool?  $a=null; $a " + operator + " 1.0;", typeStruct(operator, Float, 1, 1, 0)},
                    {"bool?  $a=true; float? $b=1.0; $a " + operator + " $b;", typeStruct(operator, Float, 1, 2, 0)},
                    {"int?   $a=null; $a " + operator + " 1.0;", typeStruct(operator, Float, 1, 1, 0)},
                    {"int?   $a=1;    float? $b=1.0; $a " + operator + " $b;", typeStruct(operator, Float, 1, 2, 0)},
                    {"float? $a=null; $a " + operator + " 1.0;", typeStruct(operator, Float, 1, 1, 0)},
                    {"float? $a=45.7; $a " + operator + " $a;", typeStruct(operator, Float, 1, 1, 0)}
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
