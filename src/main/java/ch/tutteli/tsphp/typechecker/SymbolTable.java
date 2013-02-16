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
import ch.tutteli.tsphp.common.LowerCaseStringMap;
import ch.tutteli.tsphp.common.exceptions.UnresolvedReferenceException;
import ch.tutteli.tsphp.typechecker.error.ErrorHelperRegistry;
import ch.tutteli.tsphp.typechecker.scopes.GlobalNamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.IConditionalScope;
import ch.tutteli.tsphp.typechecker.scopes.INamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.IScopeFactory;
import ch.tutteli.tsphp.typechecker.scopes.ScopeHelperRegistry;
import ch.tutteli.tsphp.typechecker.symbols.AliasTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.ErroneusTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IAliasSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IAliasTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IErroneusTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IInterfaceTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tutteli.tsphp.typechecker.symbols.IVariableSymbol;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class SymbolTable implements ISymbolTable
{

    public static String[] compoundTypes = new String[]{"array", "resource", "object"};
    private ISymbolFactory symbolFactory;
    private IScopeFactory scopeFactory;
    private ILowerCaseStringMap<IScope> globalNamespaceScopes = new LowerCaseStringMap<>();
    private IScope globalDefaultNamespace;

    public SymbolTable(ISymbolFactory aSymbolFactory, IScopeFactory aScopeFactory) {
        symbolFactory = aSymbolFactory;
        scopeFactory = aScopeFactory;

        initTypeSystem();
    }

    private void initTypeSystem() {
        globalDefaultNamespace = getOrCreateGlobalNamespace("\\");

        String[] scalarTypes = new String[]{"bool", "int", "float", "string"};
        for (String type : scalarTypes) {
            globalDefaultNamespace.define(symbolFactory.createScalarTypeSymbol(type));
        }

        ITypeSymbol object = symbolFactory.createPseudoTypeSymbol("object");
        globalDefaultNamespace.define(symbolFactory.createArrayTypeSymbol("array", object));

        globalDefaultNamespace.define(symbolFactory.createPseudoTypeSymbol("resource"));
        globalDefaultNamespace.define(object);
        globalDefaultNamespace.define(symbolFactory.createPseudoTypeSymbol("void"));
    }

    @Override
    public ILowerCaseStringMap<IScope> getGlobalNamespaceScopes() {
        return globalNamespaceScopes;
    }

    @Override
    public INamespaceScope defineNamespace(String name) {
        return scopeFactory.createNamespace(name, getOrCreateGlobalNamespace(name));
    }

    private IScope getOrCreateGlobalNamespace(String name) {
        IScope scope;
        if (globalNamespaceScopes.containsKey(name)) {
            scope = globalNamespaceScopes.get(name);
        } else {
            scope = new GlobalNamespaceScope(name);
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
        // # prevent clashes with class and const identifier
        identifier.getToken().setText("#" + identifier.getText());

        defineVariable(currentScope, modifier, type, identifier);
    }

    @Override
    public IInterfaceTypeSymbol defineInterface(IScope currentScope, ITSPHPAst modifier, ITSPHPAst identifier, ITSPHPAst extendsIds) {
        assignScopeToIdentifiers(currentScope, extendsIds);
        IInterfaceTypeSymbol interfaceSymbol = symbolFactory.createInterfaceTypeSymbol(modifier, identifier, currentScope);
        define(currentScope, identifier, interfaceSymbol);
        return interfaceSymbol;
    }

    @Override
    public IClassTypeSymbol defineClass(IScope currentScope, ITSPHPAst modifier, ITSPHPAst identifier, ITSPHPAst extendsIds, ITSPHPAst implementsIds) {
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

        // () prevent clashes with class and const identifier
        identifier.getToken().setText(identifier.getText() + "()");

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

    private void assignScopeToIdentifiers(IScope currentScope, ITSPHPAst identifierList) {
        int lenght = identifierList.getChildCount();
        for (int i = 0; i < lenght; ++i) {
            ITSPHPAst ast = identifierList.getChild(i);
            ast.setScope(currentScope);
        }
    }

    @Override
    public ISymbol resolve(ITSPHPAst ast) {
        IScope scope = getResolvingScope(ast);
        return scope.resolve(ast);
    }

    @Override
    public ISymbol resolveWithFallBack(ITSPHPAst ast) {
        IScope scope = getResolvingScope(ast);
        ISymbol symbol = scope.resolve(ast);

        if (symbol == null && !scope.equals(globalDefaultNamespace)) {
            symbol = globalDefaultNamespace.resolve(ast);
        }

        ast.setSymbol(symbol);
        return symbol;
    }

    @Override
    public ITypeSymbol resolveUseType(ITSPHPAst typeAst, ITSPHPAst alias) {

        ((IAliasSymbol) alias.getSymbol()).setGlobalNamespaceScopes(globalNamespaceScopes);

        //Alias is always pointing to a full type name. If user has omitted \ at the beginning, then we add it here
        String typeName = typeAst.getText();
        if (!isFullTypeName(typeName)) {
            typeName = "\\" + typeName;
            typeAst.setText(typeName);
        }

        ITypeSymbol typeSymbol = resolveTypeOrReturnNull(typeAst);
        if (typeSymbol == null) {
            typeSymbol =  new AliasTypeSymbol(typeAst,typeAst.getText());
        }
        return typeSymbol;

    }

    private ITypeSymbol resolveTypeOrReturnNull(ITSPHPAst typeAst) {
        ITypeSymbol typeSymbol = null;

        IScope scope = getResolvingScope(typeAst);
        if (scope != null) {
            typeSymbol = scope.resolveType(typeAst);
        }


        if (typeSymbol == null && isRelativeType(typeAst.getText())) {
            typeSymbol = resolveRelativeType(typeAst);
        }
        return typeSymbol;
    }

    @Override
    public ITypeSymbol resolveType(ITSPHPAst typeAst) {
        ITypeSymbol typeSymbol = resolveTypeOrReturnNull(typeAst);

        if (typeSymbol == null) {
            String typeName = typeAst.getText();
            if (!isFullTypeName(typeName)) {
                typeAst.setText(getEnclosingGlobalNamespaceScope(typeAst.getScope()).getScopeName() + typeName);
            }
            UnresolvedReferenceException ex = ErrorHelperRegistry.get().addAndGetUnkownTypeException(typeAst);
            typeSymbol = new ErroneusTypeSymbol(typeAst, ex);
        }else if(typeSymbol instanceof IAliasTypeSymbol){
            typeAst.setText(typeSymbol.getName());
            UnresolvedReferenceException ex = ErrorHelperRegistry.get().addAndGetUnkownTypeException(typeAst);
            typeSymbol = new ErroneusTypeSymbol(typeSymbol.getDefinitionAst(), ex);
        }
        return typeSymbol;
    }

    private IScope getResolvingScope(ITSPHPAst typeAst) {
        String typeName = typeAst.getText();
        IScope scope = typeAst.getScope();
        if (isFullTypeName(typeName)) {
            scope = ScopeHelperRegistry.get().getCorrespondingGlobalNamespace(globalNamespaceScopes, typeName);
        }
        return scope;
    }

    private boolean isFullTypeName(String typeName) {
        return typeName.substring(0, 1).equals("\\");
    }

    private boolean isRelativeType(String typeName) {
        return typeName.indexOf("\\") > 0;
    }

    private ITypeSymbol resolveRelativeType(ITSPHPAst typeAst) {
        IScope enclosingGlobalNamespaceScope = getEnclosingGlobalNamespaceScope(typeAst.getScope());
        String typeName = enclosingGlobalNamespaceScope.getScopeName() + typeAst.getText();
        typeAst.setText(typeName);
        IScope scope = ScopeHelperRegistry.get().getCorrespondingGlobalNamespace(globalNamespaceScopes, typeName);

        ITypeSymbol typeSymbol = null;
        if (scope != null) {
            typeSymbol = scope.resolveType(typeAst);
        }
        return typeSymbol;
    }

    @Override
    public ITypeSymbol resolvePrimitiveType(ITSPHPAst typeASt) {
        return (ITypeSymbol) globalDefaultNamespace.resolveType(typeASt);
    }

    private void define(IScope currentScope, ITSPHPAst identifier, ISymbol symbol) {
        identifier.setSymbol(symbol);
        identifier.setScope(currentScope);
        currentScope.define(symbol);
    }

    private IScope getEnclosingGlobalNamespaceScope(IScope scope) {
        IScope globalNamespaceScope = scope;
        IScope tmp = scope.getEnclosingScope();
        while (tmp != null) {
            globalNamespaceScope = tmp;
            tmp = tmp.getEnclosingScope();
        }
        return globalNamespaceScope;
    }
}
