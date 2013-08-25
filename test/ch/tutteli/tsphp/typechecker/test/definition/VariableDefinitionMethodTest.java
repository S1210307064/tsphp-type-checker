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
package ch.tutteli.tsphp.typechecker.test.definition;

import ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tutteli.tsphp.typechecker.test.testutils.VariableDeclarationListHelper;
import ch.tutteli.tsphp.typechecker.test.testutils.definition.ADefinitionSymbolTest;
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
public class VariableDefinitionMethodTest extends ADefinitionSymbolTest
{

    public VariableDefinitionMethodTest(String testString, String expectedResult) {
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
                "class a{ function void foo(){", ";}}",
                "\\.\\.a "
                + defaultNamespace + "a.void " + defaultNamespace + "a.foo()|" + TSPHPDefinitionWalker.Public + " ",
                "", defaultNamespace + "a.foo().", null));

        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                "namespace t; class a{ function void foo(){", ";}}",
                "\\t\\.\\t\\.a \\t\\.\\t\\.a.void \\t\\.\\t\\.a.foo()|" + TSPHPDefinitionWalker.Public + " ","",
                "\\t\\.\\t\\.a.foo().", null));


        collection.add(new Object[]{
                    "namespace t\\r; class a{ function void foo(){ int $a=1; bool $b=true,$c=false;}}",
                    "\\t\\r\\.\\t\\r\\.a "
                    + "\\t\\r\\.\\t\\r\\.a.void "
                    + "\\t\\r\\.\\t\\r\\.a.foo()|" + TSPHPDefinitionWalker.Public + " "
                    + "\\t\\r\\.\\t\\r\\.a.foo().int \\t\\r\\.\\t\\r\\.a.foo().$a "
                    + "\\t\\r\\.\\t\\r\\.a.foo().bool \\t\\r\\.\\t\\r\\.a.foo().$b "
                    + "\\t\\r\\.\\t\\r\\.a.foo().bool \\t\\r\\.\\t\\r\\.a.foo().$c"
                });
        return collection;
    }
}
