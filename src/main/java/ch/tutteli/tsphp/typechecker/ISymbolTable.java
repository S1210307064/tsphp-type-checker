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

import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.common.TSPHPAst;
import ch.tutteli.tsphp.typechecker.scopes.IConditionalScope;
import ch.tutteli.tsphp.typechecker.scopes.INamespaceScope;
import ch.tutteli.tsphp.typechecker.symbols.IClassSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import java.util.Map;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public interface ISymbolTable
{

    Map<String, IScope> getGlobalNamespaceScopes();

    INamespaceScope defineNamespace(String name);

    void defineUse(INamespaceScope currentScope, TSPHPAst type);

    void defineUse(INamespaceScope currentScope, TSPHPAst type, String alias);

    void defineConstant(IScope currentScope, TSPHPAst type, TSPHPAst identifier);

    IClassSymbol defineInterface(IScope currentScope, TSPHPAst identifier, TSPHPAst extendsIds);

    IClassSymbol defineClass(IScope currentScope, TSPHPAst modifier, TSPHPAst identifier,
            TSPHPAst extendsIds, TSPHPAst implementsIds);

    IMethodSymbol defineConstruct(IScope currentScope, TSPHPAst methodModifier, TSPHPAst identifier);

    IMethodSymbol defineMethod(IScope currentScope, TSPHPAst methodModifier,
            TSPHPAst returnTypeModifier, TSPHPAst returnType, TSPHPAst identifier);

    IConditionalScope defineConditionalScope(IScope currentScope);

    void defineVariable(IScope currentScope, TSPHPAst modifier, TSPHPAst type, TSPHPAst variableId);

    /**
     * Try to resolve the type for the given typeAst and returns an {@link TSPHPErroneusTypeSymbol} if the type could
     * not be found.
     *
     * @param typeAst The AST node which contains the type name. For instance, int, MyClass, \Exception etc.
     * @return The corresponding type or a {@link TSPHPErroneusTypeSymbol} if could not be found.
     */
    ITypeSymbol resolveType(TSPHPAst typeAst);
    
    ITypeSymbol resolvePrimitiveType(TSPHPAst typeASt);
    
    ISymbol resolve(TSPHPAst ast);

    ISymbol resolveWithFallBack(TSPHPAst ast);
}
