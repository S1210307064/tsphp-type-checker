package ch.tutteli.tsphp.typechecker.test.integration.definition;

import ch.tutteli.tsphp.typechecker.test.integration.testutils.VariableDeclarationListHelper;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.definition.ADefinitionSymbolTest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class VariableDefinitionFunctionTest extends ADefinitionSymbolTest
{

    public VariableDefinitionFunctionTest(String testString, String expectedResult) {
        super(testString, expectedResult);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        final List<Object[]> collection = new ArrayList<>();

        final String defaultNamespace = "\\.\\.";

        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                "function void foo(){", ";}",
                defaultNamespace + "void \\.\\.foo() ", "",
                defaultNamespace + "foo().", null));

        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                "namespace t; function void foo(){", ";}",
                "\\t\\.\\t\\.void \\t\\.\\t\\.foo() ", "", "\\t\\.\\t\\.foo().", null));

        collection.add(new Object[]{
                    "namespace{ function void foo(){ int $a=1; bool $b=true, $c=false;}}"
                    + "namespace b{ function void bar(){float $d;}}",
                    "\\.\\.void \\.\\.foo() "
                    + "\\.\\.foo().int \\.\\.foo().$a "
                    + "\\.\\.foo().bool \\.\\.foo().$b "
                    + "\\.\\.foo().bool \\.\\.foo().$c "
                    + "\\b\\.\\b\\.void \\b\\.\\b\\.bar() "
                    + "\\b\\.\\b\\.bar().float \\b\\.\\b\\.bar().$d"
                });
        return collection;
    }
}
