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

import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.Array;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.ArrayFalseable;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.Bool;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.BoolFalseable;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.BoolFalseableAndNullable;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.BoolNullable;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.ErrorException;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.ErrorExceptionFalseable;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.Exception;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.ExceptionFalseable;
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
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.ResourceFalseable;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.String;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.StringFalseable;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.StringFalseableAndNullable;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.StringNullable;

public class AssignHelper
{

    private static Collection<Object[]> collection;
    private static boolean isDeclaration;

    public static void addAssignments(Collection<Object[]> theCollection, boolean isItDeclaration) {
        collection = theCollection;
        isDeclaration = isItDeclaration;

        addSimpleAssignment();

        addCastingAssignment();
    }

    private static void addSimpleAssignment() {
        Object[][] types = new Object[][]{
                {"bool", "false", Bool, Bool},
                {"bool", "true", Bool, Bool},
                {"bool!", "false", BoolFalseable, Bool},
                {"bool!", "true", BoolFalseable, Bool},
                {"bool?", "false", BoolNullable, Bool},
                {"bool?", "true", BoolNullable, Bool},
                {"bool?", "null", BoolNullable, Null},
                {"bool!?", "false", BoolFalseableAndNullable, Bool},
                {"bool!?", "true", BoolFalseableAndNullable, Bool},
                {"bool!?", "null", BoolFalseableAndNullable, Null},
                {"int", "1", Int, Int},
                {"int!", "1", IntFalseable, Int},
                {"int!", "false", IntFalseable, Bool},
                {"int?", "1", IntNullable, Int},
                {"int?", "null", IntNullable, Null},
                {"int!?", "1", IntFalseableAndNullable, Int},
                {"int!?", "false", IntFalseableAndNullable, Bool},
                {"int!?", "null", IntFalseableAndNullable, Null},
                {"float", "1.3", Float, Float},
                {"float!", "1.3", FloatFalseable, Float},
                {"float!", "false", FloatFalseable, Bool},
                {"float?", "1.3", FloatNullable, Float},
                {"float?", "null", FloatNullable, Null},
                {"float!?", "1.3", FloatFalseableAndNullable, Float},
                {"float!?", "false", FloatFalseableAndNullable, Bool},
                {"float!?", "null", FloatFalseableAndNullable, Null},
                {"string", "'hello'", String, String},
                {"string", "\"hi\"", String, String},
                {"string!", "'hello'", StringFalseable, String},
                {"string!", "\"hi\"", StringFalseable, String},
                {"string!", "false", StringFalseable, Bool},
                {"string?", "'hello'", StringNullable, String},
                {"string?", "\"hi\"", StringNullable, String},
                {"string?", "null", StringNullable, Null},
                {"string!?", "'hello'", StringFalseableAndNullable, String},
                {"string!?", "\"hi\"", StringFalseableAndNullable, String},
                {"string!?", "false", StringFalseableAndNullable, Bool},
                {"string!?", "null", StringFalseableAndNullable, Null},
                //array is covered below
                {"resource", "null", Resource, Null},
                {"resource!", "null", ResourceFalseable, Null},
                {"resource!", "false", ResourceFalseable, Bool},
                {"mixed", "false", Mixed, Bool},
                {"mixed", "true", Mixed, Bool},
                {"mixed", "1", Mixed, Int},
                {"mixed", "1.0", Mixed, Float},
                {"mixed", "'hey'", Mixed, String},
                {"mixed", "\"hi\"", Mixed, String},
                {"mixed", "null", Mixed, Null},
                //mixed in conjunction with array is covered below
                //mixed in conjunction with falseable and nullable types is covered further below
        };

        String assign = isDeclaration ? " = " : "; $a = ";
        for (Object[] type : types) {
            collection.addAll(Arrays.asList(new Object[][]{
                    {
                            type[0] + " $a " + assign + type[1] + ";",
                            struct((String) type[1], (EBuiltInType) type[2], (EBuiltInType) type[3])
                    },
                    {
                            type[0] + " $b = " + type[1] + "; " + type[0] + "$a " + assign + " $b;",
                            structTwo("$b", (EBuiltInType) type[2], (EBuiltInType) type[2])
                    }
            }));
        }

        types = new Object[][]{
                {"array", "[1,2]", "array", Array, Array},
                {"array", "array(2,3,4)", "array", Array, Array},
                {"array", "null", "null", Array, Null},
                {"array!", "[1,2]", "array", ArrayFalseable, Array},
                {"array!", "array(2,3,4)", "array", ArrayFalseable, Array},
                {"array!", "null", "null", ArrayFalseable, Null},
                {"array!", "false", "false", ArrayFalseable, Bool},
                {"mixed", "[1,2]", "array", Mixed, Array},
                {"mixed", "array(3,4)", "array", Mixed, Array}
        };
        for (Object[] type : types) {
            collection.addAll(Arrays.asList(new Object[][]{
                    {
                            type[0] + " $a " + assign + type[1] + ";",
                            struct((String) type[2], (EBuiltInType) type[3], (EBuiltInType) type[4])
                    },
                    {
                            type[0] + " $b = " + type[1] + "; " + type[0] + "$a " + assign + " $b;",
                            structTwo("$b", (EBuiltInType) type[3], (EBuiltInType) type[3])
                    }
            }));
        }

        types = new Object[][]{
                {"bool", "false", Bool},
                {"bool!", "false", BoolFalseable},
                {"bool?", "null", BoolNullable},
                {"bool!?", "null", BoolFalseableAndNullable},
                {"int", "2", Int},
                {"int!", "false", IntFalseable},
                {"int?", "null", IntNullable},
                {"int!?", "null", IntFalseableAndNullable},
                {"float", "3.4", Float},
                {"float!", "false", FloatFalseable},
                {"float?", "null", FloatNullable},
                {"float!?", "null", FloatFalseableAndNullable},
                {"string", "'hi'", String},
                {"string!", "false", StringFalseable},
                {"string?", "null", StringNullable},
                {"string!?", "null", StringFalseableAndNullable},
                {"array!", "null", ArrayFalseable},
                {"resource", "null", Resource},
                {"resource!", "null", ResourceFalseable},
                {"Exception", "null", Exception},
                {"ErrorException", "null", ErrorException},
        };
        for (Object[] type : types) {
            collection.add(new Object[]{
                    type[0] + " $b= " + type[1] + "; mixed $a " + assign + " $b;",
                    structTwo("$b", Mixed, (EBuiltInType) type[2])
            });
        }


        collection.addAll(Arrays.asList(new Object[][]{
                        {"const bool a = true; bool $a " + assign + " a;", structTwo("a#", Bool, Bool)},
                        {"const bool a = true; bool! $a " + assign + " a;", structTwo("a#", BoolFalseable, Bool)},
                        {"const bool a = true; bool? $a " + assign + " a;", structTwo("a#", BoolNullable, Bool)},
                        {
                                "const bool a = true; bool!? $a " + assign + " a;",
                                structTwo("a#", BoolFalseableAndNullable, Bool)
                        },
                        {"const int a = 1; int $a " + assign + " a;", structTwo("a#", Int, Int)},
                        {"const int a = 1; int! $a " + assign + " a;", structTwo("a#", IntFalseable, Int)},
                        {"const int a = 1; int? $a " + assign + " a;", structTwo("a#", IntNullable, Int)},
                        {
                                "const int a = 1; int!? $a " + assign + " a;",
                                structTwo("a#", IntFalseableAndNullable, Int)
                        },
                        {"const float a = 1.2; float $a " + assign + " a;", structTwo("a#", Float, Float)},
                        {"const float a = 1.2; float! $a " + assign + " a;", structTwo("a#", FloatFalseable, Float)},
                        {"const float a = 1.2; float? $a " + assign + " a;", structTwo("a#", FloatNullable, Float)},
                        {
                                "const float a = 1.2; float!? $a " + assign + " a;",
                                structTwo("a#", FloatFalseableAndNullable, Float)
                        },
                        {"const string a = 'hi'; string $a " + assign + " a;", structTwo("a#", String, String)},
                        {
                                "const string a = 'hi'; string! $a " + assign + " a;",
                                structTwo("a#", StringFalseable, String)
                        },
                        {
                                "const string a = 'hi'; string? $a " + assign + " a;",
                                structTwo("a#", StringNullable, String)
                        },
                        {
                                "const string a = 'hi'; string!? $a " + assign + " a;",
                                structTwo("a#", StringFalseableAndNullable, String)
                        },
                        {"const string a = \"hey\"; string $a " + assign + " a;", structTwo("a#", String, String)},
                        {
                                "const string a = \"hey\"; string! $a " + assign + " a;",
                                structTwo("a#", StringFalseable, String)
                        },
                        {
                                "const string a = \"hey\"; string? $a " + assign + " a;",
                                structTwo("a#", StringNullable, String)
                        },
                        {
                                "const string a = \"hey\"; string!? $a " + assign + " a;",
                                structTwo("a#", StringFalseableAndNullable, String)
                        },
                        {
                                "ErrorException $b=null; ErrorException $a " + assign + " $b;",
                                structTwo("$b", ErrorException, ErrorException)
                        },
                        {"ErrorException $a " + assign + " null;", struct("null", ErrorException, Null)},
                        {
                                "ErrorException $b=null; Exception $a " + assign + " $b;",
                                structTwo("$b", Exception, ErrorException)
                        },
                        {"Exception $b=null; Exception $a " + assign + " $b;", structTwo("$b", Exception, Exception)},
                        {"Exception $a " + assign + " null;", struct("null", Exception, Null)}
                }

        ));
    }

