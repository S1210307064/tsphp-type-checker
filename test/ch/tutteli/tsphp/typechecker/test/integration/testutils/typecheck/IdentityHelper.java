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
package ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck;

import ch.tutteli.tsphp.typechecker.error.ReferenceErrorDto;

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
                    struct("false", AOperatorTypeCheckTest.Bool, AOperatorTypeCheckTest.Bool)
                },
                {
                    "const bool a = true; bool $a; $a " + operator + " a;",
                    structTwo("a#", AOperatorTypeCheckTest.Bool, AOperatorTypeCheckTest.Bool)
                },
                {
                    "bool $a; $a " + operator + " true;",
                    struct("true", AOperatorTypeCheckTest.Bool, AOperatorTypeCheckTest.Bool)
                },
                {
                    "bool? $a; $a " + operator + " false;",
                    struct("false", AOperatorTypeCheckTest.BoolNullable, AOperatorTypeCheckTest.Bool)
                },
                {
                    "const bool a = true; bool? $a; $a " + operator + " a;",
                    structTwo("a#", AOperatorTypeCheckTest.BoolNullable, AOperatorTypeCheckTest.Bool)
                },
                {
                    "bool? $a; $a  " + operator + " true;",
                    struct("true", AOperatorTypeCheckTest.BoolNullable, AOperatorTypeCheckTest.Bool)
                },
                {
                    "bool?$a; $a  " + operator + " null;",
                    struct("null", AOperatorTypeCheckTest.BoolNullable, AOperatorTypeCheckTest.Null)
                },
                {
                    "bool? $b; bool? $a; $a  " + operator + " $b;",
                    structTwo("$b", AOperatorTypeCheckTest.BoolNullable, AOperatorTypeCheckTest.BoolNullable)
                },
                {
                    "int $a; $a  " + operator + " true;",
                    struct("true", AOperatorTypeCheckTest.Int, AOperatorTypeCheckTest.Bool)
                },
                {
                    "int $a; $a  " + operator + " 1;",
                    struct("1", AOperatorTypeCheckTest.Int, AOperatorTypeCheckTest.Int)
                },
                {
                    "const int a = 1; int $a; $a  " + operator + " a;",
                    structTwo("a#", AOperatorTypeCheckTest.Int, AOperatorTypeCheckTest.Int)
                },
                {
                    "int? $a; $a  " + operator + " true;",
                    struct("true", AOperatorTypeCheckTest.IntNullable, AOperatorTypeCheckTest.Bool)
                },
                {
                    "int? $a; $a  " + operator + " 1;",
                    struct("1", AOperatorTypeCheckTest.IntNullable, AOperatorTypeCheckTest.Int)
                },
                {
                    "const int a = 1; int? $a; $a  " + operator + " a;",
                    structTwo("a#", AOperatorTypeCheckTest.IntNullable, AOperatorTypeCheckTest.Int)
                },
                {
                    "int? $a; $a  " + operator + " null;",
                    struct("null", AOperatorTypeCheckTest.IntNullable, AOperatorTypeCheckTest.Null)
                },
                {
                    "bool? $b; int? $a; $a  " + operator + " $b;",
                    structTwo("$b", AOperatorTypeCheckTest.IntNullable, AOperatorTypeCheckTest.BoolNullable)
                },
                {
                    "int? $b; int? $a; $a  " + operator + " $b;",
                    structTwo("$b", AOperatorTypeCheckTest.IntNullable, AOperatorTypeCheckTest.IntNullable)
                },
                {
                    "float $a; $a  " + operator + " true;",
                    struct("true", AOperatorTypeCheckTest.Float, AOperatorTypeCheckTest.Bool)
                },
                {
                    "float $a; $a  " + operator + " 6;",
                    struct("6", AOperatorTypeCheckTest.Float, AOperatorTypeCheckTest.Int)
                },
                {
                    "float $a; $a  " + operator + " 2.56;",
                    struct("2.56", AOperatorTypeCheckTest.Float, AOperatorTypeCheckTest.Float)
                },
                {
                    "const float a = 1; float $a; $a  " + operator + " a;",
                    structTwo("a#", AOperatorTypeCheckTest.Float, AOperatorTypeCheckTest.Float)
                },
                {
                    "float? $a; $a  " + operator + " true;",
                    struct("true", AOperatorTypeCheckTest.FloatNullable, AOperatorTypeCheckTest.Bool)
                },
                {
                    "float? $a; $a  " + operator + " 6;",
                    struct("6", AOperatorTypeCheckTest.FloatNullable, AOperatorTypeCheckTest.Int)
                },
                {
                    "float? $a; $a  " + operator + " 2.56;",
                    struct("2.56", AOperatorTypeCheckTest.FloatNullable, AOperatorTypeCheckTest.Float)
                },
                {
                    "const float a = 2.12; float? $a; $a  " + operator + " a;",
                    structTwo("a#", AOperatorTypeCheckTest.FloatNullable, AOperatorTypeCheckTest.Float)
                },
                {
                    "float? $a; $a  " + operator + " null;",
                    struct("null", AOperatorTypeCheckTest.FloatNullable, AOperatorTypeCheckTest.Null)
                },
                {
                    " bool? $b; float? $a; $a  " + operator + " $b;",
                    structTwo("$b", AOperatorTypeCheckTest.FloatNullable, AOperatorTypeCheckTest.BoolNullable)
                },
                {
                    "int? $b; float? $a; $a  " + operator + " $b;",
                    structTwo("$b", AOperatorTypeCheckTest.FloatNullable, AOperatorTypeCheckTest.IntNullable)
                },
                {
                    "float? $b; float? $a; $a  " + operator + " $b;",
                    structTwo("$b", AOperatorTypeCheckTest.FloatNullable, AOperatorTypeCheckTest.FloatNullable)
                },
                {
                    "string $a; $a  " + operator + " true;",
                    struct("true", AOperatorTypeCheckTest.String, AOperatorTypeCheckTest.Bool)
                },
                {
                    "string $a; $a  " + operator + " 1;",
                    struct("1", AOperatorTypeCheckTest.String, AOperatorTypeCheckTest.Int)
                },
                {
                    "string $a; $a  " + operator + " 5.6;",
                    struct("5.6", AOperatorTypeCheckTest.String, AOperatorTypeCheckTest.Float)
                },
                {
                    "string $a; $a  " + operator + " 'hello';",
                    struct("'hello'", AOperatorTypeCheckTest.String, AOperatorTypeCheckTest.String)
                },
                {
                    "const string a = 'hello'; string $a; $a  " + operator + " a;",
                    structTwo("a#", AOperatorTypeCheckTest.String, AOperatorTypeCheckTest.String)
                },
                {
                    "string $a; $a  " + operator + " \"yellow\";",
                    struct("\"yellow\"", AOperatorTypeCheckTest.String, AOperatorTypeCheckTest.String)
                },
                {
                    "string? $a; $a  " + operator + " true;",
                    struct("true", AOperatorTypeCheckTest.StringNullable, AOperatorTypeCheckTest.Bool)
                },
                {
                    "string? $a; $a  " + operator + " 1;",
                    struct("1", AOperatorTypeCheckTest.StringNullable, AOperatorTypeCheckTest.Int)
                },
                {
                    "string? $a; $a  " + operator + " 5.6;",
                    struct("5.6", AOperatorTypeCheckTest.StringNullable, AOperatorTypeCheckTest.Float)
                },
                {
                    "string? $a; $a  " + operator + " 'hello';",
                    struct("'hello'", AOperatorTypeCheckTest.StringNullable, AOperatorTypeCheckTest.String)
                },
                {
                    "const string a = 'hello'; string? $a; $a  " + operator + " a;",
                    structTwo("a#", AOperatorTypeCheckTest.StringNullable, AOperatorTypeCheckTest.String)
                },
                {
                    "string? $a; $a  " + operator + " \"yellow\";",
                    struct("\"yellow\"", AOperatorTypeCheckTest.StringNullable, AOperatorTypeCheckTest.String)
                },
                {
                    "string? $a; $a  " + operator + " null;",
                    struct("null", AOperatorTypeCheckTest.StringNullable, AOperatorTypeCheckTest.Null)
                },
                {
                    "bool? $b; string? $a; $a  " + operator + " $b;",
                    structTwo("$b", AOperatorTypeCheckTest.StringNullable, AOperatorTypeCheckTest.BoolNullable)
                },
                {
                    "int? $b; string? $a; $a  " + operator + " $b;",
                    structTwo("$b", AOperatorTypeCheckTest.StringNullable, AOperatorTypeCheckTest.IntNullable)
                },
                {
                    "float? $b; string? $a; $a  " + operator + " $b;",
                    structTwo("$b", AOperatorTypeCheckTest.StringNullable, AOperatorTypeCheckTest.FloatNullable)
                },
                {
                    "string? $b; string? $a; $a  " + operator + " $b;",
                    structTwo("$b", AOperatorTypeCheckTest.StringNullable, AOperatorTypeCheckTest.StringNullable)
                },
                {
                    "array $a; $a  " + operator + " [0];",
                    struct("array", AOperatorTypeCheckTest.Array, AOperatorTypeCheckTest.Array)
                },
                {
                    "array $a; $a  " + operator + " array(1,2);",
                    struct("array", AOperatorTypeCheckTest.Array, AOperatorTypeCheckTest.Array)
                },
                {
                    "array $a; $a  " + operator + " null;",
                    struct("null", AOperatorTypeCheckTest.Array, AOperatorTypeCheckTest.Null)
                },
                {
                    "resource $b; resource $a; $a  " + operator + " $b;",
                    structTwo("$b", AOperatorTypeCheckTest.Resource, AOperatorTypeCheckTest.Resource)
                },
                {
                    "resource $a; $a  " + operator + " null;",
                    struct("null", AOperatorTypeCheckTest.Resource, AOperatorTypeCheckTest.Null)
                },
                {
                    "object $a; $a  " + operator + " true;",
                    struct("true", AOperatorTypeCheckTest.Object, AOperatorTypeCheckTest.Bool)
                },
                {
                    "object $a; $a  " + operator + " false;",
                    struct("false", AOperatorTypeCheckTest.Object, AOperatorTypeCheckTest.Bool)
                },
                {
                    "object $a; $a  " + operator + " 1;",
                    struct("1", AOperatorTypeCheckTest.Object, AOperatorTypeCheckTest.Int)
                },
                {
                    "object $a; $a  " + operator + " 1.0;",
                    struct("1.0", AOperatorTypeCheckTest.Object, AOperatorTypeCheckTest.Float)
                },
                {
                    "object $a; $a  " + operator + " 'hello';",
                    struct("'hello'", AOperatorTypeCheckTest.Object, AOperatorTypeCheckTest.String)
                },
                {
                    "object $a; $a  " + operator + " [1,2];",
                    struct("array", AOperatorTypeCheckTest.Object, AOperatorTypeCheckTest.Array)
                },
                {
                    "bool? $b; object $a; $a  " + operator + " $b;",
                    structTwo("$b", AOperatorTypeCheckTest.Object, AOperatorTypeCheckTest.BoolNullable)
                },
                {
                    "int? $b; object $a; $a  " + operator + " $b;",
                    structTwo("$b", AOperatorTypeCheckTest.Object, AOperatorTypeCheckTest.IntNullable)
                },
                {
                    "float? $b; object $a; $a  " + operator + " $b;",
                    structTwo("$b", AOperatorTypeCheckTest.Object, AOperatorTypeCheckTest.FloatNullable)
                },
                {
                    "string? $b; object $a; $a  " + operator + " $b;",
                    structTwo("$b", AOperatorTypeCheckTest.Object, AOperatorTypeCheckTest.StringNullable)
                },
                {
                    "resource $b; object $a; $a  " + operator + " $b;",
                    structTwo("$b", AOperatorTypeCheckTest.Object, AOperatorTypeCheckTest.Resource)
                },
                {
                    "object $b; object $a; $a  " + operator + " $b;",
                    structTwo("$b", AOperatorTypeCheckTest.Object, AOperatorTypeCheckTest.Object)
                },
                {
                    "object $a; $a  " + operator + " null;",
                    struct("null", AOperatorTypeCheckTest.Object, AOperatorTypeCheckTest.Null)
                },
                {
                    "ErrorException $a; $a  " + operator + " null;",
                    struct("null", AOperatorTypeCheckTest.ErrorException, AOperatorTypeCheckTest.Null)
                },
                {
                    "ErrorException $b; object $a; $a  " + operator + " $b;",
                    structTwo("$b", AOperatorTypeCheckTest.Object, AOperatorTypeCheckTest.ErrorException)
                },
                {
                    "Exception $b; object $a; $a  " + operator + " $b;",
                    structTwo("$b", AOperatorTypeCheckTest.Object, AOperatorTypeCheckTest.Exception)
                },
                {
                    "Exception $a; $a  " + operator + " null;",
                    struct("null", AOperatorTypeCheckTest.Exception, AOperatorTypeCheckTest.Null)
                }
            }));
        }
        collection.addAll(Arrays.asList(new Object[][]{
            {
                "ErrorException $b; ErrorException $a; $a  " + operator + " $b;",
                structTwo("$b", AOperatorTypeCheckTest.ErrorException, AOperatorTypeCheckTest.ErrorException)
            },
            {
                "ErrorException $b; Exception $a; $a  " + operator + " $b;",
                structTwo("$b", AOperatorTypeCheckTest.Exception, AOperatorTypeCheckTest.ErrorException)
            },
            {
                "Exception $b; Exception $a; $a  " + operator + " $b;",
                structTwo("$b", AOperatorTypeCheckTest.Exception, AOperatorTypeCheckTest.Exception)
            }
        }));

        return collection;
    }

    private static TypeCheckStruct[] struct(String rightHandSide, EBuiltInType leftType, EBuiltInType rightType) {
        return new TypeCheckStruct[]{
            AOperatorTypeCheckTest.struct(operator, AOperatorTypeCheckTest.Bool, 1, 1, 0),
            AOperatorTypeCheckTest.struct("$a", leftType, 1, 1, 0, 0),
            AOperatorTypeCheckTest.struct(rightHandSide, rightType, 1, 1, 0, 1)
        };

    }

    private static TypeCheckStruct[] structTwo(String rightHandSide, EBuiltInType leftType, EBuiltInType rightType) {
        return new TypeCheckStruct[]{
            AOperatorTypeCheckTest.struct(operator, AOperatorTypeCheckTest.Bool, 1, 2, 0),
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
