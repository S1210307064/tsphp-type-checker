/*
 * Copyright 2012 Robert Stoll <rstoll@tutteli.ch>
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
package ch.tutteli.tsphp.typechecker.test.typecheck;

import ch.tutteli.tsphp.typechecker.test.testutils.TypeHelper;
import ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest;
import ch.tutteli.tsphp.typechecker.test.testutils.typecheck.TypeCheckStruct;
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
public class IdentityOperatorTest extends AOperatorTypeCheckTest
{

    public IdentityOperatorTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();
        String[] operators = new String[]{"===", "!=="};


        String[] scalarTypes = TypeHelper.getScalarTypes();
        String[] nullableScalarTypes = TypeHelper.getNullableScalarTypes();

        String[] nullableTypes = new String[]{"array", "resource"};

        for (String operator : operators) {
            for (String type : scalarTypes) {
                for (String type2 : scalarTypes) {
                    collection.add(new Object[]{
                                type + " $a; " + type2 + " $b; $a " + operator + " $b;",
                                new TypeCheckStruct[]{struct(operator, Bool, 1, 2, 0)}
                            });
                }
                collection.add(new Object[]{
                            type + " $a; object $b; $a " + operator + " $b;",
                            new TypeCheckStruct[]{struct(operator, Bool, 1, 2, 0)}
                        });
                collection.add(new Object[]{
                            "object $a; " + type + " $b; $a " + operator + " $b;",
                            new TypeCheckStruct[]{struct(operator, Bool, 1, 2, 0)}
                        });
            }
            for (String type : nullableScalarTypes) {
                for (String type2 : nullableScalarTypes) {
                    collection.add(new Object[]{
                                type + " $a; " + type2 + " $b; $a " + operator + " $b;",
                                new TypeCheckStruct[]{struct(operator, Bool, 1, 2, 0)}
                            });
                }
                collection.add(new Object[]{
                            type + " $a; object $b; $a " + operator + " $b;",
                            new TypeCheckStruct[]{struct(operator, Bool, 1, 2, 0)}
                        });
                collection.add(new Object[]{
                            "object $a; " + type + " $b; $a " + operator + " $b;",
                            new TypeCheckStruct[]{struct(operator, Bool, 1, 2, 0)}
                        });
                collection.add(new Object[]{
                            type + " $a; $a " + operator + " null;",
                            new TypeCheckStruct[]{struct(operator, Bool, 1, 1, 0)}
                        });
                collection.add(new Object[]{
                            type + " $b; null " + operator + " $b;",
                            new TypeCheckStruct[]{struct(operator, Bool, 1, 1, 0)}
                        });
            }

            for (String type : nullableTypes) {
                collection.add(new Object[]{
                            type + " $a; " + type + "$b; $a " + operator + " $b;",
                            new TypeCheckStruct[]{struct(operator, Bool, 1, 2, 0)}
                        });
                collection.add(new Object[]{
                            type + " $a; object $b; $a " + operator + " $b;",
                            new TypeCheckStruct[]{struct(operator, Bool, 1, 2, 0)}
                        });
                collection.add(new Object[]{
                            "object $a; " + type + " $b; $a " + operator + " $b;",
                            new TypeCheckStruct[]{struct(operator, Bool, 1, 2, 0)}
                        });
                collection.add(new Object[]{
                            type + " $a; $a " + operator + " null;",
                            new TypeCheckStruct[]{struct(operator, Bool, 1, 1, 0)}
                        });
                collection.add(new Object[]{
                            type + " $b; null " + operator + " $b;",
                            new TypeCheckStruct[]{struct(operator, Bool, 1, 1, 0)}
                        });


            }
            collection.add(new Object[]{
                        "object $a; object $b; $a " + operator + " $b;",
                        new TypeCheckStruct[]{struct(operator, Bool, 1, 2, 0)}
                    });
        }


        return collection;

    }
}
