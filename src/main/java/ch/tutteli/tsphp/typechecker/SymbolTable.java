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
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.common.TSPHPAst;
import ch.tutteli.tsphp.common.exceptions.ReferenceException;
import ch.tutteli.tsphp.common.exceptions.TypeCheckerException;
import ch.tutteli.tsphp.typechecker.antlr.TSPHPTypeCheckerDefinition;
import ch.tutteli.tsphp.typechecker.error.ErrorHelperRegistry;
import ch.tutteli.tsphp.typechecker.scopes.GlobalNamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.IConditionalScope;
import ch.tutteli.tsphp.typechecker.scopes.INamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.IScopeFactory;
import ch.tutteli.tsphp.typechecker.symbols.BuiltInTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IClassSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tutteli.tsphp.typechecker.symbols.IVariableSymbol;
import ch.tutteli.tsphp.typechecker.symbols.ScalarTypeSymbol;
import java.util.HashMap;
import java.util.Map;
import org.antlr.runtime.CommonToken;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class SymbolTable implements ISymbolTable
{

    public static String[] scalarTypes = new String[]{"bool", "int", "float", "string"};
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

        for (String type : scalarTypes) {
            globalDefaultNamespace.define(new ScalarTypeSymbol(type));
        }

        for (String type : compoundTypes) {
            globalDefaultNamespace.define(new BuiltInTypeSymbol(type));
        }
    }

    @Override
    public Map<String, IScope> getGlobalNamespaceScopes() {
        return globalNamespaces;
    }

    @Override
    public void defineUse(IScope currentScope, TSPHPAst type) {
        String alias = type.getText();
        alias = alias.substring(alias.lastIndexOf("\\"));
        defineUse(currentScope, type, alias);
    }

    @Override
    public void defineUse(IScope currentScope, TSPHPAst type, String alias) {
        type.scope = currentScope;
        ((INamespaceScope) currentScope).addUse(alias, type);
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
    public void defineConstant(IScope currentScope, TSPHPAst type, TSPHPAst identifier) {
        TSPHPAst modifier = new TSPHPAst();
        modifier.addChild(new TSPHPAst(new CommonToken(TSPHPTypeCheckerDefinition.Final)));

        // # prevent clashes with class and const identifier
        identifier.getToken().setText("#" + identifier.getText());

        defineVariable(currentScope, modifier, type, identifier);
    }

    @Override
    public IClassSymbol defineInterface(IScope currentScope, TSPHPAst identifier, TSPHPAst extendsIds) {
        TSPHPAst classModifier = new TSPHPAst();
        classModifier.addChild(new TSPHPAst(new CommonToken(TSPHPTypeCheckerDefinition.Abstract, "abstract")));
        return defineClass(currentScope, classModifier, identifier, extendsIds, new TSPHPAst());
    }

    @Override
    public IClassSymbol defineClass(IScope currentScope, TSPHPAst modifier, TSPHPAst identifier, TSPHPAst extendsIds, TSPHPAst implementsIds) {
        assignScopeToIdentifiers(currentScope, extendsIds);
        assignScopeToIdentifiers(currentScope, implementsIds);
        IClassSymbol classSymbol = symbolFactory.createClassSymbol(modifier, identifier, currentScope);
        identifier.symbol = classSymbol;
        currentScope.define(classSymbol);
        return classSymbol;
    }

    @Override
    public IMethodSymbol defineConstruct(IScope currentScope, TSPHPAst methodModifier, TSPHPAst identifier) {
        TSPHPAst returnTypeVoid = new TSPHPAst(new CommonToken(TSPHPTypeCheckerDefinition.Void, "void"));
        IMethodSymbol methodSymbol = defineMethod(currentScope, methodModifier, new TSPHPAst(), returnTypeVoid, identifier);
        ((IClassSymbol) currentScope).setConstruct(methodSymbol);
        return methodSymbol;
    }

    @Override
    public IMethodSymbol defineMethod(IScope currentScope, TSPHPAst methodModifier,
            TSPHPAst returnTypeModifier, TSPHPAst returnType, TSPHPAst identifier) {
        returnType.scope = currentScope;

        // () prevent clashes with class and const identifier
        identifier.getToken().setText(identifier.getText() + "()");

        IMethodSymbol methodSymbol = symbolFactory.createMethodSymbol(methodModifier, returnTypeModifier, identifier, currentScope);
        identifier.symbol = methodSymbol;
        currentScope.define(methodSymbol);
        return methodSymbol;
    }

    @Override
    public IConditionalScope defineConditionalScope(IScope currentScope) {
        return scopeFactory.createConditionalScope(currentScope);
    }

    @Override
    public void defineVariable(IScope currentScope, TSPHPAst modifier, TSPHPAst type, TSPHPAst variableId) {
        type.scope = currentScope;
        IVariableSymbol variableSymbol = symbolFactory.createVariableSymbol(modifier, variableId);
        variableId.symbol = variableSymbol;
        currentScope.define(variableSymbol);
    }

    private void assignScopeToIdentifiers(IScope currentScope, TSPHPAst identifierList) {
        int lenght = identifierList.getChildCount();
        for (int i = 0; i < lenght; ++i) {
            TSPHPAst ast = (TSPHPAst) identifierList.getChild(i);
            ast.scope = currentScope;
        }
    }

    @Override
    public ITypeSymbol resolveType(TSPHPAst typeAst) {
        ITypeSymbol type;

        IScope scope = getResolvingScope(typeAst);
        type = scope.resolveType(typeAst);
       
        if (type == null && !scope.equals(globalDefaultNamespace)) {
            type = globalDefaultNamespace.resolveType(typeAst);
        }
        if (type == null){
            ReferenceException ex = ErrorHelperRegistry.get().addAndGetUnkownTypeException(typeAst);
            type = new TSPHPErroneusTypeAst(typeAst, ex);
        }
        
        typeAst.symbol = type;
        return type;
    }

    private IScope getResolvingScope(TSPHPAst typeAst) {
        String typeName = typeAst.getText();
        IScope scope = typeAst.scope;
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
}
