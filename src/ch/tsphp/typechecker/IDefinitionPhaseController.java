package ch.tsphp.typechecker;

import ch.tsphp.common.ILowerCaseStringMap;
import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.scopes.IConditionalScope;
import ch.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tsphp.typechecker.scopes.INamespaceScope;
import ch.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tsphp.typechecker.symbols.IInterfaceTypeSymbol;
import ch.tsphp.typechecker.symbols.IMethodSymbol;

public interface IDefinitionPhaseController
{

    ILowerCaseStringMap<IGlobalNamespaceScope> getGlobalNamespaceScopes();

    IGlobalNamespaceScope getGlobalDefaultNamespace();

    INamespaceScope defineNamespace(String name);

    void defineUse(INamespaceScope currentScope, ITSPHPAst type, ITSPHPAst alias);

    IClassTypeSymbol defineClass(IScope currentScope, ITSPHPAst modifier, ITSPHPAst identifier,
            ITSPHPAst extendsIds, ITSPHPAst implementsIds);

    IConditionalScope defineConditionalScope(IScope currentScope);

    void defineConstant(IScope currentScope, ITSPHPAst modifier, ITSPHPAst type, ITSPHPAst identifier);

    IMethodSymbol defineConstruct(IScope currentScope, ITSPHPAst methodModifier, ITSPHPAst returnTypeModifier,
            ITSPHPAst returnType, ITSPHPAst identifier);

    IInterfaceTypeSymbol defineInterface(IScope currentScope, ITSPHPAst modifier, ITSPHPAst identifier,
            ITSPHPAst extendsIds);

    IMethodSymbol defineMethod(IScope currentScope, ITSPHPAst methodModifier,
            ITSPHPAst returnTypeModifier, ITSPHPAst returnType, ITSPHPAst identifier);

    void defineVariable(IScope currentScope, ITSPHPAst modifier, ITSPHPAst type, ITSPHPAst variableId);
}
