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
import java.util.Arrays;
import java.util.Collection;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
@RunWith(Parameterized.class)
public class ResolveConstantTest extends AReferenceTest
{

    public ResolveConstantTest(String testString) {
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
        return Arrays.asList(new Object[][]{
                    //conditionals
                    {"const int a=1; a;"},
                    {"const int a=1;{ a;}"},
                    {"const int a=1;if(a==1){}"},
                    {"const int a=1;if(true){ a;}"},
                    {"const int a=1;if(true){}else{ a;}"},
                    {"const int a=1;if(true){ if(true){ a;}}"},
                    {"const int a=1; int $b; switch($b){case 1: a;break;}"},
                    {"const int a=1; int $b; switch($b){case 1:{a;}break;}"},
                    {"const int a=1; int $b; switch($b){default:{a;}break;}"},
                    {"const int a=1; for(int $a=a;;){}"},
                    {"const int a=1; for(;a==1;){}"},
                    {"const int a=1; int $a;for(;;$a+=a){}"},
                    {"const int a=1; for(;;){a;}"},
                    {"const int a=1; foreach([1] as int $v){a;}"},
                    {"const int a=1; while(a==1){}"},
                    {"const int a=1; while(true)a;"},
                    {"const int a=1; while(true){a;}"},
                    {"const int a=1; do ; while(a==1);"},
                    {"const int a=1; do a; while(true);"},
                    {"const int a=1; try{a;}catch(\\Exception $ex){}"},
                    {"const int a=1; try{}catch(\\Exception $ex){a;}"},
                    //in expression (ok a; is also an expression but at the top of the AST)
                    {"const int a=1; !(1+a-a/a*a && a) || a;"},
                    //const are global
                    {"const int a=1; function void foo(){a;}"},
                    {"const int a=1; class a{ private int $a=a;}"},
                    {"const int a=1; class a{ function void foo(){a;}}"},
                    //same namespace
                    {"namespace{const int a=1;} namespace{a;}"},
                    {"namespace a{const int a=1;} namespace a{a;}"},
                    {"namespace b\\c{const int a=1;} namespace b\\c{a;}"},
                    {"namespace d\\e\\f{const int a=1;} namespace d\\e\\f{a;}"},
                    //const have a fallback mechanism to default scope
                    {"namespace{ const int a=1;} namespace a{a;}"},
                    {"namespace{ const int a=1;} namespace a\\b{a;}"},
                    {"namespace{ const int a=1;} namespace a\\b\\c{a;}"},
                    {"namespace{ const int a=1;} namespace a{function void foo(){a;}}"},
                    {"namespace{ const int a=1;} namespace a{class a{ private int $a=a;}}"},
                    {"namespace{ const int a=1;} namespace a{class a{ function void foo(){a;}}}"},
                });
    }
}
