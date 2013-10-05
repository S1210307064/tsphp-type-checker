package ch.tutteli.tsphp.typechecker.test.integration.testutils;

import ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

public class TypeHelper
{

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

    public static List<String> getAllTypesWithoutResourceAndObject() {
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
        collection.add("object");
        return collection;
    }

    public static List<String> getPrimitiveTypes() {
        List<String> collection = new ArrayList<>(7);
        collection.addAll(Arrays.asList(getScalarTypes()));
        collection.add("array");
        collection.add("resource");
        collection.add("object");
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
        adder.add("object", "object", new TreeSet<Integer>());
    }
}
