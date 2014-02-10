package ch.tsphp.typechecker.test.integration.testutils;

import ch.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tsphp.typechecker.scopes.INamespaceScope;
import ch.tsphp.typechecker.scopes.IScopeHelper;
import ch.tsphp.typechecker.scopes.ScopeFactory;

import java.util.ArrayList;
import java.util.List;

public class TestScopeFactory extends ScopeFactory
{

    public List<INamespaceScope> scopes = new ArrayList<>();

    public TestScopeFactory(IScopeHelper theScopeHelper) {
        super(theScopeHelper);
    }

    @Override
    public INamespaceScope createNamespaceScope(String name, IGlobalNamespaceScope currentScope) {
        INamespaceScope scope = super.createNamespaceScope(name, currentScope);
        scopes.add(scope);
        return scope;

    }
}
