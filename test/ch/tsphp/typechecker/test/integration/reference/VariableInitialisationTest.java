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
public class VariableInitialisationTest extends AVerifyTimesReferenceTest
{

    private static List<Object[]> collection;

    public VariableInitialisationTest(String testString, int howManyTimes) {
        super(testString, howManyTimes);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Override
    protected void verifyTimes() {
        verify(referencePhaseController, times(howManyTimes)).checkVariableIsInitialised(any(ITSPHPAst.class));
    }


    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        collection = new ArrayList<>();

        //global
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
                {"namespace{int $a;} namespace{$a=1;} namespace{$a;}", 2},
                {"namespace a{int $a;} namespace a{$a=1;} namespace a{$a;}", 2},
                {"namespace b\\c{int $a;} namespace b\\c{$a=1;} namespace b\\c{$a;}", 2},
                {"namespace d\\e\\f{int $a;} namespace d\\e\\f{$a=1;} namespace d\\e\\f{$a;}", 2}
        }));

        return collection;
    }

    private static void addVariations(String prefix, String appendix) {
        collection.addAll(Arrays.asList(new Object[][]{
                {prefix + "int $a; $a=1; $a;" + appendix, 2},
                {prefix + "int $a; $a=1; $a=1; $a=1;" + appendix, 3},
                {prefix + "int $a; $a=1; { $a; $a;}" + appendix, 3},
                {prefix + "int $a; $a=1; if($a==1){}" + appendix, 2},
                {prefix + "int $a; $a=1; if(true){ $a=2;}" + appendix, 2},
                {prefix + "int $a; $a=1; if(true){}else{ $a=2;}" + appendix, 2},
                {prefix + "int $a; $a=1; if(true){ if(true){ $a=2;}}" + appendix, 2},
                {prefix + "int $a; $a=1; int $b=1; switch($a == $b){case 1: $a;break;}" + appendix, 4 /*$b as well*/},
                {prefix + "int $a; $a=1; int $b=2; switch($b){case 1: $a;break;}" + appendix, 3 /*$b as well*/},
                {prefix + "int $a; $a=1; int $b=3; switch($b){case 1:{$a;}break;}" + appendix, 3 /*$b as well*/},
                {prefix + "int $a; $a=1; int $b=4; switch($b){default:{$a;}break;}" + appendix, 3 /*$b as well*/},
                {prefix + "int $a; $a=1; for($a;;){}" + appendix, 2},
                {prefix + "int $a; $a=1; for(;$a==1;){}" + appendix, 2},
                {prefix + "int $a; $a=1; for(;;++$a){}" + appendix, 2},
                {prefix + "int $a; $a=1; for(;;){$a;}" + appendix, 2},
                {prefix + "for(int $a=0; $a < 10; ){}" + appendix, 1},
                {prefix + "for(int $a=0; ; ++$a){}" + appendix, 1},
                {prefix + "for(int $a=0; ;){$a;}" + appendix, 1},
                {prefix + "for(int $a;$a=0,$a<10;){}" + appendix, 2},
                {prefix + "for(int $a;$a=0,true; ++$a){}" + appendix, 2},
                {prefix + "for(int $a;$a=0,true; ){$a+1;}" + appendix, 2},
                {prefix + "int $a; $a=2; foreach([1] as int $v){$a-=1;}" + appendix, 3 /* $a -= 1 -> $a = $a - 1*/},
                {prefix + "int $a; $a=2; while($a==1){}" + appendix, 2},
                {prefix + "int $a; $a=2; while(true)$a-1;" + appendix, 2},
                {prefix + "int $a; $a=2; while(true){$a/1;}" + appendix, 2},
                {prefix + "int $a; $a=2; do ; while($a==1);" + appendix, 2},
                {prefix + "int $a; $a=2; do $a; while(true);" + appendix, 2},
                {prefix + "int $a; $a=2; try{$a*2;}catch(\\Exception $ex){}" + appendix, 2},
                {prefix + "int $a; $a=2; try{}catch(\\Exception $ex){$a+=1;}" + appendix, 3 /* $a+=1 -> $a = $a + 1 */},
                //in expression (ok $a; is also an expression but at the top of the AST)
                {prefix + "int $a; $a=1;  !(1+$a-$a/$a*$a && $a) || $a;" + appendix, 7},
                //definition in for header is not in a conditional scope and thus accessible from outer scope
                {prefix + "for(int $a=0;;){} $a;" + appendix, 1},
                //definition in an catch header is not an conditional scope and thus accessible from outer scope
                {prefix + "try{}catch(\\Exception $e){} $e;" + appendix, 1},
                //do while does not create a conditional scope
                {prefix + "do{ int $a; $a=1;} while(true); $a;" + appendix, 2},
                {prefix + "do{ int $a; $a=1; if(true){$a;} }while(true); $a;" + appendix, 3},
                //implicit initialisations
                {prefix + "try{}catch(\\Exception $e){ $e;}" + appendix, 1},
                {prefix + "foreach([1] as int $v){$v;}" + appendix, 1},
                {prefix + "foreach([1] as string $k => int $v){$k; $v;}" + appendix, 2}
        }));
    }
}
