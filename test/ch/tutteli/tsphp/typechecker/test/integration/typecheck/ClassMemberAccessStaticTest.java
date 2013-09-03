/*
 * Copyright 2013 Robert Stoll <rstoll@tutteli.ch>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
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

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
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
