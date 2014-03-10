/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.typecheck;

import ch.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.AssignHelper;
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

        AssignHelper.addAssignments(collection, false);

        addCompoundAssignment();

        collection.addAll(Arrays.asList(new Object[][]{
                //see TSPHP-433
                {"bool? $b; int $i = $b = true;", new TypeCheckStruct[]{
                        struct("$i", Int, 1, 1, 1),
                        struct("=", Bool, 1, 1, 1, 0),
                        struct("$b", BoolNullable, 1, 1, 1, 0, 0),
                        struct("true", Bool, 1, 1, 1, 0, 1)
                }},
                {"float $b; string $c; int $i = $b = $c = false;", new TypeCheckStruct[]{
                        struct("$i", Int, 1, 2, 1),
                        struct("=", Bool, 1, 2, 1, 0),
                        struct("$b", Float, 1, 2, 1, 0, 0),
                        struct("=", Bool, 1, 2, 1, 0, 1),
                        struct("$c", String, 1, 2, 1, 0, 1, 0),
                        struct("false", Bool, 1, 2, 1, 0, 1, 1)
                }}
        }));

        return collection;
    }

    private static void addCompoundAssignment() {

        String[][] declarations = new String[][]{
                {"int     $a=0;    ", "class A{int $a;}     A $a = new A();", "class A{static int $a;} "},
                {"int?    $a=null; ", "class A{ int? $a;}   A $a = new A();", "class A{static int? $a;} "},
                {"float   $a=0.0;  ", "class A{float $a;}   A $a = new A();", "class A{static float $a;} "},
                {"float?  $a=null; ", "class A{float? $a;}  A $a = new A();", "class A{static float? $a;} "},
                {"string  $a='';   ", "class A{string $a;}  A $a = new A();", "class A{static string $a;} "},
                {"string? $a=null; ", "class A{string? $a;} A $a = new A();", "class A{static string? $a;} "}
        };
        String[] access = new String[]{"$a ", "$a->a ", "A::$a "};
        String[] allAssignOperators = new String[]{"+=", "-=", "/=", "*=", "&=", "|=", "^=", "%=", "<<=", ">>="};
        TypeCheckStruct[][][] structs = new TypeCheckStruct[][][]{
                {typeStruct("=", Int, 1, 1, 0), typeStruct("=", Int, 1, 2, 0), typeStruct("=", Int, 1, 1, 0)},
                {typeStruct("=", Int, 1, 2, 0), typeStruct("=", Int, 1, 3, 0), typeStruct("=", Int, 1, 2, 0)},
                {typeStruct("=", Float, 1, 1, 0), typeStruct("=", Float, 1, 2, 0), typeStruct("=", Float, 1, 1, 0)},
                {typeStruct("=", Float, 1, 2, 0), typeStruct("=", Float, 1, 3, 0), typeStruct("=", Float, 1, 2, 0)},
                {typeStruct("=", String, 1, 1, 0), typeStruct("=", String, 1, 2, 0), typeStruct("=", String, 1, 1, 0)},
                {typeStruct("=", String, 1, 2, 0), typeStruct("=", String, 1, 3, 0), typeStruct("=", String, 1, 2, 0)}
        };
        TypeCheckStruct[] struct1;
        TypeCheckStruct[] struct2;
        String decl;
        for (int i = 0; i < 3; ++i) {
            for (String operator : allAssignOperators) {
                decl = declarations[0][i];
                struct1 = structs[0][i];
                struct2 = structs[1][i];
                collection.addAll(Arrays.asList(new Object[][]{
                        {decl + access[i] + operator + " true;", struct1},
                        {decl + access[i] + operator + " false;", struct1},
                        {decl + "bool? $b=false; " + access[i] + operator + " $b;", struct2},
                        {decl + access[i] + operator + " 1;", struct1},
                        {decl + "int? $b=false; " + access[i] + operator + " $b;", struct2}
                }));
                decl = declarations[1][i];
                collection.addAll(Arrays.asList(new Object[][]{
                        {decl + access[i] + operator + " true;", struct1},
                        {decl + access[i] + operator + " false;", struct1},
                        {decl + "bool? $b=false; " + access[i] + operator + " $b;", struct2},
                        {decl + access[i] + operator + " 1;", struct1},
                        {decl + "int? $b=false;" + access[i] + operator + " $b;", struct2}
                }));
            }

            String[] floatCompatibleAssignOperators = new String[]{"+=", "-=", "/=", "*=", "%="};
            for (String operator : floatCompatibleAssignOperators) {
                decl = declarations[2][i];
                struct1 = structs[2][i];
                struct2 = structs[3][i];
                collection.addAll(Arrays.asList(new Object[][]{
                        {decl + access[i] + operator + " true;", struct1},
                        {decl + access[i] + operator + " false;", struct1},
                        {decl + "bool? $b=false;" + access[i] + operator + " $b;", struct2},
                        {decl + access[i] + operator + " 1;", struct1},
                        {decl + "int? $b=null;" + access[i] + operator + " $b;", struct2},
                        {decl + access[i] + operator + " 1.0;", struct1},
                        {decl + "float? $b=null;" + access[i] + operator + " $b;", struct2},
                }));
                decl = declarations[3][i];
                collection.addAll(Arrays.asList(new Object[][]{
                        {decl + access[i] + operator + " true;", struct1},
                        {decl + access[i] + operator + " false;", struct1},
                        {decl + "bool? $b=false;" + access[i] + operator + " $b;", struct2},
                        {decl + access[i] + operator + " 1;", struct1},
                        {decl + "int? $b=null;" + access[i] + operator + " $b;", struct2},
                        {decl + access[i] + operator + " 1.0;", struct1},
                        {decl + "float? $b=null;" + access[i] + operator + " $b;", struct2}
                }));
            }
            decl = declarations[4][i];
            struct1 = structs[4][i];
            struct2 = structs[5][i];
            collection.addAll(Arrays.asList(new Object[][]{
                    {decl + access[i] + ".= true;", struct1},
                    {decl + access[i] + ".= false;", struct1},
                    {decl + access[i] + ".= 1;", struct1},
                    {decl + access[i] + ".= 1.0;", struct1},
                    {decl + access[i] + ".= 'hello';", struct1},
                    {decl + " string? $b=null; " + access[i] + ".= $b;", struct2},
            }));
            decl = declarations[5][i];
            collection.addAll(Arrays.asList(new Object[][]{
                    {decl + access[i] + ".= false;", struct1},
                    {decl + access[i] + ".= false;", struct1},
                    {decl + access[i] + ".= 1;", struct1},
                    {decl + access[i] + ".= 1.0;", struct1},
                    {decl + access[i] + ".= 'hello';", struct1},
                    {decl + "string? $b='';" + access[i] + ".= $b;", struct2}
            }));
        }
    }
}
