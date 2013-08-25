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
public class AssignHelper
{

    private static Collection<Object[]> collection;
    private static boolean isDeclaration;

    public static void getAssignments(Collection<Object[]> theCollection, boolean isDeclaration) {
        collection = theCollection;
        AssignHelper.isDeclaration = isDeclaration;

        addSimpleAssignment();

        addCastingAssignment();
    }

    private static void addSimpleAssignment() {
        String assign = isDeclaration ? " = " : "; $a = ";
        collection.addAll(Arrays.asList(new Object[][]{
            {
                "bool $a " + assign + " false;",
                struct("false", Bool, Bool)
            },
            {
                "const bool a = true; bool $a " + assign + " a;",
                structTwo("a#", Bool, Bool)
            },
            {
                "bool $a " + assign + " true;",
                struct("true", Bool, Bool)
            },
            {
                "bool? $a " + assign + " false;",
                struct("false", BoolNullable, Bool)
            },
            {
                "const bool a = true; bool? $a " + assign + " a;",
                structTwo("a#", BoolNullable, Bool)
            },
            {
                "bool? $a " + assign + " true;",
                struct("true", BoolNullable, Bool)
            },
            {
                "bool? $a " + assign + " null;",
                struct("null", BoolNullable, Null)
            },
            {
                "bool? $b; bool? $a " + assign + " $b;",
                structTwo("$b", BoolNullable, BoolNullable)
            },
            {
                "int $a " + assign + " true;",
                struct("true", Int, Bool)
            },
            {
                "int $a " + assign + " 1;",
                struct("1", Int, Int)
            },
            {
                "const int a = 1; int $a " + assign + " a;",
                structTwo("a#", Int, Int)
            },
            {
                "int? $a " + assign + " true;",
                struct("true", IntNullable, Bool)
            },
            {
                "int? $a " + assign + " 1;",
                struct("1", IntNullable, Int)
            },
            {
                "const int a = 1; int? $a " + assign + " a;",
                structTwo("a#", IntNullable, Int)
            },
            {
                "int? $a " + assign + " null;",
                struct("null", IntNullable, Null)
            },
            {
                "bool? $b; int? $a " + assign + " $b;",
                structTwo("$b", IntNullable, BoolNullable)
            },
            {
                "int? $b; int? $a " + assign + " $b;",
                structTwo("$b", IntNullable, IntNullable)
            },
            {
                "float $a " + assign + " true;",
                struct("true", Float, Bool)
            },
            {
                "float $a " + assign + " 6;",
                struct("6", Float, Int)
            },
            {
                "float $a " + assign + " 2.56;",
                struct("2.56", Float, Float)
            },
            {
                "const float a = 1; float $a " + assign + " a;",
                structTwo("a#", Float, Float)
            },
            {
                "float? $a " + assign + " true;",
                struct("true", FloatNullable, Bool)
            },
            {
                "float? $a " + assign + " 6;",
                struct("6", FloatNullable, Int)
            },
            {
                "float? $a " + assign + " 2.56;",
                struct("2.56", FloatNullable, Float)
            },
            {
                "const float a = 2.12; float? $a " + assign + " a;",
                structTwo("a#", FloatNullable, Float)
            },
            {
                "float? $a " + assign + " null;",
                struct("null", FloatNullable, Null)
            },
            {
                " bool? $b; float? $a " + assign + " $b;",
                structTwo("$b", FloatNullable, BoolNullable)
            },
            {
                "int? $b; float? $a " + assign + " $b;",
                structTwo("$b", FloatNullable, IntNullable)
            },
            {
                "float? $b; float? $a " + assign + " $b;",
                structTwo("$b", FloatNullable, FloatNullable)
            },
            {
                "string $a " + assign + " true;",
                struct("true", String, Bool)
            },
            {
                "string $a " + assign + " 1;",
                struct("1", String, Int)
            },
            {
                "string $a " + assign + " 5.6;",
                struct("5.6", String, Float)
            },
            {
                "string $a " + assign + " 'hello';",
                struct("'hello'", String, String)
            },
            {
                "const string a = 'hello'; string $a " + assign + " a;",
                structTwo("a#", String, String)
            },
            {
                "string $a " + assign + " \"yellow\";",
                struct("\"yellow\"", String, String)
            },
            {
                "string? $a " + assign + " true;",
                struct("true", StringNullable, Bool)
            },
            {
                "string? $a " + assign + " 1;",
                struct("1", StringNullable, Int)
            },
            {
                "string? $a " + assign + " 5.6;",
                struct("5.6", StringNullable, Float)
            },
            {
                "string? $a " + assign + " 'hello';",
                struct("'hello'", StringNullable, String)
            },
            {
                "const string a = 'hello'; string? $a " + assign + " a;",
                structTwo("a#", StringNullable, String)
            },
            {
                "string? $a " + assign + " \"yellow\";",
                struct("\"yellow\"", StringNullable, String)
            },
            {
                "string? $a " + assign + " null;",
                struct("null", StringNullable, Null)
            },
            {
                "bool? $b; string? $a " + assign + " $b;",
                structTwo("$b", StringNullable, BoolNullable)
            },
            {
                "int? $b; string? $a " + assign + " $b;",
                structTwo("$b", StringNullable, IntNullable)
            },
            {
                "float? $b; string? $a " + assign + " $b;",
                structTwo("$b", StringNullable, FloatNullable)
            },
            {
                "string? $b; string? $a " + assign + " $b;",
                structTwo("$b", StringNullable, StringNullable)
            },
            {
                "array $a " + assign + " [0];",
                struct("array", Array, Array)
            },
            {
                "array $a " + assign + " array(1,2);",
                struct("array", Array, Array)
            },
            {
                "array $a " + assign + " null;",
                struct("null", Array, Null)
            },
            {
                "resource $b; resource $a " + assign + " $b;",
                structTwo("$b", Resource, Resource)
            },
            {
                "resource $a " + assign + " null;",
                struct("null", Resource, Null)
            },
            {
                "object $a " + assign + " true;",
                struct("true", Object, Bool)
            },
            {
                "object $a " + assign + " false;",
                struct("false", Object, Bool)
            },
            {
                "object $a " + assign + " 1;",
                struct("1", Object, Int)
            },
            {
                "object $a " + assign + " 1.0;",
                struct("1.0", Object, Float)
            },
            {
                "object $a " + assign + " 'hello';",
                struct("'hello'", Object, String)
            },
            {
                "object $a " + assign + " [1,2];",
                struct("array", Object, Array)
            },
            {
                "bool? $b; object $a " + assign + " $b;",
                structTwo("$b", Object, BoolNullable)
            },
            {
                "int? $b; object $a " + assign + " $b;",
                structTwo("$b", Object, IntNullable)
            },
            {
                "float? $b; object $a " + assign + " $b;",
                structTwo("$b", Object, FloatNullable)
            },
            {
                "string? $b; object $a " + assign + " $b;",
                structTwo("$b", Object, StringNullable)
            },
            {
                "resource $b; object $a " + assign + " $b;",
                structTwo("$b", Object, Resource)
            },
            {
                "object $b; object $a " + assign + " $b;",
                structTwo("$b", Object, Object)
            },
            {
                "Exception $b; object $a " + assign + " $b;",
                structTwo("$b", Object, Exception)
            },
            {
                "ErrorException $b; object $a " + assign + " $b;",
                structTwo("$b", Object, ErrorException)
            },
            {
                "object $a " + assign + " null;",
                struct("null", Object, Null)
            },
            {
                "ErrorException $b; ErrorException $a " + assign + " $b;",
                structTwo("$b", ErrorException, ErrorException)
            },
            {
                "ErrorException $a " + assign + " null;",
                struct("null", ErrorException, Null)
            },
            {
                "ErrorException $b; Exception $a " + assign + " $b;",
                structTwo("$b", Exception, ErrorException)
            },
            {
                "Exception $b; Exception $a " + assign + " $b;",
                structTwo("$b", Exception, Exception)
            },
            {
                "Exception $a " + assign + " null;",
                struct("null", Exception, Null)
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

        String assign = isDeclaration ? " = " : "; $a = ";
        String castAssign = isDeclaration ? " =() " : "; $a =() ";

        for (Object[] type2 : noCastTypes) {
            collection.add(new Object[]{
                type2[0] + " $b; cast " + typeName + " $a " + assign + " $b;",
                structTwo("$b", type, (EBuiltInType) type2[1])
            });
            collection.add(new Object[]{
                type2[0] + " $b; " + typeName + " $a " + castAssign + "  $b;",
                structCast(typeNameWithoutNullable, type, (EBuiltInType) type2[1])
            });
        }

        for (Object[] type2 : castTypes) {
            collection.add(new Object[]{
                type2[0] + " $b; cast " + typeName + " $a " + assign + " $b;",
                structCast(typeNameWithoutNullable, type, (EBuiltInType) type2[1])
            });
            collection.add(new Object[]{
                type2[0] + " $b; " + typeName + " $a " + castAssign + "  $b;",
                structCast(typeNameWithoutNullable, type, (EBuiltInType) type2[1])
            });
        }

        for (Object[] type2 : castToBoolTypes) {
            collection.add(new Object[]{
                type2[0] + " $b; cast " + typeName + " $a " + assign + " $b;",
                structCastBool("bool", type, boolType, (EBuiltInType) type2[1])
            });
            collection.add(new Object[]{
                type2[0] + " $b; " + typeName + " $a " + castAssign + "  $b;",
                structCastBool("bool", type, boolType, (EBuiltInType) type2[1])
            });
        }
    }

    private static TypeCheckStruct[] struct(String initValue, EBuiltInType leftType, EBuiltInType initType) {
        return isDeclaration
                ? new TypeCheckStruct[]{
            AOperatorTypeCheckTest.struct("$a", leftType, 1, 0, 1),
            AOperatorTypeCheckTest.struct(initValue, initType, 1, 0, 1, 0)
        }
                : new TypeCheckStruct[]{
            AOperatorTypeCheckTest.struct("=", initType, 1, 1, 0),
            AOperatorTypeCheckTest.struct("$a", leftType, 1, 1, 0, 0),
            AOperatorTypeCheckTest.struct(initValue, initType, 1, 1, 0, 1),};

    }

    private static TypeCheckStruct[] structTwo(String initValue, EBuiltInType leftType, EBuiltInType initType) {
        return isDeclaration
                ? new TypeCheckStruct[]{
            AOperatorTypeCheckTest.struct("$a", leftType, 1, 1, 1),
            AOperatorTypeCheckTest.struct(initValue, initType, 1, 1, 1, 0)
        }
                : new TypeCheckStruct[]{
            AOperatorTypeCheckTest.struct("=", initType, 1, 2, 0),
            AOperatorTypeCheckTest.struct("$a", leftType, 1, 2, 0, 0),
            AOperatorTypeCheckTest.struct(initValue, initType, 1, 2, 0, 1)
        };
    }

    private static TypeCheckStruct[] structCast(String typeName, EBuiltInType leftType, EBuiltInType initType) {
        return isDeclaration
                ? new TypeCheckStruct[]{
            AOperatorTypeCheckTest.struct("casting", leftType, 1, 1, 1, 0),
            AOperatorTypeCheckTest.struct(typeName, leftType, 1, 1, 1, 0, 0, 1),
            AOperatorTypeCheckTest.struct("$b", initType, 1, 1, 1, 0, 1)
        }
                : new TypeCheckStruct[]{
            AOperatorTypeCheckTest.struct("=", leftType, 1, 2, 0),
            AOperatorTypeCheckTest.struct("$a", leftType, 1, 2, 0, 0),
            AOperatorTypeCheckTest.struct("casting", leftType, 1, 2, 0, 1),
            AOperatorTypeCheckTest.struct(typeName, leftType, 1, 2, 0, 1, 0, 1),
            AOperatorTypeCheckTest.struct("$b", initType, 1, 2, 0, 1, 1)
        };
    }

    private static Object structCastBool(String typeName, EBuiltInType leftType,
            EBuiltInType boolType, EBuiltInType initType) {

        return isDeclaration
                ? new TypeCheckStruct[]{
            AOperatorTypeCheckTest.struct("casting", boolType, 1, 1, 1, 0),
            AOperatorTypeCheckTest.struct(typeName, boolType, 1, 1, 1, 0, 0, 1),
            AOperatorTypeCheckTest.struct("$b", initType, 1, 1, 1, 0, 1)
        }
                : new TypeCheckStruct[]{
            AOperatorTypeCheckTest.struct("=", boolType, 1, 2, 0),
            AOperatorTypeCheckTest.struct("$a", leftType, 1, 2, 0, 0),
            AOperatorTypeCheckTest.struct("casting", boolType, 1, 2, 0, 1),
            AOperatorTypeCheckTest.struct(typeName, boolType, 1, 2, 0, 1, 0, 1),
            AOperatorTypeCheckTest.struct("$b", initType, 1, 2, 0, 1, 1)
        };
    }

    public static Collection<Object[]> getAssignmentErrorTestStrings(String operator, boolean isDeclaration) {
        collection = new ArrayList<>();
        ReferenceErrorDto[] errorDto = new ReferenceErrorDto[]{new ReferenceErrorDto(operator, 2, 1)};
        String[] types = new String[]{"bool?", "int", "int?", "float", "float?", "string", "string?", "array",
            "resource", "object", "Exception", "ErrorException"};
        String $a = isDeclaration ? "\n $a " + operator : "$a; $a\n " + operator;
        for (String type : types) {
            collection.add(new Object[]{type + " $b; bool " + $a + " $b;", errorDto});
        }
        types = new String[]{"int", "int?", "float", "float?", "string", "string?", "array", "resource",
            "object", "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{type + " $b;bool? " + $a + " $b;", errorDto});
        }
        types = new String[]{"bool?", "int?", "float", "float?", "string", "string?", "array", "resource",
            "object", "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{type + " $b;int " + $a + " $b;", errorDto});
        }
        types = new String[]{"float", "float?", "string", "string?", "array", "resource",
            "object", "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{type + " $b;int? " + $a + "  $b;", errorDto});
        }
        types = new String[]{"bool?", "int?", "float?", "string", "string?", "array", "resource",
            "object", "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{type + " $b;float " + $a + "  $b;", errorDto});
        }
        types = new String[]{"string", "string?", "array", "resource", "object", "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{type + " $b;float? " + $a + "  $b;", errorDto});
        }
        types = new String[]{"bool?", "int?", "float?", "string?", "array", "resource",
            "object", "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{type + " $b;string " + $a + "  $b;", errorDto});
        }
        types = new String[]{"array", "resource", "object", "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{type + " $b;string? " + $a + "  $b;", errorDto});
        }
        types = new String[]{"bool", "bool?", "int", "int?", "float", "float?", "string", "string?", "resource",
            "object", "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{type + " $b;array " + $a + "  $b;", errorDto});
        }
        types = new String[]{"bool", "bool?", "int", "int?", "float", "float?", "string", "string?", "array",
            "object", "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{type + " $b;resource " + $a + "  $b;", errorDto});
        }
        types = new String[]{"bool", "bool?", "int", "int?", "float", "float?", "string", "string?", "array",
            "resource", "object", "Exception"};
        for (String type : types) {
            collection.add(new Object[]{type + " $b;ErrorException " + $a + "  $b;", errorDto});
        }
        types = new String[]{"bool", "bool?", "int", "int?", "float", "float?", "string", "string?", "array",
            "resource", "object"};
        for (String type : types) {
            collection.add(new Object[]{type + " $b;Exception " + $a + "  $b;", errorDto});
        }
        return collection;
    }
}
