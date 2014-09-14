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
public class FieldAccessTest extends AOperatorTypeCheckTest
{

    public FieldAccessTest(String testString, TypeCheckStruct[] struct) {
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
            collection.add(new Object[]{
                    "class A{public " + type[0] + " $a;} A $a=null; $a->a;",
                    new TypeCheckStruct[]{struct("fieAccess", (EBuiltInType) type[1], 1, 2, 0)}
            });
            collection.add(new Object[]{
                    "class A{public " + type[0] + " $a;} class B extends A{} B $a=null; $a->a;",
                    new TypeCheckStruct[]{struct("fieAccess", (EBuiltInType) type[1], 1, 3, 0)}
            });
            collection.add(new Object[]{
                    "class A{public " + type[0] + " $a; function void foo(){$this->a;}}",
                    new TypeCheckStruct[]{struct("fieAccess", (EBuiltInType) type[1], 1, 0, 4, 1, 4, 0, 0)}
            });
            collection.add(new Object[]{
                    "class A{public " + type[0] + " $a;} class B extends A{ function void foo(){$this->a;}}",
                    new TypeCheckStruct[]{struct("fieAccess", (EBuiltInType) type[1], 1, 1, 4, 0, 4, 0, 0)}
            });
            collection.add(new Object[]{
                    "class A{protected " + type[0] + " $a; function void foo(){$this->a;}}",
                    new TypeCheckStruct[]{struct("fieAccess", (EBuiltInType) type[1], 1, 0, 4, 1, 4, 0, 0)}
            });
            collection.add(new Object[]{
                    "class A{protected " + type[0] + " $a;} class B extends A{ function void foo(){$this->a;}}",
                    new TypeCheckStruct[]{struct("fieAccess", (EBuiltInType) type[1], 1, 1, 4, 0, 4, 0, 0)}
            });
            collection.add(new Object[]{
                    "class A{private " + type[0] + " $a; function void foo(){$this->a;}}",
                    new TypeCheckStruct[]{struct("fieAccess", (EBuiltInType) type[1], 1, 0, 4, 1, 4, 0, 0)}
            });
        }
        return collection;
    }
}
