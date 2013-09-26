package ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck;

import ch.tutteli.tsphp.typechecker.test.integration.testutils.reference.AReferenceAstTest;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.ScopeTestStruct;
import org.junit.Ignore;

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
