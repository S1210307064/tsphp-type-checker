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
public class InterfaceTest extends ADefinitionSymbolTest
{

    public InterfaceTest(String testString, String expectedResult) {
        super(testString, expectedResult);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        int abstr = TSPHPTypeCheckerDefinition.Abstract;

        collection.addAll(Arrays.asList(new Object[][]{
                    {"interface a{}", "\\.\\.a|" + abstr},
                    {
                        "interface a{} interface b{} interface c extends a\\b{}",
                        "\\.\\.a|" + abstr + " " + "\\.\\.b|" + abstr + " "
                        + "\\.\\.a\\b " + "\\.\\.c|" + abstr
                    },
                    {
                        "namespace x{interface a{}} namespace y{interface b{}} "
                        + "namespace z{interface c extends a\\b{}} namespace{interface d{}}",
                        "\\x\\.\\x\\.a|" + abstr + " " + "\\y\\.\\y\\.b|" + abstr + " "
                        + "\\z\\.\\z\\.a\\b " + "\\z\\.\\z\\.c|" + abstr + " " + "\\.\\.d|" + abstr
                    }
                }));


        String[] types = TypeHelper.getClassInterfaceTypes();
        for (String type : types) {
            collection.add(new Object[]{
                        "namespace b; interface a extends " + type + "{}",
                        "\\b\\.\\b\\." + type + " " + "\\b\\.\\b\\.a|" + abstr
                    });
            collection.add(new Object[]{
                        "interface a extends " + type + "," + type + "{}",
                        "\\.\\." + type + " " + "\\.\\." + type + " "
                        + "\\.\\.a|" + abstr
                    });
            collection.add(new Object[]{
                        "interface a extends " + type + "," + type + "," + type + "{}",
                        "\\.\\." + type + " " + "\\.\\." + type + " " + "\\.\\." + type + " "
                        + "\\.\\.a|" + abstr
                    });
        }


        return collection;
    }
}
