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
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.error.ErrorHelperRegistry;
import ch.tutteli.tsphp.typechecker.utils.MapHelper;

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
        definitionCheck(definitionScope.getSymbols().get(symbol.getName()).get(0), symbol);
    }

    @Override
    public void definitionCheck(ISymbol firstDefinition, ISymbol symbolToCheck) {
        if (!firstDefinition.equals(symbolToCheck)) {
            ErrorHelperRegistry.get().addAlreadyDefinedException(firstDefinition, symbolToCheck);
        }
    }

    @Override
    public ISymbol resolve(IScope scope,ITSPHPAst ast) {
        throw new UnsupportedOperationException();
    }
    
}
