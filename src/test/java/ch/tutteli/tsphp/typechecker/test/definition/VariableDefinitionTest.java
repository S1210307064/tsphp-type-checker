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
package ch.tutteli.tsphp.typechecker.test.definition;

import ch.tutteli.tsphp.typechecker.TSPHPTypeCheckerDefinition;
import ch.tutteli.tsphp.typechecker.test.utils.ATypeCheckerDefinitionTest;
import ch.tutteli.tsphp.typechecker.test.utils.VariableDeclarationListHelper;
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
public class VariableDefinitionTest extends ATypeCheckerDefinitionTest
{

    public VariableDefinitionTest(String testString, String expectedResult) {
        super(testString, expectedResult);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        String global = "global.";

        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase("", ";", "", "", null));
        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                "namespace a{", ";}", "", "a", null));
        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                "namespace a\\a{", ";}", "", "a\\a", null));

        //variable declaration in methods
        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                "class a{ function void foo(){", ";}}",
                global + "a{} " + global + "a{}.void " + global + "a{}.foo()|" + TSPHPTypeCheckerDefinition.Public + " ",
                "a{}.foo()", null));

        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                "namespace t; class a{ function void foo(){", ";}}",
                global + "t.a{} " + global + "t.a{}.void " + global + "t.a{}.foo()|" + TSPHPTypeCheckerDefinition.Public + " ",
                "t.a{}.foo()", null));

        //variable declaration in functions
        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                "function void foo(){", ";}",
                global + "void " + global + "foo() ", "foo()", null));

        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                "namespace t; function void foo(){", ";}",
                global + "t.void " + global + "t.foo() ", "t.foo()", null));

        //variable declaration in conditional blocks
        String[][] conditions = new String[][]{
            {"if(true)", ";"},
            {"if(true){", ";}"},
            {"switch($a){case 1:", ";}"},
            {"for(;;)", ";"},
            {"for(;;){", ";}"},
            {"foreach($a as object $v)", ";"},
            {"foreach($a as object $v){", ";}"},
            {"while(true)", ";"},
            {"while(true){", ";}"},
            {"do ", ";while(true);"},
            {"do{ ", ";}while(true);"}
        };
        for (String[] condition : conditions) {
            collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                    condition[0], condition[1], "", "cScope", null));
            collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                    "namespace a{" + condition[0], condition[1] + "}", "", "a.cScope", null));
        }



//        //Different namespaces
//        collection.addAll(Arrays.asList(new Object[][]{
//                    {
//                        "namespace a{int $a=1;} namespace b{float $b=1;}",
//                        global + "a.int " + global + "a.$a "
//                        + global + "b.float " + global + "b.$b"
//                    },
//                    {
//                        "namespace{int $d=1;} namespace a{float $a=1;} namespace b{int $b=1;}",
//                        global + "int " + global + "$d "
//                        + global + "a.float " + global + "a.$a "
//                        + global + "b.int " + global + "b.$b"
//                    },
//                    {
//                        "int $a; bool $b; float $c=1, $d;",
//                        global + "int " + global + "$a "
//                        + global + "bool " + global + "$b "
//                        + global + "float " + global + "$c "
//                        + global + "float " + global + "$d"
//                    },
//                    {
//                        "namespace a\\c; int $a; bool $b; float $c=1, $d;",
//                        global + "a\\c.int " + global + "a\\c.$a "
//                        + global + "a\\c.bool " + global + "a\\c.$b "
//                        + global + "a\\c.float " + global + "a\\c.$c "
//                        + global + "a\\c.float " + global + "a\\c.$d"
//                    },
//                    {
//                        "namespace b{int $a; bool $b; float $e=1.2;} namespace c\\e{ float $c=1, $d;}",
//                        global + "b.int " + global + "b.$a "
//                        + global + "b.bool " + global + "b.$b "
//                        + global + "b.float " + global + "b.$e "
//                        + global + "c\\e.float " + global + "c\\e.$c "
//                        + global + "c\\e.float " + global + "c\\e.$d"
//                    },
//                    {
//                        "namespace t\\r; class a{ function void foo(){ int $a=1; bool $b=true,$c=false;}}",
//                        global + "t\\r.a{} " + global + "t\\r.a{}.void " + global + "t\\r.a{}.foo()|" + TSPHPTypeCheckerDefinition.Public + " "
//                        + global + "t\\r.a{}.foo().int " + global + "t\\r.a{}.foo().$a "
//                        + global + "t\\r.a{}.foo().bool " + global + "t\\r.a{}.foo().$b "
//                        + global + "t\\r.a{}.foo().bool " + global + "t\\r.a{}.foo().$c"
//                    },
//                    {
//                        "namespace{ function void foo(){ int $a=1; bool $b=true, $c=false;}}"
//                        + "namespace b{ function void bar(){float $d;}}",
//                        global + "void " + global + "foo() "
//                        + global + "foo().int " + global + "foo().$a "
//                        + global + "foo().bool " + global + "foo().$b "
//                        + global + "foo().bool " + global + "foo().$c "
//                        + global + "b.void " + global + "b.bar() "
//                        + global + "b.bar().float " + global + "b.bar().$d"
//                    },}));
        return collection;
    }
}
