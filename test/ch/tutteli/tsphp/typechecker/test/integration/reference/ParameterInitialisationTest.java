package ch.tutteli.tsphp.typechecker.test.integration.reference;

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.IOverloadResolver;
import ch.tutteli.tsphp.typechecker.ISymbolResolver;
import ch.tutteli.tsphp.typechecker.ITypeCheckerController;
import ch.tutteli.tsphp.typechecker.ITypeSystem;
import ch.tutteli.tsphp.typechecker.TypeCheckerController;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.TestDefiner;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.TestSymbolFactory;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.TypeHelper;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.reference.AReferenceTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.exceptions.base.MockitoAssertionError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class ParameterInitialisationTest extends AReferenceTest
{

    private static List<Object[]> collection;
    private int times;

    public ParameterInitialisationTest(String testString, int howManyTimes) {
        super(testString);
        times = howManyTimes;
    }

    @Test
    public void test() throws RecognitionException {
        check();
        try {
            verify(controller, times(times)).checkVariableIsInitialised(any(ITSPHPAst.class));
        } catch (MockitoAssertionError e) {
            System.err.println(testString + " failed.");
            throw e;
        }
    }


    @Override
    protected void verifyReferences() {
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        collection = new ArrayList<>();

        addVariations("", "");
        addVariations("namespace{", "}");
        addVariations("namespace a;", "");
        addVariations("namespace a{", "}");
        addVariations("namespace a\\b;", "");
        addVariations("namespace a\\b\\z{", "}");
        addVariations("class a{", "}");
        addVariations("namespace{ class a{", "}}");
        addVariations("namespace a; class b{", "}");
        addVariations("namespace a{ class c{", "}}");
        addVariations("namespace a\\b; class e{", "}");
        addVariations("namespace a\\b\\z{ class m{", "}}");
        return collection;
    }

    public static void addVariations(String prefix, String appendix) {
        List<String> types = TypeHelper.getPrimitiveTypes();
        for (String type : types) {
            collection.addAll(Arrays.asList(new Object[][]{
                    {prefix + "function void foo(" + type + " $a){ $a; }" + appendix, 1},
                    {prefix + "function void foo(" + type + " $a,int  $b){ $a; $b;}" + appendix, 2},
                    {prefix + "function void foo(" + type + " $a,int? $b, cast float $c=3){$a;$b;$c;}" + appendix, 3},
                    {prefix + "function void foo(" + type + " $a, int? $b=1,cast float $c=3){$a;$b;$c;}" + appendix, 3},
                    {
                            prefix + "function void foo(" + type + " $a, int $b, int? $c=1,float $d=3){$a;$b;$c;$d;}"
                                    + appendix, 4
                    },
            }));
        }
    }

    @Override
    protected ITypeCheckerController createTypeCheckerController(
            TestSymbolFactory theSymbolFactory,
            ITypeSystem theTypeSystem,
            TestDefiner theDefiner,
            ISymbolResolver theSymbolResolver,
            IOverloadResolver theMethodResolver) {
        return spy(new TypeCheckerController(
                theSymbolFactory,
                theTypeSystem,
                theDefiner,
                theSymbolResolver,
                theMethodResolver,
                typeCheckerAstHelper));
    }

}
