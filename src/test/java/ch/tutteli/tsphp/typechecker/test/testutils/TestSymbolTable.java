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
package ch.tutteli.tsphp.typechecker.test.testutils;

import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.TSPHPAst;
import ch.tutteli.tsphp.typechecker.ISymbolTable;
import ch.tutteli.tsphp.typechecker.SymbolTable;
import ch.tutteli.tsphp.typechecker.error.IErrorHelper;
import ch.tutteli.tsphp.typechecker.symbols.IClassSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class TestSymbolTable extends SymbolTable implements ISymbolTable, ICreateSymbolListener
{

    private TestSymbolFactory symbolFactory;
    private List<Entry<ISymbol, TSPHPAst>> symbols = new ArrayList<>();
    private ISymbol newlyCreatedSymbol;

    public TestSymbolTable(TestSymbolFactory testSymbolFactory, TestScopeFactory testScopeFactory) {
        super(testSymbolFactory, testScopeFactory);
        symbolFactory = testSymbolFactory;
        symbolFactory.registerListener(this);
    }

    public List<Entry<ISymbol, TSPHPAst>> getSymbols() {
        return symbols;
    }

    @Override
    public IClassSymbol defineClass(IScope currentScope, TSPHPAst modifier, TSPHPAst identifier,
            TSPHPAst extendsIds, TSPHPAst implementsIds) {
        IClassSymbol scope = super.defineClass(currentScope, modifier, identifier, extendsIds, implementsIds);

        TSPHPAst identifiers = null;
        if (extendsIds.getChildCount() > 0 || implementsIds.getChildCount() > 0) {
            identifiers = new TSPHPAst();
            appendChildrenFromTo(extendsIds, identifiers);
            appendChildrenFromTo(implementsIds, identifiers);
        }

        symbols.add(new HashMap.SimpleEntry<>(newlyCreatedSymbol, identifiers));
        return scope;
    }

    @Override
    public IMethodSymbol defineMethod(IScope currentScope, TSPHPAst methodModifier,
            TSPHPAst returnTypeModifier, TSPHPAst returnType, TSPHPAst identifier) {
        IMethodSymbol scope = super.defineMethod(currentScope, methodModifier, returnTypeModifier, returnType, identifier);
        symbols.add(new HashMap.SimpleEntry<>(newlyCreatedSymbol, returnType));
        return scope;
    }

    @Override
    public void defineVariable(IScope currentScope, TSPHPAst modifier, TSPHPAst type, TSPHPAst variableId) {
        super.defineVariable(currentScope, modifier, type, variableId);
        symbols.add(new HashMap.SimpleEntry<>(newlyCreatedSymbol, type));
    }

    @Override
    public void setNewlyCreatedSymbol(ISymbol symbol) {
        newlyCreatedSymbol = symbol;
    }

    private void appendChildrenFromTo(TSPHPAst source, TSPHPAst target) {
        int lenght = source.getChildCount();
        for (int i = 0; i < lenght; ++i) {
            target.addChild(source.getChild(i));
        }
    }
}
