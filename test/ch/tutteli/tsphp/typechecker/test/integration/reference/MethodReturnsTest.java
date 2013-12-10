package ch.tutteli.tsphp.typechecker.test.integration.reference;

import ch.tutteli.tsphp.typechecker.test.integration.testutils.MethodModifierHelper;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.reference.AReferenceTest;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.ReturnCheckHelper;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@RunWith(Parameterized.class)
public class MethodReturnsTest extends AReferenceTest
{
    public MethodReturnsTest(String testString) {
        super(testString);
    }

    @Override
    protected void verifyReferences() {
        //nothing to check, should just not cause an error
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        String[] variations = MethodModifierHelper.getVariations();
        for (String modifier : variations) {
            collection.addAll(ReturnCheckHelper.getTestStringVariations(
                    "class a{ " + modifier + " function int foo(){", "}}"
            ));
            collection.add(new Object[]{"class a{ " + modifier + " function void foo(){} }"});
        }

        variations = MethodModifierHelper.getAbstractVariations();
        for (String modifier : variations) {
            collection.add(new Object[]{"abstract class a{ " + modifier + " function void foo(); }"});
            collection.add(new Object[]{"abstract class a{ " + modifier + " function int foo(); }"});
        }
        return collection;
    }
}
