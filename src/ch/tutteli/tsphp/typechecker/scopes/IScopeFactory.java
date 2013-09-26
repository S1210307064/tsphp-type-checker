package ch.tutteli.tsphp.typechecker.scopes;

import ch.tutteli.tsphp.common.IScope;

public interface IScopeFactory
{

    IGlobalNamespaceScope createGlobalNamespaceScope(String name);

    INamespaceScope createNamespace(String name, IScope currentScope);

    IConditionalScope createConditionalScope(IScope currentScope);
}
