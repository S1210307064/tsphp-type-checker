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
import ch.tutteli.tsphp.typechecker.error.ErrorReporterRegistry;
import ch.tutteli.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.INamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.ScopeHelperRegistry;
import ch.tutteli.tsphp.typechecker.symbols.AliasTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IAliasSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IAliasTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.ErroneusTypeSymbol;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class TypeResolver
{

    private ISymbolFactory symbolFactory;
    private ILowerCaseStringMap<IGlobalNamespaceScope> globalNamespaceScopes = new LowerCaseStringMap<>();

    public TypeResolver(ISymbolFactory theSymbolFactory,
            ILowerCaseStringMap<IGlobalNamespaceScope> theGlobalNamespaceScopes) {
        symbolFactory = theSymbolFactory;
        globalNamespaceScopes = theGlobalNamespaceScopes;
    }

    public ITypeSymbol resolveUseType(ITSPHPAst typeAst, ITSPHPAst alias) {
        ((IAliasSymbol) alias.getSymbol()).setGlobalNamespaceScopes(globalNamespaceScopes);

        //Alias is always pointing to a full type name. If user has omitted \ at the beginning, then we add it here
        String typeName = typeAst.getText();
        if (!isFullTypeName(typeName)) {
            typeName = "\\" + typeName;
            typeAst.setText(typeName);
        }

        ITypeSymbol aliasType = resolveTypeOrReturnNull(typeAst);
        if (aliasType == null) {
            aliasType = new AliasTypeSymbol(typeAst, typeAst.getText());
        }

        return aliasType;
    }

    private ITypeSymbol resolveTypeOrReturnNull(ITSPHPAst typeAst) {
        ITypeSymbol typeSymbol = null;

        String typeName = typeAst.getText();
        if (isFullTypeName(typeName)) {
            IGlobalNamespaceScope scope = ScopeHelperRegistry.get()
                    .getCorrespondingGlobalNamespace(globalNamespaceScopes, typeName);
            if (scope != null) {
                typeSymbol = scope.resolveType(typeAst);
            }
        } else {
            INamespaceScope scope = getEnclosingNamespaceScope(typeAst);
            if (scope != null) {
                typeSymbol = scope.resolveType(typeAst);

                INamespaceScope namespaceScope = (INamespaceScope) scope;

                String alias = getPotentialAlias(typeAst.getText());
                ITSPHPAst useDefinition = namespaceScope.getFirstUseDefinitionAst(alias);
                useDefinition = checkTypeNameClashAndRecoverIfNecessary(useDefinition, typeSymbol);

                if (useDefinition != null) {
                    typeSymbol = resolveAlias(useDefinition, alias, typeAst);
                }
            }
        }
        if (typeSymbol == null && isRelativeType(typeAst.getText())) {
            typeSymbol = resolveRelativeType(typeAst);
        }
        return typeSymbol;
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

    public IScope getResolvingScope(ITSPHPAst typeAst) {
        String typeName = typeAst.getText();
        IScope scope = typeAst.getScope();
        if (isFullTypeName(typeName)) {
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

    private ITSPHPAst checkTypeNameClashAndRecoverIfNecessary(ITSPHPAst useDefinition, ITypeSymbol typeSymbol) {
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

    private boolean hasTypeNameClash(ITSPHPAst useDefinition, ITypeSymbol typeSymbol) {
        return useDefinition != null && typeSymbol != null && typeSymbol.getDefinitionScope().equals(this);
    }

    private ITypeSymbol resolveAlias(ITSPHPAst useDefinition, String alias, ITSPHPAst typeAst) {
        ITypeSymbol typeSymbol;

        if (useDefinition.isDefinedEarlierThan(typeAst)) {
            typeSymbol = useDefinition.getSymbol().getType();
            String typeName = typeAst.getText();
            if (isUsedAsNamespace(alias, typeName)) {
                String fullTypeName = getFullName(typeSymbol);
                if (!fullTypeName.substring(fullTypeName.length() - 1).equals("\\")) {
                    fullTypeName += "\\";
                }
                typeName = fullTypeName + typeName.substring(alias.length() + 1);
                typeAst.setText(typeName);

                IAliasSymbol aliasSymbol = (IAliasSymbol) useDefinition.getSymbol();
                IGlobalNamespaceScope globalNamespaceScope = ScopeHelperRegistry.get().
                        getCorrespondingGlobalNamespace(aliasSymbol.getGlobalNamespaceScopes(), typeName);
                typeSymbol = globalNamespaceScope.resolveType(typeAst);
            }
        } else {
            DefinitionException ex = ErrorReporterRegistry.get().aliasForwardReference(typeAst, useDefinition);
            typeSymbol = symbolFactory.createErroneusTypeSymbol(typeAst, ex);
        }
        return typeSymbol;
    }

    private String getFullName(ITypeSymbol typeSymbol) {
        return typeSymbol.getDefinitionScope().getScopeName() + typeSymbol.getName();
    }

    private boolean isUsedAsNamespace(String alias, String typeName) {
        return !alias.equals(typeName);
    }

    public ITypeSymbol resolveType(ITSPHPAst typeAst) {
        ITypeSymbol typeSymbol = resolveTypeOrReturnNull(typeAst);

        if (typeSymbol == null) {
            String typeName = typeAst.getText();
            if (!isFullTypeName(typeName)) {
                typeAst.setText(getEnclosingGlobalNamespaceScope(typeAst.getScope()).getScopeName() + typeName);
            }
            ReferenceException ex = ErrorReporterRegistry.get().unkownType(typeAst);
            typeSymbol = new ErroneusTypeSymbol(typeAst, ex);

        } else if (typeSymbol instanceof IAliasTypeSymbol) {

            typeAst.setText(typeSymbol.getName());
            ReferenceException ex = ErrorReporterRegistry.get().unkownType(typeAst);
            typeSymbol = new ErroneusTypeSymbol(typeSymbol.getDefinitionAst(), ex);
        }
        return typeSymbol;
    }

    private boolean isFullTypeName(String typeName) {
        return typeName.substring(0, 1).equals("\\");
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

    private boolean isRelativeType(String typeName) {
        return typeName.indexOf("\\") > 0;
    }

    private ITypeSymbol resolveRelativeType(ITSPHPAst typeAst) {
        IScope enclosingGlobalNamespaceScope = getEnclosingGlobalNamespaceScope(typeAst.getScope());
        String typeName = enclosingGlobalNamespaceScope.getScopeName() + typeAst.getText();
        typeAst.setText(typeName);
        IGlobalNamespaceScope scope = ScopeHelperRegistry.get().getCorrespondingGlobalNamespace(globalNamespaceScopes, typeName);

        ITypeSymbol typeSymbol = null;
        if (scope != null) {
            typeSymbol = scope.resolveType(typeAst);
        }
        return typeSymbol;
    }
}
