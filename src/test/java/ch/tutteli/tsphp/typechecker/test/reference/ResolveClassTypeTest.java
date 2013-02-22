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

import ch.tutteli.tsphp.typechecker.test.testutils.reference.AReferenceTest;
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
public class ResolveClassTypeTest extends AReferenceTest
{

    public ResolveClassTypeTest(String testString) {
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
                    //absolute types
                    {
                        "/* 1 */ namespace {class a{}} namespace x{class a{}} namespace x\\a{ class a{}}"
                        + "namespace x\\a\\a{ class a{}} \n"
                        + "namespace { \\a $a; \\x\\a $b; \\x\\a\\a $c; \\x\\a\\a\\a $d;}"
                    },
                    {
                        "/* 2 */ namespace {class a{}} namespace x{class a{}} namespace x\\a{ class a{}}"
                        + "namespace x\\a\\a{ class a{}} \n"
                        + "namespace b{ \\a $a; \\x\\a $b; \\x\\a\\a $c; \\x\\a\\a\\a $d;}"
                    },
                    {
                        "/* 3 */ namespace {class a{}} namespace x{class a{}} namespace x\\a{ class a{}}"
                        + "namespace x\\a\\a{ class a{}}\n"
                        + "namespace c\\c{ \\a $a; \\x\\a $b; \\x\\a\\a $c; \\x\\a\\a\\a $d;}"
                    },
                    {
                        "/* 4 */ namespace {class a{}} namespace x{class a{}} namespace x\\a{ class a{}}"
                        + "namespace x\\a\\a{ class a{}}\n"
                        + "namespace d\\c\\d{ \\a $a; \\x\\a $b; \\x\\a\\a $c; \\x\\a\\a\\a $d;}"
                    },
                    //relative types in default namespace are the same as absolute names
                    {
                        "/* 5 */ namespace {class a{}} namespace x{class a{}} namespace x\\a{ class a{}}"
                        + "namespace x\\a\\a{ class a{}} \n"
                        + "namespace{ a $a; x\\a $b; x\\a\\a $c; x\\a\\a\\a $d;}"
                    },
                    //relative types
                    {
                        "/* 6 */ namespace x{class a{}} namespace x\\x{ class a{}}"
                        + "namespace x\\x\\a{ class a{}} \n"
                        + "namespace x { a $a; x\\a $b; x\\a\\a $c; }"
                    },
                    {
                        "/* 7 */ namespace x\\x{ class a{}} namespace x\\x\\x{ class a{}} namespace x\\x\\x\\a{ class a{}} \n"
                        + "namespace x\\x { a $a; x\\a $b; x\\a\\a $c; }"
                    },
                    //aliases for same namespace
                    {
                        "/* 8 */ z $a; use z as y; class z{} y $b;"
                    },
                    {
                        "/* 8.1 */ namespace {z $a; use z as y; class z{} y $b;}"
                    },
                    {
                        "/* 8.2 */ namespace b; z $a; use b\\z as y; class z{} y $b;"
                    },
                    {
                        "/* 8.3 */ namespace b {z $a; use b\\z as y; class z{} y $b;}"
                    },
                    {
                        "/* 8.4 */ namespace b\\c {z $a; use b\\c\\z as y; class z{} y $b;}"
                    },
                    //aliases for absolute types
                    {
                        "/* 9 */ namespace {class a{}} namespace x{class a{}} namespace x\\a{ class a{}}"
                        + "namespace x\\a\\a{ class a{}} \n"
                        + "namespace { use \\a as b; b $b; use \\x\\a as c; c $c; use \\x\\a\\a as d; d $d;"
                        + "use \\x\\a\\a\\a as e; e $e;}"
                    },
                    {
                        "/* 10 */ namespace {class a{}} namespace x{class a{}} namespace x\\a{ class a{}}"
                        + "namespace x\\a\\a{ class a{}} \n"
                        + "namespace b{ use \\a as b; b $b; use \\x\\a as c; c $c; use \\x\\a\\a as d; d $d;"
                        + "use \\x\\a\\a\\a as e; e $e;}"
                    },
                    {
                        "/* 11 */ namespace {class a{}} namespace x{class a{}} namespace x\\a{ class a{}}"
                        + "namespace x\\a\\a{ class a{}} \n"
                        + "namespace b\\c{ use \\a as b; b $b; use \\x\\a as c; c $c; use \\x\\a\\a as d; d $d;"
                        + "use \\x\\a\\a\\a as e; e $e;}"
                    },
                    {
                        "/* 12 */ namespace {class a{}} namespace x{class a{}} namespace x\\a{ class a{}}"
                        + "namespace x\\a\\a{ class a{}} \n"
                        + "namespace b\\c\\d{ use \\a as b; b $b; use \\x\\a as c; c $c; use \\x\\a\\a as d; d $d;"
                        + "use \\x\\a\\a\\a as e; e $e;}"
                    },
                    //aliases for "relative types" -> PHP treat them also as absolute types
                    {
                        "/* 13 */ namespace {class a{}} namespace x{class a{}} namespace x\\a{ class a{}}"
                        + "namespace x\\a\\a{ class a{}} \n"
                        + "namespace { use a as b; b $b; use x\\a as c; c $c; use x\\a\\a as d; d $d;"
                        + "use x\\a\\a\\a as e; e $e;}"
                    },
                    //aliases for relative types
                    {
                        "/* 14 */ namespace {class a{}} namespace x{class a{}} namespace x\\a{ class a{}}"
                        + "namespace x\\a\\a{ class a{}} \n"
                        + "namespace b { use a as b; b $b; use x\\a as c; c $c; use x\\a\\a as d; d $d;"
                        + "use x\\a\\a\\a as e; e $e;}"
                    },
                    {
                        "/* 15 */ namespace {class a{}} namespace x{class a{}} namespace x\\a{ class a{}}"
                        + "namespace x\\a\\a{ class a{}} \n"
                        + "namespace b\\c { use a as b; b $b; use x\\a as c; c $c; use x\\a\\a as d; d $d;"
                        + "use x\\a\\a\\a as e; e $e;}"
                    },
                    //all mixed together
                    {
                        //def
                        "/* 16 */ namespace x{ class c{}}"
                        + "namespace x\\c{class B{}} "
                        //ref
                        + "namespace x{ \n "
                        + "use x\\c as z; \n"
                        + "z\\B $a; \n"
                        + "z $b; \n"
                        + "}"
                        //z has to be later and in different namespace, otherwise use x\\c as z; is a double definition
                        + "namespace x{class z{}} "
                    }
                });
    }
}
