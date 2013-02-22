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
public class ResolveFunctionCallTest extends AReferenceScopeTest
{

    public ResolveFunctionCallTest(String testString, ReferenceScopeTestStruct[] testStructs) {
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
                        "function void foo(){} foo();",
                        structDefault("", 1, 1, 0, 0)
                    },
                    {
                        "namespace{function void foo(){} foo();}",
                        structDefault("", 1, 1, 0, 0)
                    },
                    {
                        "namespace{function void foo(){}} namespace{ foo();}",
                        structDefault("", 1, 1, 0, 0, 0)
                    },
                    {
                        "namespace{ foo();} namespace{function void foo(){}} ",
                        structDefault("", 0, 1, 0, 0, 0)
                    },
                    {
                        "namespace a{function void foo(){}} namespace a{ foo();}",
                        struct("", "\\a\\.\\a\\.", 1, 1, 0, 0, 0)},
                    {
                        "namespace a{ foo();} namespace a{function void foo(){}} ",
                        struct("", "\\a\\.\\a\\.", 0, 1, 0, 0, 0)},
                    //absolute path
                    {
                        "namespace{function void foo(){}} namespace a{ \\foo();}",
                        structDefault("\\", 1, 1, 0, 0, 0)
                    },
                    {
                        "namespace a\\b{function void foo(){}} namespace x{ \\a\\b\\foo();}",
                        struct("\\a\\b\\", "\\a\\b\\.\\a\\b\\.", 1, 1, 0, 0, 0)
                    },
                    //relative
                    {
                        "namespace a\\b{function void foo(){}} namespace a{ b\\foo();}",
                        struct("\\a\\b\\", "\\a\\b\\.\\a\\b\\.", 1, 1, 0, 0, 0)
                    },
                    {
                        "namespace a\\b{function void foo(){}} namespace { a\\b\\foo();}",
                        struct("\\a\\b\\", "\\a\\b\\.\\a\\b\\.", 1, 1, 0, 0, 0)
                    },
                    //using an alias
                    {
                        "namespace a{function void foo(){}} namespace a\\a\\c{ use a as b; b\\foo();}",
                        struct("\\a\\", "\\a\\.\\a\\.", 1, 1, 1, 0, 0)
                    },
                    {
                        "namespace a{function void foo(){}} namespace a\\a\\c{ use a as b; b\\foo();}",
                        struct("\\a\\", "\\a\\.\\a\\.", 1, 1, 1, 0, 0)
                    },
                    //fallback to global
                    {
                        "namespace{function void foo(){}} namespace a{ foo();}",
                        struct("", "\\.\\.", 1, 1, 0, 0, 0)
                    },
                    {
                        "namespace{function void foo(){}} namespace a\\b{ foo();}",
                        struct("", "\\.\\.", 1, 1, 0, 0, 0)
                    },
                    {
                        "namespace{function void foo(){}} namespace a\\a\\c{ foo();}",
                        struct("", "\\.\\.", 1, 1, 0, 0, 0)
                    }
                });
    }

    private static ReferenceScopeTestStruct[] structDefault(String prefix, Integer... accessToScope) {
        return struct(prefix, "\\.\\.", accessToScope);
    }

    private static ReferenceScopeTestStruct[] struct(String prefix, String scope, Integer... accessToScope) {
        return new ReferenceScopeTestStruct[]{
                    new ReferenceScopeTestStruct(prefix + "foo()", scope, Arrays.asList(accessToScope), "void", "\\.")
                };
    }
}
