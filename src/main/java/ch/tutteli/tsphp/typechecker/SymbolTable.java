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
import ch.tutteli.tsphp.common.ITSPHPAstAdaptor;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.common.LowerCaseStringMap;
import ch.tutteli.tsphp.common.exceptions.ReferenceException;
import ch.tutteli.tsphp.common.exceptions.TypeCheckerException;
import ch.tutteli.tsphp.typechecker.antlr.TSPHPTypeCheckerDefinition;
import ch.tutteli.tsphp.typechecker.error.ErrorReporterRegistry;
import ch.tutteli.tsphp.typechecker.scopes.GlobalNamespaceScope;
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
import ch.tutteli.tsphp.typechecker.symbols.MethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.IErroneousTypeSymbol;
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
    private ITSPHPAstAdaptor astAdaptor;
    private ILowerCaseStringMap<IGlobalNamespaceScope> globalNamespaceScopes = new LowerCaseStringMap<>();
    private IGlobalNamespaceScope globalDefaultNamespace;
    private TypeResolver typeResolver;

    public SymbolTable(ISymbolFactory aSymbolFactory, IScopeFactory aScopeFactory, ITSPHPAstAdaptor theAstAdaptor) {
        symbolFactory = aSymbolFactory;
        scopeFactory = aScopeFactory;
        astAdaptor = theAstAdaptor;
        typeResolver = new TypeResolver(aSymbolFactory, globalNamespaceScopes);

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

        //predefiend classes
        ITSPHPAst classModifier = createAst(TSPHPTypeCheckerDefinition.CLASS_MODIFIER, "cMod");
        ITSPHPAst identifier = createAst(TSPHPTypeCheckerDefinition.TYPE_NAME, "Exception");
        globalDefaultNamespace.define(symbolFactory.createClassTypeSymbol(classModifier, identifier, globalDefaultNamespace));
    }

    private ITSPHPAst createAst(int tokenType, String name) {
        return (ITSPHPAst) astAdaptor.create(0, new CommonToken(tokenType, name));
    }

    @Override
    public ILowerCaseStringMap<IGlobalNamespaceScope> getGlobalNamespaceScopes() {
        return globalNamespaceScopes;
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

    private void define(IScope currentScope, ITSPHPAst identifier, ISymbol symbol) {
        identifier.setSymbol(symbol);
        identifier.setScope(currentScope);
        currentScope.define(symbol);
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

    @Override
    public boolean checkIfInterface(ITSPHPAst typeAst, ITypeSymbol symbol) {
        boolean isInterface = symbol instanceof IInterfaceTypeSymbol || symbol instanceof IErroneousTypeSymbol;
        if (!isInterface) {
            ErrorReporterRegistry.get().interfaceExpected(typeAst);
        }
        return isInterface;
    }

    @Override
    public boolean checkIfClass(ITSPHPAst typeAst, ITypeSymbol symbol) {
        boolean isClass = symbol instanceof IClassTypeSymbol || symbol instanceof IErroneousTypeSymbol;
        if (!isClass) {
            ErrorReporterRegistry.get().classExpected(typeAst);
        }
        return isClass;
    }

    @Override
    public boolean checkForwardReference(ITSPHPAst ast) {
        ISymbol symbol = ast.getSymbol();
        boolean isNotUsedBefore = true;
        //only check if not already an error occured in conjunction with this ast (for instance missing declaration)
        if (!(symbol instanceof IErroneousTypeSymbol)) {
            ITSPHPAst definitionAst = symbol.getDefinitionAst();
            isNotUsedBefore = definitionAst.isDefinedEarlierThan(ast);
            if (!isNotUsedBefore) {
                ErrorReporterRegistry.get().forwardReference(ast, definitionAst);
            }
        }
        return isNotUsedBefore;
    }

    private void assignScopeToIdentifiers(IScope currentScope, ITSPHPAst identifierList) {
        int lenght = identifierList.getChildCount();
        for (int i = 0; i < lenght; ++i) {
            ITSPHPAst ast = identifierList.getChild(i);
            ast.setScope(currentScope);
        }
    }

    @Override
    public ISymbol resolveWithFallbackToDefaultNamespace(ITSPHPAst ast) {
        IScope scope = typeResolver.getResolvingScope(ast);
        ISymbol symbol = scope.resolve(ast);

        if (symbol == null && !scope.equals(globalDefaultNamespace)) {
            symbol = globalDefaultNamespace.resolve(ast);
        }

        ast.setSymbol(symbol);
        return symbol;
    }

    @Override
    public IClassTypeSymbol getEnclosingClass(ITSPHPAst ast) {
        IClassTypeSymbol classTypeSymbol;
        IScope scope = ast.getScope();
        while (scope != null && !(scope instanceof IClassTypeSymbol)) {
            scope = scope.getEnclosingScope();
        }
        if (scope != null) {
            classTypeSymbol = (IClassTypeSymbol) scope;
        } else {
            ReferenceException ex = ErrorReporterRegistry.get().notInClass(ast);
            classTypeSymbol = symbolFactory.createErroneusClassSymbol(ast, ex);
        }
        return classTypeSymbol;
    }

    @Override
    public IClassTypeSymbol getParentClass(ITSPHPAst ast) {
        IClassTypeSymbol classTypeSymbol = getEnclosingClass(ast);
        IClassTypeSymbol parent = classTypeSymbol.getParent();
        if (parent == null) {
            TypeCheckerException ex = ErrorReporterRegistry.get().noParentClass(classTypeSymbol.getDefinitionAst());
            classTypeSymbol = symbolFactory.createErroneusClassSymbol(ast, ex);
        }
        return classTypeSymbol;
    }

    @Override
    public ISymbol resolveClassMember(ITSPHPAst ast) {
        IScope scope = ast.getScope();
        ISymbol symbol = null;
        if ((scope instanceof MethodSymbol)) {
            IClassTypeSymbol classTypeSymbol = (IClassTypeSymbol) scope.getEnclosingScope();
            symbol = classTypeSymbol.resolveWithFallbackToParent(ast);
        }
        if (symbol == null) {
            symbol = symbolFactory.createErroneusAccessSymbol(ast, null);
        }
        ast.setSymbol(symbol);
        return symbol;
    }

    @Override
    public ITypeSymbol resolveUseType(ITSPHPAst typeAst, ITSPHPAst alias) {
        return typeResolver.resolveUseType(typeAst, alias);
    }

    @Override
    public ITypeSymbol resolveType(ITSPHPAst typeAst) {
        return typeResolver.resolveType(typeAst);
    }

    @Override
    public ITypeSymbol resolvePrimitiveType(ITSPHPAst typeASt) {
        return (ITypeSymbol) globalDefaultNamespace.resolveType(typeASt);
    }

    @Override
    public ISymbol resolve(ITSPHPAst ast) {
        ISymbol symbol = ast.getScope().resolve(ast);
        if (symbol == null) {
            ReferenceException exception = ErrorReporterRegistry.get().notDefined(ast);
            symbol = symbolFactory.createErroneusTypeSymbol(ast, exception);
        }
        return symbol;
    }
}