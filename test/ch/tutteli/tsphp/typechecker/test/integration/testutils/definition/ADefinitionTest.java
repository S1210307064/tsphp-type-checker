package ch.tutteli.tsphp.typechecker.test.integration.testutils.definition;

import ch.tutteli.tsphp.common.AstHelperRegistry;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITSPHPAstAdaptor;
import ch.tutteli.tsphp.common.ParserUnitDto;
import ch.tutteli.tsphp.common.TSPHPAstAdaptor;
import ch.tutteli.tsphp.typechecker.ITypeSystem;
import ch.tutteli.tsphp.typechecker.TypeSystem;
import ch.tutteli.tsphp.typechecker.antlrmod.ErrorReportingTSPHPDefinitionWalker;
import ch.tutteli.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.IScopeHelper;
import ch.tutteli.tsphp.typechecker.scopes.ScopeHelper;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.ATest;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.TestDefinitionPhaseController;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.TestScopeFactory;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.TestSymbolFactory;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.WriteExceptionToConsole;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.junit.Assert;
import org.junit.Ignore;

import static org.junit.Assert.assertFalse;

@Ignore
public abstract class ADefinitionTest extends ATest
{

    protected String testString;
    protected String errorMessagePrefix;
    protected TestDefinitionPhaseController definer;
    protected TestScopeFactory scopeFactory;
    protected ITSPHPAst ast;
    protected CommonTreeNodeStream commonTreeNodeStream;
    protected ITSPHPAstAdaptor adaptor;

    protected ErrorReportingTSPHPDefinitionWalker definition;
    protected TestSymbolFactory symbolFactory;
    protected IScopeHelper scopeHelper;
    protected ITypeSystem typeSystem;


    protected void verifyDefinitions() {
        assertFalse(testString.replaceAll("\n", " ") + " failed - definition phase throw exception",
                definition.hasFoundError());
    }

    public ADefinitionTest(String theTestString) {
        super();
        testString = theTestString;
        errorMessagePrefix = testString.replaceAll("\n", " ") + "\n" + testString;
        init();
    }

    private void init() {
        adaptor = createAstAdaptor();

        scopeHelper = createScopeHelper();
        scopeFactory = createTestScopeFactory(scopeHelper);
        symbolFactory = createTestSymbolFactory(scopeHelper);

        definer = createTestDefiner(symbolFactory, scopeFactory);
        typeSystem = createTypeSystem(symbolFactory, definer.getGlobalDefaultNamespace());
    }

    protected void verifyParser() {
        assertFalse(testString.replaceAll("\n", " ") + " failed - parser throw exception", parser.hasFoundError());
    }

    public void check() {
        ParserUnitDto parserUnit = parser.parse(testString);
        ast = parserUnit.compilationUnit;

        verifyParser();

        commonTreeNodeStream = new CommonTreeNodeStream(adaptor, ast);
        commonTreeNodeStream.setTokenStream(parserUnit.tokenStream);

        definition = new ErrorReportingTSPHPDefinitionWalker(commonTreeNodeStream, definer);
        definition.registerErrorLogger(new WriteExceptionToConsole());
        try {
            definition.downup(ast);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(testString + " failed. Unexpected exception occurred in the definition phase.\n"
                    + e.getMessage());
        }

        assertFalse(testString.replaceAll("\n", " ") + " failed - definition throw exception",
                definition.hasFoundError());

        verifyDefinitions();
    }

    protected IScopeHelper createScopeHelper() {
        return new ScopeHelper();
    }

    protected ITSPHPAstAdaptor createAstAdaptor() {
        return new TSPHPAstAdaptor();
    }

    protected TestScopeFactory createTestScopeFactory(IScopeHelper theScopeHelper) {
        return new TestScopeFactory(theScopeHelper);
    }

    protected TestSymbolFactory createTestSymbolFactory(IScopeHelper theScopeHelper) {
        return new TestSymbolFactory(theScopeHelper);
    }

    protected ITypeSystem createTypeSystem(
            TestSymbolFactory symbolFactory, IGlobalNamespaceScope theGlobalNamespaceScope) {
        return new TypeSystem(symbolFactory, AstHelperRegistry.get(), theGlobalNamespaceScope);
    }

    protected TestDefinitionPhaseController createTestDefiner(TestSymbolFactory theSymbolFactory,
            TestScopeFactory theScopeFactory) {
        return new TestDefinitionPhaseController(theSymbolFactory, theScopeFactory);
    }
}
