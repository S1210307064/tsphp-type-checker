package ch.tutteli.tsphp.typechecker.test.integration.testutils.reference;

import ch.tutteli.tsphp.common.IErrorReporter;
import ch.tutteli.tsphp.typechecker.ITypeCheckerController;
import ch.tutteli.tsphp.typechecker.antlrmod.ErrorReportingTSPHPReferenceWalker;
import ch.tutteli.tsphp.typechecker.error.TypeCheckErrorReporterRegistry;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.WriteExceptionToConsole;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.definition.ADefinitionTest;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.junit.Assert;
import org.junit.Ignore;

import static org.junit.Assert.assertFalse;

@Ignore
public abstract class AReferenceTest extends ADefinitionTest
{

    protected ErrorReportingTSPHPReferenceWalker reference;

    public AReferenceTest(String testString) {
        super(testString);
    }

    protected abstract void verifyReferences();

    protected void checkReferences() {
        IErrorReporter errorHelper = TypeCheckErrorReporterRegistry.get();
        assertFalse(testString + " failed. Exceptions occurred." + exceptions, errorHelper.hasFoundError());
        assertFalse(testString + " failed. reference walker exceptions occurred.", reference.hasFoundError());

        verifyReferences();
    }

    @Override
    protected void verifyDefinitions() {
        super.verifyDefinitions();
        afterVerifyDefinitions();
    }

    protected void afterVerifyDefinitions() {
        commonTreeNodeStream.reset();
        reference = createReferenceWalker(commonTreeNodeStream, controller);
        reference.registerErrorLogger(new WriteExceptionToConsole());
        try {
            reference.compilationUnit();
        } catch (RecognitionException e) {
            Assert.fail(testString + " failed. Unexpected exception occurred, " +
                    "should be caught by the ErrorReportingTSPHPReferenceWalker.\n"
                    + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(testString + " failed. Unexpected exception occurred in the reference phase.\n" + e
                    .getMessage());
        }
        checkReferences();
    }

    protected static String getAliasFullType(String type) {
        return type.substring(0, 1).equals("\\") ? type : "\\" + type;
    }

    protected static String getFullName(String namespace, String type) {
        String fullType = type;
        if (!type.substring(0, 1).equals("\\")) {
            fullType = namespace + type;
        }
        return fullType;
    }

    protected ErrorReportingTSPHPReferenceWalker createReferenceWalker(CommonTreeNodeStream theCommonTreeNodeStream,
            ITypeCheckerController theController) {
        return new ErrorReportingTSPHPReferenceWalker(theCommonTreeNodeStream, theController);
    }
}
