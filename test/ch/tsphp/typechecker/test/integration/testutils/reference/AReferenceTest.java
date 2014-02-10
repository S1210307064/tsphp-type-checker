package ch.tsphp.typechecker.test.integration.testutils.reference;

import ch.tsphp.common.IErrorReporter;
import ch.tsphp.common.ILowerCaseStringMap;
import ch.tsphp.typechecker.AccessResolver;
import ch.tsphp.typechecker.IAccessResolver;
import ch.tsphp.typechecker.IReferencePhaseController;
import ch.tsphp.typechecker.ISymbolResolver;
import ch.tsphp.typechecker.ReferencePhaseController;
import ch.tsphp.typechecker.SymbolResolver;
import ch.tsphp.typechecker.antlrmod.ErrorReportingTSPHPReferenceWalker;
import ch.tsphp.typechecker.error.TypeCheckErrorReporterRegistry;
import ch.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tsphp.typechecker.scopes.IScopeHelper;
import ch.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tsphp.typechecker.test.integration.testutils.TestSymbolFactory;
import ch.tsphp.typechecker.test.integration.testutils.WriteExceptionToConsole;
import ch.tsphp.typechecker.test.integration.testutils.definition.ADefinitionTest;
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
    protected IAccessResolver accessResolver;

    public AReferenceTest(String testString) {
        super(testString);

        init();
    }

    private void init() {
        symbolResolver = createSymbolResolver(
                scopeHelper, symbolFactory, definer.getGlobalNamespaceScopes(), definer.getGlobalDefaultNamespace());
        accessResolver = createAccessResolver(symbolFactory);
        referencePhaseController = createReferencePhaseController(
                symbolFactory, symbolResolver, definer.getGlobalDefaultNamespace());
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
        reference = createReferenceWalker(commonTreeNodeStream, referencePhaseController, accessResolver);
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

    protected IAccessResolver createAccessResolver(ISymbolFactory theSymbolFactory) {
        return new AccessResolver(theSymbolFactory);
    }

    protected IReferencePhaseController createReferencePhaseController(
            ISymbolFactory theSymbolFactory,
            ISymbolResolver theSymbolResolver,
            IGlobalNamespaceScope globalDefaultNamespace) {

        return new ReferencePhaseController(
                theSymbolFactory, theSymbolResolver, globalDefaultNamespace);
    }

    protected ErrorReportingTSPHPReferenceWalker createReferenceWalker(
            CommonTreeNodeStream theCommonTreeNodeStream,
            IReferencePhaseController theController,
            IAccessResolver theAccessResolver) {
        return new ErrorReportingTSPHPReferenceWalker(theCommonTreeNodeStream, theController, theAccessResolver);
    }
}
