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
import ch.tutteli.tsphp.typechecker.symbols.ModifierHelper;
import ch.tutteli.tsphp.typechecker.test.testutils.ATypeCheckerDefinitionSymbolTest;
import ch.tutteli.tsphp.typechecker.test.testutils.IAdder;
import ch.tutteli.tsphp.typechecker.test.testutils.ParameterListHelper;
import ch.tutteli.tsphp.typechecker.test.testutils.TypeHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
@RunWith(Parameterized.class)
public class InterfaceMethodTest extends ATypeCheckerDefinitionSymbolTest
{

    private static String prefix = "namespace a{ interface b{";
    private static String appendix = "}}";
    private static String prefixExpected = "\\a\\.\\a\\.b|" + TSPHPTypeCheckerDefinition.Abstract + " ";
    private static List<Object[]> collection;

    public InterfaceMethodTest(String testString, String expectedResult) {
        super(testString, expectedResult);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        collection = new ArrayList<>();

        final String methodModifier = ModifierHelper.getModifiers(new TreeSet<>(Arrays.asList(new Integer[]{
                    TSPHPTypeCheckerDefinition.Public,
                    TSPHPTypeCheckerDefinition.Abstract
                })));

        TypeHelper.getAllTypesInclModifier(new IAdder()
        {
            @Override
            public void add(String type, String typeExpected, SortedSet modifiers) {
                String typeModifiers = ModifierHelper.getModifiers(modifiers);
                collection.add(new Object[]{
                            prefix + "function " + type + " get();" + appendix,
                            prefixExpected + "\\a\\.\\a\\.b." + typeExpected
                            + " \\a\\.\\a\\.b.get()" + methodModifier + typeModifiers
                        });
            }
        });

        collection.add(new Object[]{
                    prefix + "function void foo();" + appendix,
                    prefixExpected + "\\a\\.\\a\\.b.void "
                    + "\\a\\.\\a\\.b.foo()" + methodModifier
                });
        collection.add(new Object[]{
                    prefix + "public function void foo();" + appendix,
                    prefixExpected + "\\a\\.\\a\\.b.void "
                    + "\\a\\.\\a\\.b.foo()" + methodModifier
                });

        collection.addAll(ParameterListHelper.getTestStrings(
                prefix + "function void foo(", ");" + appendix,
                prefixExpected + "\\a\\.\\a\\.b.void \\a\\.\\a\\.b.foo()" + methodModifier + " ",
                "\\a\\.\\a\\.b.foo().", true));
        return collection;
    }
}
