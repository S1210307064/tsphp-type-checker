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

import ch.tutteli.tsphp.common.TSPHPAst;
import java.util.List;
import org.junit.Assert;
import org.junit.Ignore;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
@Ignore
public abstract class ATypeCheckerDefinitionScopeTest extends ATypeCheckerDefinitionTest
{

    protected ScopeTestStruct[] testStructs;

    public ATypeCheckerDefinitionScopeTest(String testString, ScopeTestStruct[] theTestStructs) {
        super(testString);
        testStructs = theTestStructs;
    }

    @Override
    protected void verifyDefinitions() {
        for (int i = 0; i < testStructs.length; ++i) {
            ScopeTestStruct testStruct = testStructs[i];
            TSPHPAst testCandidate = getAst(testStruct.astAccessOrder);
            Assert.assertEquals(testString + " failed. wrong ast text,", testStruct.astText, testCandidate.toStringTree());
            Assert.assertEquals(testString + " failed. wrong scope,", testStruct.astScope, ScopeTestHelper.getEnclosingScopeNames(testCandidate.scope));
        }

    }

    private TSPHPAst getAst(List<Integer> astAccessOrder) {
        TSPHPAst tmp = ast;
        for (Integer index : astAccessOrder) {
            tmp = (TSPHPAst) tmp.getChild(index);
        }
        return tmp;
    }
}
