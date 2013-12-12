package ch.tutteli.tsphp.typechecker.test.integration.testutils.definition;

import ch.tutteli.tsphp.common.AstHelperRegistry;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITSPHPAstAdaptor;
import ch.tutteli.tsphp.common.ParserUnitDto;
import ch.tutteli.tsphp.common.TSPHPAstAdaptor;
import ch.tutteli.tsphp.typechecker.IOverloadResolver;
import ch.tutteli.tsphp.typechecker.ISymbolResolver;
import ch.tutteli.tsphp.typechecker.ITypeCheckerController;
import ch.tutteli.tsphp.typechecker.ITypeSystem;
import ch.tutteli.tsphp.typechecker.OverloadResolver;
import ch.tutteli.tsphp.typechecker.SymbolResolver;
import ch.tutteli.tsphp.typechecker.TypeCheckerController;
import ch.tutteli.tsphp.typechecker.TypeSystem;
import ch.tutteli.tsphp.typechecker.antlrmod.ErrorReportingTSPHPDefinitionWalker;
import ch.tutteli.tsphp.typechecker.scopes.IScopeHelper;
import ch.tutteli.tsphp.typechecker.scopes.ScopeHelper;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.ATest;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.TestDefiner;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.TestScopeFactory;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.TestSymbolFactory;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.WriteExceptionToConsole;
import ch.tutteli.tsphp.typechecker.utils.ITypeCheckerAstHelper;
import ch.tutteli.tsphp.typechecker.utils.TypeCheckerAstHelper;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.junit.Assert;
import org.junit.Ignore;

import static org.junit.Assert.assertFalse;

@Ignore
public abstract class ADefinitionTest extends ATest
{

    protected String testString;
    protected String errorMessagePrefix;
    protected TestDefiner definer;
    protected ITypeCheckerController controller;
    protected ITypeSystem typeSystem;
    protected TestScopeFactory scopeFactory;
    protected ITSPHPAst ast;
    protected CommonTreeNodeStream commonTreeNodeStream;
    protected ITSPHPAstAdaptor adaptor;
    protected ITypeCheckerAstHelper typeCheckerAstHelper;
    protected ErrorReportingTSPHPDefinitionWalker definition;
    protected TestSymbolFactory symbolFactory;
    protected ISymbolResolver symbolResolver;
    protected IOverloadResolver methodResolver;
    protected IScopeHelper scopeHelper;


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
        IScopeHelper scopeHelper = createScopeHelper();

        adaptor = createAstAdaptor();
        typeCheckerAstHelper = createTypeCheckerAstHelper();
        scopeFactory = createTestScopeFactory(scopeHelper);

        symbolFactory = createSymbolFactory(scopeHelper);
        definer = createTestDefiner(symbolFactory, scopeFactory);
        typeSystem = createTypeSystem(symbolFactory);

        symbolResolver = createSymbolResolver(scopeHelper, symbolFactory);

        methodResolver = createOverloadResolver(typeSystem);

        controller = createTypeCheckerController(symbolFactory, typeSystem, definer, symbolResolver, methodResolver);
    }

    protected void verifyParser() {
        assertFalse(testString.replaceAll("\n", " ") + " failed - parser throw exception", parser.hasFoundError());
    }

    public void check() {
        ParserUnitDto parserUnit = parser.parse(testString);
        ast = parserUnit.compilationUnit;

        verifyParser();

        commonTreeNodeStream = new CommonTreeNodeStream(adaptor, ast);
        //commonTreeNodeStream.setTokenStream(parserUnit.tokenStream);

        definition = new ErrorReportingTSPHPDefinitionWalker(commonTreeNodeStream, controller.getDefiner());
        definition.registerErrorLogger(new WriteExceptionToConsole());
        try {
            definition.downup(ast);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(testString + " failed. Unexpected exception occurred in the definition phase.\n" + e
                    .getMessage());
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


    protected ITypeCheckerAstHelper createTypeCheckerAstHelper() {
        return new TypeCheckerAstHelper();
    }


    protected TestScopeFactory createTestScopeFactory(IScopeHelper theScopeHelper) {
        return new TestScopeFactory(theScopeHelper);
    }

    protected TestSymbolFactory createSymbolFactory(IScopeHelper theScopeHelper) {
        return new TestSymbolFactory(theScopeHelper);
    }

    protected TestDefiner createTestDefiner(TestSymbolFactory theSymbolFactory, TestScopeFactory theScopeFactory) {
        return new TestDefiner(theSymbolFactory, theScopeFactory);
    }

    protected ITypeSystem createTypeSystem(TestSymbolFactory symbolFactory) {
        return new TypeSystem(symbolFactory, AstHelperRegistry.get(), definer.getGlobalDefaultNamespace());
    }

    protected ISymbolResolver createSymbolResolver(IScopeHelper theScopeHelper, TestSymbolFactory theSymbolFactory) {
        return new SymbolResolver(
                theScopeHelper,
                theSymbolFactory,
                definer.getGlobalNamespaceScopes(),
                definer.getGlobalDefaultNamespace());
    }

    protected IOverloadResolver createOverloadResolver(ITypeSystem theTypeSystem) {
        return new OverloadResolver(theTypeSystem);
    }

    protected ITypeCheckerController createTypeCheckerController(
            TestSymbolFactory theSymbolFactory,
            ITypeSystem theTypeSystem,
            TestDefiner theDefiner,
            ISymbolResolver theSymbolResolver,
            IOverloadResolver theMethodResolver) {
        return new TypeCheckerController(
                theSymbolFactory,
                theTypeSystem,
                theDefiner,
                theSymbolResolver,
                theMethodResolver,
                typeCheckerAstHelper);
    }
}
