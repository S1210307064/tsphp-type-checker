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
public class FieldAccessStaticTest extends AOperatorTypeCheckTest
{

    public FieldAccessStaticTest(String testString, TypeCheckStruct[] struct) {
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
                {"bool!", BoolFalseable},
                {"bool?", BoolNullable},
                {"bool!?", BoolFalseableAndNullable},
                {"int", Int},
                {"int!", IntFalseable},
                {"int?", IntNullable},
                {"int!?", IntFalseableAndNullable},
                {"float", Float},
                {"float!", FloatFalseable},
                {"float?", FloatNullable},
                {"float!?", FloatFalseableAndNullable},
                {"string", String},
                {"string!", StringFalseable},
                {"string?", StringNullable},
                {"string!?", StringFalseableAndNullable},
                {"array", Array},
                {"array!", ArrayFalseable},
                {"resource", Resource},
                {"resource!", ResourceFalseable},
                {"mixed", Mixed},
                {"Exception", Exception},
                {"Exception!", ExceptionFalseable},
                {"ErrorException", ErrorException},
                {"ErrorException!", ErrorExceptionFalseable},
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
                            "class A{public static " + type[0] + " $a;} "
                                    + "class B extends A{ function void foo(){self::$a;}}",
                            typeStruct("sMemAccess", (EBuiltInType) type[1], 1, 1, 4, 0, 4, 0, 0)
                    },
                    {
                            "class A{public static " + type[0] + " $a;} "
                                    + "class B extends A{ function void foo(){parent::$a;}}",
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
                    //can still access static fields with $this
                    {
                            "class A{public static " + type[0] + " $a; function void foo(){$this->a;}}",
                            typeStruct("fieAccess", (EBuiltInType) type[1], 1, 0, 4, 1, 4, 0, 0)
                    },
                    {
                            "class A{public static " + type[0] + " $a;} "
                                    + "class B extends A{ function void foo(){$this->a;}}",
                            typeStruct("fieAccess", (EBuiltInType) type[1], 1, 1, 4, 0, 4, 0, 0)
                    }
            }));
        }
        return collection;
    }
}