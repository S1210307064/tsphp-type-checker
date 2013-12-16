package ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck;

import ch.tutteli.tsphp.typechecker.error.ReferenceErrorDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.Array;
import static ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.Bool;
import static ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.BoolNullable;
import static ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.ErrorException;
import static ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.Exception;
import static ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.Float;
import static ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.FloatNullable;
import static ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.Int;
import static ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.IntNullable;
import static ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.Null;
import static ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.Object;
import static ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.Resource;
import static ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.String;
import static ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.StringNullable;

public class AssignHelper
{

    private static Collection<Object[]> collection;
    private static boolean isDeclaration;

    public static void addAssignments(Collection<Object[]> theCollection, boolean isDeclaration) {
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
                        "bool? $b=true; bool? $a " + assign + " $b;",
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
                        "bool? $b=null; int? $a " + assign + " $b;",
                        structTwo("$b", IntNullable, BoolNullable)
                },
                {
                        "int? $b=3; int? $a " + assign + " $b;",
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
                        " bool? $b=false; float? $a " + assign + " $b;",
                        structTwo("$b", FloatNullable, BoolNullable)
                },
                {
                        "int? $b=5; float? $a " + assign + " $b;",
                        structTwo("$b", FloatNullable, IntNullable)
                },
                {
                        "float? $b=null; float? $a " + assign + " $b;",
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
                        "bool? $b=false; string? $a " + assign + " $b;",
                        structTwo("$b", StringNullable, BoolNullable)
                },
                {
                        "int? $b=1; string? $a " + assign + " $b;",
                        structTwo("$b", StringNullable, IntNullable)
                },
                {
                        "float? $b=1; string? $a " + assign + " $b;",
                        structTwo("$b", StringNullable, FloatNullable)
                },
                {
                        "string? $b=''; string? $a " + assign + " $b;",
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
                        "resource $b=null; resource $a " + assign + " $b;",
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
                        "bool? $b=null; object $a " + assign + " $b;",
                        structTwo("$b", Object, BoolNullable)
                },
                {
                        "int? $b=null; object $a " + assign + " $b;",
                        structTwo("$b", Object, IntNullable)
                },
                {
                        "float? $b=1; object $a " + assign + " $b;",
                        structTwo("$b", Object, FloatNullable)
                },
                {
                        "string? $b=1; object $a " + assign + " $b;",
                        structTwo("$b", Object, StringNullable)
                },
                {
                        "resource $b=null; object $a " + assign + " $b;",
                        structTwo("$b", Object, Resource)
                },
                {
                        "object $b=3; object $a " + assign + " $b;",
                        structTwo("$b", Object, Object)
                },
                {
                        "Exception $b=null; object $a " + assign + " $b;",
                        structTwo("$b", Object, Exception)
                },
                {
                        "ErrorException $b=null; object $a " + assign + " $b;",
                        structTwo("$b", Object, ErrorException)
                },
                {
                        "object $a " + assign + " null;",
                        struct("null", Object, Null)
                },
                {
                        "ErrorException $b=null; ErrorException $a " + assign + " $b;",
                        structTwo("$b", ErrorException, ErrorException)
                },
                {
                        "ErrorException $a " + assign + " null;",
                        struct("null", ErrorException, Null)
                },
                {
                        "ErrorException $b=null; Exception $a " + assign + " $b;",
                        structTwo("$b", Exception, ErrorException)
                },
                {
                        "Exception $b=null; Exception $a " + assign + " $b;",
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
                {"bool", Bool, "false"}
        };
        Object[][] castTypes = new Object[][]{
                {"bool?", BoolNullable, "false"},
                {"int", Int, "1"},
                {"int?", IntNullable, "null"},
                {"float", Float, "1.0"},
                {"float?", FloatNullable, "null"},
                {"string", String, "''"},
                {"string?", StringNullable, "null"},
                {"object", Object, "false"}
        };
        Object[][] castToBoolTypes = new Object[][]{
                {"array", Array, "null"},
                {"resource", Resource, "null"},
                {"Exception", Exception, "null"},
                {"ErrorException", ErrorException, "null"}
        };
        addVariations(noCastNeededTypes, castTypes, castToBoolTypes, "bool", Bool, Bool);

        noCastNeededTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"int", Int, "1"}
        };
        castTypes = new Object[][]{
                {"bool?", BoolNullable, "null"},
                {"int?", IntNullable, "null"},
                {"float", Float, "0.0"},
                {"float?", FloatNullable, "null"},
                {"string", String, "''"},
                {"string?", StringNullable, "null"},
                {"object", Object, "null"}
        };
        addVariations(noCastNeededTypes, castTypes, castToBoolTypes, "int", Int, Bool);

        noCastNeededTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"int", Int, "0"},
                {"float", Float, "0.0"}
        };
        castTypes = new Object[][]{
                {"bool?", BoolNullable, "null"},
                {"int?", IntNullable, "null"},
                {"float?", FloatNullable, "null"},
                {"string", String, "''"},
                {"string?", StringNullable, "null"},
                {"object", Object, "null"}
        };
        addVariations(noCastNeededTypes, castTypes, castToBoolTypes, "float", Float, Bool);

        noCastNeededTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"int", Int, "0"},
                {"float", Float, "0.0"},
                {"string", String, "''"}
        };
        castTypes = new Object[][]{
                {"bool?", BoolNullable, "null"},
                {"int?", IntNullable, "null"},
                {"float?", FloatNullable, "null"},
                {"string?", StringNullable, "null"},
                {"object", Object, "null"}
        };
        addVariations(noCastNeededTypes, castTypes, castToBoolTypes, "string", String, Bool);

        noCastNeededTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"bool?", BoolNullable, "null"}
        };
        castTypes = new Object[][]{
                {"int", Int, "0"},
                {"int?", IntNullable, "null"},
                {"float", Float, "0.0"},
                {"float?", FloatNullable, "null"},
                {"string", String, "''"},
                {"string?", StringNullable, "null"},
                {"object", Object, "null"}
        };
        addVariations(noCastNeededTypes, castTypes, castToBoolTypes, "bool?", BoolNullable, BoolNullable);

        noCastNeededTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"bool?", BoolNullable, "null"},
                {"int", Int, "0"},
                {"int?", IntNullable, "null"}
        };
        castTypes = new Object[][]{
                {"float", Float, "0.0"},
                {"float?", FloatNullable, "null"},
                {"string", String, "''"},
                {"string?", StringNullable, "null"},
                {"object", Object, "null"}
        };

        addVariations(noCastNeededTypes, castTypes, castToBoolTypes, "int?", IntNullable, BoolNullable);

        noCastNeededTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"int", Int, "1"},
                {"float", Float, "0.0"},
                {"bool?", BoolNullable, "null"},
                {"int?", IntNullable, "null"},
                {"float?", FloatNullable, "null"}
        };
        castTypes = new Object[][]{
                {"string", String, "''"},
                {"string?", StringNullable, "null"},
                {"object", Object, "null"}
        };
        addVariations(noCastNeededTypes, castTypes, castToBoolTypes, "float?", FloatNullable, BoolNullable);

        noCastNeededTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"int", Int, "1"},
                {"float", Float, "0.0"},
                {"string", String, "''"},
                {"bool?", BoolNullable, "null"},
                {"int?", IntNullable, "null"},
                {"float?", FloatNullable, "null"},
                {"string?", StringNullable, "null"},};
        castTypes = new Object[][]{
                {"object", Object, "null"}
        };
        addVariations(noCastNeededTypes, castTypes, castToBoolTypes, "string?", StringNullable, BoolNullable);


        noCastNeededTypes = new Object[][]{
                {"array", Array, "[]"}
        };
        castTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"int", Int, "1"},
                {"float", Float, "0.0"},
                {"string", String, "''"},
                {"bool?", BoolNullable, "null"},
                {"int?", IntNullable, "null"},
                {"float?", FloatNullable, "null"},
                {"string?", StringNullable, "null"},
                {"object", Object, "null"},
                {"resource", Resource, "null"},
                {"Exception", Exception, "null"},
                {"ErrorException", ErrorException, "null"}
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
                    type2[0] + " $b = " + type2[2] + "; cast " + typeName + " $a " + assign + " $b;",
                    structTwo("$b", type, (EBuiltInType) type2[1])
            });
            collection.add(new Object[]{
                    type2[0] + " $b = " + type2[2] + "; " + typeName + " $a " + castAssign + "  $b;",
                    structCast(typeNameWithoutNullable, type, (EBuiltInType) type2[1])
            });
        }

        for (Object[] type2 : castTypes) {
            collection.add(new Object[]{
                    type2[0] + " $b = " + type2[2] + "; cast " + typeName + " $a " + assign + " $b;",
                    structCast(typeNameWithoutNullable, type, (EBuiltInType) type2[1])
            });
            collection.add(new Object[]{
                    type2[0] + " $b = " + type2[2] + "; " + typeName + " $a " + castAssign + "  $b;",
                    structCast(typeNameWithoutNullable, type, (EBuiltInType) type2[1])
            });
        }

        for (Object[] type2 : castToBoolTypes) {
            collection.add(new Object[]{
                    type2[0] + " $b = " + type2[2] + "; cast " + typeName + " $a " + assign + " $b;",
                    structCastBool(type, boolType, (EBuiltInType) type2[1])
            });
            collection.add(new Object[]{
                    type2[0] + " $b = " + type2[2] + "; " + typeName + " $a " + castAssign + "  $b;",
                    structCastBool(type, boolType, (EBuiltInType) type2[1])
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

    private static Object structCastBool(EBuiltInType leftType,
            EBuiltInType boolType, EBuiltInType initType) {

        return isDeclaration
                ? new TypeCheckStruct[]{
                AOperatorTypeCheckTest.struct("casting", boolType, 1, 1, 1, 0),
                AOperatorTypeCheckTest.struct("bool", boolType, 1, 1, 1, 0, 0, 1),
                AOperatorTypeCheckTest.struct("$b", initType, 1, 1, 1, 0, 1)
        }
                : new TypeCheckStruct[]{
                AOperatorTypeCheckTest.struct("=", boolType, 1, 2, 0),
                AOperatorTypeCheckTest.struct("$a", leftType, 1, 2, 0, 0),
                AOperatorTypeCheckTest.struct("casting", boolType, 1, 2, 0, 1),
                AOperatorTypeCheckTest.struct("bool", boolType, 1, 2, 0, 1, 0, 1),
                AOperatorTypeCheckTest.struct("$b", initType, 1, 2, 0, 1, 1)
        };
    }

    public static Collection<Object[]> getAssignmentErrorTestStrings(boolean isDeclaration) {
        collection = new ArrayList<>();
        ReferenceErrorDto[] errorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("=", 2, 1)};

        String $a = isDeclaration ? "\n $a " + "=" : "$a; $a\n " + "=";

        addErrorStringToCollection($a, errorDto, "bool", new String[][]{
                {"bool?", "null"},
                {"int", "0"},
                {"int?", "null"},
                {"float", "0.0"},
                {"float?", "null"},
                {"string", "''"},
                {"string?", "null"},
                {"array", "null"},
                {"resource", "null"},
                {"object", "null"},
                {"Exception", "null"},
                {"ErrorException", "null"}
        });

        addErrorStringToCollection($a, errorDto, "bool?", new String[][]{
                {"int", "0"},
                {"int?", "null"},
                {"float", "0.0"},
                {"float?", "null"},
                {"string", "''"},
                {"string?", "null"},
                {"array", "null"},
                {"resource", "null"},
                {"object", "null"},
                {"Exception", "null"},
                {"ErrorException", "null"}
        });

        addErrorStringToCollection($a, errorDto, "int", new String[][]{
                {"bool?", "null"},
                {"int?", "null"},
                {"float", "0.0"},
                {"float?", "null"},
                {"string", "''"},
                {"string?", "null"},
                {"array", "null"},
                {"resource", "null"},
                {"object", "null"},
                {"Exception", "null"},
                {"ErrorException", "null"}
        });

        addErrorStringToCollection($a, errorDto, "int?", new String[][]{
                {"float", "0.0"},
                {"float?", "null"},
                {"string", "''"},
                {"string?", "null"},
                {"array", "null"},
                {"resource", "null"},
                {"object", "null"},
                {"Exception", "null"},
                {"ErrorException", "null"}
        });

        addErrorStringToCollection($a, errorDto, "float", new String[][]{
                {"bool?", "null"},
                {"int?", "null"},
                {"float?", "null"},
                {"string", "''"},
                {"string?", "null"},
                {"array", "null"},
                {"resource", "null"},
                {"object", "null"},
                {"Exception", "null"},
                {"ErrorException", "null"}
        });

        addErrorStringToCollection($a, errorDto, "float?", new String[][]{
                {"string", "''"},
                {"string?", "null"},
                {"array", "null"},
                {"resource", "null"},
                {"object", "null"},
                {"Exception", "null"},
                {"ErrorException", "null"}
        });

        addErrorStringToCollection($a, errorDto, "string", new String[][]{
                {"bool?", "null"},
                {"int?", "null"},
                {"float?", "null"},
                {"string?", "null"},
                {"array", "null"},
                {"resource", "null"},
                {"object", "null"},
                {"Exception", "null"},
                {"ErrorException", "null"}
        });

        addErrorStringToCollection($a, errorDto, "string?", new String[][]{
                {"array", "null"},
                {"resource", "null"},
                {"object", "null"},
                {"Exception", "null"},
                {"ErrorException", "null"}
        });

        addErrorStringToCollection($a, errorDto, "array", new String[][]{
                {"bool", "false"},
                {"bool?", "null"},
                {"int", "0"},
                {"int?", "null"},
                {"float", "0.0"},
                {"float?", "null"},
                {"string", "''"},
                {"string?", "null"},
                {"resource", "null"},
                {"object", "null"},
                {"Exception", "null"},
                {"ErrorException", "null"}
        });

        addErrorStringToCollection($a, errorDto, "resource", new String[][]{
                {"bool", "false"},
                {"bool?", "null"},
                {"int", "0"},
                {"int?", "null"},
                {"float", "0.0"},
                {"float?", "null"},
                {"string", "''"},
                {"string?", "null"},
                {"array", "null"},
                {"object", "null"},
                {"Exception", "null"},
                {"ErrorException", "null"}
        });

        addErrorStringToCollection($a, errorDto, "ErrorException", new String[][]{
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
                {"object", "null"},
                {"Exception", "null"},
        });

        addErrorStringToCollection($a, errorDto, "Exception", new String[][]{
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
                {"object", "null"},
        });

        return collection;
    }

    private static void addErrorStringToCollection(String $a, ReferenceErrorDto[] errorDto,
            String variableType, String[][] types) {
        for (String[] type : types) {
            collection.add(new Object[]{
                    type[0] + " $b=" + type[1] + "; " + variableType + " " + $a + " $b;", errorDto
            });
        }
    }
}
