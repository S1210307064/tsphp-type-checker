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
package ch.tutteli.tsphp.typechecker.test.testutils;

import ch.tutteli.tsphp.common.ITSPHPAst;
import java.util.List;
import org.junit.Assert;
import org.junit.Ignore;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
@Ignore
public abstract class ADefinitionScopeTest extends ADefinitionTest
{

    protected ScopeTestStruct[] testStructs;

    public ADefinitionScopeTest(String testString, ScopeTestStruct[] theTestStructs) {
        super(testString);
        testStructs = theTestStructs;
    }

    @Override
    protected void verifyDefinitions() {
        for (int i = 0; i < testStructs.length; ++i) {
            ScopeTestStruct testStruct = testStructs[i];
            ITSPHPAst testCandidate = ScopeTestHelper.getAst(ast,testString,testStruct.astAccessOrder);
            Assert.assertNotNull(testString + " failed. testCandidate is null. should be "+testStruct.astText, testCandidate);
            Assert.assertEquals(testString + " failed. wrong ast text,", testStruct.astText,
                    testCandidate.toStringTree());

            Assert.assertEquals(testString + "--"+testStruct.astText+ " failed. wrong scope,", testStruct.astScope,
                    ScopeTestHelper.getEnclosingScopeNames(testCandidate.getScope()));
        }

    }

}
