package ch.tutteli.tsphp.typechecker.scopes;

import ch.tutteli.tsphp.common.IScope;

public class ScopeFactory implements IScopeFactory
{
    private final IScopeHelper scopeHelper;

    public ScopeFactory(IScopeHelper theScopeHelper) {
        scopeHelper = theScopeHelper;
    }

    @Override
    public IGlobalNamespaceScope createGlobalNamespaceScope(String name) {
        return new GlobalNamespaceScope(scopeHelper, name);
    }

    @Override
    public INamespaceScope createNamespace(String name, IGlobalNamespaceScope currentScope) {
        return new NamespaceScope(scopeHelper, name, currentScope);
    }

    @Override
    public IConditionalScope createConditionalScope(IScope currentScope) {
        return new ConditionalScope(scopeHelper, currentScope);
    }
}
