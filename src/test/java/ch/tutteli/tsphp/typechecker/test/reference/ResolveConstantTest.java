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

import ch.tutteli.tsphp.typechecker.test.testutils.reference.AReferenceScopeTest;
import ch.tutteli.tsphp.typechecker.test.testutils.reference.ReferenceScopeTestStruct;
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
public class ResolveConstantTest extends AReferenceScopeTest
{

    public ResolveConstantTest(String testString, ReferenceScopeTestStruct[] testStructs) {
        super(testString, testStructs);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                    //conditionals
                    {"const int a=1; a;", structDefault("", 1, 1, 0)},
                    {"const int a=1;{ a;}", structDefault("", 1, 1, 0)},
                    {"const int a=1;if(a==1){}", structDefault("", 1, 1, 0, 0)},
                    {"const int a=1;if(true){ a;}", structDefault("", 1, 1, 1, 0, 0)},
                    {"const int a=1;if(true){}else{ a;}", structDefault("", 1, 1, 2, 0, 0)},
                    {"const int a=1;if(true){ if(true){ a;}}", structDefault("", 1, 1, 1, 0, 1, 0, 0)},
                    {"const int a=1; switch(a){case 1: a;break;}", structDefault("", 1, 1, 0)},
                    {"const int a=1; int $b; switch($b){case 1: a;break;}", structDefault("", 1, 2, 2, 0, 0)},
                    {"const int a=1; int $b; switch($b){case 1:{a;}break;}", structDefault("", 1, 2, 2, 0, 0)},
                    {"const int a=1; int $b; switch($b){default:{a;}break;}", structDefault("", 1, 2, 2, 0, 0)},
                    {"const int a=1; for(int $a=a;;){}", structDefault("", 1, 1, 0, 1, 0)},
                    {"const int a=1; for(;a==1;){}", structDefault("", 1, 1, 1, 0, 0)},
                    {"const int a=1; int $a;for(;;$a+=a){}", structDefault("", 1, 2, 2, 0, 1)},
                    {"const int a=1; for(;;){a;}", structDefault("", 1, 1, 3, 0, 0)},
                    {"const int a=1; foreach([1] as int $v){a;}", structDefault("", 1, 1, 2, 0, 0)},
                    {"const int a=1; while(a==1){}", structDefault("", 1, 1, 0, 0)},
                    {"const int a=1; while(true)a;", structDefault("", 1, 1, 1, 0, 0)},
                    {"const int a=1; while(true){a;}", structDefault("", 1, 1, 1, 0, 0)},
                    {"const int a=1; do ; while(a==1);", structDefault("", 1, 1, 1, 0)},
                    {"const int a=1; do a; while(true);", structDefault("", 1, 1, 0, 0, 0)},
                    {"const int a=1; try{a;}catch(\\Exception $ex){}", structDefault("", 1, 1, 0, 0, 0)},
                    {"const int a=1; try{}catch(\\Exception $ex){a;}", structDefault("", 1, 1, 1, 1, 0, 0)},
                    //in expression (ok a; is also an expression but at the top of the AST)
                    {
                        "const int a=1; !(1+a-a/a*a && a) || a;",
                        new ReferenceScopeTestStruct[]{
                            new ReferenceScopeTestStruct("a#", "\\.\\.", Arrays.asList(new Integer[]{
                                1, 1, 0, 1}), "int", "\\."),
                            new ReferenceScopeTestStruct("a#", "\\.\\.", Arrays.asList(new Integer[]{
                                1, 1, 0, 0, 0, 1}), "int", "\\."),
                            new ReferenceScopeTestStruct("a#", "\\.\\.", Arrays.asList(new Integer[]{
                                1, 1, 0, 0, 0, 0, 0, 1}), "int", "\\."),
                            new ReferenceScopeTestStruct("a#", "\\.\\.", Arrays.asList(new Integer[]{
                                1, 1, 0, 0, 0, 0, 1, 1}), "int", "\\."),
                            new ReferenceScopeTestStruct("a#", "\\.\\.", Arrays.asList(new Integer[]{
                                1, 1, 0, 0, 0, 0, 1, 0, 0}), "int", "\\."),
                            new ReferenceScopeTestStruct("a#", "\\.\\.", Arrays.asList(new Integer[]{
                                1, 1, 0, 0, 0, 0, 1, 0, 1}), "int", "\\.")
                        }
                    },
                    //const are global
                    {"const int a=1; function void foo(){a;}", structDefault("", 1, 1, 4, 0, 0)},
                    {"const int a=1; class a{ private int $a=a;}", structDefault("", 1, 1, 4, 0, 0, 1, 0)},
                    {"const int a=1; class a{ function void foo(){a;}}", structDefault("", 1, 1, 4, 0, 4, 0, 0)},
                    //same namespace
                    {"namespace{const int a=1;} namespace{a;}", structDefault("", 1, 1, 0, 0)},
                    {"namespace a{const int a=1;} namespace a{a;}", struct("", "\\a\\.\\a\\.", 1, 1, 0, 0)},
                    {
                        "namespace b\\c{const int a=1;} namespace b\\c{a;}",
                        struct("", "\\b\\c\\.\\b\\c\\.", 1, 1, 0, 0)
                    },
                    {
                        "namespace d\\e\\f{const int a=1;} namespace d\\e\\f{a;}",
                        struct("", "\\d\\e\\f\\.\\d\\e\\f\\.", 1, 1, 0, 0)
                    },
                    //different namespace absolute
                    {
                        "namespace a{ const int a=1;} namespace x{\\a\\a;}",
                        struct("\\a\\", "\\a\\.\\a\\.", 1, 1, 0, 0)
                    },
                    //different namespace relative - defaut is like absolute
                    {"namespace a{ const int a=1;} namespace{a\\a;}", struct("\\a\\", "\\a\\.\\a\\.", 1, 1, 0, 0)},
                    //different namespace relative
                    {
                        "namespace a\\b{ const int a=1;} namespace a{ b\\a;}",
                        struct("\\a\\b\\", "\\a\\b\\.\\a\\b\\.", 1, 1, 0, 0)
                    },
                    //using an alias
                    {
                        "namespace a\\b{ const int a=1; } namespace x{ use a\\b as b; b\\a;}",
                        struct("\\a\\b\\", "\\a\\b\\.\\a\\b\\.", 1, 1, 1, 0)
                    },
                    //const have a fallback mechanism to default scope
                    {"namespace{ const int a=1;} namespace a{a;}", structDefault("", 1, 1, 0, 0)},
                    {"namespace{ const int a=1;} namespace a\\b{a;}", structDefault("", 1, 1, 0, 0)},
                    {"namespace{ const int a=1;} namespace a\\b\\c{a;}", structDefault("", 1, 1, 0, 0)},
                    {
                        "namespace{ const int a=1;} namespace a{function void foo(){a;}}",
                        structDefault("", 1, 1, 0, 4, 0, 0)
                    },
                    {
                        "namespace{ const int a=1;} namespace a{class a{ private int $a=a;}}",
                        structDefault("", 1, 1, 0, 4, 0, 0, 1, 0)
                    },
                    {
                        "namespace{ const int a=1;} namespace a{class a{ function void foo(){a;}}}",
                        structDefault("", 1, 1, 0, 4, 0, 4, 0, 0)
                    }
                });
    }

    private static ReferenceScopeTestStruct[] structDefault(String prefix, Integer... accessToScope) {
        return struct(prefix, "\\.\\.", accessToScope);
    }

    private static ReferenceScopeTestStruct[] struct(String prefix, String scope, Integer... accessToScope) {
        return new ReferenceScopeTestStruct[]{
                    new ReferenceScopeTestStruct(prefix + "a#", scope, Arrays.asList(accessToScope), "int", "\\.")
                };
    }
}
