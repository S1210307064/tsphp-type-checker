package ch.tutteli.tsphp.typechecker.test.integration.testutils.reference;

import ch.tutteli.tsphp.common.IErrorLogger;
import ch.tutteli.tsphp.common.IErrorReporter;
import ch.tutteli.tsphp.common.exceptions.TSPHPException;
import ch.tutteli.tsphp.typechecker.antlrmod.ErrorReportingTSPHPReferenceWalker;
import ch.tutteli.tsphp.typechecker.error.TypeCheckErrorReporterRegistry;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.definition.ADefinitionTest;
import org.junit.Assert;
import org.junit.Ignore;

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
        Assert.assertFalse(testString + " failed. Exceptions occurred." + exceptions,
                errorHelper.hasFoundError());

        Assert.assertFalse(testString + " failed. reference walker exceptions occurred.",
                reference.hasFoundError());

        verifyReferences();

    }

    @Override
    protected final void verifyDefinitions() {
        super.verifyDefinitions();
        commonTreeNodeStream.reset();
        reference = new ErrorReportingTSPHPReferenceWalker(commonTreeNodeStream, controller);
        reference.downup(ast);
        reference.registerErrorLogger(new IErrorLogger()
        {
            @Override
            public void log(TSPHPException exception) {
                System.out.println(exception.getMessage());
            }
        });
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
}
