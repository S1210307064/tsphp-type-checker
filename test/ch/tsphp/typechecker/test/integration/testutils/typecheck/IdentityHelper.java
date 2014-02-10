package ch.tsphp.typechecker.test.integration.testutils.typecheck;

import ch.tsphp.typechecker.error.ReferenceErrorDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static ch.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.Array;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.Bool;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.BoolNullable;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.ErrorException;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.Exception;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.Float;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.FloatNullable;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.Int;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.IntNullable;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.Null;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.Object;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.Resource;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.String;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.StringNullable;

public class IdentityHelper
{

    private static String op;

    public static Collection<Object[]> getIdentityTestStrings(String theOperator, boolean notOnlyClassInterfaceTypes) {
        op = theOperator;

        Collection<Object[]> collection = new ArrayList<>();
        if (notOnlyClassInterfaceTypes) {

            collection.addAll(Arrays.asList(new Object[][]{
                    {"bool   $a=false; $a " + op + " false;", struct("false", Bool, Bool)},
                    {"const bool a = true; bool $a=false; $a " + op + " a;", structTwo("a#", Bool, Bool)},
                    {"bool   $a=false; $a " + op + " true;", struct("true", Bool, Bool)},
                    {"bool?  $a=false; $a " + op + " false;", struct("false", BoolNullable, Bool)},
                    {"const bool a = true; bool? $a=false; $a " + op + " a;", structTwo("a#", BoolNullable, Bool)},
                    {"bool?  $a=true; $a  " + op + " true;", struct("true", BoolNullable, Bool)},
                    {"bool?  $a=true; $a  " + op + " null;", struct("null", BoolNullable, Null)},
                    {"bool?  $b=true; bool? $a=false; $a  " + op + " $b;", structTwo("$b", BoolNullable, BoolNullable)},
                    {"int    $a=0; $a  " + op + " true;", struct("true", Int, Bool)},
                    {"int    $a=0; $a  " + op + " 1;", struct("1", Int, Int)},
                    {"const int a = 1; int $a=1; $a  " + op + " a;", structTwo("a#", Int, Int)},
                    {"int?   $a=null; $a  " + op + " true;", struct("true", IntNullable, Bool)},
                    {"int?   $a=null; $a  " + op + " 1;", struct("1", IntNullable, Int)},
                    {"const int a = 1; int? $a=1; $a  " + op + " a;", structTwo("a#", IntNullable, Int)},
                    {"int?   $a=null; $a  " + op + " null;", struct("null", IntNullable, Null)},
                    {"bool?  $b=null; int? $a=1; $a  " + op + " $b;", structTwo("$b", IntNullable, BoolNullable)},
                    {"int?   $b=null; int? $a=1; $a  " + op + " $b;", structTwo("$b", IntNullable, IntNullable)},
                    {"float  $a=4.1; $a  " + op + " true;", struct("true", Float, Bool)},
                    {"float  $a=4.1; $a  " + op + " 6;", struct("6", Float, Int)},
                    {"float  $a=4.1; $a  " + op + " 2.56;", struct("2.56", Float, Float)},
                    {"const float a = 1; float $a=1.0; $a  " + op + " a;", structTwo("a#", Float, Float)},
                    {"float? $a=4.1; $a  " + op + " true;", struct("true", FloatNullable, Bool)},
                    {"float? $a=4.1; $a  " + op + " 6;", struct("6", FloatNullable, Int)},
                    {"float? $a=4.1; $a  " + op + " 2.56;", struct("2.56", FloatNullable, Float)},
                    {"const float a = 2.12; float? $a=null; $a  " + op + " a;", structTwo("a#", FloatNullable, Float)},
                    {"float? $a=null; $a  " + op + " null;", struct("null", FloatNullable, Null)},
                    {
                            "bool?  $b=null; float? $a=null; $a  " + op + " $b;",
                            structTwo("$b", FloatNullable, BoolNullable)},
                    {"int?   $b=null; float? $a=null; $a  " + op + " $b;", structTwo("$b", FloatNullable, IntNullable)},
                    {
                            "float? $b=null; float? $a=null; $a  " + op + " $b;",
                            structTwo("$b", FloatNullable, FloatNullable)},
                    {"string $a=''; $a  " + op + " true;", struct("true", String, Bool)},
                    {"string $a=''; $a  " + op + " 1;", struct("1", String, Int)},
                    {"string $a=''; $a  " + op + " 5.6;", struct("5.6", String, Float)},
                    {"string $a=''; $a  " + op + " 'hello';", struct("'hello'", String, String)},
                    {"const string a = 'hello'; string $a=''; $a  " + op + " a;", structTwo("a#", String, String)},
                    {"string  $a=''; $a  " + op + " \"yellow\";", struct("\"yellow\"", String, String)},
                    {"string? $a=null; $a  " + op + " true;", struct("true", StringNullable, Bool)},
                    {"string? $a=null; $a  " + op + " 1;", struct("1", StringNullable, Int)},
                    {"string? $a=null; $a  " + op + " 5.6;", struct("5.6", StringNullable, Float)},
                    {"string? $a=null; $a  " + op + " 'hello';", struct("'hello'", StringNullable, String)},
                    {
                            "const string a = 'hello'; string? $a=null; $a  " + op + " a;",
                            structTwo("a#", StringNullable, String)
                    },
                    {"string? $a=''; $a  " + op + " \"yellow\";", struct("\"yellow\"", StringNullable, String)},
                    {"string? $a=''; $a  " + op + " null;", struct("null", StringNullable, Null)},
                    {
                            "bool?   $b=null; string? $a=null; $a  " + op + " $b;",
                            structTwo("$b", StringNullable, BoolNullable)},
                    {
                            "int?    $b=null; string? $a=null; $a  " + op + " $b;",
                            structTwo("$b", StringNullable, IntNullable)},
                    {
                            "float?  $b=null; string? $a=null; $a  " + op + " $b;",
                            structTwo("$b", StringNullable, FloatNullable)},
                    {
                            "string? $b=null; string? $a=null; $a  " + op + " $b;",
                            structTwo("$b", StringNullable, StringNullable)},
                    {"array    $a=null; $a  " + op + " [0];", struct("array", Array, Array)},
                    {"array    $a=null; $a  " + op + " array(1,2);", struct("array", Array, Array)},
                    {"array    $a=null; $a  " + op + " null;", struct("null", Array, Null)},
                    {"resource $b=null; resource $a=null; $a  " + op + " $b;", structTwo("$b", Resource, Resource)},
                    {"resource $a=null; $a  " + op + " null;", struct("null", Resource, Null)},
                    {"object   $a=null; $a  " + op + " true;", struct("true", Object, Bool)},
                    {"object   $a=null; $a  " + op + " false;", struct("false", Object, Bool)},
                    {"object   $a=null; $a  " + op + " 1;", struct("1", Object, Int)},
                    {"object   $a=null; $a  " + op + " 1.0;", struct("1.0", Object, Float)},
                    {"object   $a=null; $a  " + op + " 'hello';", struct("'hello'", Object, String)},
                    {"object   $a=null; $a  " + op + " [1,2];", struct("array", Object, Array)},
                    {"bool?    $b=null; object $a=null; $a  " + op + " $b;", structTwo("$b", Object, BoolNullable)},
                    {"int?     $b=null; object $a=null; $a  " + op + " $b;", structTwo("$b", Object, IntNullable)},
                    {"float?   $b=null; object $a=null; $a  " + op + " $b;", structTwo("$b", Object, FloatNullable)},
                    {"string?  $b=null; object $a=null; $a  " + op + " $b;", structTwo("$b", Object, StringNullable)},
                    {"resource $b=null; object $a=null; $a  " + op + " $b;", structTwo("$b", Object, Resource)},
                    {"object   $b=null; object $a=null; $a  " + op + " $b;", structTwo("$b", Object, Object)},
                    {"object   $a=null; $a  " + op + " null;", struct("null", Object, Null)},
                    {"ErrorException $a=null; $a  " + op + " null;", struct("null", ErrorException, Null)},
                    {
                            "ErrorException $b=null; object $a=null; $a  " + op + " $b;",
                            structTwo("$b", Object, ErrorException)},
                    {"Exception $b=null; object $a=null; $a  " + op + " $b;", structTwo("$b", Object, Exception)},
                    {"Exception $a=null; $a  " + op + " null;", struct("null", Exception, Null)}
            }));
        }
        collection.addAll(Arrays.asList(new Object[][]{
                {
                        "ErrorException $b=null; ErrorException $a=null; $a  " + op + " $b;",
                        structTwo("$b", ErrorException, ErrorException)
                },
                {
                        "ErrorException $b=null; Exception $a=null; $a  " + op + " $b;",
                        structTwo("$b", Exception, ErrorException)
                },
                {
                        "Exception $b=null; Exception $a=null; $a  " + op + " $b;",
                        structTwo("$b", Exception, Exception)
                }
        }));

        return collection;
    }

