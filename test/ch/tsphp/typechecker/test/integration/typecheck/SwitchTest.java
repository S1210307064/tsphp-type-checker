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

        addVariations(Bool, "bool", "false", new Object[][]{
                {"bool", Bool, "false"}
        });
        addVariations(BoolNullable, "bool?", "false", new Object[][]{
                {"bool", Bool, "false"}, {"bool?", BoolNullable, "null"}
        });
        addVariations(Int, "int", "0", new Object[][]{
                {"bool", Bool, "false"}, {"int", Int, "1"}
        });
        addVariations(IntNullable, "int?", "null", new Object[][]{
                {"bool", Bool, "false"}, {"bool?", BoolNullable, "null"},
                {"int", Int, "1"}, {"int?", IntNullable, "null"}
        });
        addVariations(Float, "float", "0.4", new Object[][]{
                {"bool", Bool, "false"}, {"int", Int, "1"},
                {"float", Float, "1.5"}
        });
        addVariations(FloatNullable, "float?", "null", new Object[][]{
                {"bool", Bool, "false"}, {"bool?", BoolNullable, "null"},
                {"int", Int, "1"}, {"int?", IntNullable, "null"},
                {"float", Float, "1.5"}, {"float?", FloatNullable, "null"}
        });
        addVariations(String, "string", "''", new Object[][]{
                {"bool", Bool, "false"}, {"int", Int, "1"},
                {"float", Float, "1.5"}, {"string", String, "''"}
        });
        addVariations(StringNullable, "string?", "null", new Object[][]{
                {"bool", Bool, "false"}, {"bool?", BoolNullable, "null"},
                {"int", Int, "1"}, {"int?", IntNullable, "null"},
                {"float", Float, "1.5"}, {"float?", FloatNullable, "null"},
                {"string", String, "''"}, {"string?", StringNullable, "null"}
        });

        return collection;
    }

    private static void addVariations(EBuiltInType builtInType, String conditionType, String initialValue,
            Object[][] types) {

        for (Object[] type : types) {
            collection.add(new Object[]{
                    conditionType + " $cond=" + initialValue + ";"
                            + type[0] + " $a=" + type[2] + "; " + type[0] + " $b=" + type[2] + ";"
                            + type[0] + " $c=" + type[2] + ";" + type[0] + " $d=" + type[2] + "; "
                            + "switch($cond){case $a: 1; case $b: case $c: 1+1; break; case $d: 1;}",
                    new TypeCheckStruct[]{
                            struct("$cond", builtInType, 1, 5, 0),
                            struct("$a", (EBuiltInType) type[1], 1, 5, 1, 0),
                            struct("$b", (EBuiltInType) type[1], 1, 5, 3, 0),
                            struct("$c", (EBuiltInType) type[1], 1, 5, 3, 1),
                            struct("$d", (EBuiltInType) type[1], 1, 5, 5, 0)
                    }
            });
            collection.add(new Object[]{
                    conditionType + " $cond=" + initialValue + ";"
                            + type[0] + " $a=" + type[2] + "; " + type[0] + " $b=" + type[2] + ";"
                            + type[0] + " $c=" + type[2] + ";" + type[0] + " $d=" + type[2] + "; "
                            + "switch($cond){case $a: 1; case $b: case $c: 1+1; break; case $d: 1; default: 1;}",
                    new TypeCheckStruct[]{
                            struct("$cond", builtInType, 1, 5, 0),
                            struct("$a", (EBuiltInType) type[1], 1, 5, 1, 0),
                            struct("$b", (EBuiltInType) type[1], 1, 5, 3, 0),
                            struct("$c", (EBuiltInType) type[1], 1, 5, 3, 1),
                            struct("$d", (EBuiltInType) type[1], 1, 5, 5, 0)
                    }
            });
            collection.add(new Object[]{
                    conditionType + " $cond=" + initialValue + ";"
                            + type[0] + " $a=" + type[2] + "; " + type[0] + " $b=" + type[2] + ";"
                            + type[0] + " $c=" + type[2] + ";" + type[0] + " $d=" + type[2] + "; "
                            + "switch($cond){case $a: 1; case $b: default: case $c: 1+1; break; case $d: 1;}",
                    new TypeCheckStruct[]{
                            struct("$cond", builtInType, 1, 5, 0),
                            struct("$a", (EBuiltInType) type[1], 1, 5, 1, 0),
                            struct("$b", (EBuiltInType) type[1], 1, 5, 3, 0),
                            struct("$c", (EBuiltInType) type[1], 1, 5, 3, 1),
                            struct("$d", (EBuiltInType) type[1], 1, 5, 5, 0)
                    }
            });
        }
    }
}
