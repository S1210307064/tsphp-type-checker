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
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Array;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Bool;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.BoolNullable;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.ErrorException;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Exception;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Float;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.FloatNullable;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Int;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.IntNullable;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Object;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Resource;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.String;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.StringNullable;
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
public class VariableInitTest extends AOperatorTypeCheckTest
{

    private static List<Object[]> collection;

    public VariableInitTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        collection = new ArrayList<>();

        addSimpleAssignment();

        addCastingAssignment();

        return collection;
    }

    private static void addSimpleAssignment() {

        collection.addAll(Arrays.asList(new Object[][]{
            {
                "bool $a = false;",
                new TypeCheckStruct[]{struct("false", Bool, 1, 0, 1, 0)}
            },
            {
                "bool $a = true;",
                new TypeCheckStruct[]{struct("true", Bool, 1, 0, 1, 0)}
            },
            {
                "bool? $a = false;",
                new TypeCheckStruct[]{struct("false", Bool, 1, 0, 1, 0)}
            },
            {
                "bool? $a = true;",
                new TypeCheckStruct[]{struct("true", Bool, 1, 0, 1, 0)}
            },
            {
                "bool? $a = null;",
                new TypeCheckStruct[]{struct("null", Null, 1, 0, 1, 0)}
            },
            {
                "bool? $b; bool? $a = $b;",
                new TypeCheckStruct[]{struct("$b", BoolNullable, 1, 1, 1, 0)}
            },
            {
                "int $a = true;",
                new TypeCheckStruct[]{struct("true", Bool, 1, 0, 1, 0)}
            },
            {
                "int $a = 1;",
                new TypeCheckStruct[]{struct("1", Int, 1, 0, 1, 0)}
            },
            {
                "int? $a = true;",
                new TypeCheckStruct[]{struct("true", Bool, 1, 0, 1, 0)}
            },
            {
                "int? $a = 1;",
                new TypeCheckStruct[]{struct("1", Int, 1, 0, 1, 0)}
            },
            {
                "int? $a = null;",
                new TypeCheckStruct[]{struct("null", Null, 1, 0, 1, 0)}
            },
            {
                "bool? $b; int? $a = $b;",
                new TypeCheckStruct[]{struct("$b", BoolNullable, 1, 1, 1, 0)}
            },
            {
                "int? $b; int? $a = $b;",
                new TypeCheckStruct[]{struct("$b", IntNullable, 1, 1, 1, 0)}
            },
            {
                "float $a = true;",
                new TypeCheckStruct[]{struct("true", Bool, 1, 0, 1, 0)}
            },
            {
                "float $a = 6;",
                new TypeCheckStruct[]{struct("6", Int, 1, 0, 1, 0)}
            },
            {
                "float $a = 2.56;",
                new TypeCheckStruct[]{struct("2.56", Float, 1, 0, 1, 0)}
            },
            {
                "float? $a = true;",
                new TypeCheckStruct[]{struct("true", Bool, 1, 0, 1, 0)}
            },
            {
                "float? $a = 6;",
                new TypeCheckStruct[]{struct("6", Int, 1, 0, 1, 0)}
            },
            {
                "float? $a = 2.56;",
                new TypeCheckStruct[]{struct("2.56", Float, 1, 0, 1, 0)}
            },
            {
                "float? $a = null;",
                new TypeCheckStruct[]{struct("null", Null, 1, 0, 1, 0)}
            },
            {
                " bool? $b; float? $a = $b;",
                new TypeCheckStruct[]{struct("$b", BoolNullable, 1, 1, 1, 0)}
            },
            {
                "int? $b; float? $a = $b;",
                new TypeCheckStruct[]{struct("$b", IntNullable, 1, 1, 1, 0)}
            },
            {
                "float? $b; float? $a = $b;",
                new TypeCheckStruct[]{struct("$b", FloatNullable, 1, 1, 1, 0)}
            },
            {
                "string $a = true;",
                new TypeCheckStruct[]{struct("true", Bool, 1, 0, 1, 0)}
            },
            {
                "string $a = 1;",
                new TypeCheckStruct[]{struct("1", Int, 1, 0, 1, 0)}
            },
            {
                "string $a = 5.6;",
                new TypeCheckStruct[]{struct("5.6", Float, 1, 0, 1, 0)}
            },
            {
                "string $a = 'hello';",
                new TypeCheckStruct[]{struct("'hello'", String, 1, 0, 1, 0)}
            },
            {
                "string $a = \"yellow\";",
                new TypeCheckStruct[]{struct("\"yellow\"", String, 1, 0, 1, 0)}
            },
            {
                "string? $a = true;",
                new TypeCheckStruct[]{struct("true", Bool, 1, 0, 1, 0)}
            },
            {
                "string? $a = 1;",
                new TypeCheckStruct[]{struct("1", Int, 1, 0, 1, 0)}
            },
            {
                "string? $a = 5.6;",
                new TypeCheckStruct[]{struct("5.6", Float, 1, 0, 1, 0)}
            },
            {
                "string? $a = 'hello';",
                new TypeCheckStruct[]{struct("'hello'", String, 1, 0, 1, 0)}
            },
            {
                "string? $a = \"yellow\";",
                new TypeCheckStruct[]{struct("\"yellow\"", String, 1, 0, 1, 0)}
            },
            {
                "string? $a = null;",
                new TypeCheckStruct[]{struct("null", Null, 1, 0, 1, 0)}
            },
            {
                "bool? $b; string? $a = $b;",
                new TypeCheckStruct[]{struct("$b", BoolNullable, 1, 1, 1, 0)}
            },
            {
                "int? $b; string? $a = $b;",
                new TypeCheckStruct[]{struct("$b", IntNullable, 1, 1, 1, 0)}
            },
            {
                "float? $b; string? $a = $b;",
                new TypeCheckStruct[]{struct("$b", FloatNullable, 1, 1, 1, 0)}
            },
            {
                "string? $b; string? $a = $b;",
                new TypeCheckStruct[]{struct("$b", StringNullable, 1, 1, 1, 0)}
            },
            {
                "array $a = [0];",
                new TypeCheckStruct[]{struct("array", Array, 1, 0, 1, 0)}
            },
            {
                "array $a = array(1,2);",
                new TypeCheckStruct[]{struct("array", Array, 1, 0, 1, 0)}
            },
            {
                "array $a = null;",
                new TypeCheckStruct[]{struct("null", Null, 1, 0, 1, 0)}
            },
            {
                "resource $b; resource $a = $b;",
                new TypeCheckStruct[]{struct("$b", Resource, 1, 1, 1, 0)}
            },
            {
                "resource $a = null;",
                new TypeCheckStruct[]{struct("null", Null, 1, 0, 1, 0)}
            },
            {
                "object $a = true;",
                new TypeCheckStruct[]{struct("true", Bool, 1, 0, 1, 0)}
            },
            {
                "object $a = false;",
                new TypeCheckStruct[]{struct("false", Bool, 1, 0, 1, 0)}
            },
            {
                "object $a = 1;",
                new TypeCheckStruct[]{struct("1", Int, 1, 0, 1, 0)}
            },
            {
                "object $a = 1.0;",
                new TypeCheckStruct[]{struct("1.0", Float, 1, 0, 1, 0)}
            },
            {
                "object $a = 'hello';",
                new TypeCheckStruct[]{struct("'hello'", String, 1, 0, 1, 0)}
            },
            {
                "object $a = [1,2];",
                new TypeCheckStruct[]{struct("array", Array, 1, 0, 1, 0)}
            },
            {
                "bool? $b; object $a = $b;",
                new TypeCheckStruct[]{struct("$b", BoolNullable, 1, 1, 1, 0)}
            },
            {
                "int? $b; object $a = $b;",
                new TypeCheckStruct[]{struct("$b", IntNullable, 1, 1, 1, 0)}
            },
            {
                "float? $b; object $a = $b;",
                new TypeCheckStruct[]{struct("$b", FloatNullable, 1, 1, 1, 0)}
            },
            {
                "string? $b; object $a = $b;",
                new TypeCheckStruct[]{struct("$b", StringNullable, 1, 1, 1, 0)}
            },
            {
                "resource $b; object $a = $b;",
                new TypeCheckStruct[]{struct("$b", Resource, 1, 1, 1, 0)}
            },
            {
                "object $b; object $a = $b;",
                new TypeCheckStruct[]{struct("$b", Object, 1, 1, 1, 0)}
            },
            {
                "Exception $b; object $a = $b;",
                new TypeCheckStruct[]{struct("$b", Exception, 1, 1, 1, 0)}
            },
            {
                "ErrorException $b; object $a = $b;",
                new TypeCheckStruct[]{struct("$b", ErrorException, 1, 1, 1, 0)}
            },
            {
                "object $a = null;",
                new TypeCheckStruct[]{struct("null", Null, 1, 0, 1, 0)}
            },
            {
                "ErrorException $b; ErrorException $a = $b;",
                new TypeCheckStruct[]{struct("$b", ErrorException, 1, 1, 1, 0)}
            },
            {
                "ErrorException $a = null;",
                new TypeCheckStruct[]{struct("null", Null, 1, 0, 1, 0)}
            },
            {
                "ErrorException $b; Exception $a = $b;",
                new TypeCheckStruct[]{struct("$b", ErrorException, 1, 1, 1, 0)}
            },
            {
                "Exception $b; Exception $a = $b;",
                new TypeCheckStruct[]{struct("$b", Exception, 1, 1, 1, 0)}
            },
            {
                "Exception $a = null;",
                new TypeCheckStruct[]{struct("null", Null, 1, 0, 1, 0)}
            }
        }));
    }

    private static void addCastingAssignment() {

        Object[][] noCastNeededTypes = new Object[][]{
            {"bool", Bool}
        };
        Object[][] castTypes = new Object[][]{
            {"bool?", BoolNullable},
            {"int", Int},
            {"int?", IntNullable},
            {"float", Float},
            {"float?", FloatNullable},
            {"string", String},
            {"string?", StringNullable},
            {"object", Object}
        };
        Object[][] castToBoolTypes = new Object[][]{
            {"array", Array},
            {"resource", Resource},
            {"Exception", Exception},
            {"ErrorException", ErrorException}
        };
        addVariations(noCastNeededTypes, castTypes, castToBoolTypes, "bool", Bool, Bool);

        noCastNeededTypes = new Object[][]{
            {"bool", Bool},
            {"int", Int}
        };
        castTypes = new Object[][]{
            {"bool?", BoolNullable},
            {"int?", IntNullable},
            {"float", Float},
            {"float?", FloatNullable},
            {"string", String},
            {"string?", StringNullable},
            {"object", Object}
        };
        addVariations(noCastNeededTypes, castTypes, castToBoolTypes, "int", Int, Bool);

        noCastNeededTypes = new Object[][]{
            {"bool", Bool},
            {"int", Int},
            {"float", Float}
        };
        castTypes = new Object[][]{
            {"bool?", BoolNullable},
            {"int?", IntNullable},
            {"float?", FloatNullable},
            {"string", String},
            {"string?", StringNullable},
            {"object", Object}
        };
        addVariations(noCastNeededTypes, castTypes, castToBoolTypes, "float", Float, Bool);

        noCastNeededTypes = new Object[][]{
            {"bool", Bool},
            {"int", Int},
            {"float", Float},
            {"string", String}
        };
        castTypes = new Object[][]{
            {"bool?", BoolNullable},
            {"int?", IntNullable},
            {"float?", FloatNullable},
            {"string?", StringNullable},
            {"object", Object}
        };
        addVariations(noCastNeededTypes, castTypes, castToBoolTypes, "string", String, Bool);

        noCastNeededTypes = new Object[][]{
            {"bool", Bool},
            {"bool?", BoolNullable}
        };
        castTypes = new Object[][]{
            {"int", Int},
            {"int?", IntNullable},
            {"float", Float},
            {"float?", FloatNullable},
            {"string", String},
            {"string?", StringNullable},
            {"object", Object}
        };
        addVariations(noCastNeededTypes, castTypes, castToBoolTypes, "bool?", BoolNullable, BoolNullable);

        noCastNeededTypes = new Object[][]{
            {"bool", Bool},
            {"bool?", BoolNullable},
            {"int", Int},
            {"int?", IntNullable}
        };
        castTypes = new Object[][]{
            {"float", Float},
            {"float?", FloatNullable},
            {"string", String},
            {"string?", StringNullable},
            {"object", Object}
        };

        addVariations(noCastNeededTypes, castTypes, castToBoolTypes, "int?", IntNullable, BoolNullable);

        noCastNeededTypes = new Object[][]{
            {"bool", Bool},
            {"int", Int},
            {"float", Float},
            {"bool?", BoolNullable},
            {"int?", IntNullable},
            {"float?", FloatNullable}
        };
        castTypes = new Object[][]{
            {"string", String},
            {"string?", StringNullable},
            {"object", Object}
        };
        addVariations(noCastNeededTypes, castTypes, castToBoolTypes, "float?", FloatNullable, BoolNullable);

        noCastNeededTypes = new Object[][]{
            {"bool", Bool},
            {"int", Int},
            {"float", Float},
            {"string", String},
            {"bool?", BoolNullable},
            {"int?", IntNullable},
            {"float?", FloatNullable},
            {"string?", StringNullable},};
        castTypes = new Object[][]{
            {"object", Object}
        };
        addVariations(noCastNeededTypes, castTypes, castToBoolTypes, "string?", StringNullable, BoolNullable);


        noCastNeededTypes = new Object[][]{
            {"array", Array}
        };
        castTypes = new Object[][]{
            {"bool", Bool},
            {"int", Int},
            {"float", Float},
            {"string", String},
            {"bool?", BoolNullable},
            {"int?", IntNullable},
            {"float?", FloatNullable},
            {"string?", StringNullable},
            {"object", Object},
            {"resource", Resource},
            {"Exception", Exception},
            {"ErrorException", ErrorException}
        };
        castToBoolTypes = new Object[][]{};
        addVariations(noCastNeededTypes, castTypes, castToBoolTypes, "array", Array, BoolNullable);

    }

    private static void addVariations(Object[][] noCastTypes, Object[][] castTypes, Object[][] castToBoolTypes,
            String typeName, EBuiltInType type, EBuiltInType boolType) {

        String typeNameWithoutNullable = typeName;
        if (typeName.endsWith("?")) {
            typeNameWithoutNullable = typeName.substring(0, typeName.length() - 1);
        }

        for (Object[] type2 : noCastTypes) {
            collection.add(new Object[]{
                type2[0] + " $b; " + typeName + " $a =()  $b;",
                new TypeCheckStruct[]{
                    struct("casting", type, 1, 1, 1, 0),
                    struct(typeNameWithoutNullable, type, 1, 1, 1, 0, 0, 1),
                    struct("$b", (EBuiltInType) type2[1], 1, 1, 1, 0, 1)
                }
            });
            collection.add(new Object[]{
                type2[0] + " $b; cast " + typeName + " $a =  $b;",
                new TypeCheckStruct[]{
                    struct("$b", (EBuiltInType) type2[1], 1, 1, 1, 0)
                }
            });
        }

        for (Object[] type2 : castTypes) {
            collection.add(new Object[]{
                type2[0] + " $b; cast " + typeName + " $a =  $b;",
                new TypeCheckStruct[]{
                    struct("casting", type, 1, 1, 1, 0),
                    struct(typeNameWithoutNullable, type, 1, 1, 1, 0, 0, 1),
                    struct("$b", (EBuiltInType) type2[1], 1, 1, 1, 0, 1)
                }
            });
            collection.add(new Object[]{
                type2[0] + " $b; " + typeName + " $a =()  $b;",
                new TypeCheckStruct[]{
                    struct("casting", type, 1, 1, 1, 0),
                    struct(typeNameWithoutNullable, type, 1, 1, 1, 0, 0, 1),
                    struct("$b", (EBuiltInType) type2[1], 1, 1, 1, 0, 1)
                }
            });
        }

        for (Object[] type2 : castToBoolTypes) {
            collection.add(new Object[]{
                type2[0] + " $b; cast " + typeName + " $a =  $b;",
                new TypeCheckStruct[]{
                    struct("casting", boolType, 1, 1, 1, 0),
                    struct("bool", boolType, 1, 1, 1, 0, 0, 1),
                    struct("$b", (EBuiltInType) type2[1], 1, 1, 1, 0, 1)
                }
            });
            collection.add(new Object[]{
                type2[0] + " $b; " + typeName + " $a =()  $b;",
                new TypeCheckStruct[]{
                    struct("casting", type, 1, 1, 1, 0),
                    struct(typeNameWithoutNullable, type, 1, 1, 1, 0, 0, 1),
                    struct("$b", (EBuiltInType) type2[1], 1, 1, 1, 0, 1)
                }
            });
        }

    }
}
