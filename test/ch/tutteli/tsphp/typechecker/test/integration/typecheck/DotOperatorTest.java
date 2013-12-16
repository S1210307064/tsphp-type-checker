package ch.tutteli.tsphp.typechecker.test.integration.typecheck;

import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.TypeCheckStruct;
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

        collection.addAll(Arrays.asList(new Object[][]{
                {"true . false;", new TypeCheckStruct[]{struct(".", String, 1, 0, 0)}},
                {"bool    $a=false;  bool?   $b=null;  $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"bool?   $a=false;  bool    $b=false; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"bool?   $a=null;   bool?   $b=null;  $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                //
                {"true . 1;", new TypeCheckStruct[]{struct(".", String, 1, 0, 0)}},
                {"1 . true;", new TypeCheckStruct[]{struct(".", String, 1, 0, 0)}},
                {"int     $a=1;      bool?   $b=false; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"bool?   $a=null;   int     $b=0;     $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"1 . 1;", new TypeCheckStruct[]{struct(".", String, 1, 0, 0)}},
                //
                {"int?    $a=1;      bool    $b=true;  $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"bool    $a=false;  int?    $b=0;     $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"int?    $a=1;      bool?   $b=false; $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"bool?   $a=null;   int?    $b=0;     $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"int     $a=1;      int?    $b=null;  $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"int?    $a=null;   int     $b=1;     $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"int?    $a=5;      int?    $b=null;  $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                //
                {"true . 1.0;", new TypeCheckStruct[]{struct(".", String, 1, 0, 0)}},
                {"1.0 . true;", new TypeCheckStruct[]{struct(".", String, 1, 0, 0)}},
                {"float   $a=0.0;   bool?   $b=null;   $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"bool?   $a=null;  float   $b=45.4;   $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"1 . 1.0;", new TypeCheckStruct[]{struct(".", String, 1, 0, 0)}},
                {"1.0 . 1;", new TypeCheckStruct[]{struct(".", String, 1, 0, 0)}},
                {"float   $a=4.4;   int?    $b=null;   $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"int?    $a=12;    float   $b=9;      $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"1.0 . 1.0;", new TypeCheckStruct[]{struct(".", String, 1, 0, 0)}},
                //
                {"float?  $a=0;     bool    $b=true;      $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2,
                        0)}},
                {"bool    $a=false; float?  $b=null;   $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"float?  $a=0;     bool?   $b=true;   $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"bool?   $a=true;  float?  $b=3;      $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"int     $a=0;     float?  $b=8;      $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"float?  $a=0;     int     $b=9;      $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"int?    $a=0;     float?  $b=null;   $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"float?  $a=0;     int?    $b=1;      $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"float   $a=0;     float?  $b=null;   $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"float?  $a=0;     float   $b=0.0;    $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"float?  $a=0;     float?  $b=false;  $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                //
                {"true . 'hello';", new TypeCheckStruct[]{struct(".", String, 1, 0, 0)}},
                {"'hello' . true;", new TypeCheckStruct[]{struct(".", String, 1, 0, 0)}},
                {"string  $a='';    bool?   $b=true;   $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"bool?   $a=null;  string  $b='a';    $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"1 . 'hello';", new TypeCheckStruct[]{struct(".", String, 1, 0, 0)}},
                {"'hello' . 1;", new TypeCheckStruct[]{struct(".", String, 1, 0, 0)}},
                {"string  $a='b';   int?    $b=1;      $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"int?    $a=1;     string  $b='a';    $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"1.0 . 'hello';", new TypeCheckStruct[]{struct(".", String, 1, 0, 0)}},
                {"'hello' . 1.0;", new TypeCheckStruct[]{struct(".", String, 1, 0, 0)}},
                {"string  $a='2';   float?  $b=0;      $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"float?  $a=6.5;   string  $b='0';    $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"'hello' . 'hello';", new TypeCheckStruct[]{struct(".", String, 1, 0, 0)}},
                //
                {"string? $a=null;  bool $b=false;     $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"bool    $a=false; string? $b='s';    $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"string? $a=null;  string? $b='p';    $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"bool?   $a=null;  string? $b='k';    $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"int     $a=0;     string? $b='e';    $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"string? $a='';    int     $b=1;      $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"int?    $a=0;     string? $b=null;   $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"string? $a='a';   int?    $b=1;      $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"float   $a=5.4;   string? $b='';     $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"string? $a='';    float   $b=7.45;   $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"float?  $a=null;  string? $b=null;   $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"string? $a=null;  float?  $b=null;   $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"string  $a='';    string? $b=null;   $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"string? $a='1';   string  $b='v';    $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}},
                {"string? $a='39';  string? $b='v';    $a . $b;", new TypeCheckStruct[]{struct(".", String, 1, 2, 0)}}
        }));


        return collection;

    }
}
