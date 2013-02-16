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
package ch.tutteli.tsphp.typechecker.test.reference;

import ch.tutteli.tsphp.typechecker.error.DefinitionErrorDto;
import ch.tutteli.tsphp.typechecker.test.testutils.AReferenceDefinitionErrorTest;
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
public class ClassMemberDoubleDefinitionErrorTest extends AReferenceDefinitionErrorTest
{

    public ClassMemberDoubleDefinitionErrorTest(String testString, DefinitionErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {        
        List<Object[]> collection = new ArrayList<>();

        collection.addAll(VariableDoubleDefinitionErrorTest.getVariations("class a{", "}"));
        collection.addAll(VariableDoubleDefinitionErrorTest.getVariations("namespace{ class a{", "}}"));
        collection.addAll(VariableDoubleDefinitionErrorTest.getVariations("namespace a; class b{", "}"));
        collection.addAll(VariableDoubleDefinitionErrorTest.getVariations("namespace a{ class c{", "}}"));
        collection.addAll(VariableDoubleDefinitionErrorTest.getVariations("namespace a\\b; class e{", "}"));
        collection.addAll(VariableDoubleDefinitionErrorTest.getVariations("namespace a\\b\\z{ class m{", "}}"));

        return collection;
    }
}
