package ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck;

import ch.tutteli.tsphp.common.IErrorReporter;
import ch.tutteli.tsphp.typechecker.antlrmod.ErrorReportingTSPHPTypeCheckWalker;
import ch.tutteli.tsphp.typechecker.error.TypeCheckErrorReporterRegistry;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.WriteExceptionToConsole;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.reference.AReferenceTest;
import org.junit.Assert;
import org.junit.Ignore;

@Ignore
public abstract class ATypeCheckTest extends AReferenceTest
{

    protected ErrorReportingTSPHPTypeCheckWalker typeCheckWalker;

    protected abstract void verifyTypeCheck();

    public ATypeCheckTest(String testString) {
        super(testString);
    }

    @Override
    protected void verifyReferences() {
        commonTreeNodeStream.reset();
        typeCheckWalker = new ErrorReportingTSPHPTypeCheckWalker(commonTreeNodeStream, controller);
        typeCheckWalker.registerErrorLogger(new WriteExceptionToConsole());
        try {
            typeCheckWalker.downup(ast);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(testString + " failed. unexpected exception occurred in the type check phase.\n" + e
                    .getMessage());
        }
        checkErrors();
    }

    protected void checkErrors() {
        IErrorReporter errorHelper = TypeCheckErrorReporterRegistry.get();
        Assert.assertFalse(testString + " failed. Exceptions occurred." + exceptions,
                errorHelper.hasFoundError());


        Assert.assertFalse(testString + " failed. type checker walker exceptions occurred.",
                reference.hasFoundError());


        verifyTypeCheck();
    }
}
