package ch.tutteli.tsphp.typechecker.symbols;

import ch.tutteli.tsphp.common.ILowerCaseStringMap;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.typechecker.scopes.IGlobalNamespaceScope;

public interface IAliasSymbol extends ISymbol
{

    ILowerCaseStringMap<IGlobalNamespaceScope> getGlobalNamespaceScopes();

    void setGlobalNamespaceScopes(ILowerCaseStringMap<IGlobalNamespaceScope> globalNamespaceScopes);
}
