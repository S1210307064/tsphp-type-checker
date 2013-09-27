package ch.tutteli.tsphp.typechecker.test.integration.typecheck;

import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.TypeCheckStruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class SwitchTest extends AOperatorTypeCheckTest
{


    private static List<Object[]> collection;

    protected List<Exception> exceptions = new ArrayList<>();

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

        addVariations(Bool, "bool", new Object[][]{{"bool", Bool}});
        addVariations(BoolNullable, "bool?", new Object[][]{{"bool", Bool}, {"bool?", BoolNullable}});
        addVariations(Int, "int", new Object[][]{{"bool", Bool}, {"int", Int}});
        addVariations(IntNullable, "int?", new Object[][]{
            {"bool", Bool}, {"bool?", BoolNullable},
            {"int", Int}, {"int?", IntNullable}
        });
        addVariations(Float, "float", new Object[][]{
            {"bool", Bool}, {"int", Int}, {"float", Float}
        });
        addVariations(FloatNullable, "float?", new Object[][]{
            {"bool", Bool}, {"bool?", BoolNullable},
            {"int", Int}, {"int?", IntNullable},
            {"float", Float}, {"float?", FloatNullable}
        });
        addVariations(String, "string", new Object[][]{
            {"bool", Bool}, {"int", Int}, {"float", Float}, {"string", String}
        });
        addVariations(StringNullable, "string?", new Object[][]{
            {"bool", Bool}, {"bool?", BoolNullable},
            {"int", Int}, {"int?", IntNullable},
            {"float", Float}, {"float?", FloatNullable},
            {"string", String}, {"string?", StringNullable}
        });      

        return collection;
    }

    private static void addVariations(EBuiltInType builtInType, String conditionType, Object[][] types) {

        for (Object[] type : types) {
            collection.add(new Object[]{
                conditionType + " $cond;" + type[0] + " $a; " + type[0] + " $b;" + type[0] + " $c;" + type[0] + " $d; "
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
                conditionType + " $cond;" + type[0] + " $a; " + type[0] + " $b;" + type[0] + " $c;" + type[0] + " $d; "
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
                conditionType + " $cond;" + type[0] + " $a; " + type[0] + " $b;" + type[0] + " $c;" + type[0] + " $d; "
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
