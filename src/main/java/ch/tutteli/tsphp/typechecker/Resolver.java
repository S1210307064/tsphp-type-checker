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
import ch.tutteli.tsphp.common.exceptions.DefinitionException;
import ch.tutteli.tsphp.common.exceptions.ReferenceException;
import ch.tutteli.tsphp.common.exceptions.TypeCheckerException;
import ch.tutteli.tsphp.typechecker.error.ErrorReporterRegistry;
import ch.tutteli.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.INamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.ScopeHelperRegistry;
import ch.tutteli.tsphp.typechecker.symbols.IAliasSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IAliasTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolFactory;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class Resolver implements IResolver
{

    private ISymbolFactory symbolFactory;
    private ILowerCaseStringMap<IGlobalNamespaceScope> globalNamespaceScopes = new LowerCaseStringMap<>();
    private IGlobalNamespaceScope globalDefaultNamespace;

    public Resolver(ISymbolFactory theSymbolFactory,
            ILowerCaseStringMap<IGlobalNamespaceScope> theGlobalNamespaceScopes,
            IGlobalNamespaceScope theGlobalDefaultNamespace) {

        symbolFactory = theSymbolFactory;
        globalNamespaceScopes = theGlobalNamespaceScopes;
        globalDefaultNamespace = theGlobalDefaultNamespace;
    }

    @Override
    public ITypeSymbol resolveUseType(ITSPHPAst typeAst, ITSPHPAst alias) {
        ((IAliasSymbol) alias.getSymbol()).setGlobalNamespaceScopes(globalNamespaceScopes);

        //Alias is always pointing to a full type name. If user has omitted \ at the beginning, then we add it here
        String typeName = typeAst.getText();
        if (!isAbsolute(typeName)) {
            typeName = "\\" + typeName;
            typeAst.setText(typeName);
        }

        ITypeSymbol aliasType = (ITypeSymbol) resolveGlobalIdentifier(typeAst);
        if (aliasType == null) {
            aliasType = symbolFactory.createAliasTypeSymbol(typeAst, typeAst.getText());
        }

        return aliasType;
    }

    @Override
    public boolean isAbsolute(String typeName) {
        return typeName.substring(0, 1).equals("\\");
    }

    @Override
    public ISymbol resolveGlobalIdentifierWithFallback(ITSPHPAst ast) {
        ISymbol symbol = resolveGlobalIdentifier(ast);
        if (symbol == null) {
            symbol = globalDefaultNamespace.resolve(ast);
        }
        return symbol;
    }

    @Override
    public ISymbol resolveGlobalIdentifier(ITSPHPAst typeAst) {
        ISymbol symbol = null;

        String identifier = typeAst.getText();
        if (isAbsolute(identifier)) {
            IGlobalNamespaceScope scope = ScopeHelperRegistry.get()
                    .getCorrespondingGlobalNamespace(globalNamespaceScopes, identifier);
            if (scope != null) {
                symbol = scope.resolve(typeAst);
            }
        } else {
            INamespaceScope scope = getEnclosingNamespaceScope(typeAst);
            if (scope != null) {
                symbol = scope.resolve(typeAst);

                String alias = getPotentialAlias(typeAst.getText());
                ITSPHPAst useDefinition = scope.getFirstUseDefinitionAst(alias);
                useDefinition = checkTypeNameClashAndRecoverIfNecessary(useDefinition, symbol);

                if (useDefinition != null) {
                    symbol = resolveAlias(useDefinition, alias, typeAst);
                }
            }
            if (symbol == null && isRelative(typeAst.getText())) {
                symbol = resolveRelative(typeAst);
            }
        }
        return symbol;
    }

    private INamespaceScope getEnclosingNamespaceScope(ITSPHPAst ast) {
        INamespaceScope namespaceScope = null;

        IScope scope = ast.getScope();
        while (scope != null && !(scope instanceof INamespaceScope)) {
            scope = scope.getEnclosingScope();
        }
        if (scope != null) {
            namespaceScope = (INamespaceScope) scope;
        }
        return namespaceScope;
    }

    @Override
    public IScope getResolvingScope(ITSPHPAst typeAst) {
        String typeName = typeAst.getText();
        IScope scope = typeAst.getScope();
        if (isAbsolute(typeName)) {
            scope = ScopeHelperRegistry.get().getCorrespondingGlobalNamespace(globalNamespaceScopes, typeName);
        }
        return scope;
    }

    private String getPotentialAlias(String typeName) {
        int backslashPosition = typeName.indexOf("\\");
        if (backslashPosition != -1) {
            typeName = typeName.substring(0, backslashPosition);
        }
        return typeName;
    }

    private ITSPHPAst checkTypeNameClashAndRecoverIfNecessary(ITSPHPAst useDefinition, ISymbol typeSymbol) {
        if (hasTypeNameClash(useDefinition, typeSymbol)) {
            ITSPHPAst typeDefinition = typeSymbol.getDefinitionAst();
            if (useDefinition.isDefinedEarlierThan(typeDefinition)) {
                ErrorReporterRegistry.get().alreadyDefined(useDefinition, typeDefinition);
            } else {
                ErrorReporterRegistry.get().alreadyDefined(typeDefinition, useDefinition);
                //we do not use the alias if it was defined later than typeSymbol
                useDefinition = null;
            }
        }
        return useDefinition;
    }

    private boolean hasTypeNameClash(ITSPHPAst useDefinition, ISymbol symbol) {
        return useDefinition != null && symbol != null && symbol.getDefinitionScope().equals(this);
    }

    private ISymbol resolveAlias(ITSPHPAst useDefinition, String alias, ITSPHPAst typeAst) {
        ISymbol symbol;

        if (useDefinition.isDefinedEarlierThan(typeAst)) {
            symbol = useDefinition.getSymbol().getType();
            String typeName = typeAst.getText();
            if (isUsedAsNamespace(alias, typeName)) {
                String fullTypeName = symbol + "\\";
                if (!fullTypeName.substring(0, 1).equals("\\")) {
                    fullTypeName = "\\" + fullTypeName;
                }

                typeName = fullTypeName + typeName.substring(alias.length() + 1);
                typeAst.setText(typeName);

                IGlobalNamespaceScope globalNamespaceScope = ScopeHelperRegistry.get().
                        getCorrespondingGlobalNamespace(globalNamespaceScopes, typeName);
                if (globalNamespaceScope != null) {
                    symbol = globalNamespaceScope.resolve(typeAst);
                }
            }
        } else {
            DefinitionException ex = ErrorReporterRegistry.get().aliasForwardReference(typeAst, useDefinition);
            symbol = symbolFactory.createErroneusTypeSymbol(typeAst, ex);
        }
        return symbol;
    }

    private boolean isUsedAsNamespace(String alias, String typeName) {
        return !alias.equals(typeName);
    }

    @Override
    public IScope getEnclosingGlobalNamespaceScope(IScope scope) {
        IScope globalNamespaceScope = scope;
        IScope tmp = scope.getEnclosingScope();
        while (tmp != null) {
            globalNamespaceScope = tmp;
            tmp = tmp.getEnclosingScope();
        }
        return globalNamespaceScope;
    }

    private boolean isRelative(String identifier) {
        return identifier.indexOf("\\") > 0;
    }

    private ISymbol resolveRelative(ITSPHPAst typeAst) {
        IScope enclosingGlobalNamespaceScope = getEnclosingGlobalNamespaceScope(typeAst.getScope());
        String typeName = enclosingGlobalNamespaceScope.getScopeName() + typeAst.getText();
        typeAst.setText(typeName);
        IGlobalNamespaceScope scope = ScopeHelperRegistry.get().getCorrespondingGlobalNamespace(globalNamespaceScopes, typeName);

        ISymbol typeSymbol = null;
        if (scope != null) {
            typeSymbol = scope.resolve(typeAst);
        }
        return typeSymbol;
    }

    @Override
    public ISymbol resolveInClassSymbol(ITSPHPAst ast) {
        ISymbol symbol = null;
        IClassTypeSymbol classTypeSymbol = getEnclosingClass(ast);
        if (classTypeSymbol != null) {
            symbol = classTypeSymbol.resolveWithFallbackToParent(ast);
        }
        return symbol;
    }

    @Override
    public IClassTypeSymbol getEnclosingClass(ITSPHPAst ast) {
        IClassTypeSymbol classTypeSymbol = null;
        IScope scope = ast.getScope();
        while (scope != null && !(scope instanceof IClassTypeSymbol)) {
            scope = scope.getEnclosingScope();
        }
        if (scope != null) {
            classTypeSymbol = (IClassTypeSymbol) scope;
        }
        return classTypeSymbol;
    }
}
