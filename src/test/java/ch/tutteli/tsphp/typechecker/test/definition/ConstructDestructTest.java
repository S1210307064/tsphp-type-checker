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

import ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tutteli.tsphp.typechecker.symbols.ModifierHelper;
import ch.tutteli.tsphp.typechecker.test.testutils.ADefinitionSymbolTest;
import ch.tutteli.tsphp.typechecker.test.testutils.ParameterListHelper;
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
public class ConstructDestructTest extends ADefinitionSymbolTest
{

    private static String prefix = "namespace a{ class b{";
    private static String appendix = "}}";
    private static String prefixExpected = "\\a\\.\\a\\.b ";
    private static List<Object[]> collection;

    public ConstructDestructTest(String testString, String expectedResult) {
        super(testString, expectedResult);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        collection = new ArrayList<>();

        addModifiers();
        addParameters();

        return collection;
    }

    private static void addModifiers() {

        int priv = TSPHPDefinitionWalker.Private;
        int prot = TSPHPDefinitionWalker.Protected;
        int pub = TSPHPDefinitionWalker.Public;
        int fin = TSPHPDefinitionWalker.Final;

        Object[][] variations = new Object[][]{
            {"", new TreeSet<>(Arrays.asList(new Integer[]{pub}))},
            //
            {"private", new TreeSet<>(Arrays.asList(new Integer[]{priv}))},
            {"private final", new TreeSet<>(Arrays.asList(new Integer[]{priv, fin}))},
            //
            {"protected", new TreeSet<>(Arrays.asList(new Integer[]{prot}))},
            {"protected final", new TreeSet<>(Arrays.asList(new Integer[]{prot, fin}))},
            //
            {"public", new TreeSet<>(Arrays.asList(new Integer[]{pub}))},
            {"public final", new TreeSet<>(Arrays.asList(new Integer[]{pub, fin}))},
            //
            {"final", new TreeSet<>(Arrays.asList(new Integer[]{pub, fin}))},
            {"final private", new TreeSet<>(Arrays.asList(new Integer[]{priv, fin}))},
            {"final protected", new TreeSet<>(Arrays.asList(new Integer[]{prot, fin}))},
            {"final public", new TreeSet<>(Arrays.asList(new Integer[]{pub, fin}))},};

        for (Object[] variation : variations) {
            collection.add(new Object[]{
                        prefix + variation[0] + " function __construct(){}" + appendix,
                        prefixExpected + "\\a\\.\\a\\.b.void "
                        + "\\a\\.\\a\\.b.__construct()" + ModifierHelper.getModifiers((SortedSet<Integer>) variation[1])
                    });
            collection.add(new Object[]{
                        prefix + variation[0] + " function __destruct(){}" + appendix,
                        prefixExpected + "\\a\\.\\a\\.b.void "
                        + "\\a\\.\\a\\.b.__destruct()" + ModifierHelper.getModifiers((SortedSet<Integer>) variation[1])
                    });
        }

        int abstr = TSPHPDefinitionWalker.Abstract;
        variations = new Object[][]{
            {"abstract", new TreeSet<>(Arrays.asList(new Integer[]{pub, abstr}))},
            {"abstract protected", new TreeSet<>(Arrays.asList(new Integer[]{prot, abstr}))},
            {"abstract public", new TreeSet<>(Arrays.asList(new Integer[]{pub, abstr}))},
            {"protected abstract ", new TreeSet<>(Arrays.asList(new Integer[]{prot, abstr}))},
            {"public abstract", new TreeSet<>(Arrays.asList(new Integer[]{pub, abstr}))}
        };
        for (Object[] variation : variations) {
            collection.add(new Object[]{
                        prefix + variation[0] + " function __construct();" + appendix,
                        prefixExpected + "\\a\\.\\a\\.b.void "
                        + "\\a\\.\\a\\.b.__construct()" + ModifierHelper.getModifiers((SortedSet<Integer>) variation[1])
                    });
            collection.add(new Object[]{
                        prefix + variation[0] + " function __destruct();" + appendix,
                        prefixExpected + "\\a\\.\\a\\.b.void "
                        + "\\a\\.\\a\\.b.__destruct()" + ModifierHelper.getModifiers((SortedSet<Integer>) variation[1])
                    });
        }
        collection.add(new Object[]{
                    prefix + "abstract function __construct(); abstract function __destruct();" + appendix,
                    prefixExpected + "\\a\\.\\a\\.b.void "
                    + "\\a\\.\\a\\.b.__construct()" + ModifierHelper.getModifiers((SortedSet<Integer>) variations[0][1]) + " "
                    + "\\a\\.\\a\\.b.void "
                    + "\\a\\.\\a\\.b.__destruct()" + ModifierHelper.getModifiers((SortedSet<Integer>) variations[0][1])
                });
    }

    private static void addParameters() {
        collection.addAll(ParameterListHelper.getTestStrings(
                prefix + "function __construct(", "){}" + appendix,
                prefixExpected
                + "\\a\\.\\a\\.b.void \\a\\.\\a\\.b.__construct()|" + TSPHPDefinitionWalker.Public + " ",
                "\\a\\.\\a\\.b.__construct().", true));
    }
}
