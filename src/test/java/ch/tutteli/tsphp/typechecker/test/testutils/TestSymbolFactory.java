/*
 * Copyright 2013 Robert Stoll <rstoll@tutteli.ch>
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
package ch.tutteli.tsphp.typechecker.test.testutils;

import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IInterfaceTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IVariableSymbol;
import ch.tutteli.tsphp.typechecker.symbols.SymbolFactory;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class TestSymbolFactory extends SymbolFactory
{

    List<ICreateSymbolListener> listeners = new ArrayList<>();

    @Override
    public IInterfaceTypeSymbol createInterfaceTypeSymbol(ITSPHPAst modifier, ITSPHPAst identifier, IScope currentScope) {
        IInterfaceTypeSymbol symbol = super.createInterfaceTypeSymbol(modifier, identifier, currentScope);
        updateListener(symbol);
        return symbol;
    }

    @Override
    public IClassTypeSymbol createClassTypeSymbol(ITSPHPAst classModifierAst, ITSPHPAst identifier, IScope currentScope) {
        IClassTypeSymbol symbol = super.createClassTypeSymbol(classModifierAst, identifier, currentScope);
        updateListener(symbol);
        return symbol;
    }

    @Override
    public IMethodSymbol createMethodSymbol(ITSPHPAst methodModifier, ITSPHPAst returnTypeModifier, ITSPHPAst identifier,
            IScope currentScope) {
        IMethodSymbol symbol = super.createMethodSymbol(methodModifier, returnTypeModifier, identifier, currentScope);
        updateListener(symbol);
        return symbol;
    }

    @Override
    public IVariableSymbol createVariableSymbol(ITSPHPAst typeModifierAst, ITSPHPAst variableId) {
        IVariableSymbol symbol = super.createVariableSymbol(typeModifierAst, variableId);
        updateListener(symbol);
        return symbol;
    }

    public void registerListener(ICreateSymbolListener listener) {
        listeners.add(listener);
    }

    private void updateListener(ISymbol symbol) {
        for (ICreateSymbolListener listener : listeners) {
            listener.setNewlyCreatedSymbol(symbol);
        }
    }
}
