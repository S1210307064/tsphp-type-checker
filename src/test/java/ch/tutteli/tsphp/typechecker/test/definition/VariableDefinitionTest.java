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

import ch.tutteli.tsphp.typechecker.antlr.TSPHPTypeCheckerDefinition;
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
        String defaultNamespace = global + "default.default.";

        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase("", ";", "", global + "default.", defaultNamespace, null));
        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                "namespace a{", ";}", "", global + "a.", global + "a.a.", null));
        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                "namespace a\\b{", ";}", "", global + "a\\b.", global + "a\\b.a\\b.", null));

        //variable declaration in methods
        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                "class a{ function void foo(){", ";}}",
                global + "default.a{} "
                + defaultNamespace + "a{}.void " + defaultNamespace + "a{}.foo()|" + TSPHPTypeCheckerDefinition.Public + " ",
                defaultNamespace + "a{}.foo().", defaultNamespace + "a{}.foo().", null));

        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                "namespace t; class a{ function void foo(){", ";}}",
                global + "t.a{} " + global + "t.t.a{}.void " + global + "t.t.a{}.foo()|" + TSPHPTypeCheckerDefinition.Public + " ",
                global + "t.t.a{}.foo().", global + "t.t.a{}.foo().", null));

        //variable declaration in functions
        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                "function void foo(){", ";}",
                defaultNamespace + "void " + global + "default.foo() ",
                defaultNamespace + "foo().", defaultNamespace + "foo().", null));

        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                "namespace t; function void foo(){", ";}",
                global + "t.t.void " + global + "t.foo() ", global + "t.t.foo().", global + "t.t.foo().", null));

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
                    condition[0], condition[1], "", global + "default.default.cScope.", defaultNamespace + "cScope.", null));
            collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase("namespace a{" + condition[0],
                    condition[1] + "}", "", global + "a.a.cScope.", global + "a.a.cScope.", null));
        }


        //Different namespaces
        collection.addAll(Arrays.asList(new Object[][]{
                    {
                        "namespace a{int $a=1;} namespace b{float $b=1;}",
                        global + "a.a.int " + global + "a.$a "
                        + global + "b.b.float " + global + "b.$b"
                    },
                    {
                        "namespace{int $d=1;} namespace a{float $a=1;} namespace b{int $b=1;}",
                        global + "default.default.int " + global + "default.$d "
                        + global + "a.a.float " + global + "a.$a "
                        + global + "b.b.int " + global + "b.$b"
                    },
                    {
                        "int $a; bool $b; float $c=1, $d;",
                        global + "default.default.int " + global + "default.$a "
                        + global + "default.default.bool " + global + "default.$b "
                        + global + "default.default.float " + global + "default.$c "
                        + global + "default.default.float " + global + "default.$d"
                    },
                    {
                        "namespace a\\c; int $a; bool $b; float $c=1, $d;",
                        global + "a\\c.a\\c.int " + global + "a\\c.$a "
                        + global + "a\\c.a\\c.bool " + global + "a\\c.$b "
                        + global + "a\\c.a\\c.float " + global + "a\\c.$c "
                        + global + "a\\c.a\\c.float " + global + "a\\c.$d"
                    },
                    {
                        "namespace b{int $a; bool $b; float $e=1.2;} namespace c\\e{ float $c=1, $d;}",
                        global + "b.b.int " + global + "b.$a "
                        + global + "b.b.bool " + global + "b.$b "
                        + global + "b.b.float " + global + "b.$e "
                        + global + "c\\e.c\\e.float " + global + "c\\e.$c "
                        + global + "c\\e.c\\e.float " + global + "c\\e.$d"
                    },
                    {
                        "namespace t\\r; class a{ function void foo(){ int $a=1; bool $b=true,$c=false;}}",
                        global + "t\\r.a{} "
                        + global + "t\\r.t\\r.a{}.void "
                        + global + "t\\r.t\\r.a{}.foo()|" + TSPHPTypeCheckerDefinition.Public + " "
                        + global + "t\\r.t\\r.a{}.foo().int " + global + "t\\r.t\\r.a{}.foo().$a "
                        + global + "t\\r.t\\r.a{}.foo().bool " + global + "t\\r.t\\r.a{}.foo().$b "
                        + global + "t\\r.t\\r.a{}.foo().bool " + global + "t\\r.t\\r.a{}.foo().$c"
                    },
                    {
                        "namespace{ function void foo(){ int $a=1; bool $b=true, $c=false;}}"
                        + "namespace b{ function void bar(){float $d;}}",
                        global + "default.default.void " + global + "default.foo() "
                        + global + "default.default.foo().int " + global + "default.default.foo().$a "
                        + global + "default.default.foo().bool " + global + "default.default.foo().$b "
                        + global + "default.default.foo().bool " + global + "default.default.foo().$c "
                        + global + "b.b.void " + global + "b.bar() "
                        + global + "b.b.bar().float " + global + "b.b.bar().$d"
                    },}));
        return collection;
    }
}
