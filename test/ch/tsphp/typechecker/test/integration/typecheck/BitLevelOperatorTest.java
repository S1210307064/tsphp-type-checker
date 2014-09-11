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
public class BitLevelOperatorTest extends AOperatorTypeCheckTest
{

    public BitLevelOperatorTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();
        String[] arithmeticOperators = new String[]{"|", "&", "^", "<<", ">>"};
        Object[][] typesInclReturnType = new Object[][]{{"bool", Int}, {"int", Int}};
        for (String op : arithmeticOperators) {
            collection.addAll(Arrays.asList(new Object[][]{
                    {"true " + op + " false;", typeStruct(op, Int, 1, 0, 0)},
                    {"true " + op + " 1;", typeStruct(op, Int, 1, 0, 0)},
                    {"2 " + op + " true;", typeStruct(op, Int, 1, 0, 0)},
                    {"2 " + op + " 5;", typeStruct(op, Int, 1, 0, 0)},
            }));

            for (Object[] typeAndReturnType : typesInclReturnType) {
                String type = (String) typeAndReturnType[0];
                EBuiltInType rType = (EBuiltInType) typeAndReturnType[1];
                collection.addAll(Arrays.asList(new Object[][]{
                        {type + "! $a=false; bool $b=true; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "! $a=false; bool! $b=false; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "! $a=false; bool? $b=null; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "! $a=false; bool!? $b=false; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "! $a=false; int $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "! $a=false; int! $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "! $a=false; int? $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "! $a=false; int!? $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "? $a=null; bool $b=true; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "? $a=null; bool! $b=false; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "? $a=null; bool? $b=null; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "? $a=null; bool!? $b=false; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "? $a=null; int $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "? $a=null; int! $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "? $a=null; int? $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "? $a=null; int!? $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "!? $a=null; bool $b=true; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "!? $a=null; bool! $b=false; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "!? $a=null; bool? $b=null; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "!? $a=null; bool!? $b=false; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "!? $a=null; int $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "!? $a=null; int! $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "!? $a=null; int? $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                        {type + "!? $a=null; int!? $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                }));
            }
        }
        collection.addAll(Arrays.asList(new Object[][]{
                {"~true;", typeStruct("~", Int, 1, 0, 0)},
                {"~false;", typeStruct("~", Int, 1, 0, 0)},
                {"~23098;", typeStruct("~", Int, 1, 0, 0)},
                {"bool $a=true; ~$a;", typeStruct("~", Int, 1, 1, 0)},
                {"bool! $a=true; ~$a;", typeStruct("~", Int, 1, 1, 0)},
                {"bool? $a=true; ~$a;", typeStruct("~", Int, 1, 1, 0)},
                {"bool!? $a=true; ~$a;", typeStruct("~", Int, 1, 1, 0)},
                {"int $a=true; ~$a;", typeStruct("~", Int, 1, 1, 0)},
                {"int! $a=true; ~$a;", typeStruct("~", Int, 1, 1, 0)},
                {"int? $a=true; ~$a;", typeStruct("~", Int, 1, 1, 0)},
                {"int!? $a=true; ~$a;", typeStruct("~", Int, 1, 1, 0)},
        }));
        return collection;
    }
}
