/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.scopes;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.typechecker.symbols.IAliasSymbol;

import java.util.List;
import java.util.Map;

/**
 * A namespace scope contains all symbols defined on the namespace level as well as use definitions.
 */
public interface INamespaceScope extends ICaseInsensitiveScope
{

    void defineUse(IAliasSymbol symbol);

    boolean useDefinitionCheck(IAliasSymbol symbol);

    /**
     * Return one or more AST which contains the use declaration for the alias or null if the alias could not be found.
     *
     * @param alias The alias which shall be found
     * @return A list of symbols or null
     */
    List<IAliasSymbol> getUse(String alias);

    /**
     * Return the corresponding definition ast of the first definition found for the given {@code alias} ignoring case.
     *
     * @param alias The name of the alias which shall be found
     * @return The definition ast or null if the alias wasn't found
     */
    ITSPHPAst getCaseInsensitiveFirstUseDefinitionAst(String alias);

    @Override
    /**
     *  Return only the use definition defined in this namespace scope.
     *
     *  All other definitions are delegated to the corresponding global namespace and can be found there.
     */
    Map<String, List<ISymbol>> getSymbols();
}
