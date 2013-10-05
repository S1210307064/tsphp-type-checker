package ch.tutteli.tsphp.typechecker.test.integration.testutils.reference;

import ch.tutteli.tsphp.common.AstHelperRegistry;
import ch.tutteli.tsphp.typechecker.IDefiner;
import ch.tutteli.tsphp.typechecker.IOverloadResolver;
import ch.tutteli.tsphp.typechecker.ISymbolResolver;
import ch.tutteli.tsphp.typechecker.ITypeCheckerController;
import ch.tutteli.tsphp.typechecker.ITypeSystem;
import ch.tutteli.tsphp.typechecker.OverloadResolver;
import ch.tutteli.tsphp.typechecker.SymbolResolver;
import ch.tutteli.tsphp.typechecker.TypeCheckerController;
import ch.tutteli.tsphp.typechecker.TypeSystem;
import ch.tutteli.tsphp.typechecker.scopes.IScopeFactory;
import ch.tutteli.tsphp.typechecker.scopes.IScopeHelper;
import ch.tutteli.tsphp.typechecker.scopes.ScopeHelper;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.ATest;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.TestDefiner;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.TestScopeFactory;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.TestSymbolFactory;
import ch.tutteli.tsphp.typechecker.utils.AstHelper;
import org.junit.Ignore;

@Ignore
public class ATypeSystemTest extends ATest
{

    protected IScopeFactory scopeFactory;
    protected ITypeCheckerController controller;

    public ATypeSystemTest() {
        super();
        IScopeHelper scopeHelper = new ScopeHelper();

        scopeFactory = new TestScopeFactory(scopeHelper);
        TestSymbolFactory symbolFactory = new TestSymbolFactory(scopeHelper);
        IDefiner definer = new TestDefiner(symbolFactory, scopeFactory);

        ITypeSystem typeSystem = new TypeSystem(
                symbolFactory,
                AstHelperRegistry.get(),
                definer.getGlobalDefaultNamespace());

        ISymbolResolver symbolResolver = new SymbolResolver(
                scopeHelper,
                symbolFactory,
                definer.getGlobalNamespaceScopes(),
                definer.getGlobalDefaultNamespace());

        IOverloadResolver methodResolver = new OverloadResolver(typeSystem);

        controller = new TypeCheckerController(
                symbolFactory,
                typeSystem,
                definer,
                symbolResolver,
                methodResolver,
                new AstHelper());
    }
}
