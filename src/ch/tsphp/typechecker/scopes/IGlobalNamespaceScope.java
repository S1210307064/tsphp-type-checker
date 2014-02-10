package ch.tsphp.typechecker.scopes;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.ITypeSymbol;

public interface IGlobalNamespaceScope extends ICaseInsensitiveScope
{
    /**
     * Return the ITypeSymbol which clashes with the given identifier (the right identifier of a use statement)
     * or null if there is not any type name clash.
     */
    ITypeSymbol getTypeSymbolWhichClashesWithUse(ITSPHPAst identifier);
}
