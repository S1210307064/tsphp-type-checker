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

import ch.tutteli.tsphp.typechecker.test.testutils.ATypeCheckerDefinitionScopeTest;
import ch.tutteli.tsphp.typechecker.test.testutils.ATypeCheckerDoubleDefinitionTest;
import ch.tutteli.tsphp.typechecker.test.testutils.ScopeTestHelper;
import ch.tutteli.tsphp.typechecker.test.testutils.ScopeTestStruct;
import java.util.ArrayList;
import java.util.Arrays;
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
public class DoubleVariableDefinitionTest extends ATypeCheckerDoubleDefinitionTest
{

    public DoubleVariableDefinitionTest(String testString, String theNamespace, String theIdentifier, int howMany) {
        super(testString, theNamespace, theIdentifier, howMany);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();
        collection.addAll(getDifferentNamespaces("int $a;", "$a", 1));
        collection.addAll(getDifferentNamespaces("int $a; int $a;", "$a", 2));
        collection.addAll(getDifferentNamespaces("int $a; int $b; int $a;", "$a", 2));
        collection.addAll(getDifferentNamespaces("int $a; { int $a;}", "$a", 2));
        return collection;
    }
}