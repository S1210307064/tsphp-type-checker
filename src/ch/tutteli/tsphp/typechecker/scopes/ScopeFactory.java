package ch.tutteli.tsphp.typechecker.scopes;

import ch.tutteli.tsphp.common.IScope;

public class ScopeFactory implements IScopeFactory
{

    @Override
    public IGlobalNamespaceScope createGlobalNamespaceScope(String name) {
        return new GlobalNamespaceScope(name);
    }

    @Override
    public INamespaceScope createNamespace(String name, IGlobalNamespaceScope currentScope) {
        return new NamespaceScope(name, currentScope);
    }

    @Override
    public IConditionalScope createConditionalScope(IScope currentScope) {
        return new ConditionalScope(currentScope);
    }
}
