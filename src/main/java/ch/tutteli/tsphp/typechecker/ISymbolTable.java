/*
 * Copyright 2012 Robert Stoll <rstoll@tutteli.ch>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package ch.tutteli.tsphp.typechecker;

import ch.tutteli.tsphp.common.ILowerCaseStringMap;
import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.typechecker.scopes.IConditionalScope;
import ch.tutteli.tsphp.typechecker.scopes.INamespaceScope;
import ch.tutteli.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IInterfaceTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public interface ISymbolTable
{

    ILowerCaseStringMap<IScope> getGlobalNamespaceScopes();

    INamespaceScope defineNamespace(String name);

    void defineUse(INamespaceScope currentScope, ITSPHPAst type, ITSPHPAst alias);

    void defineConstant(IScope currentScope, ITSPHPAst modifier, ITSPHPAst type, ITSPHPAst identifier);

    IInterfaceTypeSymbol defineInterface(IScope currentScope, ITSPHPAst modifier, ITSPHPAst identifier,
            ITSPHPAst extendsIds);

    IClassTypeSymbol defineClass(IScope currentScope, ITSPHPAst modifier, ITSPHPAst identifier,
            ITSPHPAst extendsIds, ITSPHPAst implementsIds);

    IMethodSymbol defineConstruct(IScope currentScope, ITSPHPAst methodModifier,
            ITSPHPAst returnTypeModifier, ITSPHPAst returnType, ITSPHPAst identifier);

    IMethodSymbol defineMethod(IScope currentScope, ITSPHPAst methodModifier,
            ITSPHPAst returnTypeModifier, ITSPHPAst returnType, ITSPHPAst identifier);

    IConditionalScope defineConditionalScope(IScope currentScope);

    void defineVariable(IScope currentScope, ITSPHPAst modifier, ITSPHPAst type, ITSPHPAst variableId);

    ISymbol resolve(ITSPHPAst ast);

    ISymbol resolveWithFallBack(ITSPHPAst ast);

    ITypeSymbol resolveUseType(ITSPHPAst typeAst, ITSPHPAst alias);

    /**
     * Try to resolve the type for the given typeAst and returns an {@link TSPHPErroneusTypeSymbol} if the type could
     * not be found.
     *
     * @param typeAst The AST node which contains the type name. For instance, int, MyClass, \Exception etc.
     * @return The corresponding type or a {@link TSPHPErroneusTypeSymbol} if could not be found.
     */
    ITypeSymbol resolveType(ITSPHPAst typeAst);

    ITypeSymbol resolvePrimitiveType(ITSPHPAst typeASt);
}
