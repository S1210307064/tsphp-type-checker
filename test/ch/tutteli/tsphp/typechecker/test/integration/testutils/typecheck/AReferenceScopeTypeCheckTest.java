package ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck;

import ch.tutteli.tsphp.typechecker.test.integration.testutils.reference.ReferenceScopeTestStruct;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.reference.AReferenceScopeTest;
import org.junit.Ignore;

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
