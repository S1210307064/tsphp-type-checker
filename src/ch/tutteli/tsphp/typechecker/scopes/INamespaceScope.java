package ch.tutteli.tsphp.typechecker.scopes;

import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.symbols.IAliasSymbol;
import java.util.List;
import java.util.Map;

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
