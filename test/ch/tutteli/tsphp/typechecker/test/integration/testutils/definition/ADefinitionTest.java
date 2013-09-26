package ch.tutteli.tsphp.typechecker.test.integration.testutils.definition;

import ch.tutteli.tsphp.common.AstHelperRegistry;
import ch.tutteli.tsphp.common.IErrorLogger;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITSPHPAstAdaptor;
import ch.tutteli.tsphp.common.ParserUnitDto;
import ch.tutteli.tsphp.common.TSPHPAstAdaptor;
import ch.tutteli.tsphp.common.exceptions.TSPHPException;
import ch.tutteli.tsphp.typechecker.IOverloadResolver;
import ch.tutteli.tsphp.typechecker.ISymbolResolver;
import ch.tutteli.tsphp.typechecker.ITypeCheckerController;
import ch.tutteli.tsphp.typechecker.ITypeSystem;
import ch.tutteli.tsphp.typechecker.OverloadResolver;
import ch.tutteli.tsphp.typechecker.SymbolResolver;
import ch.tutteli.tsphp.typechecker.TypeCheckerController;
import ch.tutteli.tsphp.typechecker.TypeSystem;
import ch.tutteli.tsphp.typechecker.antlr.ErrorReportingTSPHPDefinitionWalker;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.TestScopeFactory;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.TestSymbolFactory;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.ATest;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.TestDefiner;
import ch.tutteli.tsphp.typechecker.utils.AstHelper;
import ch.tutteli.tsphp.typechecker.utils.IAstHelper;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.junit.Assert;
import org.junit.Ignore;

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
    protected IAstHelper astHelper;
    protected ErrorReportingTSPHPDefinitionWalker definition;

    protected void verifyDefinitions() {
        Assert.assertFalse(testString.replaceAll("\n", " ") + " failed - definition phase throw exception", definition.hasFoundError());
    }

    public ADefinitionTest(String theTestString) {
        super();
        testString = theTestString;
        errorMessagePrefix = testString.replaceAll("\n", " ") + "\n" + testString;
        init();
    }

    private void init() {
        adaptor = new TSPHPAstAdaptor();
        astHelper = new AstHelper();
        scopeFactory = new TestScopeFactory();
        TestSymbolFactory symbolFactory = new TestSymbolFactory();
        definer = new TestDefiner(symbolFactory, scopeFactory);
        typeSystem = new TypeSystem(symbolFactory, AstHelperRegistry.get(), definer.getGlobalDefaultNamespace());
        ISymbolResolver symbolResolver = new SymbolResolver(symbolFactory, definer.getGlobalNamespaceScopes(),
                definer.getGlobalDefaultNamespace());
        IOverloadResolver methodResolver = new OverloadResolver(typeSystem);

        controller = new TypeCheckerController(
                symbolFactory,
                typeSystem,
                definer,
                symbolResolver,
                methodResolver,
                astHelper);
    }

    public void check() throws RecognitionException {
        ParserUnitDto parserUnit = parser.parse(testString);
        ast = parserUnit.compilationUnit;

        Assert.assertFalse(testString.replaceAll("\n", " ") + " failed - parser throw exception", parser.hasFoundError());

        commonTreeNodeStream = new CommonTreeNodeStream(adaptor, ast);
        commonTreeNodeStream.setTokenStream(parserUnit.tokenStream);

        definition = new ErrorReportingTSPHPDefinitionWalker(commonTreeNodeStream, controller.getDefiner());
        definition.addErrorLogger(new IErrorLogger()
        {
            @Override
            public void log(TSPHPException exception) {
                System.out.println(exception.getMessage());
            }
        });
        definition.downup(ast);

        Assert.assertFalse(testString.replaceAll("\n", " ") + " failed - definition throw exception",
                definition.hasFoundError());

        verifyDefinitions();
    }
}
