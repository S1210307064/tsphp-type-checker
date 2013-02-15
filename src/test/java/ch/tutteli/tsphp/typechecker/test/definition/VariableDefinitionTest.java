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
import ch.tutteli.tsphp.typechecker.test.testutils.ATypeCheckerDefinitionSymbolTest;
import ch.tutteli.tsphp.typechecker.test.testutils.VariableDeclarationListHelper;
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
public class VariableDefinitionTest extends ATypeCheckerDefinitionSymbolTest
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

        String defaultNamespace = "\\.\\.";

        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase("", ";", "", defaultNamespace, null));
        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                "namespace a{", ";}", "", "\\a\\.\\a\\.", null));
        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                "namespace a\\b{", ";}", "", "\\a\\b\\.\\a\\b\\.", null));

        //variable declaration in methods
        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                "class a{ function void foo(){", ";}}",
                "\\.\\.a "
                + defaultNamespace + "a.void " + defaultNamespace + "a.foo()|" + TSPHPTypeCheckerDefinition.Public + " ",
                defaultNamespace + "a.foo().", null));

        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                "namespace t; class a{ function void foo(){", ";}}",
                "\\t\\.\\t\\.a \\t\\.\\t\\.a.void \\t\\.\\t\\.a.foo()|" + TSPHPTypeCheckerDefinition.Public + " ",
                "\\t\\.\\t\\.a.foo().", null));

        //variable declaration in functions
        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                "function void foo(){", ";}",
                defaultNamespace + "void \\.\\.foo() ",
                defaultNamespace + "foo().", null));

        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                "namespace t; function void foo(){", ";}",
                "\\t\\.\\t\\.void \\t\\.\\t\\.foo() ", "\\t\\.\\t\\.foo().", null));

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
                    condition[0], condition[1], "", defaultNamespace + "cScope.", null));
            collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase("namespace a{" + condition[0],
                    condition[1] + "}", "", "\\a\\.\\a\\.cScope.", null));
        }


        //Different namespaces
        collection.addAll(Arrays.asList(new Object[][]{
                    {
                        "namespace a{int $a=1;} namespace b{float $b=1;}",
                        "\\a\\.\\a\\.int \\a\\.\\a\\.$a "
                        + "\\b\\.\\b\\.float \\b\\.\\b\\.$b"
                    },
                    {
                        "namespace{int $d=1;} namespace a{float $a=1;} namespace b{int $b=1;}",
                        "\\.\\.int \\.\\.$d "
                        + "\\a\\.\\a\\.float \\a\\.\\a\\.$a "
                        + "\\b\\.\\b\\.int \\b\\.\\b\\.$b"
                    },
                    {
                        "int $a; bool $b; float $c=1, $d;",
                        "\\.\\.int \\.\\.$a "
                        + "\\.\\.bool \\.\\.$b "
                        + "\\.\\.float \\.\\.$c "
                        + "\\.\\.float \\.\\.$d"
                    },
                    {
                        "namespace a\\c; int $a; bool $b; float $c=1, $d;",
                        "\\a\\c\\.\\a\\c\\.int \\a\\c\\.\\a\\c\\.$a "
                        + "\\a\\c\\.\\a\\c\\.bool \\a\\c\\.\\a\\c\\.$b "
                        + "\\a\\c\\.\\a\\c\\.float \\a\\c\\.\\a\\c\\.$c "
                        + "\\a\\c\\.\\a\\c\\.float \\a\\c\\.\\a\\c\\.$d"
                    },
                    {
                        "namespace b{int $a; bool $b; float $e=1.2;} namespace c\\e{ float $c=1, $d;}",
                        "\\b\\.\\b\\.int \\b\\.\\b\\.$a "
                        + "\\b\\.\\b\\.bool \\b\\.\\b\\.$b "
                        + "\\b\\.\\b\\.float \\b\\.\\b\\.$e "
                        + "\\c\\e\\.\\c\\e\\.float \\c\\e\\.\\c\\e\\.$c "
                        + "\\c\\e\\.\\c\\e\\.float \\c\\e\\.\\c\\e\\.$d"
                    },
                    {
                        "namespace t\\r; class a{ function void foo(){ int $a=1; bool $b=true,$c=false;}}",
                        "\\t\\r\\.\\t\\r\\.a "
                        + "\\t\\r\\.\\t\\r\\.a.void "
                        + "\\t\\r\\.\\t\\r\\.a.foo()|" + TSPHPTypeCheckerDefinition.Public + " "
                        + "\\t\\r\\.\\t\\r\\.a.foo().int \\t\\r\\.\\t\\r\\.a.foo().$a "
                        + "\\t\\r\\.\\t\\r\\.a.foo().bool \\t\\r\\.\\t\\r\\.a.foo().$b "
                        + "\\t\\r\\.\\t\\r\\.a.foo().bool \\t\\r\\.\\t\\r\\.a.foo().$c"
                    },
                    {
                        "namespace{ function void foo(){ int $a=1; bool $b=true, $c=false;}}"
                        + "namespace b{ function void bar(){float $d;}}",
                        "\\.\\.void \\.\\.foo() "
                        + "\\.\\.foo().int \\.\\.foo().$a "
                        + "\\.\\.foo().bool \\.\\.foo().$b "
                        + "\\.\\.foo().bool \\.\\.foo().$c "
                        + "\\b\\.\\b\\.void \\b\\.\\b\\.bar() "
                        + "\\b\\.\\b\\.bar().float \\b\\.\\b\\.bar().$d"
                    },}));
        return collection;
    }
}
