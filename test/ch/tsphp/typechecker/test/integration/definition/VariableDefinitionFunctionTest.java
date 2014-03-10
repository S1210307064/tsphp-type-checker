/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.definition;

import ch.tsphp.typechecker.test.integration.testutils.VariableDeclarationListHelper;
import ch.tsphp.typechecker.test.integration.testutils.definition.ADefinitionSymbolTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
