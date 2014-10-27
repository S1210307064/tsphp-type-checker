/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.testutils;

import ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.Array;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.ArrayFalseable;
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
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.Resource;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.ResourceFalseable;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.String;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.StringFalseable;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.StringFalseableAndNullable;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.StringNullable;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType.Void;


public class TypeHelper
{

    public static String[] getClassInterfaceTypes() {
        return new String[]{
                "a",
                "a\\C",
                "a\\b\\A",
                "\\e",
                "\\f\\D",
                "\\g\\b\\A"
        };
    }

    public static List<String> getAllTypesWithoutScalar() {
        List<String> collection = new ArrayList<>();
        collection.addAll(Arrays.asList(getClassInterfaceTypes()));
        collection.add("array");
        collection.add("resource");
        collection.add("mixed");
        return collection;
    }

    public static List<String> getPrimitiveTypes() {
        List<String> collection = new ArrayList<>(7);
        collection.addAll(Arrays.asList(getScalarTypes()));
        collection.add("array");
        collection.add("resource");
        collection.add("mixed");
        return collection;
    }

    public static String[] getScalarTypes() {
        return new String[]{
                "bool",
                "int",
                "float",
                "string"
        };
    }

    public static void addAllTypesInclModifier(IAdder adder) {
        String[] types = getScalarTypes();
        int cast = TSPHPDefinitionWalker.Cast;
        int questionMark = TSPHPDefinitionWalker.QuestionMark;
        int logicNot = TSPHPDefinitionWalker.LogicNot;

        for (String type : types) {
            adder.add(type, type, new TreeSet<Integer>());
            adder.add(type + "!", type, new TreeSet<>(Arrays.asList(new Integer[]{logicNot})));
            adder.add(type + "?", type, new TreeSet<>(Arrays.asList(new Integer[]{questionMark})));
            adder.add(type + "!?", type, new TreeSet<>(Arrays.asList(new Integer[]{questionMark, logicNot})));
            adder.add("cast " + type, type, new TreeSet<>(Arrays.asList(new Integer[]{cast})));
            adder.add("cast " + type + "!", type, new TreeSet<>(Arrays.asList(new Integer[]{cast, logicNot})));
            adder.add("cast " + type + "?", type, new TreeSet<>(Arrays.asList(new Integer[]{cast, questionMark})));
            adder.add("cast " + type + "!?", type,
                    new TreeSet<>(Arrays.asList(new Integer[]{cast, questionMark, logicNot})));
        }

        adder.add("array", "array", new TreeSet<Integer>());
        adder.add("array!", "array", new TreeSet<>(Arrays.asList(new Integer[]{logicNot})));
        adder.add("cast array", "array", new TreeSet<>(Arrays.asList(new Integer[]{cast})));
        adder.add("cast array!", "array", new TreeSet<>(Arrays.asList(new Integer[]{cast, logicNot})));

        types = getClassInterfaceTypes();
        for (String type : types) {
            adder.add(type, type, new TreeSet<Integer>());
            adder.add(type + "!", type, new TreeSet<>(Arrays.asList(new Integer[]{logicNot})));
            adder.add("cast " + type, type, new TreeSet<>(Arrays.asList(new Integer[]{cast})));
            adder.add("cast " + type + "!", type, new TreeSet<>(Arrays.asList(new Integer[]{cast, logicNot})));
        }
        adder.add("resource", "resource", new TreeSet<Integer>());
        adder.add("resource!", "resource", new TreeSet<>(Arrays.asList(new Integer[]{logicNot})));
        adder.add("mixed", "mixed", new TreeSet<Integer>());
    }


    public static String[][] getAllTypesInclDefaultValueWithoutExceptions() {
        return new String[][]{
                {"bool", "false"},
                {"bool!", "false"},
                {"bool?", "null"},
                {"bool!?", "null"},
                {"int", "0"},
                {"int!", "false"},
                {"int?", "null"},
                {"int!?", "null"},
                {"float", "0.0"},
                {"float!", "false"},
                {"float?", "null"},
                {"float!?", "null"},
                {"string", "''"},
                {"string!", "false"},
                {"string?", "null"},
                {"string!?", "null"},
                {"array", "null"},
                {"array!", "null"},
                {"resource", "null"},
                {"resource!", "null"},
                {"mixed", "null"},
        };
    }

    public static String[][] getAllTypesInclDefaultValue() {
        return new String[][]{
                {"bool", "false"},
                {"bool!", "false"},
                {"bool?", "null"},
                {"bool!?", "null"},
                {"int", "0"},
                {"int!", "false"},
                {"int?", "null"},
                {"int!?", "null"},
                {"float", "0.0"},
                {"float!", "false"},
                {"float?", "null"},
                {"float!?", "null"},
                {"string", "''"},
                {"string!", "false"},
                {"string?", "null"},
                {"string!?", "null"},
                {"array", "null"},
                {"array!", "null"},
                {"resource", "null"},
                {"resource!", "null"},
                {"mixed", "null"},
                {"Exception", "null"},
                {"Exception!", "null"},
                {"ErrorException", "null"},
                {"ErrorException!", "null"}
        };
    }


    public static Object[][] getAllTypesInclTokenAndDefaultValue() {
        return new Object[][]{
                {"bool", Bool, "false"},
                {"bool!", BoolFalseable, "false"},
                {"bool?", BoolNullable, "null"},
                {"bool!?", BoolFalseableAndNullable, "null"},
                {"int", Int, "0"},
                {"int!", IntFalseable, "false"},
                {"int?", IntNullable, "null"},
                {"int!?", IntFalseableAndNullable, "null"},
                {"float", Float, "0.0"},
                {"float!", FloatFalseable, "false"},
                {"float?", FloatNullable, "null"},
                {"float!?", FloatFalseableAndNullable, "null"},
                {"string", String, "''"},
                {"string!", StringFalseable, "false"},
                {"string?", StringNullable, "null"},
                {"string!?", StringFalseableAndNullable, "null"},
                {"array", Array, "null"},
                {"array!", ArrayFalseable, "false"},
                {"resource", Resource, "null"},
                {"resource!", ResourceFalseable, "null"},
                {"mixed", Mixed, "null"},
                {"\\Exception", Exception, "null"},
                {"\\ErrorException", ErrorException, "null"},
                {"void", Void, ""}
        };
    }
}
