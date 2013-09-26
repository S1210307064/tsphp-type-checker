package ch.tutteli.tsphp.typechecker.scopes;

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.symbols.IAliasSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IInterfaceTypeSymbol;
import java.util.List;

public interface INamespaceScope extends ICaseInsensitiveScope
{

    void defineUse(IAliasSymbol symbol);

    boolean useDefinitionCheck(IAliasSymbol symbol);

    boolean interfaceDefinitionCheck(IInterfaceTypeSymbol symbol);

    boolean classDefinitionCheck(IClassTypeSymbol classTypeSymbol);

    /**
     * Return one or more AST which contains the use declaration for the alias or null if the alias could not be found.
     *
     * @param alias The alias which shall be found
     * @return A list of ASTs or null
     */
    List<IAliasSymbol> getUse(String alias);

    ITSPHPAst getFirstUseDefinitionAst(String alias);
}
