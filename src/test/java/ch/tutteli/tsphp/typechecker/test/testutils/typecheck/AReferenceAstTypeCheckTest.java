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
package ch.tutteli.tsphp.typechecker.test.testutils.typecheck;

import ch.tutteli.tsphp.typechecker.test.testutils.ScopeTestStruct;
import ch.tutteli.tsphp.typechecker.test.testutils.reference.AReferenceAstTest;
import org.junit.Ignore;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
@Ignore
public abstract class AReferenceAstTypeCheckTest extends AOperatorTypeCheckTest
{

    protected ScopeTestStruct[] scopeTestStructs;

    public AReferenceAstTypeCheckTest(String testString, ScopeTestStruct[] theScopeTestStructs,
            TypeCheckStruct[] typeCheckStructs) {
        super(testString, typeCheckStructs);
        scopeTestStructs = theScopeTestStructs;
    }

    @Override
    protected void verifyTypeCheck() {
        super.verifyTypeCheck();
        AReferenceAstTest.verifyReferences(scopeTestStructs, ast, testString);
    }
}
