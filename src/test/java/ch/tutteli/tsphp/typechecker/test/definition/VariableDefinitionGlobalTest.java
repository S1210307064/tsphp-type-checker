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

import ch.tutteli.tsphp.typechecker.test.testutils.VariableDeclarationListHelper;
import ch.tutteli.tsphp.typechecker.test.testutils.definition.ADefinitionSymbolTest;
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
public class VariableDefinitionGlobalTest extends ADefinitionSymbolTest
{

    public VariableDefinitionGlobalTest(String testString, String expectedResult) {
        super(testString, expectedResult);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        final List<Object[]> collection = new ArrayList<>();

        final String defaultNamespace = "\\.\\.";
        final String aNamespace = "\\a\\.\\a\\.";

        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase("", ";", "","", defaultNamespace, null));
        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                "namespace a{", ";}", "","", aNamespace, null));
        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                "namespace a\\b{", ";}", "","", "\\a\\b\\.\\a\\b\\.", null));


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
                  }));
        return collection;
    }
}
