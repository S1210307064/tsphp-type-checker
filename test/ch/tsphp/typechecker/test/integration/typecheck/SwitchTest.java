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
public class SwitchTest extends AOperatorTypeCheckTest
{

    private static List<Object[]> collection;

    public SwitchTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        collection = new ArrayList<>();

        Object[][] noCastNeededTypes = new Object[][]{
                {"bool", Bool, "false"}
        };
        Object[][] implicitCastNeededTypes = new Object[][]{};
        addVariations(Bool, "bool", "false", noCastNeededTypes, implicitCastNeededTypes);

        noCastNeededTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"bool!", BoolFalseable, "false"}
        };
        implicitCastNeededTypes = new Object[][]{};
        addVariations(BoolFalseable, "bool!", "false", noCastNeededTypes, implicitCastNeededTypes);


        noCastNeededTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"bool?", BoolNullable, "null"}
        };
        implicitCastNeededTypes = new Object[][]{};
        addVariations(BoolNullable, "bool?", "null", noCastNeededTypes, implicitCastNeededTypes);

        noCastNeededTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"bool!", BoolFalseable, "false"},
                {"bool?", BoolNullable, "null"},
                {"bool!?", BoolFalseableAndNullable, "null"}
        };
        implicitCastNeededTypes = new Object[][]{};
        addVariations(BoolFalseableAndNullable, "bool!?", "null", noCastNeededTypes, implicitCastNeededTypes);

        noCastNeededTypes = new Object[][]{
                {"int", Int, "1"}
        };
        implicitCastNeededTypes = new Object[][]{
                {"bool", Bool, "false"},
        };
        addVariations(Int, "int", "0", noCastNeededTypes, implicitCastNeededTypes);

        noCastNeededTypes = new Object[][]{
                {"int", Int, "1"},
                {"int!", IntFalseable, "false"}
        };
        implicitCastNeededTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"bool!", BoolFalseable, "false"},
        };
        addVariations(IntFalseable, "int!", "false", noCastNeededTypes, implicitCastNeededTypes);

        noCastNeededTypes = new Object[][]{
                {"int", Int, "1"},
                {"int?", IntNullable, "null"},
        };
        implicitCastNeededTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"bool?", BoolNullable, "null"},
        };
        addVariations(IntNullable, "int?", "null", noCastNeededTypes, implicitCastNeededTypes);

        noCastNeededTypes = new Object[][]{
                {"int", Int, "1"},
                {"int!", IntFalseable, "false"},
                {"int?", IntNullable, "null"},
                {"int!?", IntFalseableAndNullable, "null"}
        };
        implicitCastNeededTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"bool!", BoolFalseable, "false"},
                {"bool?", BoolNullable, "null"},
                {"bool!?", BoolFalseableAndNullable, "null"},
        };
        addVariations(IntFalseableAndNullable, "int!?", "null", noCastNeededTypes, implicitCastNeededTypes);

        noCastNeededTypes = new Object[][]{
                {"float", Float, "1.5"}
        };
        implicitCastNeededTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"int", Int, "1"},
        };
        addVariations(Float, "float", "0.4", noCastNeededTypes, implicitCastNeededTypes);

        noCastNeededTypes = new Object[][]{

                {"float", Float, "1.5"},
                {"float!", FloatFalseable, "false"},
        };
        implicitCastNeededTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"bool!", BoolFalseable, "false"},
                {"int", Int, "1"},
                {"int!", IntFalseable, "false"},
        };
        addVariations(FloatFalseable, "float!", "false", noCastNeededTypes, implicitCastNeededTypes);

        noCastNeededTypes = new Object[][]{

                {"float", Float, "1.5"},
                {"float?", FloatNullable, "null"}
        };
        implicitCastNeededTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"bool?", BoolNullable, "null"},
                {"int", Int, "1"},
                {"int?", IntNullable, "null"},
        };
        addVariations(FloatNullable, "float?", "null", noCastNeededTypes, implicitCastNeededTypes);

        noCastNeededTypes = new Object[][]{
                {"float", Float, "1.5"},
                {"float!", FloatFalseable, "false"},
                {"float?", FloatNullable, "null"},
                {"float!?", FloatFalseableAndNullable, "null"},
        };
        implicitCastNeededTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"bool!", BoolFalseable, "false"},
                {"bool?", BoolNullable, "null"},
                {"bool!?", BoolFalseableAndNullable, "null"},
                {"int", Int, "1"},
                {"int!", IntFalseable, "false"},
                {"int?", IntNullable, "null"},
                {"int!?", IntFalseableAndNullable, "null"},
        };
        addVariations(FloatFalseableAndNullable, "float!?", "null", noCastNeededTypes, implicitCastNeededTypes);

        noCastNeededTypes = new Object[][]{
                {"string", String, "''"}
        };
        implicitCastNeededTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"int", Int, "1"},
                {"float", Float, "1.5"},
        };
        addVariations(String, "string", "''", noCastNeededTypes, implicitCastNeededTypes);

        noCastNeededTypes = new Object[][]{
                {"string", String, "''"},
                {"string!", StringFalseable, "false"},
        };
        implicitCastNeededTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"bool!", BoolFalseable, "false"},
                {"int", Int, "1"},
                {"int!", IntFalseable, "false"},
                {"float", Float, "1.5"},
                {"float!", FloatFalseable, "false"},
        };
        addVariations(StringFalseable, "string!", "false", noCastNeededTypes, implicitCastNeededTypes);

        noCastNeededTypes = new Object[][]{
                {"string", String, "''"},
                {"string?", StringNullable, "null"}
        };
        implicitCastNeededTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"bool?", BoolNullable, "null"},
                {"int", Int, "1"},
                {"int?", IntNullable, "null"},
                {"float", Float, "1.5"},
                {"float?", FloatNullable, "null"},
        };
        addVariations(StringNullable, "string?", "null", noCastNeededTypes, implicitCastNeededTypes);

        noCastNeededTypes = new Object[][]{
                {"string", String, "''"},
                {"string!", StringFalseable, "false"},
                {"string?", StringNullable, "null"},
                {"string!?", StringFalseableAndNullable, "null"}
        };
        implicitCastNeededTypes = new Object[][]{
                {"bool", Bool, "false"},
                {"bool!", BoolFalseable, "false"},
                {"bool?", BoolNullable, "null"},
                {"bool!?", BoolFalseableAndNullable, "null"},
                {"int", Int, "1"},
                {"int!", IntFalseable, "false"},
                {"int?", IntNullable, "null"},
                {"int!?", IntFalseableAndNullable, "null"},
                {"float", Float, "1.5"},
                {"float!", FloatFalseable, "false"},
                {"float?", FloatNullable, "null"},
                {"float!?", FloatFalseableAndNullable, "null"},
        };
        addVariations(StringFalseableAndNullable, "string!?", "null", noCastNeededTypes, implicitCastNeededTypes);

        return collection;
    }

    private static void addVariations(EBuiltInType builtInType, String conditionType, String initialValue,
            Object[][] noCastNeededTypes, Object[][] implicitCastNeededTypes) {

        for (Object[] type : noCastNeededTypes) {
            collection.addAll(Arrays.asList(new Object[][]{
                    {
                            conditionType + " $cond=" + initialValue + ";"
                                    + type[0] + " $a=" + type[2] + "; " + type[0] + " $b=" + type[2] + ";"
                                    + type[0] + " $c=" + type[2] + ";" + type[0] + " $d=" + type[2] + "; "
                                    + "switch($cond){case $a: 1; case $b: case $c: 1+1; break; case $d: 1;}",
                            createNoCastNeededTypeCheckStruct(builtInType, (EBuiltInType) type[1])
                    },
                    {
                            conditionType + " $cond=" + initialValue + ";"
                                    + type[0] + " $a=" + type[2] + "; " + type[0] + " $b=" + type[2] + ";"
                                    + type[0] + " $c=" + type[2] + ";" + type[0] + " $d=" + type[2] + "; "
                                    + "switch($cond){case $a: 1; case $b: case $c: 1+1; break; case $d: 1; default: " +
                                    "1;}",
                            createNoCastNeededTypeCheckStruct(builtInType, (EBuiltInType) type[1])
                    },
                    {
                            conditionType + " $cond=" + initialValue + ";"
                                    + type[0] + " $a=" + type[2] + "; " + type[0] + " $b=" + type[2] + ";"
                                    + type[0] + " $c=" + type[2] + ";" + type[0] + " $d=" + type[2] + "; "
                                    + "switch($cond){case $a: 1; case $b: default: case $c: 1+1; break; case $d: 1;}",
                            createNoCastNeededTypeCheckStruct(builtInType, (EBuiltInType) type[1])
                    }
            }));
        }
        //TODO rstoll TSPHP-831 switch cases and casting
//        for (Object[] type : implicitCastNeededTypes) {
//            collection.addAll(Arrays.asList(new Object[][]{
//                    {
//                            conditionType + " $cond=" + initialValue + ";"
//                                    + type[0] + " $a=" + type[2] + "; " + type[0] + " $b=" + type[2] + ";"
//                                    + type[0] + " $c=" + type[2] + ";" + type[0] + " $d=" + type[2] + "; "
//                                    + "switch($cond){case $a: 1; case $b: case $c: 1+1; break; case $d: 1;}",
//                            createCastTypeCheckStruct(builtInType, (EBuiltInType) type[1])
//                    },
//                    {
//                            conditionType + " $cond=" + initialValue + ";"
//                                    + type[0] + " $a=" + type[2] + "; " + type[0] + " $b=" + type[2] + ";"
//                                    + type[0] + " $c=" + type[2] + ";" + type[0] + " $d=" + type[2] + "; "
//                                    + "switch($cond){case $a: 1; case $b: case $c: 1+1; break; case $d: 1; default:
// " +
//                                    "1;}",
//                            createCastTypeCheckStruct(builtInType, (EBuiltInType) type[1])
//                    },
//                    {
//                            conditionType + " $cond=" + initialValue + ";"
//                                    + type[0] + " $a=" + type[2] + "; " + type[0] + " $b=" + type[2] + ";"
//                                    + type[0] + " $c=" + type[2] + ";" + type[0] + " $d=" + type[2] + "; "
//                                    + "switch($cond){case $a: 1; case $b: default: case $c: 1+1; break; case $d: 1;}",
//                            createCastTypeCheckStruct(builtInType, (EBuiltInType) type[1])
//                    }
//            }));
//        }
    }

    private static TypeCheckStruct[] createCastTypeCheckStruct(EBuiltInType builtInType, EBuiltInType initType) {
        return new TypeCheckStruct[]{
                struct("$cond", builtInType, 1, 5, 0),
                struct("casting", builtInType, 1, 5, 1, 0),
                struct("$a", initType, 1, 5, 1, 0, 1),
                struct("casting", builtInType, 1, 5, 3, 0),
                struct("$b", initType, 1, 5, 3, 0, 1),
                struct("casting", builtInType, 1, 5, 3, 1),
                struct("$c", initType, 1, 5, 3, 1, 1),
                struct("casting", builtInType, 1, 5, 5, 0),
                struct("$d", initType, 1, 5, 5, 0, 1)
        };
    }

    private static TypeCheckStruct[] createNoCastNeededTypeCheckStruct(
            EBuiltInType builtInType, EBuiltInType initType) {
        return new TypeCheckStruct[]{
                struct("$cond", builtInType, 1, 5, 0),
                struct("$a", initType, 1, 5, 1, 0),
                struct("$b", initType, 1, 5, 3, 0),
                struct("$c", initType, 1, 5, 3, 1),
                struct("$d", initType, 1, 5, 5, 0)
        };
    }
}
