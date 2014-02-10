package ch.tsphp.typechecker.test.integration.reference;

import ch.tsphp.typechecker.test.integration.testutils.ReturnCheckHelper;
import ch.tsphp.typechecker.test.integration.testutils.reference.AReferenceTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@RunWith(Parameterized.class)
public class FunctionReturnsTest extends AReferenceTest
{
    public FunctionReturnsTest(String testString) {
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
        collection.addAll(ReturnCheckHelper.getTestStringVariations("function int foo(){", "}"));
        collection.add(new Object[]{"function void foo(){}"});
        collection.add(new Object[]{"function void foo(){return;}"});
        return collection;
    }
}
