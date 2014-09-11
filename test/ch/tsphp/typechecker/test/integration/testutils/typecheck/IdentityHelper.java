/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.testutils.typecheck;

import ch.tsphp.typechecker.error.ReferenceErrorDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.Array;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.Bool;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.BoolFalseable;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.BoolFalseableAndNullable;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.BoolNullable;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.ErrorException;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.Exception;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.Float;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.FloatFalseable;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.FloatFalseableAndNullable;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.FloatNullable;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.Int;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.IntFalseable;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.IntFalseableAndNullable;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.IntNullable;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.Mixed;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.Null;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.Resource;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.String;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.StringFalseable;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.StringFalseableAndNullable;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.StringNullable;

public class IdentityHelper
{

    private static String op;

    public static Collection<Object[]> getIdentityTestStrings(String theOperator, boolean notOnlyClassInterfaceTypes) {
        op = theOperator;

        Collection<Object[]> collection = new ArrayList<>();
        if (notOnlyClassInterfaceTypes) {

            collection.addAll(Arrays.asList(new Object[][]{
                    {"true " + op + "   true;", struct(0, "true", Bool, "true", Bool)},
                    {"true " + op + "  false;", struct(0, "true", Bool, "false", Bool)},
                    {"false " + op + " true;", struct(0, "false", Bool, "true", Bool)},
                    {"false " + op + " false;", struct(0, "false", Bool, "false", Bool)},
                    {"2 " + op + " 1;", struct(0, "2", Int, "1", Int)},
                    {"1.4 " + op + " 5.6;", struct(0, "1.4", Float, "5.6", Float)},
                    {"'hello' " + op + " 'hi';", struct(0, "'hello'", String, "'hi'", String)},
                    {"'hello' " + op + " \"hi\";", struct(0, "'hello'", String, "\"hi\"", String)},
                    {"\"hello\" " + op + " 'hi';", struct(0, "\"hello\"", String, "'hi'", String)},
                    {"\"hello\" " + op + " \"hi\";", struct(0, "\"hello\"", String, "\"hi\"", String)},
                    {"[1,2] " + op + " [4,5];", struct(0, "array", Array, "array", Array)},
                    {"[1,2] " + op + " array(4,5);", struct(0, "array", Array, "array", Array)},
                    {"array(1,2) " + op + " [4,5];", struct(0, "array", Array, "array", Array)},
                    {"array(1,2) " + op + " array(4,5);", struct(0, "array", Array, "array", Array)},

                    {"bool $a=false; $a " + op + " false;", struct(1, "$a", Bool, "false", Bool)},
                    {"bool $a=false; $a " + op + " true;", struct(1, "$a", Bool, "true", Bool)},
                    {"bool $a=false; false " + op + " $a;", struct(1, "false", Bool, "$a", Bool)},
                    {"bool $a=false; true " + op + " $a;", struct(1, "true", Bool, "$a", Bool)},
                    {"bool  $b=true; bool $a=false; $a  " + op + " $b;", struct(2, "$a", Bool, "$b", Bool)},
                    {"const bool a = true; bool $a=false; $a " + op + " a;", struct(2, "$a", Bool, "a#", Bool)},
                    {"mixed $b = null; bool $a=false; $a " + op + " $b;", struct(2, "$a", Bool, "$b", Mixed)},
                    {"bool! $a=false; $a " + op + " false;", struct(1, "$a", BoolFalseable, "false", Bool)},
                    {"bool! $a=false; $a " + op + " true;", struct(1, "$a", BoolFalseable, "true", Bool)},
                    {"bool! $a=false; false " + op + " $a;", struct(1, "false", Bool, "$a", BoolFalseable)},
                    {"bool! $a=false; true " + op + " $a;", struct(1, "true", Bool, "$a", BoolFalseable)},
                    {
                            "bool!  $b=true; bool! $a=false; $a  " + op + " $b;",
                            struct(2, "$a", BoolFalseable, "$b", BoolFalseable)
                    },
                    {"mixed $b = null; bool! $a=false; $a " + op + " $b;", struct(2, "$a", BoolFalseable, "$b", Mixed)},
                    {"bool? $a=false; $a " + op + " false;", struct(1, "$a", BoolNullable, "false", Bool)},
                    {"bool? $a=false; $a " + op + " true;", struct(1, "$a", BoolNullable, "true", Bool)},
                    {"bool? $a=false; $a " + op + " null;", struct(1, "$a", BoolNullable, "null", Null)},
                    {"bool? $a=false; false " + op + " $a;", struct(1, "false", Bool, "$a", BoolNullable)},
                    {"bool? $a=false; true " + op + " $a;", struct(1, "true", Bool, "$a", BoolNullable)},
                    {"bool? $a=false; null " + op + " $a;", struct(1, "null", Null, "$a", BoolNullable)},
                    {
                            "bool?  $b=true; bool? $a=false; $a  " + op + " $b;",
                            struct(2, "$a", BoolNullable, "$b", BoolNullable)
                    },
                    {"mixed $b = null; bool? $a=false; $a " + op + " $b;", struct(2, "$a", BoolNullable, "$b", Mixed)},
                    {"bool!? $a=true; $a " + op + "  true;", struct(1, "$a", BoolFalseableAndNullable, "true", Bool)},
                    {"bool!? $a=true; $a " + op + " false;", struct(1, "$a", BoolFalseableAndNullable, "false", Bool)},
                    {"bool!? $a=true; $a " + op + "  null;", struct(1, "$a", BoolFalseableAndNullable, "null", Null)},
                    {"bool!? $a=true; true  " + op + " $a;", struct(1, "true", Bool, "$a", BoolFalseableAndNullable)},
                    {"bool!? $a=true; false " + op + " $a;", struct(1, "false", Bool, "$a", BoolFalseableAndNullable)},
                    {"bool!? $a=true; null  " + op + " $a;", struct(1, "null", Null, "$a", BoolFalseableAndNullable)},
                    {
                            "bool!?  $b=true; bool!? $a=false; $a  " + op + " $b;",
                            struct(2, "$a", BoolFalseableAndNullable, "$b", BoolFalseableAndNullable)
                    },
                    {
                            "mixed $b = null; bool!? $a=false; $a " + op + " $b;",
                            struct(2, "$a", BoolFalseableAndNullable, "$b", Mixed)
                    },
                    {"int  $a=0; $a  " + op + " 1;", struct(1, "$a", Int, "1", Int)},
                    {"int  $a=0; 1  " + op + " $a;", struct(1, "1", Int, "$a", Int)},
                    {"int  $b=1; int $a=2; $a  " + op + " $b;", struct(2, "$a", Int, "$b", Int)},
                    {"const int a = 1; int $a=1; $a  " + op + " a;", struct(2, "$a", Int, "a#", Int)},
                    {"mixed $b = null; int $a=1; $a " + op + " $b;", struct(2, "$a", Int, "$b", Mixed)},
                    {"int! $a=0; $a     " + op + " 1;", struct(1, "$a", IntFalseable, "1", Int)},
                    {"int! $a=0; $a " + op + " false;", struct(1, "$a", IntFalseable, "false", Bool)},
                    {"int! $a=0; 1     " + op + " $a;", struct(1, "1", Int, "$a", IntFalseable)},
                    {"int! $a=0; false " + op + " $a;", struct(1, "false", Bool, "$a", IntFalseable)},
                    {"int! $b=1; int! $a=2; $a  " + op + " $b;", struct(2, "$a", IntFalseable, "$b", IntFalseable)},
                    {"mixed $b = null; int! $a=1; $a " + op + " $b;", struct(2, "$a", IntFalseable, "$b", Mixed)},
                    {"int? $a=0; $a     " + op + " 1;", struct(1, "$a", IntNullable, "1", Int)},
                    {"int? $a=0; $a  " + op + " null;", struct(1, "$a", IntNullable, "null", Null)},
                    {"int? $a=0; 1     " + op + " $a;", struct(1, "1", Int, "$a", IntNullable)},
                    {"int? $a=0; null  " + op + " $a;", struct(1, "null", Null, "$a", IntNullable)},
                    {"int? $b=1; int? $a=2; $a  " + op + " $b;", struct(2, "$a", IntNullable, "$b", IntNullable)},
                    {"mixed $b = null; int? $a=1; $a " + op + " $b;", struct(2, "$a", IntNullable, "$b", Mixed)},
                    {"int!? $a=0; $a      " + op + " 1;", struct(1, "$a", IntFalseableAndNullable, "1", Int)},
                    {"int!? $a=0; $a  " + op + " false;", struct(1, "$a", IntFalseableAndNullable, "false", Bool)},
                    {"int!? $a=0; $a   " + op + " null;", struct(1, "$a", IntFalseableAndNullable, "null", Null)},
                    {"int!? $a=0; 1      " + op + " $a;", struct(1, "1", Int, "$a", IntFalseableAndNullable)},
                    {"int!? $a=0; false  " + op + " $a;", struct(1, "false", Bool, "$a", IntFalseableAndNullable)},
                    {"int!? $a=0; null   " + op + " $a;", struct(1, "null", Null, "$a", IntFalseableAndNullable)},
                    {
                            "int!? $b=1; int!? $a=2; $a  " + op + " $b;",
                            struct(2, "$a", IntFalseableAndNullable, "$b", IntFalseableAndNullable)
                    },
                    {
                            "mixed $b = null; int!? $a=1; $a " + op + " $b;",
                            struct(2, "$a", IntFalseableAndNullable, "$b", Mixed)
                    },
                    {"float  $a=4.1; $a  " + op + " 2.56;", struct(1, "$a", Float, "2.56", Float)},
                    {"float  $a=4.1; 2.56 " + op + "  $a;", struct(1, "2.56", Float, "$a", Float)},
                    {"float  $b=4.1; float $a=2.56; $a " + op + " $b;", struct(2, "$a", Float, "$b", Float)},
                    {"const float a = 1; float $a=1.0; $a  " + op + " a;", struct(2, "$a", Float, "a#", Float)},
                    {"mixed $b = null; float $a=1.5; $a " + op + " $b;", struct(2, "$a", Float, "$b", Mixed)},
                    {"float! $a=4.1; $a    " + op + " 2.56;", struct(1, "$a", FloatFalseable, "2.56", Float)},
                    {"float! $a=4.1; $a   " + op + " false;", struct(1, "$a", FloatFalseable, "false", Bool)},
                    {"float! $a=4.1; 2.56    " + op + " $a;", struct(1, "2.56", Float, "$a", FloatFalseable)},
                    {"float! $a=4.1; false   " + op + " $a;", struct(1, "false", Bool, "$a", FloatFalseable)},
                    {
                            "float! $b=4.1; float! $a=2.56; $a " + op + " $b;",
                            struct(2, "$a", FloatFalseable, "$b", FloatFalseable)
                    },
                    {"mixed $b = null; float! $a=1.5; $a " + op + " $b;", struct(2, "$a", FloatFalseable, "$b", Mixed)},
                    {"float? $a=4.1; $a    " + op + " 2.56;", struct(1, "$a", FloatNullable, "2.56", Float)},
                    {"float? $a=4.1; $a    " + op + " null;", struct(1, "$a", FloatNullable, "null", Null)},
                    {"float? $a=4.1; 2.56    " + op + " $a;", struct(1, "2.56", Float, "$a", FloatNullable)},
                    {"float? $a=4.1; null    " + op + " $a;", struct(1, "null", Null, "$a", FloatNullable)},
                    {
                            "float? $b=4.1; float? $a=2.56; $a " + op + " $b;",
                            struct(2, "$a", FloatNullable, "$b", FloatNullable)
                    },
                    {"mixed $b = null; float? $a=1.5; $a " + op + " $b;", struct(2, "$a", FloatNullable, "$b", Mixed)},
                    {"float!? $a=4.1; $a  " + op + " 2.56;", struct(1, "$a", FloatFalseableAndNullable, "2.56", Float)},
                    {"float!? $a=4.1; $a " + op + " false;", struct(1, "$a", FloatFalseableAndNullable, "false", Bool)},
                    {"float!? $a=4.1; $a  " + op + " null;", struct(1, "$a", FloatFalseableAndNullable, "null", Null)},
                    {"float!? $a=4.1; 2.56  " + op + " $a;", struct(1, "2.56", Float, "$a", FloatFalseableAndNullable)},
                    {"float!? $a=4.1; false " + op + " $a;", struct(1, "false", Bool, "$a", FloatFalseableAndNullable)},
                    {"float!? $a=4.1; null  " + op + " $a;", struct(1, "null", Null, "$a", FloatFalseableAndNullable)},
                    {
                            "float!? $b=4.1; float!? $a=2.56; $a " + op + " $b;",
                            struct(2, "$a", FloatFalseableAndNullable, "$b", FloatFalseableAndNullable)
                    },
                    {
                            "mixed $b = null; float!? $a=1.5; $a " + op + " $b;",
                            struct(2, "$a", FloatFalseableAndNullable, "$b", Mixed)
                    },
                    {"string $a=''; $a  " + op + " 'hi';", struct(1, "$a", String, "'hi'", String)},
                    {"string $a=''; $a  " + op + " \"hi\";", struct(1, "$a", String, "\"hi\"", String)},
                    {"string $a=''; 'hi'  " + op + " $a;", struct(1, "'hi'", String, "$a", String)},
                    {"string $a=''; \"hi\"  " + op + " $a;", struct(1, "\"hi\"", String, "$a", String)},
                    {"string $b=''; string $a=''; $a " + op + " $b;", struct(2, "$a", String, "$b", String)},
                    {
                            "const string a = 'hello'; string $a=''; $a  " + op + " a;",
                            struct(2, "$a", String, "a#", String)
                    },
                    {"mixed $b = null; string $a='hi'; $a " + op + " $b;", struct(2, "$a", String, "$b", Mixed)},
                    {"string! $a=''; $a   " + op + " 'hi';", struct(1, "$a", StringFalseable, "'hi'", String)},
                    {"string! $a=''; $a " + op + " \"hi\";", struct(1, "$a", StringFalseable, "\"hi\"", String)},
                    {"string! $a=''; $a  " + op + " false;", struct(1, "$a", StringFalseable, "false", Bool)},
                    {"string! $a=''; 'hi'   " + op + " $a;", struct(1, "'hi'", String, "$a", StringFalseable)},
                    {"string! $a=''; \"hi\" " + op + " $a;", struct(1, "\"hi\"", String, "$a", StringFalseable)},
                    {"string! $a=''; false  " + op + " $a;", struct(1, "false", Bool, "$a", StringFalseable)},
                    {
                            "string! $b=''; string! $a=''; $a " + op + " $b;",
                            struct(2, "$a", StringFalseable, "$b", StringFalseable)
                    },
                    {
                            "mixed $b = null; string! $a='hi'; $a " + op + " $b;",
                            struct(2, "$a", StringFalseable, "$b", Mixed)
                    },
                    {"string? $a=''; $a   " + op + " 'hi';", struct(1, "$a", StringNullable, "'hi'", String)},
                    {"string? $a=''; $a " + op + " \"hi\";", struct(1, "$a", StringNullable, "\"hi\"", String)},
                    {"string? $a=''; $a   " + op + " null;", struct(1, "$a", StringNullable, "null", Null)},
                    {"string? $a=''; 'hi'   " + op + " $a;", struct(1, "'hi'", String, "$a", StringNullable)},
                    {"string? $a=''; \"hi\" " + op + " $a;", struct(1, "\"hi\"", String, "$a", StringNullable)},
                    {"string? $a=''; null   " + op + " $a;", struct(1, "null", Null, "$a", StringNullable)},
                    {
                            "string? $b=''; string? $a=''; $a " + op + " $b;",
                            struct(2, "$a", StringNullable, "$b", StringNullable)
                    },
                    {
                            "mixed $b = null; string? $a='hi'; $a " + op + " $b;",
                            struct(2, "$a", StringNullable, "$b", Mixed)
                    },
                    {
                            "string!? $a=''; $a " + op + " 'hi';",
                            struct(1, "$a", StringFalseableAndNullable, "'hi'", String)
                    },
                    {
                            "string!? $a=''; $a  " + op + " \"hi\";",
                            struct(1, "$a", StringFalseableAndNullable, "\"hi\"", String)
                    },
                    {
                            "string!? $a=''; $a " + op + " false;",
                            struct(1, "$a", StringFalseableAndNullable, "false", Bool)
                    },
                    {"string!? $a=''; $a  " + op + " null;", struct(1, "$a", StringFalseableAndNullable, "null", Null)},
                    {
                            "string!? $a=''; 'hi'  " + op + " $a;",
                            struct(1, "'hi'", String, "$a", StringFalseableAndNullable)
                    },
                    {
                            "string!? $a=''; \"hi\"  " + op + " $a;",
                            struct(1, "\"hi\"", String, "$a", StringFalseableAndNullable)
                    },
                    {
                            "string!? $a=''; false  " + op + " $a;",
                            struct(1, "false", Bool, "$a", StringFalseableAndNullable)
                    },
                    {"string!? $a=''; null  " + op + " $a;", struct(1, "null", Null, "$a", StringFalseableAndNullable)},
                    {
                            "string!? $b=''; string!? $a=''; $a " + op + " $b;",
                            struct(2, "$a", StringFalseableAndNullable, "$b", StringFalseableAndNullable)
                    },
                    {
                            "mixed $b = null; string!? $a='hi'; $a " + op + " $b;",
                            struct(2, "$a", StringFalseableAndNullable, "$b", Mixed)
                    },
                    {"array    $a=null; $a  " + op + " [0];", struct(1, "$a", Array, "array", Array)},
                    {"array    $a=null; $a  " + op + " array(1,2);", struct(1, "$a", Array, "array", Array)},
                    {"array    $a=null; $a  " + op + " null;", struct(1, "$a", Array, "null", Null)},
                    {"array    $a=null; [0]  " + op + " $a;", struct(1, "array", Array, "$a", Array)},
                    {"array    $a=null; array(1,2)  " + op + " $a;", struct(1, "array", Array, "$a", Array)},
                    {"array    $a=null; null  " + op + " $a;", struct(1, "null", Null, "$a", Array)},
                    {"mixed $b = null; array $a=[2,3]; $a " + op + " $b;", struct(2, "$a", Array, "$b", Mixed)},
                    {"resource $a=null; $a  " + op + " null;", struct(1, "$a", Resource, "null", Null)},
                    {
                            "resource $b=null; resource $a=null; $a  " + op + " $b;",
                            struct(2, "$a", Resource, "$b", Resource)
                    },
                    {"mixed $b = null; resource $a=null; $a " + op + " $b;", struct(2, "$a", Resource, "$b", Mixed)},
                    {"mixed   $a=null; $a  " + op + " null;", struct(1, "$a", Mixed, "null", Null)},
                    {"mixed   $a=null; $a  " + op + " true;", struct(1, "$a", Mixed, "true", Bool)},
                    {"mixed   $a=null; $a  " + op + " false;", struct(1, "$a", Mixed, "false", Bool)},
                    {"mixed   $a=null; $a  " + op + " 1;", struct(1, "$a", Mixed, "1", Int)},
                    {"mixed   $a=null; $a  " + op + " 1.0;", struct(1, "$a", Mixed, "1.0", Float)},
                    {"mixed   $a=null; $a  " + op + " 'hello';", struct(1, "$a", Mixed, "'hello'", String)},
                    {"mixed   $a=null; $a  " + op + " \"hi\";", struct(1, "$a", Mixed, "\"hi\"", String)},
                    {"mixed   $a=null; $a  " + op + " [1,2];", struct(1, "$a", Mixed, "array", Array)},
                    {"mixed   $a=null; $a  " + op + " array(1,5);", struct(1, "$a", Mixed, "array", Array)},
                    {"bool  $b=false; mixed $a=null; $a  " + op + " $b;", struct(2, "$a", Mixed, "$b", Bool)},
                    {"bool! $b=false; mixed $a=null; $a  " + op + " $b;", struct(2, "$a", Mixed, "$b", BoolFalseable)},
                    {"bool? $b=false; mixed $a=null; $a  " + op + " $b;", struct(2, "$a", Mixed, "$b", BoolNullable)},
                    {
                            "bool!? $b=false; mixed $a=null; $a  " + op + " $b;",
                            struct(2, "$a", Mixed, "$b", BoolFalseableAndNullable)
                    },
                    {"int  $b=1; mixed $a=null; $a  " + op + " $b;", struct(2, "$a", Mixed, "$b", Int)},
                    {"int! $b=1; mixed $a=null; $a  " + op + " $b;", struct(2, "$a", Mixed, "$b", IntFalseable)},
                    {"int? $b=1; mixed $a=null; $a  " + op + " $b;", struct(2, "$a", Mixed, "$b", IntNullable)},
                    {
                            "int!? $b=1; mixed $a=null; $a  " + op + " $b;",
                            struct(2, "$a", Mixed, "$b", IntFalseableAndNullable)
                    },
                    {"float  $b=1.5; mixed $a=null; $a  " + op + " $b;", struct(2, "$a", Mixed, "$b", Float)},
                    {"float! $b=1.5; mixed $a=null; $a  " + op + " $b;", struct(2, "$a", Mixed, "$b", FloatFalseable)},
                    {"float? $b=1.5; mixed $a=null; $a  " + op + " $b;", struct(2, "$a", Mixed, "$b", FloatNullable)},
                    {
                            "float!? $b=1.5; mixed $a=null; $a  " + op + " $b;",
                            struct(2, "$a", Mixed, "$b", FloatFalseableAndNullable)
                    },
                    {"string $b='hi'; mixed $a=null; $a " + op + " $b;", struct(2, "$a", Mixed, "$b", String)},
                    {
                            "string! $b='hi'; mixed $a=null; $a  " + op + " $b;",
                            struct(2, "$a", Mixed, "$b", StringFalseable)
                    },
                    {
                            "string? $b='hi'; mixed $a=null; $a  " + op + " $b;",
                            struct(2, "$a", Mixed, "$b", StringNullable)
                    },
                    {
                            "string!? $b='hi'; mixed $a=null; $a  " + op + " $b;",
                            struct(2, "$a", Mixed, "$b", StringFalseableAndNullable)
                    },
                    {"array $b=null; mixed $a=null; $a  " + op + " $b;", struct(2, "$a", Mixed, "$b", Array)},
                    {"resource $b=null; mixed $a=null; $a  " + op + " $b;", struct(2, "$a", Mixed, "$b", Resource)},
                    {
                            "ErrorException $b=null; mixed $a=null; $a  " + op + " $b;",
                            struct(2, "$a", Mixed, "$b", ErrorException)
                    },
                    {
                            "Exception $b=null; mixed $a=null; $a  " + op + " $b;",
                            struct(2, "$a", Mixed, "$b", Exception)
                    },
                    {"mixed   $b=null; mixed $a=null; $a  " + op + " $b;", struct(2, "$a", Mixed, "$b", Mixed)},
                    {"ErrorException $a=null; $a  " + op + " null;", struct(1, "$a", ErrorException, "null", Null)},
                    {
                            "Exception $b=null; ErrorException $a=null; $a  " + op + " $b;",
                            struct(2, "$a", ErrorException, "$b", Exception)
                    },
                    {
                            "mixed $b=null; ErrorException $a=null; $a  " + op + " $b;",
                            struct(2, "$a", ErrorException, "$b", Mixed)
                    },
                    {"Exception $a=null; $a  " + op + " null;", struct(1, "$a", Exception, "null", Null)},
                    {
                            "ErrorException $b=null; Exception $a=null; $a  " + op + " $b;",
                            struct(2, "$a", Exception, "$b", ErrorException)
                    },
                    {"mixed $b=null; Exception $a=null; $a  " + op + " $b;", struct(2, "$a", Exception, "$b", Mixed)},

            }));
        }
        collection.addAll(Arrays.asList(new Object[][]{
                {
                        "ErrorException $b=null; ErrorException $a=null; $a  " + op + " $b;",
                        struct(2, "$a", ErrorException, "$b", ErrorException)
                },
                {
                        "ErrorException $b=null; Exception $a=null; $a  " + op + " $b;",
                        struct(2, "$a", Exception, "$b", ErrorException)
                },
                {
                        "Exception $b=null; Exception $a=null; $a  " + op + " $b;",
                        struct(2, "$a", Exception, "$b", Exception)
                }
        }));

        return collection;
    }


    private static TypeCheckStruct[] struct(
            int pos, String leftHandSide, EBuiltInType leftType, String rightHandSide, EBuiltInType rightType) {
        return new TypeCheckStruct[]{
                AOperatorTypeCheckTest.struct(op, Bool, 1, pos, 0),
                AOperatorTypeCheckTest.struct(leftHandSide, leftType, 1, pos, 0, 0),
                AOperatorTypeCheckTest.struct(rightHandSide, rightType, 1, pos, 0, 1)
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
