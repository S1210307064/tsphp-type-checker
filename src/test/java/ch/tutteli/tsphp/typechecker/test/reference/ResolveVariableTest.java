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
package ch.tutteli.tsphp.typechecker.test.reference;

import ch.tutteli.tsphp.typechecker.test.testutils.AReferenceTest;
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
public class ResolveVariableTest extends AReferenceTest
{

    private static List<Object[]> collection;

    public ResolveVariableTest(String testString) {
        super(testString);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Override
    protected void verifyReferences() {
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
                    {"namespace{int $a;} namespace{$a=1;}"},
                    {"namespace a{int $a;} namespace a{$a=1;}"},
                    {"namespace b\\c{int $a;} namespace b\\c{$a=1;}"},
                    {"namespace d\\e\\f{int $a;} namespace d\\e\\f{$a=1;}"}
                }));

        return collection;
    }


    private static void addVariations(String prefix, String appendix) {


        collection.addAll(Arrays.asList(new Object[][]{
                    {prefix + "int $a;  $a;" + appendix},
                    {prefix + "int $a; { $a=2;}" + appendix},
                    {prefix + "int $a; if($a==1){}" + appendix},
                    {prefix + "int $a; if(true){ $a=2;}" + appendix},
                    {prefix + "int $a; if(true){}else{ $a=2;}" + appendix},
                    {prefix + "int $a; if(true){ if(true){ $a=2;}}" + appendix},
                    {prefix + "int $a;  int $b; switch($b){case 1: $a;break;}" + appendix},
                    {prefix + "int $a;  int $b; switch($b){case 1:{$a;}break;}" + appendix},
                    {prefix + "int $a;  int $b; switch($b){default:{$a;}break;}" + appendix},
                    {prefix + "int $a;  for($a=1;;){}" + appendix},
                    {prefix + "int $a;  for(;$a==1;){}" + appendix},
                    {prefix + "int $a;  for(;;++$a){}" + appendix},
                    {prefix + "int $a;  for(;;){$a=1;}" + appendix},
                    {prefix + "for(int $a;;){$a=1;}" + appendix},
                    {prefix + "foreach([1] as int $v){$v=1;}" + appendix},
                    {prefix + "int $a;  foreach([1] as int $v){$a=1;}" + appendix},
                    {prefix + "int $a;  while($a==1){}" + appendix},
                    {prefix + "int $a;  while(true)$a=1;" + appendix},
                    {prefix + "int $a;  while(true){$a=1;}" + appendix},
                    {prefix + "int $a;  do ; while($a==1);" + appendix},
                    {prefix + "int $a;  do $a; while(true);" + appendix},
                    {prefix + "int $a;  try{$a=1;}catch(\\Exception $ex){}" + appendix},
                    {prefix + "int $a;  try{}catch(\\Exception $ex){$a=1;}" + appendix},
                    //in expression (ok $a; is also an expression but at the top of the AST)
                    {prefix + "int $a;  !(1+$a-$a/$a*$a && $a) || $a;" + appendix}
                }));
    }
}