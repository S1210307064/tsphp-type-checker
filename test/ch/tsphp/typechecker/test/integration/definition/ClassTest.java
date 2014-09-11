/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.definition;

import ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tsphp.typechecker.test.integration.testutils.TypeHelper;
import ch.tsphp.typechecker.test.integration.testutils.definition.ADefinitionSymbolTest;
import ch.tsphp.typechecker.utils.ModifierHelper;
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

        SortedSet<Integer> modifiers = new TreeSet<>();
        modifiers.add(TSPHPDefinitionWalker.Final);
        modifiers.add(TSPHPDefinitionWalker.QuestionMark);
        String fin = ModifierHelper.getModifiersAsString(modifiers);
        modifiers = new TreeSet<>();
        modifiers.add(TSPHPDefinitionWalker.Abstract);
        modifiers.add(TSPHPDefinitionWalker.QuestionMark);
        String abstr = ModifierHelper.getModifiersAsString(modifiers);
        modifiers = new TreeSet<>();
        modifiers.add(TSPHPDefinitionWalker.QuestionMark);
        String none = ModifierHelper.getModifiersAsString(modifiers);

        collection.addAll(Arrays.asList(new Object[][]{
                {"class a{}", "\\.\\.a" + none},
                {"final class a{}", "\\.\\.a" + fin},
                {"abstract class a{}", "\\.\\.a" + abstr},
                {
                        "class a{} class b{} class c extends a\\b{}",
                        "\\.\\.a" + none + " \\.\\.b" + none + " \\.\\.a\\b \\.\\.c" + none
                },
                {
                        "namespace x{class a{}} namespace y{class b{}} "
                                + "namespace z{class c extends a\\b{}} namespace{class d{}}",
                        "\\x\\.\\x\\.a" + none + " \\y\\.\\y\\.b" + none + " \\z\\.\\z\\.a\\b "
                                + "\\z\\.\\z\\.c" + none + " \\.\\.d" + none
                }
        }));


        String[] types = TypeHelper.getClassInterfaceTypes();
        for (String type : types) {
            collection.add(new Object[]{
                    "namespace b; class a extends " + type + "{}",
                    "\\b\\.\\b\\." + type + " \\b\\.\\b\\.a" + none
            });

            collection.add(new Object[]{
                    "abstract class a implements " + type + "{}",
                    "\\.\\." + type + " \\.\\.a" + abstr
            });
            collection.add(new Object[]{
                    "namespace c; final class a implements " + type + "," + type + "{}",
                    "\\c\\.\\c\\." + type + " \\c\\.\\c\\." + type + " \\c\\.\\c\\.a" + fin
            });
            collection.add(new Object[]{
                    "class a implements " + type + "," + type + "," + type + "{}",
                    "\\.\\." + type + " \\.\\." + type + " \\.\\." + type + " \\.\\.a" + none
            });

            collection.add(new Object[]{
                    "class a extends " + type + " implements " + type + "{}",
                    "\\.\\." + type + " \\.\\." + type + " \\.\\.a" + none
            });
        }


        return collection;
    }
}
