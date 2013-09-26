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
public class CastTest extends ADefinitionScopeTest
{

    public CastTest(String testString, ScopeTestStruct[] theTestStructs) {
        super(testString, theTestStructs);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        collection.addAll(ScopeTestHelper.getVariations("", "", "(int) $a", "int", "\\.\\.",
                new Integer[]{1}, new Integer[]{0, 1}));

        collection.addAll(ScopeTestHelper.getVariations("namespace a;", "", "(float?) $a", "float",
                "\\a\\.\\a\\.", new Integer[]{1}, new Integer[]{0, 1}));
        collection.addAll(ScopeTestHelper.getVariations("namespace a;", "", "(cast float) $a", "float",
                "\\a\\.\\a\\.", new Integer[]{1}, new Integer[]{0, 1}));
        collection.addAll(ScopeTestHelper.getVariations("namespace a;", "", "(cast float?) $a", "float",
                "\\a\\.\\a\\.", new Integer[]{1}, new Integer[]{0, 1}));
        
        collection.addAll(ScopeTestHelper.getVariations("namespace a\\b{", "}", "(MyClass) $a", "MyClass",
                "\\a\\b\\.\\a\\b\\.", new Integer[]{1}, new Integer[]{0, 1}));
        collection.addAll(ScopeTestHelper.getVariations("namespace a\\b{", "}", "(cast MyClass) $a", "MyClass",
                "\\a\\b\\.\\a\\b\\.", new Integer[]{1}, new Integer[]{0, 1}));

        //nBody function block
        collection.addAll(ScopeTestHelper.getVariations("function void foo(){", "}", "(cast string?) $a", "string",
                "\\.\\.foo().", new Integer[]{1, 0, 4}, new Integer[]{0, 1}));

        //nBody class classBody mDecl block
        collection.addAll(ScopeTestHelper.getVariations("class a{ function void foo(){", "}}", "(bool) $a", "bool",
                "\\.\\.a.foo().", new Integer[]{1, 0, 4, 0, 4}, new Integer[]{0, 1}));
        return collection;
    }
}
