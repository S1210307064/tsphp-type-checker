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
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.TSPHPAst;
import ch.tutteli.tsphp.common.exceptions.ReferenceException;
import ch.tutteli.tsphp.typechecker.antlr.TSPHPTypeCheckerDefinition;
import ch.tutteli.tsphp.typechecker.error.ErrorHelperRegistry;
import ch.tutteli.tsphp.typechecker.scopes.GlobalNamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.IConditionalScope;
import ch.tutteli.tsphp.typechecker.scopes.INamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.IScopeFactory;
import ch.tutteli.tsphp.typechecker.symbols.IAliasSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IClassSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tutteli.tsphp.typechecker.symbols.IVariableSymbol;
import java.util.HashMap;
import java.util.Map;
import org.antlr.runtime.CommonToken;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class SymbolTable implements ISymbolTable
{

    public static String[] compoundTypes = new String[]{"array", "resource", "object"};
    private ISymbolFactory symbolFactory;
    private IScopeFactory scopeFactory;
    private Map<String, IScope> globalNamespaces = new HashMap<>();
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

        globalDefaultNamespace.define(symbolFactory.createPseudoTypeSymbol("resource"));
        ITypeSymbol object = symbolFactory.createPseudoTypeSymbol("object");
        globalDefaultNamespace.define(object);
        globalDefaultNamespace.define(symbolFactory.createArrayTypeSymbol("array", object));
    }

    @Override
    public Map<String, IScope> getGlobalNamespaceScopes() {
        return globalNamespaces;
    }

    @Override
    public INamespaceScope defineNamespace(String name) {
        return scopeFactory.createNamespace(name, getOrCreateGlobalNamespace(name));
    }

    private IScope getOrCreateGlobalNamespace(String name) {
        IScope scope;
        if (globalNamespaces.containsKey(name)) {
            scope = globalNamespaces.get(name);
        } else {
            scope = new GlobalNamespaceScope(name);
            globalNamespaces.put(name, scope);
        }
        return scope;
    }

    @Override
    public void defineUse(INamespaceScope currentScope, ITSPHPAst type, ITSPHPAst alias) {
        type.setScope(currentScope);
        IAliasSymbol aliasSymbol = symbolFactory.createAliasSymbol(alias, alias.getText());
        type.setSymbol(aliasSymbol);
        currentScope.defineUse(aliasSymbol);
    }

    @Override
    public void defineConstant(IScope currentScope, ITSPHPAst type, ITSPHPAst identifier) {
        ITSPHPAst modifier = new TSPHPAst();
        modifier.addChild(new TSPHPAst(new CommonToken(TSPHPTypeCheckerDefinition.Final)));

        // # prevent clashes with class and const identifier
        identifier.getToken().setText("#" + identifier.getText());

        defineVariable(currentScope, modifier, type, identifier);
    }

    @Override
    public IClassSymbol defineInterface(IScope currentScope, ITSPHPAst identifier, ITSPHPAst extendsIds) {
        ITSPHPAst classModifier = new TSPHPAst();
        classModifier.addChild(new TSPHPAst(new CommonToken(TSPHPTypeCheckerDefinition.Abstract, "abstract")));
        return defineClass(currentScope, classModifier, identifier, extendsIds, new TSPHPAst());
    }

    @Override
    public IClassSymbol defineClass(IScope currentScope, ITSPHPAst modifier, ITSPHPAst identifier, ITSPHPAst extendsIds, ITSPHPAst implementsIds) {
        assignScopeToIdentifiers(currentScope, extendsIds);
        assignScopeToIdentifiers(currentScope, implementsIds);
        IClassSymbol classSymbol = symbolFactory.createClassSymbol(modifier, identifier, currentScope);
        identifier.setSymbol(classSymbol);
        currentScope.define(classSymbol);
        return classSymbol;
    }

    @Override
    public IMethodSymbol defineConstruct(IScope currentScope, ITSPHPAst methodModifier, ITSPHPAst identifier) {
        ITSPHPAst returnTypeVoid = new TSPHPAst(new CommonToken(TSPHPTypeCheckerDefinition.Void, "void"));
        IMethodSymbol methodSymbol = defineMethod(currentScope, methodModifier, new TSPHPAst(), returnTypeVoid, identifier);
        ((IClassSymbol) currentScope).setConstruct(methodSymbol);
        return methodSymbol;
    }

    @Override
    public IMethodSymbol defineMethod(IScope currentScope, ITSPHPAst methodModifier,
            ITSPHPAst returnTypeModifier, ITSPHPAst returnType, ITSPHPAst identifier) {
        returnType.setScope(currentScope);

        // () prevent clashes with class and const identifier
        identifier.getToken().setText(identifier.getText() + "()");

        IMethodSymbol methodSymbol = symbolFactory.createMethodSymbol(methodModifier, returnTypeModifier, identifier, currentScope);
        identifier.setSymbol(methodSymbol);
        currentScope.define(methodSymbol);
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
        variableId.setSymbol(variableSymbol);
        currentScope.define(variableSymbol);
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
    public ITypeSymbol resolveType(ITSPHPAst typeAst) {
        IScope scope = getResolvingScope(typeAst);
        ITypeSymbol typeSymbol = scope.resolveType(typeAst);

        if (typeSymbol == null) {
            ReferenceException ex = ErrorHelperRegistry.get().addAndGetUnkownTypeException(typeAst);
            typeSymbol = new TSPHPErroneusTypeSymbol(typeAst, ex);
        }
        return typeSymbol;

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

    private IScope getResolvingScope(ITSPHPAst typeAst) {
        String typeName = typeAst.getText();
        IScope scope = typeAst.getScope();
        if (isFullTypeName(typeName)) {
            scope = getCorrespondingGlobalNamespace(typeName);
        }
        return scope;
    }

    private boolean isFullTypeName(String typeName) {
        return typeName.substring(0, 1).equals("\\");
    }

    private IScope getCorrespondingGlobalNamespace(String typeName) {
        int lastBackslashPosition = typeName.lastIndexOf("\\") + 1;
        String namespaceName = typeName.substring(0, lastBackslashPosition);
        return globalNamespaces.get(namespaceName);
    }

    @Override
    public ITypeSymbol resolvePrimitiveType(ITSPHPAst typeASt) {
        return (ITypeSymbol) globalDefaultNamespace.resolveType(typeASt);
    }
}
