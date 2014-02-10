package ch.tsphp.typechecker.test.integration.reference;

import ch.tsphp.typechecker.test.integration.testutils.MethodModifierHelper;
import ch.tsphp.typechecker.test.integration.testutils.reference.AReferenceTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@RunWith(Parameterized.class)
public class ConstructDestructReturnsTest extends AReferenceTest
{
    public ConstructDestructReturnsTest(String testString) {
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

        String[] variations = MethodModifierHelper.getConstructDestructVariations();
        for (String modifier : variations) {
            collection.add(new Object[]{"class a{ " + modifier + " function __construct(){} }"});
            collection.add(new Object[]{"class a{ " + modifier + " function __destruct(){} }"});
            collection.add(new Object[]{"abstract class a{ " + modifier + " function __construct(){} }"});
            collection.add(new Object[]{"abstract class a{ " + modifier + " function __destruct(){} }"});
        }

        variations = MethodModifierHelper.getAbstractVariations();
        for (String modifier : variations) {
            collection.add(new Object[]{"abstract class a{ " + modifier + " function __construct(); }"});
            collection.add(new Object[]{"abstract class a{ " + modifier + " function __destruct(); }"});
        }
        return collection;
    }
}
