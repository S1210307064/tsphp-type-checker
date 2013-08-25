/*
 * Copyright 2013 Robert Stoll <rstoll@tutteli.ch>
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

import ch.tutteli.tsphp.typechecker.test.testutils.reference.AReferenceAstTest;
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
public class ResolveNewTest extends AReferenceAstTest
{

    public ResolveNewTest(String testString, ScopeTestStruct[] testStructs) {
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
                        "class a{} a $a = new a();",
                        new ScopeTestStruct[]{
                            instanceOf("a", "\\.\\.", 1, 1, 1, 0, 0)
                        }
                    },
                    {
                        "namespace b{class a{} a $a = new a();}",
                        new ScopeTestStruct[]{
                            instanceOf("a", "\\b\\.\\b\\.",  1, 1, 1, 0, 0)
                        }
                    },
                    {
                        "class a{} use a as b; b $a = new b();",
                        new ScopeTestStruct[]{
                            instanceOf("b", "\\.\\.",  1, 2, 1, 0, 0)
                        }
                    },
                    {
                        "namespace b{class a{}} namespace x{ use b\\a as b;  b $a = new b();}",
                        new ScopeTestStruct[]{
                            instanceOf("b", "\\b\\.\\b\\.", 1, 1, 1, 1, 0, 0)
                        }
                    }
                });
    }

    public static ScopeTestStruct instanceOf(String callee, String scope, Integer... accessToScope) {
        return new ScopeTestStruct(callee, scope, Arrays.asList(accessToScope));
    }
}
