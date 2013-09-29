package ch.tutteli.tsphp.typechecker.scopes;

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.symbols.IAliasSymbol;
import java.util.List;

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
}
