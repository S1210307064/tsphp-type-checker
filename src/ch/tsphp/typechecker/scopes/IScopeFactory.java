package ch.tsphp.typechecker.scopes;

import ch.tsphp.common.IScope;

public interface IScopeFactory
{

    IGlobalNamespaceScope createGlobalNamespaceScope(String name);

    INamespaceScope createNamespaceScope(String name, IGlobalNamespaceScope currentScope);

    IConditionalScope createConditionalScope(IScope currentScope);
}
