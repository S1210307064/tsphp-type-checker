/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

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
