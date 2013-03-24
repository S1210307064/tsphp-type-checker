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
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Bool;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.BoolNullable;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Float;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Int;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.String;
import ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AssignHelper;
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

        AssignHelper.getAssignments(collection, false);

        addCompoundAssignment();

        
        collection.addAll(Arrays.asList(new Object[][]{
            //see TSPHP - 433
            {"bool? $b; int $i = $b = true;", new TypeCheckStruct[]{
                    struct("$i", Int, 1, 1, 1),
                    struct("=", Bool, 1, 1, 1, 0),
                    struct("$b", BoolNullable, 1, 1, 1, 0, 0),
                    struct("true", Bool, 1, 1, 1, 0, 1)
                }
            },
            {"float $b; string $c; int $i = $b = $c = false;", new TypeCheckStruct[]{
                    struct("$i", Int, 1, 2, 1),
                    struct("=", Bool, 1, 2, 1, 0),
                    struct("$b", Float, 1, 2, 1, 0, 0),
                    struct("=", Bool, 1, 2, 1, 0, 1),
                    struct("$c", String, 1, 2, 1, 0, 1, 0),
                    struct("false", Bool, 1, 2, 1, 0, 1, 1)
                }
            },
        }));

        return collection;
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
                {"int? $a; $a " + operator + " true;", new TypeCheckStruct[]{struct("=", Int, 1, 1, 0)}},
                {"int? $a; $a " + operator + " false;", new TypeCheckStruct[]{struct("=", Int, 1, 1, 0)}},
                {
                    "int? $a; bool? $b; $a " + operator + " $b;",
                    new TypeCheckStruct[]{struct("=", Int, 1, 2, 0)}
                },
                {"int? $a; $a " + operator + " 1;", new TypeCheckStruct[]{struct("=", Int, 1, 1, 0)}},
                {"int? $a; int? $b; $a " + operator + " $b;", new TypeCheckStruct[]{struct("=", Int, 1, 2, 0)}},}));
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
                {"float? $a; $a " + operator + " true;", new TypeCheckStruct[]{struct("=", Float, 1, 1, 0)}},
                {"float? $a; $a " + operator + " false;", new TypeCheckStruct[]{struct("=", Float, 1, 1, 0)}},
                {
                    "float? $a; bool? $b; $a " + operator + " $b;",
                    new TypeCheckStruct[]{struct("=", Float, 1, 2, 0)}
                },
                {"float? $a; $a " + operator + " 1;", new TypeCheckStruct[]{struct("=", Float, 1, 1, 0)}},
                {
                    "float? $a; int? $b; $a " + operator + " $b;",
                    new TypeCheckStruct[]{struct("=", Float, 1, 2, 0)}
                },
                {"float? $a; $a " + operator + " 1.0;", new TypeCheckStruct[]{struct("=", Float, 1, 1, 0)}},
                {"float? $a; float? $b; $a " + operator + " $b;", new TypeCheckStruct[]{struct("=", Float, 1, 2, 0)}}
            }));
        }
        collection.addAll(Arrays.asList(new Object[][]{
            {"string $a; $a .= true;", new TypeCheckStruct[]{struct("=", String, 1, 1, 0)}},
            {"string $a; $a .= false;", new TypeCheckStruct[]{struct("=", String, 1, 1, 0)}},
            {"string $a; $a .= 1;", new TypeCheckStruct[]{struct("=", String, 1, 1, 0)}},
            {"string $a; $a .= 1.0;", new TypeCheckStruct[]{struct("=", String, 1, 1, 0)}},
            {"string $a; $a .= 'hello';", new TypeCheckStruct[]{struct("=", String, 1, 1, 0)}},
            {"string $a; string? $b; $a .= $b;", new TypeCheckStruct[]{struct("=", String, 1, 2, 0)}},
            {"string? $a; $a .= false;", new TypeCheckStruct[]{struct("=", String, 1, 1, 0)}},
            {"string? $a; $a .= false;", new TypeCheckStruct[]{struct("=", String, 1, 1, 0)}},
            {"string? $a; $a .= 1;", new TypeCheckStruct[]{struct("=", String, 1, 1, 0)}},
            {"string? $a; $a .= 1.0;", new TypeCheckStruct[]{struct("=", String, 1, 1, 0)}},
            {"string? $a; $a .= 'hello';", new TypeCheckStruct[]{struct("=", String, 1, 1, 0)}},
            {"string? $a; string? $b; $a .= $b;", new TypeCheckStruct[]{struct("=", String, 1, 2, 0)}}
        }));

    }
}
