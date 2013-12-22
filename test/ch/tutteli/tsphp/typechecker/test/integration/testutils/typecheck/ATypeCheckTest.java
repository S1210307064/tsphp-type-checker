package ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck;

import ch.tutteli.tsphp.common.IErrorReporter;
import ch.tutteli.tsphp.typechecker.IOverloadResolver;
import ch.tutteli.tsphp.typechecker.ISymbolResolver;
import ch.tutteli.tsphp.typechecker.ITypeCheckPhaseController;
import ch.tutteli.tsphp.typechecker.ITypeSystem;
import ch.tutteli.tsphp.typechecker.IVisibilityChecker;
import ch.tutteli.tsphp.typechecker.OverloadResolver;
import ch.tutteli.tsphp.typechecker.TypeCheckPhaseController;
import ch.tutteli.tsphp.typechecker.antlrmod.ErrorReportingTSPHPTypeCheckWalker;
import ch.tutteli.tsphp.typechecker.error.TypeCheckErrorReporterRegistry;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.TestSymbolFactory;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.WriteExceptionToConsole;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.reference.AReferenceTest;
import ch.tutteli.tsphp.typechecker.utils.ITypeCheckerAstHelper;
import ch.tutteli.tsphp.typechecker.utils.TypeCheckerAstHelper;
import org.junit.Assert;
import org.junit.Ignore;

@Ignore
public abstract class ATypeCheckTest extends AReferenceTest
{

    protected ErrorReportingTSPHPTypeCheckWalker typeCheckWalker;
    protected ITypeCheckPhaseController typeCheckPhaseController;

    protected IOverloadResolver overloadResolver;
    protected ITypeCheckerAstHelper typeCheckerAstHelper;

    protected abstract void verifyTypeCheck();

    public ATypeCheckTest(String testString) {
        super(testString);

        init();
    }


    private void init() {

        overloadResolver = createOverloadResolver(typeSystem);
        typeCheckerAstHelper = createTypeCheckerAstHelper();

        typeCheckPhaseController = createTypeCheckerController(
                symbolFactory, symbolResolver, typeSystem, overloadResolver, visibilityChecker, typeCheckerAstHelper);
    }

    @Override
    protected void verifyReferences() {
        commonTreeNodeStream.reset();
        typeCheckWalker = new ErrorReportingTSPHPTypeCheckWalker(
                commonTreeNodeStream, typeCheckPhaseController, typeSystem);

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

    protected IOverloadResolver createOverloadResolver(ITypeSystem theTypeSystem) {
        return new OverloadResolver(theTypeSystem);
    }

    protected ITypeCheckerAstHelper createTypeCheckerAstHelper() {
        return new TypeCheckerAstHelper();
    }

    protected ITypeCheckPhaseController createTypeCheckerController(
            TestSymbolFactory theSymbolFactory,
            ISymbolResolver theSymbolResolver, ITypeSystem theTypeSystem,
            IOverloadResolver theOverloadResolver,
            IVisibilityChecker theVisibilityChecker,
            ITypeCheckerAstHelper theAstHelper) {

        return new TypeCheckPhaseController(
                theSymbolFactory,
                theSymbolResolver, theTypeSystem,
                theOverloadResolver,
                theVisibilityChecker,
                theAstHelper);
    }

}
