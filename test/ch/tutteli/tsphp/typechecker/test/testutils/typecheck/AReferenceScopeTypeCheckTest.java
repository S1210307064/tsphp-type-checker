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
package ch.tutteli.tsphp.typechecker.test.testutils.typecheck;

import ch.tutteli.tsphp.typechecker.test.testutils.reference.AReferenceScopeTest;
import ch.tutteli.tsphp.typechecker.test.testutils.reference.ReferenceScopeTestStruct;
import org.junit.Ignore;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
@Ignore
public abstract class AReferenceScopeTypeCheckTest extends AOperatorTypeCheckTest
{

    protected ReferenceScopeTestStruct[] scopeTestStructs;

    public AReferenceScopeTypeCheckTest(String testString, ReferenceScopeTestStruct[] theScopeTestStructs,
            TypeCheckStruct[] typeCheckStructs) {
        super(testString, typeCheckStructs);
        scopeTestStructs = theScopeTestStructs;
    }

    @Override
    protected void verifyTypeCheck() {
        super.verifyTypeCheck();
        AReferenceScopeTest.verifyReferences(scopeTestStructs, ast, testString);
    }
}
