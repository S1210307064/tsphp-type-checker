package ch.tutteli.tsphp.typechecker.test.integration.testutils.reference;

import ch.tutteli.tsphp.common.IErrorReporter;
import ch.tutteli.tsphp.common.ILowerCaseStringMap;
import ch.tutteli.tsphp.typechecker.IReferencePhaseController;
import ch.tutteli.tsphp.typechecker.ISymbolResolver;
import ch.tutteli.tsphp.typechecker.IVisibilityChecker;
import ch.tutteli.tsphp.typechecker.ReferencePhaseController;
import ch.tutteli.tsphp.typechecker.SymbolResolver;
import ch.tutteli.tsphp.typechecker.VisibilityChecker;
import ch.tutteli.tsphp.typechecker.antlrmod.ErrorReportingTSPHPReferenceWalker;
import ch.tutteli.tsphp.typechecker.error.TypeCheckErrorReporterRegistry;
import ch.tutteli.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.IScopeHelper;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.TestSymbolFactory;
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
    protected IReferencePhaseController referencePhaseController;
    protected ISymbolResolver symbolResolver;
    protected IVisibilityChecker visibilityChecker;

    public AReferenceTest(String testString) {
        super(testString);

        init();
    }

    private void init() {
        symbolResolver = createSymbolResolver(
                scopeHelper, symbolFactory, definer.getGlobalNamespaceScopes(), definer.getGlobalDefaultNamespace());
        visibilityChecker = createVisibilityChecker();
        referencePhaseController = createReferencePhaseController(
                symbolFactory, symbolResolver, visibilityChecker, definer.getGlobalDefaultNamespace());
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
        reference = createReferenceWalker(commonTreeNodeStream, referencePhaseController);
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
            Assert.fail(testString + " failed. Unexpected exception occurred in the reference phase.\n"
                    + e.getMessage());
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

    protected ISymbolResolver createSymbolResolver(
            IScopeHelper theScopeHelper,
            TestSymbolFactory theSymbolFactory,
            ILowerCaseStringMap<IGlobalNamespaceScope> namespaceScopes,
            IGlobalNamespaceScope theGlobalDefaultNamespace) {
        return new SymbolResolver(theScopeHelper, theSymbolFactory, namespaceScopes, theGlobalDefaultNamespace);
    }

    protected IVisibilityChecker createVisibilityChecker() {
        return new VisibilityChecker();
    }

    protected IReferencePhaseController createReferencePhaseController(
            ISymbolFactory theSymbolFactory,
            ISymbolResolver theSymbolResolver,
            IVisibilityChecker theVisibilityChecker,
            IGlobalNamespaceScope globalDefaultNamespace) {

        return new ReferencePhaseController(
                theSymbolFactory, theSymbolResolver, theVisibilityChecker, globalDefaultNamespace);
    }

    protected ErrorReportingTSPHPReferenceWalker createReferenceWalker(
            CommonTreeNodeStream theCommonTreeNodeStream, IReferencePhaseController theController) {
        return new ErrorReportingTSPHPReferenceWalker(theCommonTreeNodeStream, theController);
    }
}
