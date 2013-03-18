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

import ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Array;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Bool;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.BoolNullable;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Exception;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Float;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.FloatNullable;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Int;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.IntNullable;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Object;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Resource;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.String;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.StringNullable;
import ch.tutteli.tsphp.typechecker.test.testutils.typecheck.EBuiltInType;
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
public class AssignmentOperatorTest extends AOperatorTypeCheckTest
{

    private static List<Object[]> collection;

    public AssignmentOperatorTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        collection = new ArrayList<>();

        addSimpleAssignment();

        addCastingAssignment();

        addCompoundAssignment();

        return collection;
    }

    private static void addSimpleAssignment() {
        collection.addAll(Arrays.asList(new Object[][]{
            {
                "bool $a; $a = false;",
                new TypeCheckStruct[]{struct("=", Bool, 1, 1, 0)}
            },
            {
                "const bool a = false; bool $a; $a = a;",
                new TypeCheckStruct[]{struct("=", Bool, 1, 2, 0)}
            },
            {
                "bool $a; $a = true;",
                new TypeCheckStruct[]{struct("=", Bool, 1, 1, 0)}
            },
            {
                "bool? $a; $a = false;",
                new TypeCheckStruct[]{struct("=", BoolNullable, 1, 1, 0)}
            },
            {
                "const bool a = false; bool? $a; $a = a;",
                new TypeCheckStruct[]{struct("=", BoolNullable, 1, 2, 0)}
            },
            {
                "bool? $a; $a = true;",
                new TypeCheckStruct[]{struct("=", BoolNullable, 1, 1, 0)}
            },
            {
                "bool? $a; $a = null;",
                new TypeCheckStruct[]{struct("=", BoolNullable, 1, 1, 0)}
            },
            {
                "bool? $a; bool? $b; $a = $b;",
                new TypeCheckStruct[]{struct("=", BoolNullable, 1, 2, 0)}
            },
            {
                "int $a; $a = true;",
                new TypeCheckStruct[]{struct("=", Int, 1, 1, 0)}
            },
            {
                "int $a; $a = 1;",
                new TypeCheckStruct[]{struct("=", Int, 1, 1, 0)}
            },
            {
                "const int a = 2; int $a; $a = a;",
                new TypeCheckStruct[]{struct("=", Int, 1, 2, 0)}
            },
            {
                "int? $a; $a = true;",
                new TypeCheckStruct[]{struct("=", IntNullable, 1, 1, 0)}
            },
            {
                "int? $a; $a = 1;",
                new TypeCheckStruct[]{struct("=", IntNullable, 1, 1, 0)}
            },
            {
                "const int a = 1; int? $a; $a = a;",
                new TypeCheckStruct[]{struct("=", IntNullable, 1, 2, 0)}
            },
            {
                "int? $a; $a = null;",
                new TypeCheckStruct[]{struct("=", IntNullable, 1, 1, 0)}
            },
            {
                "int? $a; bool? $b; $a = $b;",
                new TypeCheckStruct[]{struct("=", IntNullable, 1, 2, 0)}
            },
            {
                "int? $a; int? $b; $a = $b;",
                new TypeCheckStruct[]{struct("=", IntNullable, 1, 2, 0)}
            },
            {
                "float $a; $a = true;",
                new TypeCheckStruct[]{struct("=", Float, 1, 1, 0)}
            },
            {
                "float $a; $a = 6;",
                new TypeCheckStruct[]{struct("=", Float, 1, 1, 0)}
            },
            {
                "float $a; $a = 2.56;",
                new TypeCheckStruct[]{struct("=", Float, 1, 1, 0)}
            },
            {
                "const float a = 1.2; float $a; $a = a;",
                new TypeCheckStruct[]{struct("=", Float, 1, 2, 0)}
            },
            {
                "float? $a; $a = true;",
                new TypeCheckStruct[]{struct("=", FloatNullable, 1, 1, 0)}
            },
            {
                "float? $a; $a = 6;",
                new TypeCheckStruct[]{struct("=", FloatNullable, 1, 1, 0)}
            },
            {
                "float? $a; $a = 2.56;",
                new TypeCheckStruct[]{struct("=", FloatNullable, 1, 1, 0)}
            },
            {
                "const float a = 0.1e2; float? $a; $a = a;",
                new TypeCheckStruct[]{struct("=", FloatNullable, 1, 2, 0)}
            },
            {
                "float? $a; $a = null;",
                new TypeCheckStruct[]{struct("=", FloatNullable, 1, 1, 0)}
            },
            {
                "float? $a; bool? $b; $a = $b;",
                new TypeCheckStruct[]{struct("=", FloatNullable, 1, 2, 0)}
            },
            {
                "float? $a; int? $b; $a = $b;",
                new TypeCheckStruct[]{struct("=", FloatNullable, 1, 2, 0)}
            },
            {
                "float? $a; float? $b; $a = $b;",
                new TypeCheckStruct[]{struct("=", FloatNullable, 1, 2, 0)}
            },
            {
                "string $a; $a = true;",
                new TypeCheckStruct[]{struct("=", String, 1, 1, 0)}
            },
            {
                "string $a; $a = 1;",
                new TypeCheckStruct[]{struct("=", String, 1, 1, 0)}
            },
            {
                "string $a; $a = 5.6;",
                new TypeCheckStruct[]{struct("=", String, 1, 1, 0)}
            },
            {
                "string $a; $a = 'hello';",
                new TypeCheckStruct[]{struct("=", String, 1, 1, 0)}
            },
            {
                "const string a = 'hello'; string $a; $a = a;",
                new TypeCheckStruct[]{struct("=", String, 1, 2, 0)}
            },
            {
                "string $a; $a = \"yellow\";",
                new TypeCheckStruct[]{struct("=", String, 1, 1, 0)}
            },
            {
                "string? $a; $a = true;",
                new TypeCheckStruct[]{struct("=", StringNullable, 1, 1, 0)}
            },
            {
                "string? $a; $a = 1;",
                new TypeCheckStruct[]{struct("=", StringNullable, 1, 1, 0)}
            },
            {
                "string? $a; $a = 5.6;",
                new TypeCheckStruct[]{struct("=", StringNullable, 1, 1, 0)}
            },
            {
                "string? $a; $a = 'hello';",
                new TypeCheckStruct[]{struct("=", StringNullable, 1, 1, 0)}
            },
            {
                "const string a = 'velo'; string? $a; $a = a;",
                new TypeCheckStruct[]{struct("=", StringNullable, 1, 2, 0)}
            },
            {
                "string? $a; $a = \"yellow\";",
                new TypeCheckStruct[]{struct("=", StringNullable, 1, 1, 0)}
            },
            {
                "string? $a; $a = null;",
                new TypeCheckStruct[]{struct("=", StringNullable, 1, 1, 0)}
            },
            {
                "string? $a; bool? $b; $a = $b;",
                new TypeCheckStruct[]{struct("=", StringNullable, 1, 2, 0)}
            },
            {
                "string? $a; int? $b; $a = $b;",
                new TypeCheckStruct[]{struct("=", StringNullable, 1, 2, 0)}
            },
            {
                "string? $a; float? $b; $a = $b;",
                new TypeCheckStruct[]{struct("=", StringNullable, 1, 2, 0)}
            },
            {
                "string? $a; string? $b; $a = $b;",
                new TypeCheckStruct[]{struct("=", StringNullable, 1, 2, 0)}
            },
            {
                "array $a; $a = [0];",
                new TypeCheckStruct[]{struct("=", Array, 1, 1, 0)}
            },
            {
                "array $a; $a = array(1,2);",
                new TypeCheckStruct[]{struct("=", Array, 1, 1, 0)}
            },
            {
                "resource $a; resource $b; $a = $b;",
                new TypeCheckStruct[]{struct("=", Resource, 1, 2, 0)}
            },
            {
                "object $a; $a = true;",
                new TypeCheckStruct[]{struct("=", Object, 1, 1, 0)}
            },
            {
                "object $a; $a = false;",
                new TypeCheckStruct[]{struct("=", Object, 1, 1, 0)}
            },
            {
                "object $a; $a = 1;",
                new TypeCheckStruct[]{struct("=", Object, 1, 1, 0)}
            }, {
                "object $a; $a = 1.0;",
                new TypeCheckStruct[]{struct("=", Object, 1, 1, 0)}
            }, {
                "object $a; $a = 'hello';",
                new TypeCheckStruct[]{struct("=", Object, 1, 1, 0)}
            }, {
                "object $a; $a = [1,2];",
                new TypeCheckStruct[]{struct("=", Object, 1, 1, 0)}
            },
            {
                "object $a; bool? $b; $a = $b;",
                new TypeCheckStruct[]{struct("=", Object, 1, 2, 0)}
            },
            {
                "object $a; int? $b; $a = $b;",
                new TypeCheckStruct[]{struct("=", Object, 1, 2, 0)}
            },
            {
                "object $a; float? $b; $a = $b;",
                new TypeCheckStruct[]{struct("=", Object, 1, 2, 0)}
            },
            {
                "object $a; string? $b; $a = $b;",
                new TypeCheckStruct[]{struct("=", Object, 1, 2, 0)}
            },
            {
                "object $a; resource $b; $a = $b;",
                new TypeCheckStruct[]{struct("=", Object, 1, 2, 0)}
            },
            {
                "object $a; object $b; $a = $b;",
                new TypeCheckStruct[]{struct("=", Object, 1, 2, 0)}
            },
            {
                "object $a; Exception $b; $a = $b;",
                new TypeCheckStruct[]{struct("=", Object, 1, 2, 0)}
            },
            {
                "object $a; ErrorException $b; $a = $b;",
                new TypeCheckStruct[]{struct("=", Object, 1, 2, 0)}
            },
            {
                "ErrorException $a; ErrorException $b; $a = $b;",
                new TypeCheckStruct[]{struct("=", ErrorException, 1, 2, 0)}
            },
            {
                "Exception $a; ErrorException $b; $a = $b;",
                new TypeCheckStruct[]{struct("=", Exception, 1, 2, 0)}
            },
            {
                "Exception $a; Exception $b; $a = $b;",
                new TypeCheckStruct[]{struct("=", Exception, 1, 2, 0)}
            }
        }));
    }

    private static void addCastingAssignment() {
        String[][] castCombinations = new String[][]{{"", "=()"}, {"cast ", "="}};

        Object[][] typesWithBuiltInTypes = new Object[][]{
            {"int", Int},
            {"int?", IntNullable},
            {"float", Float},
            {"float?", FloatNullable},
            {"string", String},
            {"string?", StringNullable}
        };
        Object[][] typesWithBuiltInTypes2 = new Object[][]{
            {"bool", Bool},
            {"bool?", BoolNullable},
            {"array", Array}
        };

        String[] types = new String[]{"bool", "bool?", "int", "int?", "float", "float?", "string", "string?"};

        String[] types2 = new String[]{"bool", "bool?", "int", "int?", "float", "float?", "string", "string?",
            "array", "resource", "object", "Exception", "ErrorException"};
        for (String[] castCombination : castCombinations) {
            for (Object[] type : typesWithBuiltInTypes) {
                for (String type2 : types) {
                    collection.add(new Object[]{
                        castCombination[0] + type[0] + " $a; " + type2 + " $b; $a " + castCombination[1] + " $b;",
                        new TypeCheckStruct[]{struct("=", (EBuiltInType) type[1], 1, 2, 0)}
                    });
                }
            }

            for (Object[] type : typesWithBuiltInTypes2) {
                for (String type2 : types2) {
                    collection.add(new Object[]{
                        castCombination[0] + type[0] + " $a; " + type2 + " $b; $a " + castCombination[1] + " $b;",
                        new TypeCheckStruct[]{struct("=", (EBuiltInType) type[1], 1, 2, 0)}
                    });
                }
            }
            collection.add(new Object[]{
                castCombination[0] + "resource $a; object $b; $a " + castCombination[1] + " $b;",
                new TypeCheckStruct[]{struct("=", Resource, 1, 2, 0)}
            });
        }
    }

    private static void addCompoundAssignment() {

        String[] allAssignOperators = new String[]{"+=", "-=", "/=", "*=", "&=", "|=", "^=", "%=", "<<=", ">>="};
        for (String operator : allAssignOperators) {
            collection.addAll(Arrays.asList(new Object[][]{
                {"int $a; $a " + operator + " true;", new TypeCheckStruct[]{struct("=", Int, 1, 1, 0)}},
                {"int $a; $a " + operator + " false;", new TypeCheckStruct[]{struct("=", Int, 1, 1, 0)}},
                {"int $a; bool? $b; $a " + operator + " $b;", new TypeCheckStruct[]{struct("=", Int, 1, 2, 0)}},
                {"int $a; $a " + operator + " 1;", new TypeCheckStruct[]{struct("=", Int, 1, 1, 0)}},
                {"int $a; int? $b; $a " + operator + " $b;", new TypeCheckStruct[]{struct("=", Int, 1, 2, 0)}},
                {"int? $a; $a " + operator + " true;", new TypeCheckStruct[]{struct("=", IntNullable, 1, 1, 0)}},
                {"int? $a; $a " + operator + " false;", new TypeCheckStruct[]{struct("=", IntNullable, 1, 1, 0)}},
                {
                    "int? $a; bool? $b; $a " + operator + " $b;",
                    new TypeCheckStruct[]{struct("=", IntNullable, 1, 2, 0)}
                },
                {"int? $a; $a " + operator + " 1;", new TypeCheckStruct[]{struct("=", IntNullable, 1, 1, 0)}},
                {"int? $a; int? $b; $a " + operator + " $b;", new TypeCheckStruct[]{struct("=", IntNullable, 1, 2, 0)}},}));
        }
        String[] floatCompatibleAssignOperators = new String[]{"+=", "-=", "/=", "*=", "%="};
        for (String operator : floatCompatibleAssignOperators) {
            collection.addAll(Arrays.asList(new Object[][]{
                {"float $a; $a " + operator + " true;", new TypeCheckStruct[]{struct("=", Float, 1, 1, 0)}},
                {"float $a; $a " + operator + " false;", new TypeCheckStruct[]{struct("=", Float, 1, 1, 0)}},
                {"float $a; bool? $b; $a " + operator + " $b;", new TypeCheckStruct[]{struct("=", Float, 1, 2, 0)}},
                {"float $a; $a " + operator + " 1;", new TypeCheckStruct[]{struct("=", Float, 1, 1, 0)}},
                {"float $a; int? $b; $a " + operator + " $b;", new TypeCheckStruct[]{struct("=", Float, 1, 2, 0)}},
                {"float $a; $a " + operator + " 1.0;", new TypeCheckStruct[]{struct("=", Float, 1, 1, 0)}},
                {"float $a; float? $b; $a " + operator + " $b;", new TypeCheckStruct[]{struct("=", Float, 1, 2, 0)}},
                {"float? $a; $a " + operator + " true;", new TypeCheckStruct[]{struct("=", FloatNullable, 1, 1, 0)}},
                {"float? $a; $a " + operator + " false;", new TypeCheckStruct[]{struct("=", FloatNullable, 1, 1, 0)}},
                {
                    "float? $a; bool? $b; $a " + operator + " $b;",
                    new TypeCheckStruct[]{struct("=", FloatNullable, 1, 2, 0)}
                },
                {"float? $a; $a " + operator + " 1;", new TypeCheckStruct[]{struct("=", FloatNullable, 1, 1, 0)}},
                {
                    "float? $a; int? $b; $a " + operator + " $b;",
                    new TypeCheckStruct[]{struct("=", FloatNullable, 1, 2, 0)}
                },
                {"float? $a; $a " + operator + " 1.0;", new TypeCheckStruct[]{struct("=", FloatNullable, 1, 1, 0)}},
                {"float? $a; float? $b; $a " + operator + " $b;", new TypeCheckStruct[]{struct("=", FloatNullable, 1, 2, 0)}},}));
        }
        collection.addAll(Arrays.asList(new Object[][]{
            {"string $a; $a .= true;", new TypeCheckStruct[]{struct("=", String, 1, 1, 0)}},
            {"string $a; $a .= false;", new TypeCheckStruct[]{struct("=", String, 1, 1, 0)}},
            {"string $a; $a .= 1;", new TypeCheckStruct[]{struct("=", String, 1, 1, 0)}},
            {"string $a; $a .= 1.0;", new TypeCheckStruct[]{struct("=", String, 1, 1, 0)}},
            {"string $a; $a .= 'hello';", new TypeCheckStruct[]{struct("=", String, 1, 1, 0)}},
            {"string $a; string? $b; $a .= $b;", new TypeCheckStruct[]{struct("=", String, 1, 2, 0)}},
            {"string? $a; $a .= false;", new TypeCheckStruct[]{struct("=", StringNullable, 1, 1, 0)}},
            {"string? $a; $a .= false;", new TypeCheckStruct[]{struct("=", StringNullable, 1, 1, 0)}},
            {"string? $a; $a .= 1;", new TypeCheckStruct[]{struct("=", StringNullable, 1, 1, 0)}},
            {"string? $a; $a .= 1.0;", new TypeCheckStruct[]{struct("=", StringNullable, 1, 1, 0)}},
            {"string? $a; $a .= 'hello';", new TypeCheckStruct[]{struct("=", StringNullable, 1, 1, 0)}},
            {"string? $a; string? $b; $a .= $b;", new TypeCheckStruct[]{struct("=", StringNullable, 1, 2, 0)}}
        }));

    }
}
