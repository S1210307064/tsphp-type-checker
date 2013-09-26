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

    IGlobalNamespaceScope getCorrespondingGlobalNamespace(
            ILowerCaseStringMap<IGlobalNamespaceScope> globalNamespaceScopes, String typeName);

    ISymbol resolve(IScope scope, ITSPHPAst ast);
}
