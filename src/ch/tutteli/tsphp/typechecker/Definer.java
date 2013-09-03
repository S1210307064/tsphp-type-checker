/*
 * Copyright 2013 Robert Stoll <rstoll@tutteli.ch>
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
import ch.tutteli.tsphp.common.LowerCaseStringMap;
import ch.tutteli.tsphp.typechecker.scopes.IConditionalScope;
import ch.tutteli.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.INamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.IScopeFactory;
import ch.tutteli.tsphp.typechecker.symbols.IAliasSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IInterfaceTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tutteli.tsphp.typechecker.symbols.IVariableSymbol;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class Definer implements IDefiner
{

    private ISymbolFactory symbolFactory;
    private IScopeFactory scopeFactory;
    //
    private ILowerCaseStringMap<IGlobalNamespaceScope> globalNamespaceScopes = new LowerCaseStringMap<>();
    private IGlobalNamespaceScope globalDefaultNamespace;

    public Definer(ISymbolFactory aSymbolFactory, IScopeFactory aScopeFactory) {
        symbolFactory = aSymbolFactory;
        scopeFactory = aScopeFactory;
        globalDefaultNamespace = getOrCreateGlobalNamespace("\\");

    }

    @Override
    public ILowerCaseStringMap<IGlobalNamespaceScope> getGlobalNamespaceScopes() {
        return globalNamespaceScopes;
    }

    @Override
    public IGlobalNamespaceScope getGlobalDefaultNamespace() {
        return globalDefaultNamespace;
    }

    @Override
    public INamespaceScope defineNamespace(String name) {
        return scopeFactory.createNamespace(name, getOrCreateGlobalNamespace(name));
    }

    private IGlobalNamespaceScope getOrCreateGlobalNamespace(String name) {
        IGlobalNamespaceScope scope;
        if (globalNamespaceScopes.containsKey(name)) {
            scope = globalNamespaceScopes.get(name);
        } else {
            scope = scopeFactory.createGlobalNamespaceScope(name);
            globalNamespaceScopes.put(name, scope);
        }
        return scope;
    }

    @Override
    public void defineUse(INamespaceScope currentScope, ITSPHPAst type, ITSPHPAst alias) {
        type.setScope(currentScope);
        IAliasSymbol aliasSymbol = symbolFactory.createAliasSymbol(alias, alias.getText());
        alias.setSymbol(aliasSymbol);
        alias.setScope(currentScope);
        currentScope.defineUse(aliasSymbol);
    }

    @Override
    public void defineConstant(IScope currentScope, ITSPHPAst modifier, ITSPHPAst type, ITSPHPAst identifier) {
        defineVariable(currentScope, modifier, type, identifier);
    }

    @Override
    public IInterfaceTypeSymbol defineInterface(IScope currentScope, ITSPHPAst modifier, ITSPHPAst identifier,
            ITSPHPAst extendsIds) {
        assignScopeToIdentifiers(currentScope, extendsIds);

        IInterfaceTypeSymbol interfaceSymbol = symbolFactory.createInterfaceTypeSymbol(modifier,
                identifier, currentScope);

        define(currentScope, identifier, interfaceSymbol);
        return interfaceSymbol;
    }

    private void assignScopeToIdentifiers(IScope currentScope, ITSPHPAst identifierList) {
        int lenght = identifierList.getChildCount();
        for (int i = 0; i < lenght; ++i) {
            ITSPHPAst ast = identifierList.getChild(i);
            ast.setScope(currentScope);
        }
    }

    private void define(IScope currentScope, ITSPHPAst identifier, ISymbol symbol) {
        identifier.setSymbol(symbol);
        identifier.setScope(currentScope);
        currentScope.define(symbol);
    }

    @Override
    public IClassTypeSymbol defineClass(IScope currentScope, ITSPHPAst modifier, ITSPHPAst identifier,
            ITSPHPAst extendsIds, ITSPHPAst implementsIds) {
        assignScopeToIdentifiers(currentScope, extendsIds);
        assignScopeToIdentifiers(currentScope, implementsIds);
        IClassTypeSymbol classSymbol = symbolFactory.createClassTypeSymbol(modifier, identifier, currentScope);
        define(currentScope, identifier, classSymbol);
        return classSymbol;
    }

    @Override
    public IMethodSymbol defineConstruct(IScope currentScope, ITSPHPAst methodModifier,
            ITSPHPAst returnTypeModifier, ITSPHPAst returnType, ITSPHPAst identifier) {

        IMethodSymbol methodSymbol = defineMethod(currentScope, methodModifier,
                returnTypeModifier, returnType, identifier);

        ((IClassTypeSymbol) currentScope).setConstruct(methodSymbol);
        return methodSymbol;
    }

    @Override
    public IMethodSymbol defineMethod(IScope currentScope, ITSPHPAst methodModifier,
            ITSPHPAst returnTypeModifier, ITSPHPAst returnType, ITSPHPAst identifier) {
        returnType.setScope(currentScope);

        IMethodSymbol methodSymbol = symbolFactory.createMethodSymbol(methodModifier,
                returnTypeModifier, identifier, currentScope);

        define(currentScope, identifier, methodSymbol);
        return methodSymbol;
    }

    @Override
    public IConditionalScope defineConditionalScope(IScope currentScope) {
        return scopeFactory.createConditionalScope(currentScope);
    }

    @Override
    public void defineVariable(IScope currentScope, ITSPHPAst modifier, ITSPHPAst type, ITSPHPAst variableId) {
        type.setScope(currentScope);
        IVariableSymbol variableSymbol = symbolFactory.createVariableSymbol(modifier, variableId);
        define(currentScope, variableId, variableSymbol);
    }
}
