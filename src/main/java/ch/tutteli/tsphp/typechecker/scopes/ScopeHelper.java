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
import ch.tutteli.tsphp.typechecker.TSPHPErroneusTypeAst;
import ch.tutteli.tsphp.typechecker.error.ErrorHelperRegistry;
import ch.tutteli.tsphp.typechecker.error.IErrorHelper;
import ch.tutteli.tsphp.typechecker.utils.MapHelper;
import java.util.List;
import java.util.Map;
import org.antlr.runtime.Token;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class ScopeHelper implements IScopeHelper
{

    @Override
    public void define(IScope definitionScope, ISymbol symbol) {
        MapHelper.addToListMap(definitionScope.getSymbols(), symbol.getName(), symbol);
        symbol.setDefinitionScope(definitionScope);
    }

    @Override
    public void definitionCheck(IScope definitionScope, ISymbol symbol) {
        ISymbol firstDefinition = definitionScope.getSymbols().get(symbol.getName()).get(0);

        //was symbol already declared
        if (!firstDefinition.equals(symbol)) {
            ErrorHelperRegistry.get().addAlreadyDefinedException(firstDefinition, symbol);
        }
    }

    @Override
    public ISymbol resolve(IScope scope, String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ITypeSymbol resolveType(INamespaceScope scope, TSPHPAst typeAst) {
        ITypeSymbol typeSymbol = scope.getEnclosingScope().resolveType(typeAst);
        typeSymbol = changeToAliasTypeSymbolIfDefined(scope, typeAst, typeSymbol);

//        if (typeSymbol != null && isNotFullTypeName(typeSymbol.getDefinitionAst().getText())) {
//            Token token = typeSymbol.getDefinitionAst().getToken();
//            token.setText(scope.getScopeName() + token.getText());
//        }

        return typeSymbol;
    }

    private ITypeSymbol changeToAliasTypeSymbolIfDefined(INamespaceScope scope, TSPHPAst typeAst, ITypeSymbol typeSymbol) {

        IErrorHelper errorHelper = ErrorHelperRegistry.get();

        TSPHPAst useDefinition = resolveAlias(typeAst.getText(), scope);
        if (hasTypeNameClash(scope, useDefinition, typeSymbol)) {
            useDefinition = errorHelper.addAlreadyDefinedExceptionAndRecover(
                    typeSymbol.getDefinitionAst(), useDefinition);
        }

        if (useDefinition != null) {
            if (isNotForwardReference(useDefinition)) {
                typeSymbol = useDefinition.symbol.getType();
            } else {
                DefinitionException ex = errorHelper.addUseForwardReferenceException(typeAst, useDefinition);
                typeSymbol = new TSPHPErroneusTypeAst(typeAst, ex);
            }
        }
        return typeSymbol;
    }

    private boolean hasTypeNameClash(IScope scope, TSPHPAst useDefinition, ITypeSymbol typeSymbol) {
        return useDefinition != null && typeSymbol != null && typeSymbol.getDefinitionScope().equals(scope);
    }

    public boolean isNotForwardReference(TSPHPAst useDefinition) {
        return useDefinition.symbol.getType() != null;
    }

    @Override
    public TSPHPAst resolveAlias(String typeName, INamespaceScope namespace) {
        String alias = getPotentialAlias(typeName);
        return namespace.getOneUse(alias);
    }

    private String getPotentialAlias(String typeName) {
        int backslashPosition = typeName.indexOf("\\") + 1;
        if (backslashPosition != -1) {
            typeName = typeName.substring(0, backslashPosition);
        }
        return typeName;
    }

    private boolean isNotFullTypeName(String typeName) {
        return !typeName.substring(0, 1).equals("\\");
    }
}
