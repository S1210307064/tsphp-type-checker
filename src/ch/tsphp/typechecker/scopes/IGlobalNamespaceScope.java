/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.scopes;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;

/**
 * A global namespace scope contains all corresponding namespaces.
 * <p/>
 * For instance, the global default namespace scope contains all default namespaces which are most probably defined
 * in several files.
 */
public interface IGlobalNamespaceScope extends ICaseInsensitiveScope
{
    /**
     * Return the ITypeSymbol which clashes with the given identifier (the right identifier of a use statement)
     * or null if there is not any type name clash.
     */
    ITypeSymbol getTypeSymbolWhichClashesWithUse(ITSPHPAst identifier);
}
