/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.reference;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.test.integration.testutils.reference.AVerifyTimesReferenceTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class ResolveVariableTest extends AVerifyTimesReferenceTest
{

    private static List<Object[]> collection;

    public ResolveVariableTest(String testString, int howManyTimes) {
        super(testString, howManyTimes);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Override
    protected void verifyTimes() {
        verify(referencePhaseController, times(howManyTimes)).resolveVariable(any(ITSPHPAst.class));
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        collection = new ArrayList<>();

        //global constants
        addVariations("", "");
        addVariations("namespace{", "}");
        addVariations("namespace a;", "");
        addVariations("namespace a{", "}");
        addVariations("namespace a\\b;", "");
        addVariations("namespace a\\b\\z{", "}");

        //functions
        addVariations("function void foo(){", "}");
        addVariations("namespace{function void foo(){", "}}");
        addVariations("namespace a;function void foo(){", "}");
        addVariations("namespace a{function void foo(){", "}}");
        addVariations("namespace a\\b;function void foo(){", "}");
        addVariations("namespace a\\b\\z{function void foo(){", "}}");

        //methods
        addVariations("class a{ function void foo(){", "}}");
        addVariations("namespace{ class a{ function void foo(){", "}}}");
        addVariations("namespace a; class a{ function void foo(){", "}}");
        addVariations("namespace a{ class a { function void foo(){", "}}}");
        addVariations("namespace a\\b; class a{ function void foo(){", "}}");
        addVariations("namespace a\\b\\z{ class a{ function void foo(){", "}}}");

        collection.addAll(Arrays.asList(new Object[][]{
                //same namespace
                {"namespace{int $a;} namespace{$a=1;}", 1},
                {"namespace a{int $a;} namespace a{$a=1;}", 1},
                {"namespace b\\c{int $a;} namespace b\\c{$a=1;}", 1},
                {"namespace d\\e\\f{int $a;} namespace d\\e\\f{$a=1;}", 1}
        }));

        return collection;
    }

    private static void addVariations(String prefix, String appendix) {


        collection.addAll(Arrays.asList(new Object[][]{
                {prefix + "int $a=0;  $a;" + appendix, 1},
                {prefix + "int $a=0; { $a=2;}" + appendix, 1},
                {prefix + "int $a=0; if($a==1){}" + appendix, 1},
                {prefix + "int $a=0; if(true){ $a=2;}" + appendix, 1},
                {prefix + "int $a=0; if(true){}else{ $a=2;}" + appendix, 1},
                {prefix + "int $a=0; if(true){ if(true){ $a=2;}}" + appendix, 1},
                {prefix + "int $a=0;  int $b=0; switch($a = $b){case 1: $a;break;}" + appendix, 3},
                {prefix + "int $a=0;  int $b=0; switch($b){case 1: $a;break;}" + appendix, 2},
                {prefix + "int $a=0;  int $b=0; switch($b){case 1:{$a;}break;}" + appendix, 2},
                {prefix + "int $a=0;  int $b=0; switch($b){default:{$a;}break;}" + appendix, 2},
                {prefix + "int $a=0;  for($a=1;;){}" + appendix, 1},
                {prefix + "int $a=0;  for(;$a==1;){}" + appendix, 1},
                {prefix + "int $a=0;  for(;;++$a){}" + appendix, 1},
                {prefix + "int $a=0;  for(;;){$a=1;}" + appendix, 1},
                {prefix + "for(int $a=0;;){$a=1;}" + appendix, 1},
                {prefix + "foreach([1] as int $v){$v=1;}" + appendix, 1},
                {prefix + "int $a=1;  foreach([1] as int $v){$a=1;}" + appendix, 1},
                {prefix + "int $a=1;  while($a==1){}" + appendix, 1},
                {prefix + "int $a=1;  while(true)$a=1;" + appendix, 1},
                {prefix + "int $a=1;  while(true){$a=1;}" + appendix, 1},
                {prefix + "int $a=1;  do ; while($a==1);" + appendix, 1},
                {prefix + "int $a=1;  do $a; while(true);" + appendix, 1},
                {prefix + "int $a=1;  try{$a=1;}catch(\\Exception $ex){}" + appendix, 1},
                {prefix + "int $a=1;  try{}catch(\\Exception $ex){$a=1;}" + appendix, 1},
                //definition in for header is not in a conditional scope and thus accessible from outer scope
                {prefix + "for(int $a=1;;){} $a;" + appendix, 1},
                //definition in an catch header is not an conditional scope and thus accessible from outer scope
                {prefix + "try{}catch(\\Exception $e){} $e;" + appendix, 1},
                //do while does not create a conditional scope
                {prefix + "do int $a=0; while(true); $a;" + appendix, 1},
                {prefix + "do{ int $a=0; if(true){$a;} }while(true);" + appendix, 1},
                //in expressions
                {prefix + "int $a=1;  !(1+$a-$a/$a*$a && $a) || $a;" + appendix, 6},
                {prefix + "int $a=1; exit($a + $a);" + appendix, 2}
        }));
    }
}
