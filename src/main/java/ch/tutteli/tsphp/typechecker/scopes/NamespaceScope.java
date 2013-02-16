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
package ch.tutteli.tsphp.typechecker.scopes;

import ch.tutteli.tsphp.common.ILowerCaseStringMap;
import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.common.LowerCaseStringMap;
import ch.tutteli.tsphp.common.exceptions.DefinitionException;
import ch.tutteli.tsphp.typechecker.error.ErrorHelperRegistry;
import ch.tutteli.tsphp.typechecker.symbols.ErroneusTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IAliasSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IInterfaceTypeSymbol;
import ch.tutteli.tsphp.typechecker.utils.MapHelper;
import java.util.List;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class NamespaceScope extends AScope implements INamespaceScope
{

    private ILowerCaseStringMap<List<IAliasSymbol>> uses = new LowerCaseStringMap<>();

    public NamespaceScope(String scopeName, IScope globalNamespaceScope) {
        super(scopeName, globalNamespaceScope);
    }

    @Override
    public void define(ISymbol symbol) {
        //we define symbols in the corresponding global namespace scope in order that it can be found from other
        //namespaces as well
        enclosingScope.define(symbol);
        //However, definition scope is this one, is used for alias resolving and name clashes
        symbol.setDefinitionScope(this);
    }

    @Override
    public void definitionCheck(ISymbol symbol) {
        //check in global namespace scope, because they have been defined there
        enclosingScope.definitionCheck(symbol);
    }

    @Override
    public void defineUse(IAliasSymbol symbol) {
        MapHelper.addToListMap(uses, symbol.getName(), symbol);
        symbol.setDefinitionScope(this);
    }

    @Override
    public void useDefinitionCheck(IAliasSymbol symbol) {
        ScopeHelperRegistry.get().definitionCheck(uses.get(symbol.getName()).get(0), symbol);
    }

    @Override
    public void interfaceDefinitionCheck(IInterfaceTypeSymbol symbol) {
        //check in global namespace scope, because they have been defined there
        enclosingScope.definitionCheck(symbol);
    }

    @Override
    public void classDefinitionCheck(IClassTypeSymbol symbol) {
        //check in global namespace scope, because they have been defined there
        enclosingScope.definitionCheck(symbol);
    }

    @Override
    public List<IAliasSymbol> getUse(String alias) {
        return uses.get(alias);
    }

    @Override
    public ISymbol resolve(ITSPHPAst ast) {
        //we resolve from the corresponding global namespace scope 
        return enclosingScope.resolve(ast);
    }

    @Override
    public ITypeSymbol resolveType(ITSPHPAst typeAst) {
        ITypeSymbol typeSymbol = enclosingScope.resolveType(typeAst);

        String alias = getPotentialAlias(typeAst.getText());
        ITSPHPAst useDefinition = getFirstUseDefinitionAst(alias);
        useDefinition = checkTypeNameClashAndRecoverIfNecessary(useDefinition, typeSymbol);

        if (useDefinition != null) {
            typeSymbol = resolveAlias(useDefinition, alias, typeAst);
        }
        return typeSymbol;
    }

    private String getPotentialAlias(String typeName) {
        int backslashPosition = typeName.indexOf("\\");
        if (backslashPosition != -1) {
            typeName = typeName.substring(0, backslashPosition);
        }
        return typeName;
    }

    private ITSPHPAst getFirstUseDefinitionAst(String alias) {
        return uses.containsKey(alias) ? uses.get(alias).get(0).getDefinitionAst() : null;
    }

    private ITSPHPAst checkTypeNameClashAndRecoverIfNecessary(ITSPHPAst useDefinition, ITypeSymbol typeSymbol) {
        if (hasTypeNameClash(useDefinition, typeSymbol)) {
            ITSPHPAst typeDefinition = typeSymbol.getDefinitionAst();
            if (useDefinition.isDefinedEarlierThan(typeDefinition)) {
                ErrorHelperRegistry.get().addAlreadyDefinedException(useDefinition, typeDefinition);
            } else {
                ErrorHelperRegistry.get().addAlreadyDefinedException(typeDefinition, useDefinition);
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
                IScope globalNamespaceScope = ScopeHelperRegistry.get().
                        getCorrespondingGlobalNamespace(aliasSymbol.getGlobalNamespaceScopes(), typeName);
                typeSymbol = globalNamespaceScope.resolveType(typeAst);
            }
        } else {
            DefinitionException ex = ErrorHelperRegistry.get().addAndGetUseForwardReferenceException(typeAst, useDefinition);
            typeSymbol = new ErroneusTypeSymbol(typeAst, ex);
        }
        return typeSymbol;
    }

    private String getFullName(ITypeSymbol typeSymbol) {
        return typeSymbol.getDefinitionScope().getScopeName() + typeSymbol.getName();
    }

    private boolean isUsedAsNamespace(String alias, String typeName) {
        return !alias.equals(typeName);
    }
}