    private static TypeCheckStruct[] struct(String rightHandSide, EBuiltInType leftType, EBuiltInType rightType) {
        return new TypeCheckStruct[]{
                AOperatorTypeCheckTest.struct(op, Bool, 1, 1, 0),
                AOperatorTypeCheckTest.struct("$a", leftType, 1, 1, 0, 0),
                AOperatorTypeCheckTest.struct(rightHandSide, rightType, 1, 1, 0, 1)
        };

    }

    private static TypeCheckStruct[] structTwo(String rightHandSide, EBuiltInType leftType, EBuiltInType rightType) {
        return new TypeCheckStruct[]{
                AOperatorTypeCheckTest.struct(op, Bool, 1, 2, 0),
                AOperatorTypeCheckTest.struct("$a", leftType, 1, 2, 0, 0),
                AOperatorTypeCheckTest.struct(rightHandSide, rightType, 1, 2, 0, 1)
        };

    }

    public static Collection<Object[]> getIdentityErrorTestStrings(String operator) {
        List<Object[]> collection = new ArrayList<>();
        ReferenceErrorDto[] errorDto = new ReferenceErrorDto[]{new ReferenceErrorDto(operator, 2, 1)};


        addErrorStringsToCollection(collection, errorDto, operator, "bool", "false", new String[][]{
                {"array", "null"},
                {"resource", "null"},
                {"Exception", "null"},
                {"ErrorException", "null"}
        });


        addErrorStringsToCollection(collection, errorDto, operator, "bool?", "null", new String[][]{
                {"int", "0"},
                {"float", "0.0"},
                {"array", "null"},
                {"resource", "null"},
                {"Exception", "null"},
                {"ErrorException", "null"}
        });

        addErrorStringsToCollection(collection, errorDto, operator, "int", "6", new String[][]{
                {"bool?", "null"},
                {"array", "null"},
                {"resource", "null"},
                {"Exception", "null"},
                {"ErrorException", "null"}
        });

        addErrorStringsToCollection(collection, errorDto, operator, "int?", "6", new String[][]{
                {"float", "0.41"},
                {"string", "''"},
                {"array", "null"},
                {"resource", "null"},
                {"Exception", "null"},
                {"ErrorException", "null"}
        });

        addErrorStringsToCollection(collection, errorDto, operator, "float", "7.1", new String[][]{
                {"bool?", "null"},
                {"int?", "null"},
                {"array", "null"},
                {"resource", "null"},
                {"Exception", "null"},
                {"ErrorException", "null"}
        });

        addErrorStringsToCollection(collection, errorDto, operator, "float?", "7.1", new String[][]{
                {"string", "''"},
                {"array", "null"},
                {"resource", "null"},
                {"Exception", "null"},
                {"ErrorException", "null"}
        });

        addErrorStringsToCollection(collection, errorDto, operator, "string", "''", new String[][]{
                {"bool?", "null"},
                {"int?", "null"},
                {"float?", "null"},
                {"array", "null"},
                {"resource", "null"},
                {"Exception", "null"},
                {"ErrorException", "null"}
        });

        addErrorStringsToCollection(collection, errorDto, operator, "string?", "''", new String[][]{
                {"array", "null"},
                {"resource", "null"},
                {"Exception", "null"},
                {"ErrorException", "null"}
        });

        addErrorStringsToCollection(collection, errorDto, operator, "array", "null", new String[][]{
                {"bool", "false"},
                {"bool?", "null"},
                {"int", "0"},
                {"int?", "null"},
                {"float", "0.0"},
                {"float?", "null"},
                {"string", "''"},
                {"string?", "null"},
                {"resource", "null"},
                {"Exception", "null"},
                {"ErrorException", "null"}
        });

        addErrorStringsToCollection(collection, errorDto, operator, "resource", "null", new String[][]{
                {"bool", "false"},
                {"bool?", "null"},
                {"int", "0"},
                {"int?", "null"},
                {"float", "0.0"},
                {"float?", "null"},
                {"string", "''"},
                {"string?", "null"},
                {"array", "null"},
                {"Exception", "null"},
                {"ErrorException", "null"}
        });

        addErrorStringsToCollection(collection, errorDto, operator, "ErrorException", "null",
                new String[][]{
                        {"bool", "false"},
                        {"bool?", "null"},
                        {"int", "0"},
                        {"int?", "null"},
                        {"float", "0.0"},
                        {"float?", "null"},
                        {"string", "''"},
                        {"string?", "null"},
                        {"array", "null"},
                        {"resource", "null"},
                });
        addErrorStringsToCollection(collection, errorDto, operator, "Exception", "null", new String[][]{
                {"bool", "false"},
                {"bool?", "null"},
                {"int", "0"},
                {"int?", "null"},
                {"float", "0.0"},
                {"float?", "null"},
                {"string", "''"},
                {"string?", "null"},
                {"array", "null"},
                {"resource", "null"},
        });

        return collection;
    }

    private static void addErrorStringsToCollection(List<java.lang.Object[]> collection,
            ReferenceErrorDto[] errorDto, String operator,
            String variableType, String initialValue, String[][] types) {
        for (String[] type : types) {
            collection.add(new Object[]{
                    type[0] + " $b=" + type[1] + ";" + variableType + "$a=" + initialValue + ";"
                            + " $a\n " + operator + "  $b;",
                    errorDto
            });
        }
    }
}
