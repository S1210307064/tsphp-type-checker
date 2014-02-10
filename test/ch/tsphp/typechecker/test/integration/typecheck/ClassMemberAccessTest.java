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
public class ClassMemberAccessTest extends AOperatorTypeCheckTest
{

    public ClassMemberAccessTest(String testString, TypeCheckStruct[] struct) {
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
                {"string", String},
                {"bool?", BoolNullable},
                {"int?", IntNullable},
                {"float?", FloatNullable},
                {"string?", StringNullable},
                {"array", Array},
                {"resource", Resource},
                {"object", Object},
                {"Exception", Exception},
                {"ErrorException", ErrorException}
        };

        for (Object[] type : types) {
            collection.add(new Object[]{
                    "class A{public " + type[0] + " $a;} A $a=null; $a->a;",
                    new TypeCheckStruct[]{struct("memAccess", (EBuiltInType) type[1], 1, 2, 0)}
            });
            collection.add(new Object[]{
                    "class A{public " + type[0] + " $a;} class B extends A{} B $a=null; $a->a;",
                    new TypeCheckStruct[]{struct("memAccess", (EBuiltInType) type[1], 1, 3, 0)}
            });
            collection.add(new Object[]{
                    "class A{public " + type[0] + " $a; function void foo(){$this->a;}}",
                    new TypeCheckStruct[]{struct("memAccess", (EBuiltInType) type[1], 1, 0, 4, 1, 4, 0, 0)}
            });
            collection.add(new Object[]{
                    "class A{public " + type[0] + " $a;} class B extends A{ function void foo(){$this->a;}}",
                    new TypeCheckStruct[]{struct("memAccess", (EBuiltInType) type[1], 1, 1, 4, 0, 4, 0, 0)}
            });
            collection.add(new Object[]{
                    "class A{protected " + type[0] + " $a; function void foo(){$this->a;}}",
                    new TypeCheckStruct[]{struct("memAccess", (EBuiltInType) type[1], 1, 0, 4, 1, 4, 0, 0)}
            });
            collection.add(new Object[]{
                    "class A{protected " + type[0] + " $a;} class B extends A{ function void foo(){$this->a;}}",
                    new TypeCheckStruct[]{struct("memAccess", (EBuiltInType) type[1], 1, 1, 4, 0, 4, 0, 0)}
            });
            collection.add(new Object[]{
                    "class A{private " + type[0] + " $a; function void foo(){$this->a;}}",
                    new TypeCheckStruct[]{struct("memAccess", (EBuiltInType) type[1], 1, 0, 4, 1, 4, 0, 0)}
            });
        }
        return collection;
    }
}
