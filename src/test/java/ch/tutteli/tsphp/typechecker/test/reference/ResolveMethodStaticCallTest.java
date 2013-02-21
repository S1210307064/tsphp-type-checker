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

import ch.tutteli.tsphp.typechecker.test.testutils.AReferenceStaticScopeTest;
import ch.tutteli.tsphp.typechecker.test.testutils.ScopeTestStruct;
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
public class ResolveMethodStaticCallTest extends AReferenceStaticScopeTest
{

    public ResolveMethodStaticCallTest(String testString, ScopeTestStruct[] testStructs) {
        super(testString, testStructs);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                    {
                        "class a{static function void foo(){}} a::foo();",
                        new ScopeTestStruct[]{
                            callee("a", "\\.\\.", 1, 1, 0),
                            functionDefault(1, 1, 1)
                        }
                    },
                    {
                        "namespace{class a{static function void foo(){}} a::foo();}",
                        new ScopeTestStruct[]{
                            callee("a", "\\.\\.", 1, 1, 0),
                            functionDefault(1, 1, 1)
                        }
                    },
                    {
                        "namespace a{class a{static function void foo(){}} a::foo();}",
                        new ScopeTestStruct[]{
                            callee("a", "\\a\\.\\a\\.", 1, 1, 0),
                            function("\\a\\.\\a\\.", 1, 1, 1)
                        }
                    },
                    {
                        "namespace a{class a{static function void foo(){}}} namespace a{ a::foo();}",
                        new ScopeTestStruct[]{
                            callee("a", "\\a\\.\\a\\.", 1, 1, 0, 0),
                            function("\\a\\.\\a\\.", 1, 1, 0, 1)
                        }
                    },
                    {
                        "namespace{ a::foo();} namespace{class a{static function void foo(){}}}",
                        new ScopeTestStruct[]{
                            callee("a", "\\.\\.", 0, 1, 0, 0),
                            functionDefault(0, 1, 0, 1)
                        }
                    },
                    {
                        "namespace a{class a{static function void foo(){}}} namespace a{ a::foo();}",
                        new ScopeTestStruct[]{
                            callee("a", "\\a\\.\\a\\.", 1, 1, 0, 0),
                            function("\\a\\.\\a\\.", 1, 1, 0, 1)
                        }
                    },
                    {
                        "namespace a{ a::foo();} namespace a{class a{static function void foo(){}}}",
                        new ScopeTestStruct[]{
                            callee("a", "\\a\\.\\a\\.", 0, 1, 0, 0),
                            function("\\a\\.\\a\\.", 0, 1, 0, 1)
                        }
                    },
                    //absolute path
                    {
                        "namespace{class a{static function void foo(){}}} namespace a{ \\a::foo();}",
                        new ScopeTestStruct[]{
                            callee("\\a", "\\.\\.", 1, 1, 0, 0),
                            functionDefault(1, 1, 0, 1)
                        }
                    },
                    {
                        "namespace a\\b{class a{static function void foo(){}}} namespace x{ \\a\\b\\a::foo();}",
                        new ScopeTestStruct[]{
                            callee("\\a\\b\\a", "\\a\\b\\.\\a\\b\\.", 1, 1, 0, 0),
                            function("\\a\\b\\.\\a\\b\\.", 1, 1, 0, 1)
                        }
                    },
                    //relative
                    {
                        "namespace a\\b{class a{static function void foo(){}}} namespace a{ b\\a::foo();}",
                        new ScopeTestStruct[]{
                            callee("\\a\\b\\a", "\\a\\b\\.\\a\\b\\.", 1, 1, 0, 0),
                            function("\\a\\b\\.\\a\\b\\.", 1, 1, 0, 1)
                        }
                    },
                    {
                        "namespace a\\b{class a{static function void foo(){}}} namespace { a\\b\\a::foo();}",
                        new ScopeTestStruct[]{
                            callee("\\a\\b\\a", "\\a\\b\\.\\a\\b\\.", 1, 1, 0, 0),
                            function("\\a\\b\\.\\a\\b\\.", 1, 1, 0, 1)
                        }
                    },
                    //using an alias
                    {
                        "namespace a{class a{static function void foo(){}}} "
                        + "namespace a\\a\\c{ use a as b; b\\a::foo();}",
                        new ScopeTestStruct[]{
                            callee("\\a\\a", "\\a\\.\\a\\.", 1, 1, 1, 0),
                            function("\\a\\.\\a\\.", 1, 1, 1, 1)
                        }
                    },
                    {
                        "namespace a{class a{static function void foo(){}}} "
                        + "namespace a\\a\\c{ use a\\a as b; b::foo();}",
                        new ScopeTestStruct[]{
                            callee("b", "\\a\\.\\a\\.", 1, 1, 1, 0),
                            function("\\a\\.\\a\\.", 1, 1, 1, 1)
                        }
                    },});
    }

    private static ScopeTestStruct callee(String callee, String scope, Integer... accessToScope) {
        return new ScopeTestStruct(callee, scope, Arrays.asList(accessToScope));
    }

    private static ScopeTestStruct functionDefault(Integer... accessToScope) {
        return function("\\.\\.", accessToScope);
    }

    private static ScopeTestStruct function(String scope, Integer... accessToScope) {
        return new ScopeTestStruct("foo()", scope + "a.", Arrays.asList(accessToScope));
    }
}
