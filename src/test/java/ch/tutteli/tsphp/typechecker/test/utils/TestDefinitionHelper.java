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

import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.TSPHPAst;
import ch.tutteli.tsphp.typechecker.DefinitionHelper;
import ch.tutteli.tsphp.typechecker.IDefinitionHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class TestDefinitionHelper extends DefinitionHelper implements IDefinitionHelper, ICreateSymbolListener
{
    private static TestSymbolFactory symbolFactory = new TestSymbolFactory();
    private List<Entry<ISymbol, TSPHPAst>> symbols = new ArrayList<>();
    
    private ISymbol newlyCreatedSymbol;

    public TestDefinitionHelper() {
        super(symbolFactory);
        symbolFactory.registerListener(this);
    }

    public List<Entry<ISymbol, TSPHPAst>>  getSymbols() {
        return symbols;
    }

    @Override
    public void defineVariable(IScope currentScope, TSPHPAst type, TSPHPAst modifier, TSPHPAst variableId) {
        super.defineVariable(currentScope, type, modifier, variableId);
        symbols.add(new HashMap.SimpleEntry<>(newlyCreatedSymbol,type));
    }

    @Override
    public void setNewlyCreatedSymbol(ISymbol symbol) {
         newlyCreatedSymbol = symbol;
    }
}
