/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.definition;

import ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tsphp.typechecker.symbols.ModifierHelper;
import ch.tsphp.typechecker.test.integration.testutils.IAdder;
import ch.tsphp.typechecker.test.integration.testutils.ParameterListHelper;
import ch.tsphp.typechecker.test.integration.testutils.TypeHelper;
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

@RunWith(Parameterized.class)
public class InterfaceMethodTest extends ADefinitionSymbolTest
{

    private static String prefix = "namespace a{ interface b{";
    private static String appendix = "}}";
    private static String prefixExpected = "\\a\\.\\a\\.b|" + TSPHPDefinitionWalker.Abstract + " ";
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
                TSPHPDefinitionWalker.Public,
                TSPHPDefinitionWalker.Abstract
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
