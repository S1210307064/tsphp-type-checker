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
public class DotOperatorTest extends AOperatorTypeCheckTest
{

    public DotOperatorTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();
        String[] types = new String[]{"bool", "int", "float"};
        String op = ".";
        collection.addAll(Arrays.asList(new Object[][]{
                {"true " + op + " false;", typeStruct(op, String, 1, 0, 0)},
                {"true " + op + " 2;", typeStruct(op, String, 1, 0, 0)},
                {"true " + op + " 2.0;", typeStruct(op, String, 1, 0, 0)},
                {"true " + op + " 'hi';", typeStruct(op, String, 1, 0, 0)},
                {"1 " + op + " false;", typeStruct(op, String, 1, 0, 0)},
                {"1 " + op + " 2;", typeStruct(op, String, 1, 0, 0)},
                {"1 " + op + " 2.0;", typeStruct(op, String, 1, 0, 0)},
                {"1 " + op + " 'hi';", typeStruct(op, String, 1, 0, 0)},
                {"1.0 " + op + " false;", typeStruct(op, String, 1, 0, 0)},
                {"1.0 " + op + " 2;", typeStruct(op, String, 1, 0, 0)},
                {"1.0 " + op + " 2.0;", typeStruct(op, String, 1, 0, 0)},
                {"1.3 " + op + " 'hi';", typeStruct(op, String, 1, 0, 0)},
                {"true " + op + " 1 " + op + " 10;", new TypeCheckStruct[]{
                        struct(op, String, 1, 0, 0),
                        struct(op, String, 1, 0, 0, 0)}
                },
                {"1 " + op + " 1 " + op + " false;", new TypeCheckStruct[]{
                        struct(op, String, 1, 0, 0),
                        struct(op, String, 1, 0, 0, 0),
                        struct(op, String, 1, 0, 0, 0)}
                },
                {"1 " + op + " 1 " + op + " 10;", new TypeCheckStruct[]{
                        struct(op, String, 1, 0, 0),
                        struct(op, String, 1, 0, 0, 0)}
                },
                {"1 " + op + " 5.0 " + op + " 10;", new TypeCheckStruct[]{
                        struct(op, String, 1, 0, 0),
                        struct(op, String, 1, 0, 0, 0)}
                },
                {"1 " + op + " 5.0 " + op + " true;", new TypeCheckStruct[]{
                        struct(op, String, 1, 0, 0),
                        struct(op, String, 1, 0, 0, 0)}
                },
                {"2.0 " + op + " 5.0 " + op + " 10.3;", new TypeCheckStruct[]{
                        struct(op, String, 1, 0, 0),
                        struct(op, String, 1, 0, 0, 0)}
                },
                {"1 " + op + " 'hi' " + op + " 10.3;", new TypeCheckStruct[]{
                        struct(op, String, 1, 0, 0),
                        struct(op, String, 1, 0, 0, 0)}
                }
        }));
        EBuiltInType rType = EBuiltInType.String;
        for (String type : types) {
            collection.addAll(Arrays.asList(new Object[][]{
                    {type + "! $a=false; bool   $b=true; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "! $a=false; bool!  $b=false; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "! $a=false; bool?  $b=null; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "! $a=false; bool!? $b=false; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "! $a=false; int   $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "! $a=false; int!  $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "! $a=false; int?  $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "! $a=false; int!? $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "! $a=false; float   $b=1.0; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "! $a=false; float!  $b=1.0; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "! $a=false; float?  $b=1.0; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "! $a=false; float!? $b=1.0; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "! $a=false; string   $b='hi'; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "! $a=false; string!  $b='hi'; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "! $a=false; string?  $b='hi'; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "! $a=false; string!? $b='hi'; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "? $a=null; bool   $b=true; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "? $a=null; bool!  $b=false; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "? $a=null; bool?  $b=null; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "? $a=null; bool!? $b=false; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "? $a=null; int   $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "? $a=null; int!  $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "? $a=null; int?  $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "? $a=null; int!? $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "? $a=null; float   $b=1.0; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "? $a=null; float!  $b=1.0; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "? $a=null; float?  $b=1.0; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "? $a=null; float!? $b=1.0; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "! $a=false; string   $b='hi'; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "! $a=false; string!  $b='hi'; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "! $a=false; string?  $b='hi'; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "! $a=false; string!? $b='hi'; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "!? $a=null; bool   $b=true; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "!? $a=null; bool!  $b=false; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "!? $a=null; bool?  $b=null; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "!? $a=null; bool!? $b=false; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "!? $a=null; int   $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "!? $a=null; int!  $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "!? $a=null; int?  $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "!? $a=null; int!? $b=1; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "!? $a=null; float   $b=1.0; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "!? $a=null; float!  $b=1.0; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "!? $a=null; float?  $b=1.0; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "!? $a=null; float!? $b=1.0; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "! $a=false; string   $b='hi'; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "! $a=false; string!  $b='hi'; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "! $a=false; string?  $b='hi'; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
                    {type + "! $a=false; string!? $b='hi'; $a " + op + " $b;", typeStruct(op, rType, 1, 2, 0)},
            }));
        }

        return collection;

    }
}
