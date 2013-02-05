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
package ch.tutteli.tsphp.typechecker.test;

import ch.tutteli.tsphp.typechecker.test.utils.ATypeCheckerTest;
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
public class VariableDefinitionTest extends ATypeCheckerTest
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
        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase("", ";", "default"));
        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase("namespace a{", ";}", "a"));
        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase("namespace a\\a{", ";}", "a\\a"));
        collection.addAll(Arrays.asList(new Object[][]{
                    {"namespace a{int $a=1;} namespace b{float $b=1;}", "a.int a.$a b.float b.$b"},
                    {
                        "namespace{int $d=1;} namespace a{float $a=1;} namespace b{int $b=1;}",
                        "default.int default.$d a.float a.$a b.int b.$b"
                    },
                    {
                        "int $a; bool $b; float $c=1, $d;",
                        "default.int default.$a default.bool default.$b "
                        + "default.float default.$c default.float default.$d"
                    },
                    {
                        "namespace a\\c; int $a; bool $b; float $c=1, $d;",
                        "a\\c.int a\\c.$a a\\c.bool a\\c.$b "
                        + "a\\c.float a\\c.$c a\\c.float a\\c.$d"
                    },
                    {
                        "namespace b{int $a; bool $b;} namespace c\\e{ float $c=1, $d;}",
                        "b.int b.$a b.bool b.$b "
                        + "c\\e.float c\\e.$c c\\e.float c\\e.$d"
                    },
                }));
        return collection;
    }
}
