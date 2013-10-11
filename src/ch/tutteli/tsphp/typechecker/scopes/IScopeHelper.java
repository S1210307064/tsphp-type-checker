package ch.tutteli.tsphp.typechecker.scopes;

import ch.tutteli.tsphp.common.ILowerCaseStringMap;
import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import java.util.List;
import java.util.Map;

public interface IScopeHelper
{

    void define(IScope definitionScope, ISymbol symbol);

    boolean doubleDefinitionCheck(Map<String, List<ISymbol>> symbols, ISymbol symbol);

    boolean doubleDefinitionCheck(Map<String, List<ISymbol>> symbols, ISymbol symbol,
            IAlreadyDefinedMethodCaller errorMethodCaller);

    boolean doubleDefinitionCheck(ISymbol firstDefinition, ISymbol symbolToCheck);

    boolean doubleDefinitionCheck(ISymbol firstDefinition, ISymbol symbolToCheck,
            IAlreadyDefinedMethodCaller errorMethodCaller);

    /**
     * Return the corresponding global namespace from the given globalNamespaceScopes for the given typeName.
     *
     * As a quick reminder, namespace identifier always end with an \ (backslash) for instance:
     *
     * - \
     * - \ch\
     * - \ch\tutteli\
     *
     * @return The corresponding global namespace or null in the case where it could not be found
     */
    IGlobalNamespaceScope getCorrespondingGlobalNamespace(
            ILowerCaseStringMap<IGlobalNamespaceScope> globalNamespaceScopes, String typeName);

    ISymbol resolve(IScope scope, ITSPHPAst ast);
}
