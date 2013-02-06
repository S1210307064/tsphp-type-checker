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

import ch.tutteli.tsphp.typechecker.TSPHPTypeCheckerDefinition;
import ch.tutteli.tsphp.typechecker.symbols.ModifierHelper;
import ch.tutteli.tsphp.typechecker.test.utils.ATypeCheckerTest;
import ch.tutteli.tsphp.typechecker.test.utils.IAdder;
import ch.tutteli.tsphp.typechecker.test.utils.TypeHelper;
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
public class FunctionTest extends ATypeCheckerTest
{

    public FunctionTest(String testString, String expectedResult) {
        super(testString, expectedResult);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        final List<Object[]> collection = new ArrayList<>();

        TypeHelper.getAllTypesInclModifier(new IAdder()
        {
            @Override
            public void add(String type, String typeExpected, SortedSet modifiers) {
                String typeModifiers = ModifierHelper.getModifiers(modifiers);
                collection.add(new Object[]{
                            "function " + type + " get(){}",
                            "global." + typeExpected + " global.get()" + typeModifiers
                        });
            }
        });

        return collection;
    }
}
