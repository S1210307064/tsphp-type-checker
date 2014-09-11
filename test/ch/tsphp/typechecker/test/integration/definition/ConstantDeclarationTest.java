/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.definition;

import ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tsphp.typechecker.test.integration.testutils.definition.ADefinitionSymbolTest;
import ch.tsphp.typechecker.test.integration.testutils.definition.ConstantHelper;
import ch.tsphp.typechecker.utils.ModifierHelper;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

@RunWith(Parameterized.class)
public class ConstantDeclarationTest extends ADefinitionSymbolTest
{

    public ConstantDeclarationTest(String testString, String expectedResult) {
        super(testString, expectedResult);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        collection.addAll(ConstantHelper.testStrings("", "", "", "\\.\\.", true));
        collection.addAll(ConstantHelper.testStrings("namespace a {", "}", "", "\\a\\.\\a\\.", true));
        collection.addAll(ConstantHelper.testStrings("namespace a\\b {", "}", "", "\\a\\b\\.\\a\\b\\.", true));

        SortedSet<Integer> modifiers = new TreeSet<>();
        modifiers.add(TSPHPDefinitionWalker.QuestionMark);
        String none = ModifierHelper.getModifiersAsString(modifiers);
        modifiers = new TreeSet<>();
        modifiers.add(TSPHPDefinitionWalker.Abstract);
        modifiers.add(TSPHPDefinitionWalker.QuestionMark);
        String abstr = ModifierHelper.getModifiersAsString(modifiers);

        //class constants
        collection.addAll(ConstantHelper.testStrings(
                "namespace a\\b\\c; class f{", "}", "\\a\\b\\c\\.\\a\\b\\c\\.f" + none + " ",
                "\\a\\b\\c\\.\\a\\b\\c\\.f.", true));

        //interface constants
        collection.addAll(ConstantHelper.testStrings(
                "namespace a\\b\\c; interface f{", "}",
                "\\a\\b\\c\\.\\a\\b\\c\\.f" + abstr + " ",
                "\\a\\b\\c\\.\\a\\b\\c\\.f.", true));

        return collection;
    }
}
