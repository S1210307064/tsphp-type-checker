package ch.tutteli.tsphp.typechecker.test.integration.testutils.definition;

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.ScopeTestHelper;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.ScopeTestStruct;
import org.junit.Assert;
import org.junit.Ignore;

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
        super.verifyDefinitions();
        verifyDefinitions(testStructs, ast, testString);
    }

    public static void verifyDefinitions(ScopeTestStruct[] testStructs, ITSPHPAst ast, String testString) {
        for (int i = 0; i < testStructs.length; ++i) {
            ScopeTestStruct testStruct = testStructs[i];
            ITSPHPAst testCandidate = ScopeTestHelper.getAst(ast, testString, testStruct.astAccessOrder);
            Assert.assertNotNull(testString + " failed. testCandidate is null. should be " + testStruct.astText, testCandidate);
            Assert.assertEquals(testString + " failed. wrong ast text,", testStruct.astText,
                    testCandidate.toStringTree());

            Assert.assertEquals(testString + "--" + testStruct.astText + " failed. wrong scope,", testStruct.astScope,
                    ScopeTestHelper.getEnclosingScopeNames(testCandidate.getScope()));
        }

    }
}
