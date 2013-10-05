package ch.tutteli.tsphp.typechecker.test.integration.testutils;

import ch.tutteli.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.INamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.IScopeHelper;
import ch.tutteli.tsphp.typechecker.scopes.ScopeFactory;

import java.util.ArrayList;
import java.util.List;

public class TestScopeFactory extends ScopeFactory
{

    public List<INamespaceScope> scopes = new ArrayList<>();

    public TestScopeFactory(IScopeHelper theScopeHelper) {
        super(theScopeHelper);
    }

    @Override
    public INamespaceScope createNamespace(String name, IGlobalNamespaceScope currentScope) {
        INamespaceScope scope = super.createNamespace(name,currentScope);
        scopes.add(scope);
        return scope;

    }
}
