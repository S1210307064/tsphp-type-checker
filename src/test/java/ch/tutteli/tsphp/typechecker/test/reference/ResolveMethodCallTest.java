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

import ch.tutteli.tsphp.typechecker.test.testutils.AReferenceScopeTest;
import ch.tutteli.tsphp.typechecker.test.testutils.ReferenceScopeTestStruct;
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
public class ResolveMethodCallTest extends AReferenceScopeTest
{

    public ResolveMethodCallTest(String testString, ReferenceScopeTestStruct[] testStructs) {
        super(testString, testStructs);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        String dfault = "\\.\\.";
        return Arrays.asList(new Object[][]{
                    {
                        "class a{function void foo(){}} a $a; $a->foo();",
                        new ReferenceScopeTestStruct[]{
                            callee("a", dfault, "$a", dfault, 1, 2, 0),
                            functionDefault(1, 2, 1)
                        }
                    },
                    {
                        "namespace{class a{function void foo(){}} a $a; $a->foo();}",
                        new ReferenceScopeTestStruct[]{
                            callee("a", dfault, "$a", dfault, 1, 2, 0),
                            functionDefault(1, 2, 1)
                        }
                    },
                    {
                        "namespace a{class a{function void foo(){}} a $a; $a->foo();}",
                        new ReferenceScopeTestStruct[]{
                            callee("a", "\\a\\.\\a\\.", "$a", "\\a\\.\\a\\.", 1, 2, 0),
                            function("\\a\\.\\a\\.", 1, 2, 1)
                        }
                    },
                    {
                        "namespace {class a{function void foo(){}}} namespace { a $a; $a->foo();}",
                        new ReferenceScopeTestStruct[]{
                            callee("a", dfault, "$a", dfault, 1, 1, 1, 0),
                            functionDefault(1, 1, 1, 1)
                        }
                    },
                    {
                        "namespace{ a $a; $a->foo();} namespace{class a{function void foo(){}}}",
                        new ReferenceScopeTestStruct[]{
                            callee("a", dfault, "$a", dfault, 0, 1, 1, 0),
                            functionDefault(0, 1, 1, 1)
                        }
                    },
                    {
                        "namespace a{class a{function void foo(){}}} namespace a{ a $a; $a->foo();}",
                        new ReferenceScopeTestStruct[]{
                            callee("a", "\\a\\.\\a\\.", "$a", "\\a\\.\\a\\.", 1, 1, 1, 0),
                            function("\\a\\.\\a\\.", 1, 1, 1, 1)
                        }
                    },
                    {
                        "namespace a{ a $a; $a->foo();} namespace a{class a{function void foo(){}}}",
                        new ReferenceScopeTestStruct[]{
                            callee("a", "\\a\\.\\a\\.", "$a", "\\a\\.\\a\\.", 0, 1, 1, 0),
                            function("\\a\\.\\a\\.", 0, 1, 1, 1)
                        }
                    },
                    //absolute path
                    {
                        "namespace{class a{function void foo(){}}} namespace a{ \\a $a; $a->foo();}",
                        new ReferenceScopeTestStruct[]{
                            callee("a", dfault, "$a", "\\a\\.\\a\\.", 1, 1, 1, 0),
                            functionDefault(1, 1, 1, 1)
                        }
                    },
                    {
                        "namespace a\\b{class a{function void foo(){}}} namespace x{ \\a\\b\\a $a; $a->foo();}",
                        new ReferenceScopeTestStruct[]{
                            callee("a", "\\a\\b\\.\\a\\b\\.", "$a", "\\x\\.\\x\\.", 1, 1, 1, 0),
                            function("\\a\\b\\.\\a\\b\\.", 1, 1, 1, 1)
                        }
                    },
                    //relative
                    {
                        "namespace a\\b{class a{function void foo(){}}} namespace a{ b\\a $a; $a->foo();}",
                        new ReferenceScopeTestStruct[]{
                            callee("a", "\\a\\b\\.\\a\\b\\.", "$a", "\\a\\.\\a\\.", 1, 1, 1, 0),
                            function("\\a\\b\\.\\a\\b\\.", 1, 1, 1, 1)
                        }
                    },
                    {
                        "namespace a\\b{class a{function void foo(){}}} namespace { a\\b\\a $a; $a->foo();}",
                        new ReferenceScopeTestStruct[]{
                            callee("a", "\\a\\b\\.\\a\\b\\.", "$a", dfault, 1, 1, 1, 0),
                            function("\\a\\b\\.\\a\\b\\.", 1, 1, 1, 1)
                        }
                    },
                    //using an alias
                    {
                        "namespace a{class a{function void foo(){}}} "
                        + "namespace a\\a\\c{ use a as b; b\\a $a; $a->foo();}",
                        new ReferenceScopeTestStruct[]{
                            callee("a","\\a\\.\\a\\.","$a", "\\a\\a\\c\\.\\a\\a\\c\\.", 1, 1, 2, 0),
                            function("\\a\\.\\a\\.", 1, 1, 2, 1)
                        }
                    },
                    {
                        "namespace a{class a{function void foo(){}}} "
                        + "namespace a\\a\\c{ use a\\a as b; b $a; $a->foo();}",
                        new ReferenceScopeTestStruct[]{
                            callee("a","\\a\\.\\a\\.","$a", "\\a\\a\\c\\.\\a\\a\\c\\.", 1, 1, 2, 0),
                            function("\\a\\.\\a\\.", 1, 1, 2, 1)
                        }
                    },});
    }

    private static ReferenceScopeTestStruct callee(String type, String typeScope,
            String callee, String scope, Integer... accessToScope) {
        return new ReferenceScopeTestStruct(callee, scope, Arrays.asList(accessToScope), type, typeScope);
    }

    private static ReferenceScopeTestStruct functionDefault(Integer... accessToScope) {
        return function("\\.\\.", accessToScope);
    }

    private static ReferenceScopeTestStruct function(String scope, Integer... accessToScope) {
        return new ReferenceScopeTestStruct("foo()", scope + "a.", Arrays.asList(accessToScope), "void", "\\.");
    }
}
