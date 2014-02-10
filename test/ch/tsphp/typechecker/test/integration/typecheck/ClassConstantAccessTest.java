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
public class ClassConstantAccessTest extends AOperatorTypeCheckTest
{

    public ClassConstantAccessTest(String testString, TypeCheckStruct[] struct) {
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
                            "class A{const " + type[0] + " a = false;} A::a;",
                            typeStruct("sMemAccess", (EBuiltInType) type[1], 1, 1, 0)
                    },
                    {
                            "interface A{const " + type[0] + " a = false;} A::a;",
                            typeStruct("sMemAccess", (EBuiltInType) type[1], 1, 1, 0)
                    },
                    {
                            "class A{const " + type[0] + " a = false;} class B extends A{} B::a;",
                            typeStruct("sMemAccess", (EBuiltInType) type[1], 1, 2, 0)
                    },
                    {
                            "interface A{const " + type[0] + " a = false;} class B implements A{} B::a;",
                            typeStruct("sMemAccess", (EBuiltInType) type[1], 1, 2, 0)
                    },
                    {
                            "class A{const " + type[0] + " a = false; function void foo(){self::a;}}",
                            typeStruct("sMemAccess", (EBuiltInType) type[1], 1, 0, 4, 1, 4, 0, 0)
                    },
                    {
                            "class A{const " + type[0] + " a = false; function void foo(){A::a;}}",
                            typeStruct("sMemAccess", (EBuiltInType) type[1], 1, 0, 4, 1, 4, 0, 0)
                    },
                    {
                            "class A{const " + type[0] + " a = false;} class B extends A{ function void foo()" +
                                    "{self::a;}}",
                            typeStruct("sMemAccess", (EBuiltInType) type[1], 1, 1, 4, 0, 4, 0, 0)
                    },
                    {
                            "interface A{const " + type[0] + " a = false;} class B implements A{ function void foo()" +
                                    "{self::a;}}",
                            typeStruct("sMemAccess", (EBuiltInType) type[1], 1, 1, 4, 0, 4, 0, 0)
                    },
                    {
                            "class A{const " + type[0] + " a = false;} class B extends A{ function void foo()" +
                                    "{parent::a;}}",
                            typeStruct("sMemAccess", (EBuiltInType) type[1], 1, 1, 4, 0, 4, 0, 0)
                    },
                    {
                            "class A{const " + type[0] + " a = false;} class B extends A{ function void foo(){A::a;}}",
                            typeStruct("sMemAccess", (EBuiltInType) type[1], 1, 1, 4, 0, 4, 0, 0)
                    },
                    {
                            "class A{const " + type[0] + " a = false;} class B extends A{ function void foo(){B::a;}}",
                            typeStruct("sMemAccess", (EBuiltInType) type[1], 1, 1, 4, 0, 4, 0, 0)
                    },
                    {
                            "interface A{const " + type[0] + " a = false;} class B implements A{ function void foo()" +
                                    "{A::a;}}",
                            typeStruct("sMemAccess", (EBuiltInType) type[1], 1, 1, 4, 0, 4, 0, 0)
                    },
                    {
                            "interface A{const " + type[0] + " a = false;} class B implements A{ function void foo()" +
                                    "{B::a;}}",
                            typeStruct("sMemAccess", (EBuiltInType) type[1], 1, 1, 4, 0, 4, 0, 0)
                    }
            }));
        }
        return collection;
    }
}
