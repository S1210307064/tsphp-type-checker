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
public class RelationalOperatorTest extends AOperatorTypeCheckTest
{

    public RelationalOperatorTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        String[] operators = new String[]{"<", "<=", ">=", ">"};
        String[] types = new String[]{"int", "float"};
        for (String op : operators) {
            collection.addAll(Arrays.asList(new Object[][]{
                    {"1 " + op + " 2;", typeStruct(op, Bool, 1, 0, 0)},
                    {"1 " + op + " 2.0;", typeStruct(op, Bool, 1, 0, 0)},
                    {"1.0 " + op + " 2;", typeStruct(op, Bool, 1, 0, 0)},
                    {"1.0 " + op + " 2.0;", typeStruct(op, Bool, 1, 0, 0)},
            }));

            EBuiltInType rType = Bool;

            for (String type : types) {
                collection.addAll(Arrays.asList(new Object[][]{
                        {type + "! $a=false; int $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "! $a=false; int! $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "! $a=false; int? $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "! $a=false; int!? $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "! $a=false; float $b=1.0; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "! $a=false; float! $b=1.0; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "! $a=false; float? $b=1.0; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "! $a=false; float!? $b=1.0; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "? $a=null; int $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "? $a=null; int! $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "? $a=null; int? $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "? $a=null; int!? $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "? $a=null; float $b=1.0; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "? $a=null; float! $b=1.0; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "? $a=null; float? $b=1.0; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "? $a=null; float!? $b=1.0; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "!? $a=null; int $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "!? $a=null; int! $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "!? $a=null; int? $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "!? $a=null; int!? $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "!? $a=null; float $b=1.0; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "!? $a=null; float! $b=1.0; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "!? $a=null; float? $b=1.0; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "!? $a=null; float!? $b=1.0; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                }));
            }
        }
        return collection;
    }
}
