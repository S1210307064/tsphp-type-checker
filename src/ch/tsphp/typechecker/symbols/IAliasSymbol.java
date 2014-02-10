package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.ILowerCaseStringMap;
import ch.tsphp.common.ISymbol;
import ch.tsphp.typechecker.scopes.IGlobalNamespaceScope;

public interface IAliasSymbol extends ISymbol
{

    ILowerCaseStringMap<IGlobalNamespaceScope> getGlobalNamespaceScopes();

    void setGlobalNamespaceScopes(ILowerCaseStringMap<IGlobalNamespaceScope> globalNamespaceScopes);
}
