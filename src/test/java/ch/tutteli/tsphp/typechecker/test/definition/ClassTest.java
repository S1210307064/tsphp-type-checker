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
import ch.tutteli.tsphp.typechecker.test.testutils.ADefinitionSymbolTest;
import ch.tutteli.tsphp.typechecker.test.testutils.TypeHelper;
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
public class ClassTest extends ADefinitionSymbolTest
{

    public ClassTest(String testString, String expectedResult) {
        super(testString, expectedResult);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        int fin = TSPHPTypeCheckerDefinition.Final;
        int abstr = TSPHPTypeCheckerDefinition.Abstract;

        collection.addAll(Arrays.asList(new Object[][]{
                    {"class a{}", "\\.\\.a"},
                    {"final class a{}", "\\.\\.a|" + fin},
                    {"abstract class a{}", "\\.\\.a|" + abstr},
                    {
                        "class a{} class b{} class c extends a\\b{}",
                        "\\.\\.a \\.\\.b \\.\\.a\\b \\.\\.c"
                    },
                    {
                        "namespace x{class a{}} namespace y{class b{}} "
                        + "namespace z{class c extends a\\b{}} namespace{class d{}}",
                        "\\x\\.\\x\\.a \\y\\.\\y\\.b \\z\\.\\z\\.a\\b \\z\\.\\z\\.c \\.\\.d"
                    }
                }));


        String[] types = TypeHelper.getClassInterfaceTypes();
        for (String type : types) {
            collection.add(new Object[]{
                        "namespace b; class a extends " + type + "{}",
                        "\\b\\.\\b\\." + type + " \\b\\.\\b\\.a"
                    });

            collection.add(new Object[]{
                        "abstract class a implements " + type + "{}",
                        "\\.\\." + type + " \\.\\.a|" + abstr
                    });
            collection.add(new Object[]{
                        "namespace c; final class a implements " + type + "," + type + "{}",
                        "\\c\\.\\c\\." + type + " \\c\\.\\c\\." + type + " \\c\\.\\c\\.a|" + fin
                    });
            collection.add(new Object[]{
                        "class a implements " + type + "," + type + "," + type + "{}",
                        "\\.\\." + type + " \\.\\." + type + " \\.\\." + type + " \\.\\.a"
                    });

            collection.add(new Object[]{
                        "class a extends " + type + " implements " + type + "{}",
                        "\\.\\." + type + " \\.\\." + type + " \\.\\.a"
                    });
        }


        return collection;
    }
}
