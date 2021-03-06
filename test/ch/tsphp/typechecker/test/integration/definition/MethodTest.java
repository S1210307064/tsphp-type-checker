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
public class MethodTest extends ADefinitionSymbolTest
{

    private static String prefix = "namespace a{ class b{";
    private static String appendix = "}}";
    private static String prefixExpected = "\\a\\.\\a\\.b ";
    private static List<Object[]> collection;

    public MethodTest(String testString, String expectedResult) {
        super(testString, expectedResult);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        collection = new ArrayList<>();

        addReturnTypes();
        addModifiers();
        addParameters();

        return collection;
    }

    private static void addReturnTypes() {
        TypeHelper.getAllTypesInclModifier(new IAdder()
        {
            @Override
            public void add(String type, String typeExpected, SortedSet modifiers) {
                String typeModifiers = ModifierHelper.getModifiers(modifiers);
                collection.add(new Object[]{
                        prefix + "function " + type + " get(){}" + appendix,
                        prefixExpected + "\\a\\.\\a\\.b." + typeExpected
                                + " \\a\\.\\a\\.b.get()|" + TSPHPDefinitionWalker.Public + typeModifiers
                });
            }
        });
    }


    @SuppressWarnings("unchecked")
    private static void addModifiers() {

        int priv = TSPHPDefinitionWalker.Private;
        int prot = TSPHPDefinitionWalker.Protected;
        int pub = TSPHPDefinitionWalker.Public;
        int fin = TSPHPDefinitionWalker.Final;
        int stat = TSPHPDefinitionWalker.Static;

        Object[][] variations = new Object[][]{
                {"", new TreeSet<>(Arrays.asList(new Integer[]{pub}))},
                //
                {"private", new TreeSet<>(Arrays.asList(new Integer[]{priv}))},
                {"private static", new TreeSet<>(Arrays.asList(new Integer[]{priv, stat}))},
                {"private final", new TreeSet<>(Arrays.asList(new Integer[]{priv, fin}))},
                {"private final static", new TreeSet<>(Arrays.asList(new Integer[]{priv, stat, fin}))},
                {"private static final", new TreeSet<>(Arrays.asList(new Integer[]{priv, fin, stat}))},
                //
                {"protected", new TreeSet<>(Arrays.asList(new Integer[]{prot}))},
                {"protected static", new TreeSet<>(Arrays.asList(new Integer[]{prot, stat}))},
                {"protected final", new TreeSet<>(Arrays.asList(new Integer[]{prot, fin}))},
                {"protected static final", new TreeSet<>(Arrays.asList(new Integer[]{prot, stat, fin}))},
                {"protected final static", new TreeSet<>(Arrays.asList(new Integer[]{prot, fin, stat}))},
                //
                {"public", new TreeSet<>(Arrays.asList(new Integer[]{pub}))},
                {"public static", new TreeSet<>(Arrays.asList(new Integer[]{pub, stat}))},
                {"public final", new TreeSet<>(Arrays.asList(new Integer[]{pub, fin}))},
                {"public static final", new TreeSet<>(Arrays.asList(new Integer[]{pub, stat, fin}))},
                {"public final static", new TreeSet<>(Arrays.asList(new Integer[]{pub, fin, stat}))},
                //
                {"static", new TreeSet<>(Arrays.asList(new Integer[]{pub, stat}))},
                {"static private", new TreeSet<>(Arrays.asList(new Integer[]{priv, stat}))},
                {"static private final", new TreeSet<>(Arrays.asList(new Integer[]{priv, stat, fin}))},
                {"static protected", new TreeSet<>(Arrays.asList(new Integer[]{prot, stat}))},
                {"static protected final", new TreeSet<>(Arrays.asList(new Integer[]{prot, stat, fin}))},
                {"static public", new TreeSet<>(Arrays.asList(new Integer[]{pub, stat}))},
                {"static public final", new TreeSet<>(Arrays.asList(new Integer[]{pub, stat, fin}))},
                {"static final", new TreeSet<>(Arrays.asList(new Integer[]{pub, stat, fin}))},
                {"static final private", new TreeSet<>(Arrays.asList(new Integer[]{priv, stat, fin}))},
                {"static final protected", new TreeSet<>(Arrays.asList(new Integer[]{prot, stat, fin}))},
                {"static final public", new TreeSet<>(Arrays.asList(new Integer[]{pub, stat, fin}))},
                //
                {"final", new TreeSet<>(Arrays.asList(new Integer[]{pub, fin}))},
                {"final private", new TreeSet<>(Arrays.asList(new Integer[]{priv, fin}))},
                {"final private static", new TreeSet<>(Arrays.asList(new Integer[]{priv, fin, stat}))},
                {"final protected", new TreeSet<>(Arrays.asList(new Integer[]{prot, fin}))},
                {"final protected static", new TreeSet<>(Arrays.asList(new Integer[]{prot, fin, stat}))},
                {"final public", new TreeSet<>(Arrays.asList(new Integer[]{pub, fin}))},
                {"final public static", new TreeSet<>(Arrays.asList(new Integer[]{pub, fin, stat}))},
                {"final static", new TreeSet<>(Arrays.asList(new Integer[]{pub, fin, stat}))},
                {"final static private", new TreeSet<>(Arrays.asList(new Integer[]{priv, fin, stat}))},
                {"final static protected", new TreeSet<>(Arrays.asList(new Integer[]{prot, fin, stat}))},
                {"final static public", new TreeSet<>(Arrays.asList(new Integer[]{pub, fin, stat}))}
        };

        for (Object[] variation : variations) {
            collection.add(new Object[]{
                    prefix + variation[0] + " function void foo(){}" + appendix,
                    prefixExpected + "\\a\\.\\a\\.b.void "
                            + "\\a\\.\\a\\.b.foo()" + ModifierHelper.getModifiers((SortedSet<Integer>) variation[1])
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
                    prefix + variation[0] + " function void foo();" + appendix,
                    prefixExpected + "\\a\\.\\a\\.b.void "
                            + "\\a\\.\\a\\.b.foo()" + ModifierHelper.getModifiers((SortedSet<Integer>) variation[1])
            });
        }
        collection.add(new Object[]{
                prefix + "abstract function void foo(); abstract function void bar();" + appendix,
                prefixExpected + "\\a\\.\\a\\.b.void "
                        + "\\a\\.\\a\\.b.foo()" + ModifierHelper.getModifiers((SortedSet<Integer>) variations[0][1])
                        + " "
                        + "\\a\\.\\a\\.b.void "
                        + "\\a\\.\\a\\.b.bar()" + ModifierHelper.getModifiers((SortedSet<Integer>) variations[0][1])
        });
    }

    private static void addParameters() {
        collection.addAll(ParameterListHelper.getTestStrings(
                prefix + "function void foo(", "){}" + appendix,
                prefixExpected + "\\a\\.\\a\\.b.void \\a\\.\\a\\.b.foo()|" + TSPHPDefinitionWalker.Public + " ",
                "\\a\\.\\a\\.b.foo().", true));
    }
}
