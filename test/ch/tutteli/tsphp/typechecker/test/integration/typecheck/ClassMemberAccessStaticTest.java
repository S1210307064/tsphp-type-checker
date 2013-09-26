package ch.tutteli.tsphp.typechecker.test.integration.typecheck;

import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.TypeCheckStruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class ClassMemberAccessStaticTest extends AOperatorTypeCheckTest
{

    public ClassMemberAccessStaticTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        Object[][] types = new Object[][]{
            {"bool", Bool},
            {"int", Int},
            {"float", Float},
            {"string", String}
        };

        for (Object[] type : types) {
            collection.addAll(Arrays.asList(new Object[][]{
                {
                    "class A{public static " + type[0] + " $a;} A::$a;",
                    typeStruct("sMemAccess", (EBuiltInType) type[1], 1, 1, 0)
                },
                {
                    "class A{public static " + type[0] + " $a;} class B extends A{} B::$a;",
                    typeStruct("sMemAccess", (EBuiltInType) type[1], 1, 2, 0)
                },
                {
                    "class A{public static " + type[0] + " $a; function void foo(){self::$a;}}",
                    typeStruct("sMemAccess", (EBuiltInType) type[1], 1, 0, 4, 1, 4, 0, 0)
                },
                {
                    "class A{public static " + type[0] + " $a;} class B extends A{ function void foo(){self::$a;}}",
                    typeStruct("sMemAccess", (EBuiltInType) type[1], 1, 1, 4, 0, 4, 0, 0)
                },
                {
                    "class A{public static " + type[0] + " $a;} class B extends A{ function void foo(){parent::$a;}}",
                    typeStruct("sMemAccess", (EBuiltInType) type[1], 1, 1, 4, 0, 4, 0, 0)
                },
                {
                    "class A{protected static " + type[0] + " $a; function void foo(){self::$a;}}",
                    typeStruct("sMemAccess", (EBuiltInType) type[1], 1, 0, 4, 1, 4, 0, 0)
                },
                {
                    "class A{protected static " + type[0] + " $a;} "
                    + "class B extends A{ function void foo(){self::$a;}}",
                    typeStruct("sMemAccess", (EBuiltInType) type[1], 1, 1, 4, 0, 4, 0, 0)
                },
                {
                    "class A{protected static " + type[0] + " $a;} "
                    + "class B extends A{ function void foo(){parent::$a;}}",
                    typeStruct("sMemAccess", (EBuiltInType) type[1], 1, 1, 4, 0, 4, 0, 0)
                },
                {
                    "class A{private static " + type[0] + " $a; function void foo(){self::$a;}}",
                    typeStruct("sMemAccess", (EBuiltInType) type[1], 1, 0, 4, 1, 4, 0, 0)
                },
                //can still access static class members with $this
                {
                    "class A{public static " + type[0] + " $a; function void foo(){$this->a;}}",
                    typeStruct("memAccess", (EBuiltInType) type[1], 1, 0, 4, 1, 4, 0, 0)
                },
                {
                    "class A{public static " + type[0] + " $a;} class B extends A{ function void foo(){$this->a;}}",
                    typeStruct("memAccess", (EBuiltInType) type[1], 1, 1, 4, 0, 4, 0, 0)
                }
            }));
        }
        return collection;
    }
}
