/*
 * Copyright 2012 Robert Stoll <rstoll@tutteli.ch>
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

import ch.tutteli.tsphp.typechecker.test.testutils.ScopeTestHelper;
import ch.tutteli.tsphp.typechecker.test.testutils.ScopeTestStruct;
import ch.tutteli.tsphp.typechecker.test.testutils.definition.ADefinitionScopeTest;
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
