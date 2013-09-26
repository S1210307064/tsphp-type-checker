package ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck;

import ch.tutteli.tsphp.common.IErrorLogger;
import ch.tutteli.tsphp.common.IErrorReporter;
import ch.tutteli.tsphp.common.exceptions.TSPHPException;
import ch.tutteli.tsphp.typechecker.antlr.ErrorReportingTSPHPTypeCheckWalker;
import ch.tutteli.tsphp.typechecker.error.ErrorReporterRegistry;
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
        typeCheckWalker.addErrorLogger(new IErrorLogger()
        {
            @Override
            public void log(TSPHPException exception) {
                System.out.println(exception.getMessage());
            }
        });
        typeCheckWalker.downup(ast);
        checkErrors();
    }

    protected void checkErrors() {
        IErrorReporter errorHelper = ErrorReporterRegistry.get();
        Assert.assertFalse(testString + " failed. Exceptions occured." + exceptions,
                errorHelper.hasFoundError());


        Assert.assertFalse(testString + " failed. type checker walker exceptions occured.",
                reference.hasFoundError());


        verifyTypeCheck();
    }
}