    private static void addCastingAssignment() {

        Object[][] noCastNeededTypes = new Object[][]{
                {"bool", Bool, "false"}
        };
        Object[][] castTypes = new Object[][]{
                {"bool!", BoolFalseable, "false"},
                {"bool?", BoolNullable, "false"},
                {"bool!?", BoolFalseableAndNullable, "false"},
                {"int", Int, "1"},
                {"int!", IntFalseable, "false"},
                {"int?", IntNullable, "null"},
                {"int!?", IntFalseableAndNullable, "null"},
                {"float", Float, "1.0"},
                {"float!", FloatFalseable, "false"},
                {"float?", FloatNullable, "null"},
                {"float!?", FloatFalseableAndNullable, "null"},
                {"string", String, "''"},
                {"string!", StringFalseable, "false"},
                {"string?", StringNullable, "null"},
                {"string!?", StringFalseableAndNullable, "null"},
                {"array", Array, "null"},
                {"array!", ArrayFalseable, "null"},
                {"resource", Resource, "null"},
                {"resource!", ResourceFalseable, "null"},
                {"mixed", Mixed, "false"},
                {"Exception", Exception, "null"},
                {"Exception!", ExceptionFalseable, "null"},
                {"ErrorException", ErrorException, "null"},
                {"ErrorException!", ErrorExceptionFalseable, "null"}
        };
        addVariations(noCastNeededTypes, castTypes, "bool", Bool);

        noCastNeededTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"bool!", BoolFalseable, "false"}
        };
        castTypes = new Object[][]{
                {"bool?", BoolNullable, "null"},
                {"bool!?", BoolFalseableAndNullable, "false"},
                {"int", Int, "1"},
                {"int!", IntFalseable, "false"},
                {"int?", IntNullable, "null"},
                {"int!?", IntFalseableAndNullable, "null"},
                {"float", Float, "1.0"},
                {"float!", FloatFalseable, "false"},
                {"float?", FloatNullable, "null"},
                {"float!?", FloatFalseableAndNullable, "null"},
                {"string", String, "''"},
                {"string!", StringFalseable, "false"},
                {"string?", StringNullable, "null"},
                {"string!?", StringFalseableAndNullable, "null"},
                {"array", Array, "null"},
                {"array!", ArrayFalseable, "null"},
                {"resource", Resource, "null"},
                {"resource!", ResourceFalseable, "null"},
                {"mixed", Mixed, "false"},
                {"Exception", Exception, "null"},
                {"Exception!", ExceptionFalseable, "null"},
                {"ErrorException", ErrorException, "null"},
                {"ErrorException!", ErrorExceptionFalseable, "null"}
        };
        addVariations(noCastNeededTypes, castTypes, "bool!", BoolFalseable);

        noCastNeededTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"bool?", BoolNullable, "null"}
        };
        castTypes = new Object[][]{
                {"bool!", BoolFalseable, "false"},
                {"bool!?", BoolFalseableAndNullable, "false"},
                {"int", Int, "1"},
                {"int!", IntFalseable, "false"},
                {"int?", IntNullable, "null"},
                {"int!?", IntFalseableAndNullable, "null"},
                {"float", Float, "1.0"},
                {"float!", FloatFalseable, "false"},
                {"float?", FloatNullable, "null"},
                {"float!?", FloatFalseableAndNullable, "null"},
                {"string", String, "''"},
                {"string!", StringFalseable, "false"},
                {"string?", StringNullable, "null"},
                {"string!?", StringFalseableAndNullable, "null"},
                {"array", Array, "null"},
                {"array!", ArrayFalseable, "null"},
                {"resource", Resource, "null"},
                {"resource!", ResourceFalseable, "null"},
                {"mixed", Mixed, "false"},
                {"Exception", Exception, "null"},
                {"Exception!", ExceptionFalseable, "null"},
                {"ErrorException", ErrorException, "null"},
                {"ErrorException!", ErrorExceptionFalseable, "null"}
        };
        addVariations(noCastNeededTypes, castTypes, "bool?", BoolNullable);

        noCastNeededTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"bool!", BoolFalseable, "false"},
                {"bool?", BoolNullable, "null"},
                {"bool!?", BoolFalseableAndNullable, "false"},
        };
        castTypes = new Object[][]{
                {"int", Int, "1"},
                {"int!", IntFalseable, "false"},
                {"int?", IntNullable, "null"},
                {"int!?", IntFalseableAndNullable, "null"},
                {"float", Float, "1.0"},
                {"float!", FloatFalseable, "false"},
                {"float?", FloatNullable, "null"},
                {"float!?", FloatFalseableAndNullable, "null"},
                {"string", String, "''"},
                {"string!", StringFalseable, "false"},
                {"string?", StringNullable, "null"},
                {"string!?", StringFalseableAndNullable, "null"},
                {"array", Array, "null"},
                {"array!", ArrayFalseable, "null"},
                {"resource", Resource, "null"},
                {"resource!", ResourceFalseable, "null"},
                {"mixed", Mixed, "false"},
                {"Exception", Exception, "null"},
                {"Exception!", ExceptionFalseable, "null"},
                {"ErrorException", ErrorException, "null"},
                {"ErrorException!", ErrorExceptionFalseable, "null"}
        };
        addVariations(noCastNeededTypes, castTypes, "bool!?", BoolFalseableAndNullable);

        noCastNeededTypes = new Object[][]{
                {"int", Int, "1"}
        };
        castTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"bool!", BoolFalseable, "false"},
                {"bool?", BoolNullable, "false"},
                {"bool!?", BoolFalseableAndNullable, "false"},
                {"int!", IntFalseable, "false"},
                {"int?", IntNullable, "null"},
                {"int!?", IntFalseableAndNullable, "null"},
                {"float", Float, "1.0"},
                {"float!", FloatFalseable, "false"},
                {"float?", FloatNullable, "null"},
                {"float!?", FloatFalseableAndNullable, "null"},
                {"string", String, "''"},
                {"string!", StringFalseable, "false"},
                {"string?", StringNullable, "null"},
                {"string!?", StringFalseableAndNullable, "null"},
                {"mixed", Mixed, "null"}
        };
        addVariations(noCastNeededTypes, castTypes, "int", Int);

        noCastNeededTypes = new Object[][]{
                {"int", Int, "1"},
                {"int!", IntFalseable, "false"},
        };
        castTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"bool!", BoolFalseable, "false"},
                {"bool?", BoolNullable, "false"},
                {"bool!?", BoolFalseableAndNullable, "false"},
                {"int?", IntNullable, "null"},
                {"int!?", IntFalseableAndNullable, "null"},
                {"float", Float, "1.0"},
                {"float!", FloatFalseable, "false"},
                {"float?", FloatNullable, "null"},
                {"float!?", FloatFalseableAndNullable, "null"},
                {"string", String, "''"},
                {"string!", StringFalseable, "false"},
                {"string?", StringNullable, "null"},
                {"string!?", StringFalseableAndNullable, "null"},
                {"mixed", Mixed, "null"}
        };
        addVariations(noCastNeededTypes, castTypes, "int!", IntFalseable);

        noCastNeededTypes = new Object[][]{
                {"int", Int, "1"},
                {"int?", IntNullable, "null"},
        };
        castTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"bool!", BoolFalseable, "false"},
                {"bool?", BoolNullable, "false"},
                {"bool!?", BoolFalseableAndNullable, "false"},
                {"int!", IntFalseable, "false"},
                {"int!?", IntFalseableAndNullable, "null"},
                {"float", Float, "1.0"},
                {"float!", FloatFalseable, "false"},
                {"float?", FloatNullable, "null"},
                {"float!?", FloatFalseableAndNullable, "null"},
                {"string", String, "''"},
                {"string!", StringFalseable, "false"},
                {"string?", StringNullable, "null"},
                {"string!?", StringFalseableAndNullable, "null"},
                {"mixed", Mixed, "null"}
        };
        addVariations(noCastNeededTypes, castTypes, "int?", IntNullable);

        noCastNeededTypes = new Object[][]{
                {"int", Int, "1"},
                {"int!", IntFalseable, "false"},
                {"int?", IntNullable, "null"},
                {"int!?", IntFalseableAndNullable, "null"},
        };
        castTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"bool!", BoolFalseable, "false"},
                {"bool?", BoolNullable, "false"},
                {"bool!?", BoolFalseableAndNullable, "false"},
                {"float", Float, "1.0"},
                {"float!", FloatFalseable, "false"},
                {"float?", FloatNullable, "null"},
                {"float!?", FloatFalseableAndNullable, "null"},
                {"string", String, "''"},
                {"string!", StringFalseable, "false"},
                {"string?", StringNullable, "null"},
                {"string!?", StringFalseableAndNullable, "null"},
                {"mixed", Mixed, "null"}
        };
        addVariations(noCastNeededTypes, castTypes, "int!?", IntFalseableAndNullable);

        noCastNeededTypes = new Object[][]{
                {"float", Float, "1.0"},
        };
        castTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"bool!", BoolFalseable, "false"},
                {"bool?", BoolNullable, "false"},
                {"bool!?", BoolFalseableAndNullable, "false"},
                {"int", Int, "1"},
                {"int!", IntFalseable, "false"},
                {"int?", IntNullable, "null"},
                {"int!?", IntFalseableAndNullable, "null"},
                {"float!", FloatFalseable, "false"},
                {"float?", FloatNullable, "null"},
                {"float!?", FloatFalseableAndNullable, "null"},
                {"string", String, "''"},
                {"string!", StringFalseable, "false"},
                {"string?", StringNullable, "null"},
                {"string!?", StringFalseableAndNullable, "null"},
                {"mixed", Mixed, "null"}
        };
        addVariations(noCastNeededTypes, castTypes, "float", Float);

        noCastNeededTypes = new Object[][]{
                {"float", Float, "1.0"},
                {"float!", FloatFalseable, "false"},
        };
        castTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"bool!", BoolFalseable, "false"},
                {"bool?", BoolNullable, "false"},
                {"bool!?", BoolFalseableAndNullable, "false"},
                {"int", Int, "1"},
                {"int!", IntFalseable, "false"},
                {"int?", IntNullable, "null"},
                {"int!?", IntFalseableAndNullable, "null"},
                {"float?", FloatNullable, "null"},
                {"float!?", FloatFalseableAndNullable, "null"},
                {"string", String, "''"},
                {"string!", StringFalseable, "false"},
                {"string?", StringNullable, "null"},
                {"string!?", StringFalseableAndNullable, "null"},
                {"mixed", Mixed, "null"}
        };
        addVariations(noCastNeededTypes, castTypes, "float!", FloatFalseable);

        noCastNeededTypes = new Object[][]{
                {"float", Float, "1.0"},
                {"float?", FloatNullable, "null"},
        };
        castTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"bool!", BoolFalseable, "false"},
                {"bool?", BoolNullable, "false"},
                {"bool!?", BoolFalseableAndNullable, "false"},
                {"int", Int, "1"},
                {"int!", IntFalseable, "false"},
                {"int?", IntNullable, "null"},
                {"int!?", IntFalseableAndNullable, "null"},
                {"float!", FloatFalseable, "false"},
                {"float!?", FloatFalseableAndNullable, "null"},
                {"string", String, "''"},
                {"string!", StringFalseable, "false"},
                {"string?", StringNullable, "null"},
                {"string!?", StringFalseableAndNullable, "null"},
                {"mixed", Mixed, "null"}
        };
        addVariations(noCastNeededTypes, castTypes, "float?", FloatNullable);

        noCastNeededTypes = new Object[][]{
                {"float", Float, "1.0"},
                {"float!", FloatFalseable, "false"},
                {"float?", FloatNullable, "null"},
                {"float!?", FloatFalseableAndNullable, "null"},
        };
        castTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"bool!", BoolFalseable, "false"},
                {"bool?", BoolNullable, "false"},
                {"bool!?", BoolFalseableAndNullable, "false"},
                {"int", Int, "1"},
                {"int!", IntFalseable, "false"},
                {"int?", IntNullable, "null"},
                {"int!?", IntFalseableAndNullable, "null"},
                {"string", String, "''"},
                {"string!", StringFalseable, "false"},
                {"string?", StringNullable, "null"},
                {"string!?", StringFalseableAndNullable, "null"},
                {"mixed", Mixed, "null"}
        };
        addVariations(noCastNeededTypes, castTypes, "float!?", FloatFalseableAndNullable);

        noCastNeededTypes = new Object[][]{
                {"string", String, "''"},
        };
        castTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"bool!", BoolFalseable, "false"},
                {"bool?", BoolNullable, "false"},
                {"bool!?", BoolFalseableAndNullable, "false"},
                {"int", Int, "1"},
                {"int!", IntFalseable, "false"},
                {"int?", IntNullable, "null"},
                {"int!?", IntFalseableAndNullable, "null"},
                {"float", Float, "1.0"},
                {"float!", FloatFalseable, "false"},
                {"float?", FloatNullable, "null"},
                {"float!?", FloatFalseableAndNullable, "null"},
                {"string!", StringFalseable, "false"},
                {"string?", StringNullable, "null"},
                {"string!?", StringFalseableAndNullable, "null"},
                {"mixed", Mixed, "null"}
        };
        addVariations(noCastNeededTypes, castTypes, "string", String);

        noCastNeededTypes = new Object[][]{
                {"string", String, "''"},
                {"string!", StringFalseable, "false"},
        };
        castTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"bool!", BoolFalseable, "false"},
                {"bool?", BoolNullable, "false"},
                {"bool!?", BoolFalseableAndNullable, "false"},
                {"int", Int, "1"},
                {"int!", IntFalseable, "false"},
                {"int?", IntNullable, "null"},
                {"int!?", IntFalseableAndNullable, "null"},
                {"float", Float, "1.0"},
                {"float!", FloatFalseable, "false"},
                {"float?", FloatNullable, "null"},
                {"float!?", FloatFalseableAndNullable, "null"},
                {"string?", StringNullable, "null"},
                {"string!?", StringFalseableAndNullable, "null"},
                {"mixed", Mixed, "null"}
        };
        addVariations(noCastNeededTypes, castTypes, "string!", StringFalseable);

        noCastNeededTypes = new Object[][]{
                {"string", String, "''"},
                {"string?", StringNullable, "null"},
        };
        castTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"bool!", BoolFalseable, "false"},
                {"bool?", BoolNullable, "false"},
                {"bool!?", BoolFalseableAndNullable, "false"},
                {"int", Int, "1"},
                {"int!", IntFalseable, "false"},
                {"int?", IntNullable, "null"},
                {"int!?", IntFalseableAndNullable, "null"},
                {"float", Float, "1.0"},
                {"float!", FloatFalseable, "false"},
                {"float?", FloatNullable, "null"},
                {"float!?", FloatFalseableAndNullable, "null"},
                {"string!", StringFalseable, "false"},
                {"string!?", StringFalseableAndNullable, "null"},
                {"mixed", Mixed, "null"}
        };
        addVariations(noCastNeededTypes, castTypes, "string?", StringNullable);

        noCastNeededTypes = new Object[][]{
                {"string", String, "''"},
                {"string!", StringFalseable, "false"},
                {"string?", StringNullable, "null"},
                {"string!?", StringFalseableAndNullable, "null"},
        };
        castTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"bool!", BoolFalseable, "false"},
                {"bool?", BoolNullable, "false"},
                {"bool!?", BoolFalseableAndNullable, "false"},
                {"int", Int, "1"},
                {"int!", IntFalseable, "false"},
                {"int?", IntNullable, "null"},
                {"int!?", IntFalseableAndNullable, "null"},
                {"float", Float, "1.0"},
                {"float!", FloatFalseable, "false"},
                {"float?", FloatNullable, "null"},
                {"float!?", FloatFalseableAndNullable, "null"},
                {"mixed", Mixed, "null"}
        };
        addVariations(noCastNeededTypes, castTypes, "string!?", StringFalseableAndNullable);

        noCastNeededTypes = new Object[][]{
                {"array", Array, "[]"}
        };
        castTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"bool!", BoolFalseable, "false"},
                {"bool?", BoolNullable, "false"},
                {"bool!?", BoolFalseableAndNullable, "false"},
                {"int", Int, "1"},
                {"int!", IntFalseable, "false"},
                {"int?", IntNullable, "null"},
                {"int!?", IntFalseableAndNullable, "null"},
                {"float", Float, "1.0"},
                {"float!", FloatFalseable, "false"},
                {"float?", FloatNullable, "null"},
                {"float!?", FloatFalseableAndNullable, "null"},
                {"string", String, "''"},
                {"string!", StringFalseable, "false"},
                {"string?", StringNullable, "null"},
                {"string!?", StringFalseableAndNullable, "null"},
                {"array!", ArrayFalseable, "null"},
                {"resource", Resource, "null"},
                {"resource!", ResourceFalseable, "null"},
                {"mixed", Mixed, "false"},
                {"Exception", Exception, "null"},
                {"Exception!", ExceptionFalseable, "null"},
                {"ErrorException", ErrorException, "null"},
                {"ErrorException!", ErrorExceptionFalseable, "null"}
        };
        addVariations(noCastNeededTypes, castTypes, "array", Array);

        noCastNeededTypes = new Object[][]{
                {"array", Array, "[]"},
                {"array!", ArrayFalseable, "null"}
        };
        castTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"bool!", BoolFalseable, "false"},
                {"bool?", BoolNullable, "false"},
                {"bool!?", BoolFalseableAndNullable, "false"},
                {"int", Int, "1"},
                {"int!", IntFalseable, "false"},
                {"int?", IntNullable, "null"},
                {"int!?", IntFalseableAndNullable, "null"},
                {"float", Float, "1.0"},
                {"float!", FloatFalseable, "false"},
                {"float?", FloatNullable, "null"},
                {"float!?", FloatFalseableAndNullable, "null"},
                {"string", String, "''"},
                {"string!", StringFalseable, "false"},
                {"string?", StringNullable, "null"},
                {"string!?", StringFalseableAndNullable, "null"},
                {"resource", Resource, "null"},
                {"resource!", ResourceFalseable, "null"},
                {"mixed", Mixed, "false"},
                {"Exception", Exception, "null"},
                {"Exception!", ExceptionFalseable, "null"},
                {"ErrorException", ErrorException, "null"},
                {"ErrorException!", ErrorExceptionFalseable, "null"}
        };
        addVariations(noCastNeededTypes, castTypes, "array!", ArrayFalseable);

        noCastNeededTypes = new Object[][]{
                {"Exception", Exception, "null"},
                {"ErrorException", ErrorException, "null"},
        };
        castTypes = new Object[][]{
                {"mixed", Mixed, "false"},
                {"Exception!", ExceptionFalseable, "null"},
        };
        addVariations(noCastNeededTypes, castTypes, "Exception", Exception);
        addSubTypeFalseableCasting(
                "ErrorException", "null", ErrorExceptionFalseable, ErrorException, "Exception", Exception);


        noCastNeededTypes = new Object[][]{
                {"Exception", Exception, "null"},
                {"Exception!", ExceptionFalseable, "null"},
        };
        castTypes = new Object[][]{
                {"mixed", Mixed, "false"},
                {"ErrorException", ErrorException, "null"},
                //TODO rstoll TSPHP-291 Generics - if TSPHP has implemented a mechanism for covariant generics, then
                //this assignment would work if falseable types were implemented by such an internal class
                //{"ErrorException!", ErrorExceptionFalseable, "null"}
        };
        addVariations(noCastNeededTypes, castTypes, "Exception!", ExceptionFalseable);

        noCastNeededTypes = new Object[][]{
                {"ErrorException", ErrorException, "null"},
        };
        castTypes = new Object[][]{
                {"mixed", Mixed, "false"},
                {"Exception", Exception, "null"},
                //TODO rstoll TSPHP-291 Generics - if TSPHP has implemented a mechanism for covariant generics, then
                //this assignment would work if falseable types were implemented by such an internal class
                //{"Exception!", ExceptionFalseable, "null"},
                {"ErrorException!", ErrorExceptionFalseable, "null"},
        };
        addVariations(noCastNeededTypes, castTypes, "ErrorException", ErrorException);

        noCastNeededTypes = new Object[][]{
                {"ErrorException", ErrorException, "null"},
                {"ErrorException!", ErrorExceptionFalseable, "null"}
        };
        castTypes = new Object[][]{
                {"mixed", Mixed, "false"},
                //TODO rstoll TSPHP-291 Generics - if TSPHP has implemented a mechanism for covariant generics, then
                //this assignment would work if falseable types were implemented by such an internal class
                //{"Exception", Exception, "null"},
                //{"Exception!", ExceptionFalseable, "null"},
        };
        addVariations(noCastNeededTypes, castTypes, "ErrorException!", ErrorExceptionFalseable);
    }

    private static void addSubTypeFalseableCasting(String rightTypeNameWithoutFalseable, String initValue,
            EBuiltInType rightType, EBuiltInType castingType, String leftTypeName, EBuiltInType leftType) {

        String assign = isDeclaration ? " = " : "; $a = ";
        String castAssign = isDeclaration ? " =() " : "; $a =() ";

        collection.add(new Object[]{
                rightTypeNameWithoutFalseable + "! $b = " + initValue + "; "
                        + "cast " + leftTypeName + " $a " + assign + " $b;",
                structSubCast(rightTypeNameWithoutFalseable, leftType, rightType, castingType)
        });
        collection.add(new Object[]{
                rightTypeNameWithoutFalseable + "! $b = " + initValue + "; "
                        + "cast " + leftTypeName + " $a " + castAssign + " $b;",
                structSubCast(rightTypeNameWithoutFalseable, leftType, rightType, castingType)
        });
    }

    private static void addVariations(
            Object[][] noCastTypes, Object[][] castTypes, String typeName, EBuiltInType type) {

        String assign = isDeclaration ? " = " : "; $a = ";
        String castAssign = isDeclaration ? " =() " : "; $a =() ";

        for (Object[] type2 : noCastTypes) {
            collection.add(new Object[]{
                    type2[0] + " $b = " + type2[2] + "; cast " + typeName + " $a " + assign + " $b;",
                    structTwo("$b", type, (EBuiltInType) type2[1])
            });
            collection.add(new Object[]{
                    type2[0] + " $b = " + type2[2] + "; " + typeName + " $a " + castAssign + "  $b;",
                    structCast(typeName, type, (EBuiltInType) type2[1])
            });
        }

        for (Object[] type2 : castTypes) {
            collection.add(new Object[]{
                    type2[0] + " $b = " + type2[2] + "; cast " + typeName + " $a " + assign + " $b;",
                    structCast(typeName, type, (EBuiltInType) type2[1])
            });
            collection.add(new Object[]{
                    type2[0] + " $b = " + type2[2] + "; " + typeName + " $a " + castAssign + "  $b;",
                    structCast(typeName, type, (EBuiltInType) type2[1])
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

    private static TypeCheckStruct[] structSubCast(
            String typeName, EBuiltInType leftType, EBuiltInType rightType, EBuiltInType castingType) {
        return isDeclaration
                ? new TypeCheckStruct[]{
                AOperatorTypeCheckTest.struct("casting", castingType, 1, 1, 1, 0),
                AOperatorTypeCheckTest.struct(typeName, castingType, 1, 1, 1, 0, 0, 1),
                AOperatorTypeCheckTest.struct("$b", rightType, 1, 1, 1, 0, 1)
        }
                : new TypeCheckStruct[]{
                AOperatorTypeCheckTest.struct("=", castingType, 1, 2, 0),
                AOperatorTypeCheckTest.struct("$a", leftType, 1, 2, 0, 0),
                AOperatorTypeCheckTest.struct("casting", castingType, 1, 2, 0, 1),
                AOperatorTypeCheckTest.struct(typeName, castingType, 1, 2, 0, 1, 0, 1),
                AOperatorTypeCheckTest.struct("$b", rightType, 1, 2, 0, 1, 1)
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
                {"mixed", "null"},
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
                {"mixed", "null"},
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
                {"mixed", "null"},
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
                {"mixed", "null"},
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
                {"mixed", "null"},
                {"Exception", "null"},
                {"ErrorException", "null"}
        });

        addErrorStringToCollection($a, errorDto, "float?", new String[][]{
                {"string", "''"},
                {"string?", "null"},
                {"array", "null"},
                {"resource", "null"},
                {"mixed", "null"},
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
                {"mixed", "null"},
                {"Exception", "null"},
                {"ErrorException", "null"}
        });

        addErrorStringToCollection($a, errorDto, "string?", new String[][]{
                {"array", "null"},
                {"resource", "null"},
                {"mixed", "null"},
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
                {"mixed", "null"},
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
                {"mixed", "null"},
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
                {"mixed", "null"},
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
                {"mixed", "null"},
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
