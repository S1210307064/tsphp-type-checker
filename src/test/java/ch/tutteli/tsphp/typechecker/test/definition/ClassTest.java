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
import ch.tutteli.tsphp.typechecker.test.utils.TypeHelper;
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
public class ClassTest extends ATypeCheckerDefinitionTest
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
        String global = "global";

        collection.addAll(Arrays.asList(new Object[][]{
                    {"class a{}", global + ".a"},
                    {"final class a{}", global + ".a|" + fin},
                    {"abstract class a{}", global + ".a|" + abstr},
                    {
                        "class a{} class b{} class c extends a\\b{}",
                        global + ".a " + global + ".b " + global + ".a\\b " + global + ".c"
                    },
                    {
                        "namespace x{class a{}} namespace y{class b{}} "
                        + "namespace z{class c extends a\\b{}} namespace{class d{}}",
                        global + ".x.a " + global + ".y.b " + global + ".z.a\\b " + global + ".z.c " + global + ".d"
                    }
                }));


        String[] types = TypeHelper.getClassInterfaceTypes();
        for (String type : types) {
            collection.add(new Object[]{
                        "namespace b; class a extends " + type + "{}",
                        global + ".b." + type + " " + global + ".b.a"
                    });
            collection.add(new Object[]{
                        "final class a extends " + type + "," + type + "{}",
                        global + "." + type + " " + global + "." + type + " "
                        + global + ".a|" + fin
                    });
            collection.add(new Object[]{
                        "abstract class a extends " + type + "," + type + "," + type + "{}",
                        global + "." + type + " " + global + "." + type + " " + global + "." + type + " "
                        + global + ".a|" + abstr
                    });

            collection.add(new Object[]{
                        "abstract class a implements " + type + "{}",
                        global + "." + type + " " + global + ".a|" + abstr
                    });
            collection.add(new Object[]{
                        "namespace c; final class a implements " + type + "," + type + "{}",
                        global + ".c." + type + " " + global + ".c." + type + " "
                        + global + ".c.a|" + fin
                    });
            collection.add(new Object[]{
                        "class a implements " + type + "," + type + "," + type + "{}",
                        global + "." + type + " " + global + "." + type + " " + global + "." + type + " "
                        + global + ".a"
                    });

            collection.add(new Object[]{
                        "class a extends " + type + " implements " + type + "{}",
                        global + "." + type + " " + global + "." + type + " "
                        + global + ".a"
                    });
        }


        return collection;
    }
}
