package ch.tutteli.tsphp.typechecker;

import ch.tutteli.tsphp.common.ILowerCaseStringMap;
import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.scopes.IConditionalScope;
import ch.tutteli.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.INamespaceScope;
import ch.tutteli.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IInterfaceTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;

public interface IDefiner
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
