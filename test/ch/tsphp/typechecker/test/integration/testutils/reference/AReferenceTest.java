/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.testutils.reference;

import ch.tsphp.common.ILowerCaseStringMap;
import ch.tsphp.typechecker.AccessResolver;
import ch.tsphp.typechecker.IAccessResolver;
import ch.tsphp.typechecker.IReferencePhaseController;
import ch.tsphp.typechecker.ISymbolResolver;
import ch.tsphp.typechecker.ReferencePhaseController;
import ch.tsphp.typechecker.SymbolResolver;
import ch.tsphp.typechecker.antlrmod.ErrorReportingTSPHPReferenceWalker;
import ch.tsphp.typechecker.error.ITypeCheckerErrorReporter;
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
                scopeHelper,
                symbolFactory,
                typeCheckErrorReporter,
                definer.getGlobalNamespaceScopes(),
                definer.getGlobalDefaultNamespace());

        accessResolver = createAccessResolver(symbolFactory, typeCheckErrorReporter);
        referencePhaseController = createReferencePhaseController(
                symbolFactory, symbolResolver, typeCheckErrorReporter, definer.getGlobalDefaultNamespace());
    }

    protected abstract void verifyReferences();

    protected void checkReferences() {
        assertFalse(testString + " failed. Exceptions occurred." + exceptions, typeCheckErrorReporter.hasFoundError());
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
            ITypeCheckerErrorReporter theTypeCheckerErrorReporter,
            ILowerCaseStringMap<IGlobalNamespaceScope> namespaceScopes,
            IGlobalNamespaceScope theGlobalDefaultNamespace) {
        return new SymbolResolver(
                theScopeHelper,
                theSymbolFactory,
                theTypeCheckerErrorReporter,
                namespaceScopes,
                theGlobalDefaultNamespace);
    }

    protected IAccessResolver createAccessResolver(
            ISymbolFactory theSymbolFactory, ITypeCheckerErrorReporter theTypeCheckerErrorReporter) {
        return new AccessResolver(theSymbolFactory, theTypeCheckerErrorReporter);
    }

    protected IReferencePhaseController createReferencePhaseController(
            ISymbolFactory theSymbolFactory,
            ISymbolResolver theSymbolResolver,
            ITypeCheckerErrorReporter theTypeCheckerErrorReporter,
            IGlobalNamespaceScope globalDefaultNamespace) {
        return new ReferencePhaseController(
                theSymbolFactory, theSymbolResolver, theTypeCheckerErrorReporter, globalDefaultNamespace);
    }

    protected ErrorReportingTSPHPReferenceWalker createReferenceWalker(
            CommonTreeNodeStream theCommonTreeNodeStream,
            IReferencePhaseController theController,
            IAccessResolver theAccessResolver) {
        return new ErrorReportingTSPHPReferenceWalker(theCommonTreeNodeStream, theController, theAccessResolver);
    }
}
