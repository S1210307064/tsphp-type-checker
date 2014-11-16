/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.testutils.typecheck;

import ch.tsphp.typechecker.test.integration.testutils.reference.AReferenceTypeScopeTest;
import ch.tsphp.typechecker.test.integration.testutils.reference.TypeScopeTestStruct;
import org.junit.Ignore;

@Ignore
public abstract class AReferenceScopeTypeCheckTest extends AOperatorTypeCheckTest
{

    protected TypeScopeTestStruct[] scopeTestStructs;

    public AReferenceScopeTypeCheckTest(String testString, TypeScopeTestStruct[] theScopeTestStructs,
            TypeCheckStruct[] typeCheckStructs) {
        super(testString, typeCheckStructs);
        scopeTestStructs = theScopeTestStructs;
    }

    @Override
    protected void verifyTypeCheck() {
        super.verifyTypeCheck();
        AReferenceTypeScopeTest.verifyReferences(scopeTestStructs, ast, testString);
    }
}
