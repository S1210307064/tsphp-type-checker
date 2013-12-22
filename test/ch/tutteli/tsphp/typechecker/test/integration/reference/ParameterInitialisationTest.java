package ch.tutteli.tsphp.typechecker.test.integration.reference;

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.TypeHelper;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.reference.AVerifyTimesReferenceTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class ParameterInitialisationTest extends AVerifyTimesReferenceTest
{

    private static List<Object[]> collection;

    public ParameterInitialisationTest(String testString, int howManyTimes) {
        super(testString, howManyTimes);
    }

    @Test
    public void test() throws RecognitionException {
        check();

    }

    @Override
    protected void verifyTimes() {
        verify(referencePhaseController, times(howManyTimes)).checkVariableIsInitialised(any(ITSPHPAst.class));
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
}
