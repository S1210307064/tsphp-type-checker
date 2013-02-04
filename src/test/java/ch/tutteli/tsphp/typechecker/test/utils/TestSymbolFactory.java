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
package ch.tutteli.tsphp.typechecker.test.utils;

import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.TSPHPAst;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tutteli.tsphp.typechecker.symbols.IVariableSymbol;
import ch.tutteli.tsphp.typechecker.symbols.SymbolFactory;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
class TestSymbolFactory extends SymbolFactory implements ISymbolFactory
{

    private List<ISymbol> symbols = new ArrayList<>();

    public TestSymbolFactory() {
    }

    @Override
    public IVariableSymbol createVariableSymbol(TSPHPAst typeModifierAst, TSPHPAst variableId) {
        IVariableSymbol variableSymbol = super.createVariableSymbol(typeModifierAst, variableId);
        symbols.add(variableSymbol);
        return variableSymbol;
    }

    public List<ISymbol> getSymbols() {
        return symbols;
    }
}
