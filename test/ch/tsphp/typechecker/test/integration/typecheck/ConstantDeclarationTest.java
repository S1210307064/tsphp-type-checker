package ch.tsphp.typechecker.test.integration.typecheck;

import ch.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.ConstantInitialValueHelper;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.TypeCheckStruct;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class ConstantDeclarationTest extends AOperatorTypeCheckTest
{

    public ConstantDeclarationTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();
        collection.addAll(ConstantInitialValueHelper.testStrings("const ", ";", "a", false, false, 1));
        collection.addAll(ConstantInitialValueHelper.testStrings("class A{const ", ";}", "a",
                false, false, 1, 0, 4));

        collection.add(new Object[]{
                "const int b = 1; const int a = b;", new TypeCheckStruct[]{struct("b#", Int, 1, 1, 1, 0)}
        });

        return collection;
    }
}
