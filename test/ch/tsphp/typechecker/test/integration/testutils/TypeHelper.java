/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.testutils;

import ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

public class TypeHelper
{
    public static final EBuiltInType Bool = EBuiltInType.Bool;
    public static final EBuiltInType BoolNullable = EBuiltInType.BoolNullable;
    public static final EBuiltInType Int = EBuiltInType.Int;
    public static final EBuiltInType IntNullable = EBuiltInType.IntNullable;
    public static final EBuiltInType Float = EBuiltInType.Float;
    public static final EBuiltInType FloatNullable = EBuiltInType.FloatNullable;
    public static final EBuiltInType String = EBuiltInType.String;
    public static final EBuiltInType StringNullable = EBuiltInType.StringNullable;
    public static final EBuiltInType Array = EBuiltInType.Array;
    public static final EBuiltInType Resource = EBuiltInType.Resource;
    public static final EBuiltInType Mixed = EBuiltInType.Mixed;
    public static final EBuiltInType Exception = EBuiltInType.Exception;
    public static final EBuiltInType ErrorException = EBuiltInType.ErrorException;
    public static final EBuiltInType Null = EBuiltInType.Null;
    public static final EBuiltInType Void = EBuiltInType.Void;

    public static List<String> getAllTypes() {
        List<String> types = new ArrayList<>();
        types.addAll(getPrimitiveTypes());
        types.addAll(Arrays.asList(getClassInterfaceTypes()));
        return types;
    }

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

    public static List<String> getAllTypesWithoutResourceAndMixed() {
        List<String> collection = new ArrayList<>();
        collection.addAll(Arrays.asList(getScalarTypes()));
        collection.add("array");
        collection.addAll(Arrays.asList(getClassInterfaceTypes()));
        return collection;
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

    public static String[] getNullableScalarTypes() {
        return new String[]{
                "bool?",
                "int?",
                "float?",
                "string?"
        };
    }

    public static String[] getScalarTypes() {
        return new String[]{
                "bool",
                "int",
                "float",
                "string"
        };
    }

    public static void getAllTypesInclModifier(IAdder adder) {
        String[] types = getScalarTypes();
        int cast = TSPHPDefinitionWalker.Cast;
        int questionMark = TSPHPDefinitionWalker.QuestionMark;

        for (String type : types) {
            adder.add(type, type, new TreeSet<Integer>());
            adder.add("cast " + type, type, new TreeSet<>(Arrays.asList(new Integer[]{cast})));
            adder.add(type + "?", type, new TreeSet<>(Arrays.asList(new Integer[]{questionMark})));
            adder.add("cast " + type + "?", type, new TreeSet<>(Arrays.asList(new Integer[]{cast, questionMark})));
        }

        adder.add("array", "array", new TreeSet<Integer>());
        adder.add("cast array", "array", new TreeSet<>(Arrays.asList(new Integer[]{cast})));

        types = getClassInterfaceTypes();
        for (String type : types) {
            adder.add(type, type, new TreeSet<Integer>());
            adder.add("cast " + type, type, new TreeSet<>(Arrays.asList(new Integer[]{cast})));
        }
        adder.add("resource", "resource", new TreeSet<Integer>());
        adder.add("mixed", "mixed", new TreeSet<Integer>());
    }


    public static String[][] getTypesInclDefaultValueWithoutExceptions() {
        return new String[][]{
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
        };
    }

    public static String[][] getTypesInclDefaultValue() {
        return new String[][]{
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
                {"ErrorException", "null"}
        };
    }


    public static Object[][] getTypesInclTokenAndDefaultValue() {
        return new Object[][]{
                {"bool", Bool, "false"},
                {"int", Int, "0"},
                {"float", Float, "0.0"},
                {"string", String, "''"},
                {"bool?", BoolNullable, "null"},
                {"int?", IntNullable, "null"},
                {"float?", FloatNullable, "null"},
                {"string?", StringNullable, "null"},
                {"array", Array, "null"},
                {"resource", Resource, "null"},
                {"mixed", Mixed, "null"},
                {"\\Exception", Exception, "null"},
                {"\\ErrorException", ErrorException, "null"},
                {"void", Void, ""}
        };
    }
}
