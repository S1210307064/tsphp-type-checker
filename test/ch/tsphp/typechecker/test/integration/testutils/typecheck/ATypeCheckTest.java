/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.testutils.typecheck;

import ch.tsphp.typechecker.IAccessResolver;
import ch.tsphp.typechecker.IOverloadResolver;
import ch.tsphp.typechecker.ISymbolResolver;
import ch.tsphp.typechecker.ITypeCheckPhaseController;
import ch.tsphp.typechecker.ITypeSystem;
import ch.tsphp.typechecker.OverloadResolver;
import ch.tsphp.typechecker.TypeCheckPhaseController;
import ch.tsphp.typechecker.antlrmod.ErrorReportingTSPHPTypeCheckWalker;
import ch.tsphp.typechecker.error.ITypeCheckerErrorReporter;
import ch.tsphp.typechecker.test.integration.testutils.TestSymbolFactory;
import ch.tsphp.typechecker.test.integration.testutils.WriteExceptionToConsole;
import ch.tsphp.typechecker.test.integration.testutils.reference.AReferenceTest;
import ch.tsphp.typechecker.utils.ITypeCheckerAstHelper;
import ch.tsphp.typechecker.utils.TypeCheckerAstHelper;
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
                symbolFactory,
                symbolResolver,
                typeCheckErrorReporter,
                typeSystem,
                overloadResolver,
                accessResolver,
                typeCheckerAstHelper);
    }

    @Override
    protected void verifyReferences() {
        commonTreeNodeStream.reset();
        typeCheckWalker = new ErrorReportingTSPHPTypeCheckWalker(
                commonTreeNodeStream, typeCheckPhaseController, accessResolver, typeSystem);

        typeCheckWalker.registerErrorLogger(new WriteExceptionToConsole());
        try {
            typeCheckWalker.downup(ast);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(testString + " failed. unexpected exception occurred in the type check phase.\n"
                    + e.getMessage());
        }
        checkErrors();
    }

    protected void checkErrors() {
        Assert.assertFalse(testString + " failed. Exceptions occurred." + exceptions,
                typeCheckErrorReporter.hasFoundError());


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
            ISymbolResolver theSymbolResolver,
            ITypeCheckerErrorReporter theTypeCheckerErrorReporter,
            ITypeSystem theTypeSystem,
            IOverloadResolver theOverloadResolver,
            IAccessResolver theVisibilityChecker,
            ITypeCheckerAstHelper theAstHelper) {

        return new TypeCheckPhaseController(
                theSymbolFactory,
                theSymbolResolver,
                theTypeCheckerErrorReporter,
                theTypeSystem,
                theOverloadResolver,
                theVisibilityChecker,
                theAstHelper);
    }

}
