/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.definition;

import ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tsphp.typechecker.test.integration.testutils.VariableDeclarationListHelper;
import ch.tsphp.typechecker.test.integration.testutils.definition.ADefinitionSymbolTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.QuestionMark;

@RunWith(Parameterized.class)
public class ClassMemberTest extends ADefinitionSymbolTest
{

    public ClassMemberTest(String testString, String expectedResult) {
        super(testString, expectedResult);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @SuppressWarnings("unchecked")
    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();
        int priv = TSPHPDefinitionWalker.Private;
        int prot = TSPHPDefinitionWalker.Protected;
        int pub = TSPHPDefinitionWalker.Public;
        int stat = TSPHPDefinitionWalker.Static;
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
                {"static public", new TreeSet<>(Arrays.asList(new Integer[]{pub, stat}))},};

        for (Object[] variation : variations) {
            collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                    "class a{ " + variation[0] + " ", ";}", "\\.\\.a|" + QuestionMark + " ", "", "\\.\\.a.",
                    (SortedSet<Integer>) variation[1]));
        }

        return collection;
    }
}
