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
import ch.tutteli.tsphp.typechecker.test.utils.ATypeCheckerTest;
import ch.tutteli.tsphp.typechecker.test.utils.VariableDeclarationListHelper;
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
public class ClassMemberTest extends ATypeCheckerTest
{

    public ClassMemberTest(String testString, String expectedResult) {
        super(testString, expectedResult);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();
        int priv = TSPHPTypeCheckerDefinition.Private;
        int prot = TSPHPTypeCheckerDefinition.Protected;
        int pub = TSPHPTypeCheckerDefinition.Public;
        int stat = TSPHPTypeCheckerDefinition.Static;
        Object[][] variations = new Object[][]{
            {"", new TreeSet<>(Arrays.asList(new Integer[]{pub}))},
            {"private", new TreeSet<>(Arrays.asList(new Integer[]{priv}))},
            {"private static", new TreeSet<>(Arrays.asList(new Integer[]{priv, stat}))},
            {"protected", new TreeSet<>(Arrays.asList(new Integer[]{prot}))},
            {"protected static ", new TreeSet<>(Arrays.asList(new Integer[]{prot, stat}))},
            {"public", new TreeSet<>(Arrays.asList(new Integer[]{pub}))},
            {"public static ", new TreeSet<>(Arrays.asList(new Integer[]{pub, stat}))},
            {"static", new TreeSet<>(Arrays.asList(new Integer[]{pub, stat}))},    
            {"static private", new TreeSet<>(Arrays.asList(new Integer[]{priv, stat}))},
            {"static protected", new TreeSet<>(Arrays.asList(new Integer[]{prot, stat}))},
            {"static public", new TreeSet<>(Arrays.asList(new Integer[]{pub, stat}))},            
        };
        String global = "global";
        for (Object[] variation : variations) {
            collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                    "class a{ " + variation[0] + " ", ";}", global + ".a ", "a", (SortedSet<Integer>) variation[1]));
        }

        return collection;
    }
}
