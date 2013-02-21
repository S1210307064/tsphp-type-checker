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
import ch.tutteli.tsphp.common.exceptions.DefinitionException;
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
import ch.tutteli.tsphp.typechecker.symbols.IAliasTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.ICanBeStatic;
import ch.tutteli.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IInterfaceTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tutteli.tsphp.typechecker.symbols.IVariableSymbol;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.IErroneousSymbol;
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
    private IResolver resolver;

    public SymbolTable(ISymbolFactory aSymbolFactory, IScopeFactory aScopeFactory, ITSPHPAstAdaptor theAstAdaptor) {
        symbolFactory = aSymbolFactory;
        scopeFactory = aScopeFactory;
        astAdaptor = theAstAdaptor;
        initTypeSystem();

        resolver = new Resolver(aSymbolFactory, globalNamespaceScopes, globalDefaultNamespace);
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
        identifier.getToken().setText(identifier.getText() + "#");

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
        if (!(symbol instanceof IErroneousSymbol)) {
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
    public IVariableSymbol resolveConstant(ITSPHPAst ast) {
        ISymbol symbol = resolver.resolveGlobalIdentifierWithFallback(ast);
        if (symbol == null) {
            ReferenceException exception = ErrorReporterRegistry.get().notDefined(ast);
            symbol = symbolFactory.createErroneousVariableSymbol(ast, exception);
        }
        return (IVariableSymbol) symbol;
    }

    @Override
    public IVariableSymbol resolveStaticConstant(ITSPHPAst callee, ITSPHPAst id) {
        return resolveStaticVariable(callee, id);
    }

    @Override
    public IMethodSymbol resolveStaticMethod(ITSPHPAst callee, ITSPHPAst id) {
        IMethodSymbol symbol;
        ISymbol calleeSymbol = callee.getSymbol();
        if (!(calleeSymbol instanceof IErroneousSymbol)) {
            symbol = (IMethodSymbol) resolveStatic(callee, id);
        } else {
            IErroneousSymbol erroneousSymbol = (IErroneousSymbol) calleeSymbol;
            symbol = symbolFactory.createErroneousMethodSymbol(id, erroneousSymbol.getException());
        }
        if (symbol == null) {
            ReferenceException exception = ErrorReporterRegistry.get().notDefined(id);
            symbol = symbolFactory.createErroneousMethodSymbol(id, exception);
        }
        return symbol;
    }

    private ICanBeStatic resolveStatic(ITSPHPAst callee, ITSPHPAst id) {
        IClassTypeSymbol classTypeSymbol = (IClassTypeSymbol) callee.getSymbol();
        ICanBeStatic symbol = (ICanBeStatic) classTypeSymbol.resolve(id);
        if (symbol != null && !symbol.isStatic()) {
            ErrorReporterRegistry.get().notStatic(callee);
        }
        return symbol;
    }

    @Override
    public IVariableSymbol resolveStaticVariable(ITSPHPAst callee, ITSPHPAst id) {
        IVariableSymbol symbol;
        ISymbol calleeSymbol = callee.getSymbol();
        if (!(calleeSymbol instanceof IErroneousSymbol)) {
            symbol = (IVariableSymbol) resolveStatic(callee, id);
        } else {
            IErroneousSymbol erroneousSymbol = (IErroneousSymbol) calleeSymbol;
            symbol = symbolFactory.createErroneousVariableSymbol(id, erroneousSymbol.getException());
        }
        if (symbol == null) {
            ReferenceException exception = ErrorReporterRegistry.get().notDefined(id);
            symbol = symbolFactory.createErroneousVariableSymbol(id, exception);
        }
        return symbol;
    }

    @Override
    public IMethodSymbol resolveFunction(ITSPHPAst ast) {
        ISymbol symbol = resolver.resolveGlobalIdentifierWithFallback(ast);
        if (symbol == null) {
            ReferenceException exception = ErrorReporterRegistry.get().notDefined(ast);
            symbol = symbolFactory.createErroneousMethodSymbol(ast, exception);
        }
        return (IMethodSymbol) symbol;
    }

    @Override
    public IVariableSymbol resolveClassConstant(ITSPHPAst ast) {
        return resolveClassMember(ast);
    }

    private ISymbol resolveInClassSymbol(ITSPHPAst ast) {
        IClassTypeSymbol classTypeSymbol = getEnclosingClass(ast);
        ISymbol symbol;
        if (classTypeSymbol != null) {
            symbol = classTypeSymbol.resolveWithFallbackToParent(ast);
        } else {
            ReferenceException exception = ErrorReporterRegistry.get().notInClass(ast);
            symbol = symbolFactory.createErroneousAccessSymbol(ast, exception);
        }
        return symbol;
    }

    @Override
    public IVariableSymbol resolveClassMember(ITSPHPAst ast) {
        ISymbol symbol = resolveInClassSymbol(ast);
        if (symbol == null) {
            ReferenceException exception = ErrorReporterRegistry.get().notDefined(ast);
            symbol = symbolFactory.createErroneousVariableSymbol(ast, exception);
        }
        return (IVariableSymbol) symbol;
    }

    @Override
    public IMethodSymbol resolveMethod(ITSPHPAst callee, ITSPHPAst id) {
        ISymbol symbol = callee.getSymbol().getType();
        IMethodSymbol methodSymbol;
        if (!(symbol instanceof IErroneousSymbol)) {
            if (symbol instanceof IClassTypeSymbol) {
                IClassTypeSymbol classTypeSymbol = (IClassTypeSymbol) symbol;
                methodSymbol = (IMethodSymbol) classTypeSymbol.resolveWithFallbackToParent(id);
            } else {
                DefinitionException exception = ErrorReporterRegistry.get().methodNotDefined(callee, id);
                methodSymbol = symbolFactory.createErroneousMethodSymbol(id, exception);
            }
        }else{
            methodSymbol = symbolFactory.createErroneousMethodSymbol(id, ((IErroneousSymbol)symbol).getException());
        }
        return methodSymbol;
    }

    @Override
    public IVariableSymbol resolveVariable(ITSPHPAst ast) {
        ISymbol symbol = ast.getScope().resolve(ast);
        if (symbol == null) {
            ReferenceException exception = ErrorReporterRegistry.get().notDefined(ast);
            symbol = symbolFactory.createErroneousVariableSymbol(ast, exception);
        }
        return (IVariableSymbol) symbol;
    }

    @Override
    public ITypeSymbol resolveUseType(ITSPHPAst typeAst, ITSPHPAst alias) {
        return resolver.resolveUseType(typeAst, alias);
    }

    @Override
    public ITypeSymbol resolveType(ITSPHPAst typeAst) {
        ITypeSymbol symbol = (ITypeSymbol) resolver.resolveGlobalIdentifier(typeAst);

        if (symbol == null) {
            String typeName = typeAst.getText();
            if (!resolver.isAbsolute(typeName)) {
                typeAst.setText(resolver.getEnclosingGlobalNamespaceScope(typeAst.getScope()).getScopeName() + typeName);
            }
            ReferenceException ex = ErrorReporterRegistry.get().unkownType(typeAst);
            symbol = symbolFactory.createErroneousTypeSymbol(typeAst, ex);

        } else if (symbol instanceof IAliasTypeSymbol) {

            typeAst.setText(symbol.getName());
            ReferenceException ex = ErrorReporterRegistry.get().unkownType(typeAst);
            symbol = symbolFactory.createErroneousTypeSymbol(symbol.getDefinitionAst(), ex);
        }
        return symbol;
    }

    @Override
    public ITypeSymbol resolvePrimitiveType(ITSPHPAst typeASt) {
        return (ITypeSymbol) globalDefaultNamespace.resolve(typeASt);
    }

    @Override
    public IClassTypeSymbol getEnclosingClass(ITSPHPAst ast) {
        IClassTypeSymbol classTypeSymbol = resolver.getEnclosingClass(ast);
        if (classTypeSymbol == null) {
            ReferenceException ex = ErrorReporterRegistry.get().notInClass(ast);
            classTypeSymbol = symbolFactory.createErroneousClassSymbol(ast, ex);
        }
        return classTypeSymbol;
    }

    @Override
    public IClassTypeSymbol getParentClass(ITSPHPAst ast) {
        IClassTypeSymbol classTypeSymbol = getEnclosingClass(ast);
        IClassTypeSymbol parent = classTypeSymbol.getParent();
        if (parent == null) {
            TypeCheckerException ex = ErrorReporterRegistry.get().noParentClass(classTypeSymbol.getDefinitionAst());
            parent = symbolFactory.createErroneousClassSymbol(ast, ex);
        }
        return parent;
    }
}