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
package ch.tutteli.tsphp.typechecker.test.testutils.reference;

import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.typechecker.test.testutils.ScopeTestHelper;
import org.junit.Assert;
import org.junit.Ignore;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
@Ignore
public abstract class AReferenceScopeTest extends AReferenceTest
{

    protected ReferenceScopeTestStruct[] testStructs;

    public AReferenceScopeTest(String testString, ReferenceScopeTestStruct[] theTestStructs) {
        super(testString);
        testStructs = theTestStructs;
    }

    @Override
    protected void verifyReferences() {
        verifyReferences(testStructs, ast, testString);
    }

    public static void verifyReferences(ReferenceScopeTestStruct[] scopeTestStructs, ITSPHPAst ast, String testString) {
        for (int i = 0; i < scopeTestStructs.length; ++i) {
            ReferenceScopeTestStruct testStruct = scopeTestStructs[i];
            ITSPHPAst testCandidate = ScopeTestHelper.getAst(ast, testString, testStruct.astAccessOrder);
            Assert.assertNotNull(testString + " failed. testCandidate is null. should be " + testStruct.astText, testCandidate);
            Assert.assertEquals(testString + " failed. wrong ast text,", testStruct.astText,
                    testCandidate.toStringTree());

            ISymbol symbol = testCandidate.getSymbol();
            Assert.assertNotNull(testString + " -- " + testStruct.astText + " failed. symbol was null", symbol);
            Assert.assertEquals(testString + " -- " + testStruct.astText + " failed. wrong scope,",
                    testStruct.astScope, ScopeTestHelper.getEnclosingScopeNames(symbol.getDefinitionScope()));

            ITypeSymbol typeSymbol = symbol.getType();
            Assert.assertNotNull(testString + " -- " + testStruct.astText + " failed. type was null", typeSymbol);
            Assert.assertEquals(testString + " -- " + testStruct.astText + " failed. wrong type scope,",
                    testStruct.typeScope, ScopeTestHelper.getEnclosingScopeNames(typeSymbol.getDefinitionScope()));
            Assert.assertEquals(testString + " -- " + testStruct.astText + " failed. wrong type text,",
                    testStruct.typeText, typeSymbol.getName());
        }
    }
}
