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

import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.common.TSPHPAst;
import ch.tutteli.tsphp.common.exceptions.DefinitionException;
import ch.tutteli.tsphp.common.exceptions.ReferenceException;
import ch.tutteli.tsphp.typechecker.TSPHPErroneusTypeSymbol;
import ch.tutteli.tsphp.typechecker.error.ErrorHelperRegistry;
import ch.tutteli.tsphp.typechecker.error.IErrorHelper;
import ch.tutteli.tsphp.typechecker.symbols.IAliasSymbol;
import ch.tutteli.tsphp.typechecker.utils.MapHelper;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class NamespaceScope extends AScope implements INamespaceScope
{

    private Map<String, List<IAliasSymbol>> uses = new LinkedHashMap<>();

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
    }

    @Override
    public void useDefinitionCheck(IAliasSymbol symbol) {
        ScopeHelperRegistry.get().definitionCheck(uses.get(symbol.getName()).get(0), symbol);
    }

    @Override
    public List<IAliasSymbol> getUse(String alias) {
        return uses.get(alias);
    }

    @Override
    public TSPHPAst getFirstUseDefinitionAst(String alias) {
        return uses.containsKey(alias) ? uses.get(alias).get(0).getDefinitionAst() : null;
    }

    @Override
    public ISymbol resolve(TSPHPAst ast) {
        //we resolve from the corresponding global namespace scope 
        return enclosingScope.resolve(ast);
    }

    @Override
    public ITypeSymbol resolveType(TSPHPAst typeAst) {
        ITypeSymbol typeSymbol = enclosingScope.resolveType(typeAst);
        typeSymbol = changeToAliasTypeSymbolIfDefined(typeAst, typeSymbol);

        if (typeSymbol == null) {
            ReferenceException ex = ErrorHelperRegistry.get().addAndGetUnkownTypeException(typeAst);
            typeSymbol = new TSPHPErroneusTypeSymbol(typeAst, ex);
        }

        return typeSymbol;
    }

    private ITypeSymbol changeToAliasTypeSymbolIfDefined(TSPHPAst typeAst, ITypeSymbol typeSymbol) {

        IErrorHelper errorHelper = ErrorHelperRegistry.get();

        TSPHPAst useDefinition = resolveAlias(typeAst.getText());
        if (hasTypeNameClash(useDefinition, typeSymbol)) {
            useDefinition = errorHelper.addAlreadyDefinedExceptionAndRecover(
                    typeSymbol.getDefinitionAst(), useDefinition);
        }

        if (useDefinition != null) {
            if (isNotForwardReference(useDefinition)) {
                typeSymbol = useDefinition.symbol.getType();
            } else {
                DefinitionException ex = errorHelper.addUseForwardReferenceException(typeAst, useDefinition);
                typeSymbol = new TSPHPErroneusTypeSymbol(typeAst, ex);
            }
        }
        return typeSymbol;
    }

    private boolean hasTypeNameClash(TSPHPAst useDefinition, ITypeSymbol typeSymbol) {
        return useDefinition != null && typeSymbol != null && typeSymbol.getDefinitionScope().equals(this);
    }

    public boolean isNotForwardReference(TSPHPAst useDefinition) {
        return useDefinition.symbol.getType() != null;
    }

    private TSPHPAst resolveAlias(String typeName) {
        String alias = getPotentialAlias(typeName);
        return getFirstUseDefinitionAst(alias);
    }

    private String getPotentialAlias(String typeName) {
        int backslashPosition = typeName.indexOf("\\") + 1;
        if (backslashPosition != -1) {
            typeName = typeName.substring(0, backslashPosition);
        }
        return typeName;
    }
}
