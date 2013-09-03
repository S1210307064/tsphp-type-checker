/*
 * Copyright 2013 Robert Stoll <rstoll@tutteli.ch>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
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

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
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
