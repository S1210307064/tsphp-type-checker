package ch.tutteli.tsphp.typechecker.test.integration.testutils.reference;

import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.ScopeTestHelper;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.ScopeTestStruct;
import org.junit.Assert;
import org.junit.Ignore;

@Ignore
public abstract class AReferenceAstTest extends AReferenceTest
{

    protected ScopeTestStruct[] testStructs;

    public AReferenceAstTest(String testString, ScopeTestStruct[] theTestStructs) {
        super(testString);
        testStructs = theTestStructs;
    }

    @Override
    protected void verifyReferences() {
        verifyReferences(testStructs, ast, testString);
    }

    public static void verifyReferences(ScopeTestStruct[] testStructs, ITSPHPAst ast, String testString) {
        for (ScopeTestStruct testStruct : testStructs) {
            ITSPHPAst testCandidate = ScopeTestHelper.getAst(ast, testString, testStruct.astAccessOrder);
            Assert.assertNotNull(testString + " failed. testCandidate is null. should be " + testStruct.astText, testCandidate);
            Assert.assertEquals(testString + " failed. wrong ast text,", testStruct.astText,
                    testCandidate.toStringTree());

            ISymbol symbol = testCandidate.getSymbol();
            Assert.assertNotNull(testString + " -- " + testStruct.astText + " failed. symbol was null", symbol);
            Assert.assertEquals(testString + " -- " + testStruct.astText + " failed. wrong scope,",
                    testStruct.astScope, ScopeTestHelper.getEnclosingScopeNames(symbol.getDefinitionScope()));
        }
    }
}
