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
package ch.tutteli.tsphp.typechecker.test.testutils.typecheck;

import ch.tutteli.tsphp.typechecker.error.ReferenceErrorDto;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Array;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Bool;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.BoolNullable;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Float;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.FloatNullable;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Int;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.IntNullable;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Resource;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.String;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.StringNullable;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Object;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Null;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.ErrorException;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Exception;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class IdentityHelper
{

    private static String operator;

    public static Collection<Object[]> getIdentityTestStrings(String theOperator, boolean notOnlyClassInterfaceTypes) {
        operator = theOperator;

        Collection<Object[]> collection = new ArrayList<>();
        if (notOnlyClassInterfaceTypes) {

            collection.addAll(Arrays.asList(new Object[][]{
                {
                    "bool $a; $a " + operator + " false;",
                    struct("false", Bool, Bool)
                },
                {
                    "const bool a = true; bool $a; $a " + operator + " a;",
                    structTwo("a#", Bool, Bool)
                },
                {
                    "bool $a; $a " + operator + " true;",
                    struct("true", Bool, Bool)
                },
                {
                    "bool? $a; $a " + operator + " false;",
                    struct("false", BoolNullable, Bool)
                },
                {
                    "const bool a = true; bool? $a; $a " + operator + " a;",
                    structTwo("a#", BoolNullable, Bool)
                },
                {
                    "bool? $a; $a  " + operator + " true;",
                    struct("true", BoolNullable, Bool)
                },
                {
                    "bool?$a; $a  " + operator + " null;",
                    struct("null", BoolNullable, Null)
                },
                {
                    "bool? $b; bool? $a; $a  " + operator + " $b;",
                    structTwo("$b", BoolNullable, BoolNullable)
                },
                {
                    "int $a; $a  " + operator + " true;",
                    struct("true", Int, Bool)
                },
                {
                    "int $a; $a  " + operator + " 1;",
                    struct("1", Int, Int)
                },
                {
                    "const int a = 1; int $a; $a  " + operator + " a;",
                    structTwo("a#", Int, Int)
                },
                {
                    "int? $a; $a  " + operator + " true;",
                    struct("true", IntNullable, Bool)
                },
                {
                    "int? $a; $a  " + operator + " 1;",
                    struct("1", IntNullable, Int)
                },
                {
                    "const int a = 1; int? $a; $a  " + operator + " a;",
                    structTwo("a#", IntNullable, Int)
                },
                {
                    "int? $a; $a  " + operator + " null;",
                    struct("null", IntNullable, Null)
                },
                {
                    "bool? $b; int? $a; $a  " + operator + " $b;",
                    structTwo("$b", IntNullable, BoolNullable)
                },
                {
                    "int? $b; int? $a; $a  " + operator + " $b;",
                    structTwo("$b", IntNullable, IntNullable)
                },
                {
                    "float $a; $a  " + operator + " true;",
                    struct("true", Float, Bool)
                },
                {
                    "float $a; $a  " + operator + " 6;",
                    struct("6", Float, Int)
                },
                {
                    "float $a; $a  " + operator + " 2.56;",
                    struct("2.56", Float, Float)
                },
                {
                    "const float a = 1; float $a; $a  " + operator + " a;",
                    structTwo("a#", Float, Float)
                },
                {
                    "float? $a; $a  " + operator + " true;",
                    struct("true", FloatNullable, Bool)
                },
                {
                    "float? $a; $a  " + operator + " 6;",
                    struct("6", FloatNullable, Int)
                },
                {
                    "float? $a; $a  " + operator + " 2.56;",
                    struct("2.56", FloatNullable, Float)
                },
                {
                    "const float a = 2.12; float? $a; $a  " + operator + " a;",
                    structTwo("a#", FloatNullable, Float)
                },
                {
                    "float? $a; $a  " + operator + " null;",
                    struct("null", FloatNullable, Null)
                },
                {
                    " bool? $b; float? $a; $a  " + operator + " $b;",
                    structTwo("$b", FloatNullable, BoolNullable)
                },
                {
                    "int? $b; float? $a; $a  " + operator + " $b;",
                    structTwo("$b", FloatNullable, IntNullable)
                },
                {
                    "float? $b; float? $a; $a  " + operator + " $b;",
                    structTwo("$b", FloatNullable, FloatNullable)
                },
                {
                    "string $a; $a  " + operator + " true;",
                    struct("true", String, Bool)
                },
                {
                    "string $a; $a  " + operator + " 1;",
                    struct("1", String, Int)
                },
                {
                    "string $a; $a  " + operator + " 5.6;",
                    struct("5.6", String, Float)
                },
                {
                    "string $a; $a  " + operator + " 'hello';",
                    struct("'hello'", String, String)
                },
                {
                    "const string a = 'hello'; string $a; $a  " + operator + " a;",
                    structTwo("a#", String, String)
                },
                {
                    "string $a; $a  " + operator + " \"yellow\";",
                    struct("\"yellow\"", String, String)
                },
                {
                    "string? $a; $a  " + operator + " true;",
                    struct("true", StringNullable, Bool)
                },
                {
                    "string? $a; $a  " + operator + " 1;",
                    struct("1", StringNullable, Int)
                },
                {
                    "string? $a; $a  " + operator + " 5.6;",
                    struct("5.6", StringNullable, Float)
                },
                {
                    "string? $a; $a  " + operator + " 'hello';",
                    struct("'hello'", StringNullable, String)
                },
                {
                    "const string a = 'hello'; string? $a; $a  " + operator + " a;",
                    structTwo("a#", StringNullable, String)
                },
                {
                    "string? $a; $a  " + operator + " \"yellow\";",
                    struct("\"yellow\"", StringNullable, String)
                },
                {
                    "string? $a; $a  " + operator + " null;",
                    struct("null", StringNullable, Null)
                },
                {
                    "bool? $b; string? $a; $a  " + operator + " $b;",
                    structTwo("$b", StringNullable, BoolNullable)
                },
                {
                    "int? $b; string? $a; $a  " + operator + " $b;",
                    structTwo("$b", StringNullable, IntNullable)
                },
                {
                    "float? $b; string? $a; $a  " + operator + " $b;",
                    structTwo("$b", StringNullable, FloatNullable)
                },
                {
                    "string? $b; string? $a; $a  " + operator + " $b;",
                    structTwo("$b", StringNullable, StringNullable)
                },
                {
                    "array $a; $a  " + operator + " [0];",
                    struct("array", Array, Array)
                },
                {
                    "array $a; $a  " + operator + " array(1,2);",
                    struct("array", Array, Array)
                },
                {
                    "array $a; $a  " + operator + " null;",
                    struct("null", Array, Null)
                },
                {
                    "resource $b; resource $a; $a  " + operator + " $b;",
                    structTwo("$b", Resource, Resource)
                },
                {
                    "resource $a; $a  " + operator + " null;",
                    struct("null", Resource, Null)
                },
                {
                    "object $a; $a  " + operator + " true;",
                    struct("true", Object, Bool)
                },
                {
                    "object $a; $a  " + operator + " false;",
                    struct("false", Object, Bool)
                },
                {
                    "object $a; $a  " + operator + " 1;",
                    struct("1", Object, Int)
                },
                {
                    "object $a; $a  " + operator + " 1.0;",
                    struct("1.0", Object, Float)
                },
                {
                    "object $a; $a  " + operator + " 'hello';",
                    struct("'hello'", Object, String)
                },
                {
                    "object $a; $a  " + operator + " [1,2];",
                    struct("array", Object, Array)
                },
                {
                    "bool? $b; object $a; $a  " + operator + " $b;",
                    structTwo("$b", Object, BoolNullable)
                },
                {
                    "int? $b; object $a; $a  " + operator + " $b;",
                    structTwo("$b", Object, IntNullable)
                },
                {
                    "float? $b; object $a; $a  " + operator + " $b;",
                    structTwo("$b", Object, FloatNullable)
                },
                {
                    "string? $b; object $a; $a  " + operator + " $b;",
                    structTwo("$b", Object, StringNullable)
                },
                {
                    "resource $b; object $a; $a  " + operator + " $b;",
                    structTwo("$b", Object, Resource)
                },
                {
                    "object $b; object $a; $a  " + operator + " $b;",
                    structTwo("$b", Object, Object)
                },
                {
                    "object $a; $a  " + operator + " null;",
                    struct("null", Object, Null)
                },
                {
                    "ErrorException $a; $a  " + operator + " null;",
                    struct("null", ErrorException, Null)
                },
                {
                    "ErrorException $b; object $a; $a  " + operator + " $b;",
                    structTwo("$b", Object, ErrorException)
                },
                {
                    "Exception $b; object $a; $a  " + operator + " $b;",
                    structTwo("$b", Object, Exception)
                },
                {
                    "Exception $a; $a  " + operator + " null;",
                    struct("null", Exception, Null)
                }
            }));
        }
        collection.addAll(Arrays.asList(new Object[][]{
            {
                "ErrorException $b; ErrorException $a; $a  " + operator + " $b;",
                structTwo("$b", ErrorException, ErrorException)
            },
            {
                "ErrorException $b; Exception $a; $a  " + operator + " $b;",
                structTwo("$b", Exception, ErrorException)
            },
            {
                "Exception $b; Exception $a; $a  " + operator + " $b;",
                structTwo("$b", Exception, Exception)
            }
        }));

        return collection;
    }

    private static TypeCheckStruct[] struct(String rightHandSide, EBuiltInType leftType, EBuiltInType rightType) {
        return new TypeCheckStruct[]{
            AOperatorTypeCheckTest.struct(operator, Bool, 1, 1, 0),
            AOperatorTypeCheckTest.struct("$a", leftType, 1, 1, 0, 0),
            AOperatorTypeCheckTest.struct(rightHandSide, rightType, 1, 1, 0, 1)
        };

    }

    private static TypeCheckStruct[] structTwo(String rightHandSide, EBuiltInType leftType, EBuiltInType rightType) {
        return new TypeCheckStruct[]{
            AOperatorTypeCheckTest.struct(operator, Bool, 1, 2, 0),
            AOperatorTypeCheckTest.struct("$a", leftType, 1, 2, 0, 0),
            AOperatorTypeCheckTest.struct(rightHandSide, rightType, 1, 2, 0, 1)
        };

    }

    public static Collection<Object[]> getIdentityErrorTestStrings(String operator, boolean isDeclaration) {
        List<Object[]> collection = new ArrayList<>();
        ReferenceErrorDto[] errorDto = new ReferenceErrorDto[]{new ReferenceErrorDto(operator, 2, 1)};

        String $a = isDeclaration ? "$a\n " + operator : "$a; $a\n " + operator;

        String[] types = new String[]{"array", "resource", "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{type + " $b;bool " + $a + "  $b;", errorDto});
        }

        types = new String[]{"int", "float", "string", "array", "resource", "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{type + " $b;bool? " + $a + "  $b;", errorDto});
        }

        types = new String[]{"bool?", "array", "resource", "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{type + " $b;int " + $a + "  $b;", errorDto});
        }

        types = new String[]{"float", "string", "array", "resource", "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{type + " $b;int? " + $a + "  $b;", errorDto});
        }

        types = new String[]{"bool?", "int?", "array", "resource", "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{type + " $b;float " + $a + "  $b;", errorDto});
        }

        types = new String[]{"string", "array", "resource", "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{type + " $b;float? " + $a + "  $b;", errorDto});
        }

        types = new String[]{"bool?", "int?", "float?", "array", "resource", "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{type + " $b;string " + $a + "  $b;", errorDto});
        }

        types = new String[]{"array", "resource", "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{type + " $b;string? " + $a + "  $b;", errorDto});
        }

        types = new String[]{"bool", "bool?", "int", "int?", "float", "float?", "string", "string?",
            "resource", "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{type + " $b;array " + $a + "  $b;", errorDto});
        }

        types = new String[]{"bool", "bool?", "int", "int?", "float", "float?", "string", "string?",
            "array", "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{type + " $b;resource " + $a + "  $b;", errorDto});
        }
        types = new String[]{"bool", "bool?", "int", "int?", "float", "float?", "string", "string?",
            "array", "resource"};
        for (String type : types) {
            collection.add(new Object[]{type + " $b;ErrorException " + $a + "  $b;", errorDto});
        }
        types = new String[]{"bool", "bool?", "int", "int?", "float", "float?", "string", "string?",
            "array", "resource"};
        for (String type : types) {
            collection.add(new Object[]{type + " $b;Exception " + $a + "  $b;", errorDto});
        }

        return collection;
    }
}
