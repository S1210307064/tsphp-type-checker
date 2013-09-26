package ch.tutteli.tsphp.typechecker.test.integration.definition;

import ch.tutteli.tsphp.typechecker.test.integration.testutils.ScopeTestHelper;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.ScopeTestStruct;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.definition.ADefinitionScopeTest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class AtomConstantTest extends ADefinitionScopeTest
{

    public AtomConstantTest(String testString, ScopeTestStruct[] theTestStructs) {
        super(testString, theTestStructs);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        collection.addAll(ScopeTestHelper.testStringsDefaultNamespace());
        collection.addAll(ScopeTestHelper.testStrings("namespace a;", "", "\\a\\.\\a\\", new Integer[]{1}));
        collection.addAll(ScopeTestHelper.testStrings("namespace a\\b{", "}", "\\a\\b\\.\\a\\b\\", new Integer[]{1}));

        //nBody function block
        collection.addAll(ScopeTestHelper.testStrings("function void foo(){", "}", "\\.\\.foo()", new Integer[]{1, 0, 4}));

        //nBody class classBody mDecl block
        collection.addAll(ScopeTestHelper.testStrings("class a{ function void foo(){", "}}",
                "\\.\\.a.foo()", new Integer[]{1, 0, 4, 0, 4}));

        return collection;
    }
}
