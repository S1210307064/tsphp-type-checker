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

import ch.tutteli.tsphp.common.AstHelperRegistry;
import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITSPHPAstAdaptor;
import ch.tutteli.tsphp.common.TSPHPAst;
import ch.tutteli.tsphp.typechecker.ISymbolTable;
import ch.tutteli.tsphp.typechecker.SymbolTable;
import ch.tutteli.tsphp.typechecker.scopes.INamespaceScope;
import ch.tutteli.tsphp.typechecker.symbols.IAliasSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IInterfaceTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class TestSymbolTable extends SymbolTable implements ISymbolTable, ICreateSymbolListener
{

    private TestSymbolFactory symbolFactory;
    private List<Entry<ISymbol, ITSPHPAst>> symbols = new ArrayList<>();
    private ISymbol newlyCreatedSymbol;

    public TestSymbolTable(TestSymbolFactory testSymbolFactory, TestScopeFactory testScopeFactory,
            ITSPHPAstAdaptor astAdaptor) {
        super(testSymbolFactory, testScopeFactory, astAdaptor);
        symbolFactory = testSymbolFactory;
        symbolFactory.registerListener(this);
    }

    public List<Entry<ISymbol, ITSPHPAst>> getSymbols() {
        return symbols;
    }

    @Override
    public void defineUse(INamespaceScope currentScope, ITSPHPAst type, ITSPHPAst alias) {
        super.defineUse(currentScope, type, alias);
        symbols.add(new HashMap.SimpleEntry<>(alias.getSymbol(), type));
    }

    @Override
    public IInterfaceTypeSymbol defineInterface(IScope currentScope, ITSPHPAst modifier, ITSPHPAst identifier,
            ITSPHPAst extendsIds) {
        IInterfaceTypeSymbol symbol = super.defineInterface(currentScope, modifier, identifier, extendsIds);
        ITSPHPAst identifiers = null;
        if (extendsIds.getChildCount() > 0) {
            identifiers = new TSPHPAst();
            appendChildrenFromTo(extendsIds, identifiers);
        }
        symbols.add(new HashMap.SimpleEntry<>(newlyCreatedSymbol, identifiers));
        return symbol;
    }

    @Override
    public IClassTypeSymbol defineClass(IScope currentScope, ITSPHPAst modifier, ITSPHPAst identifier,
            ITSPHPAst extendsIds, ITSPHPAst implementsIds) {
        IClassTypeSymbol scope = super.defineClass(currentScope, modifier, identifier, extendsIds, implementsIds);

        ITSPHPAst identifiers = null;
        if (extendsIds.getChildCount() > 0 || implementsIds.getChildCount() > 0) {
            identifiers = new TSPHPAst();
            appendChildrenFromTo(extendsIds, identifiers);
            appendChildrenFromTo(implementsIds, identifiers);
        }
        symbols.add(new HashMap.SimpleEntry<>(newlyCreatedSymbol, identifiers));

        return scope;
    }

    @Override
    public IMethodSymbol defineMethod(IScope currentScope, ITSPHPAst methodModifier,
            ITSPHPAst returnTypeModifier, ITSPHPAst returnType, ITSPHPAst identifier) {
        IMethodSymbol scope = super.defineMethod(currentScope, methodModifier, returnTypeModifier, returnType, identifier);
        symbols.add(new HashMap.SimpleEntry<>(newlyCreatedSymbol, returnType));
        return scope;
    }

    @Override
    public void defineVariable(IScope currentScope, ITSPHPAst modifier, ITSPHPAst type, ITSPHPAst variableId) {
        super.defineVariable(currentScope, modifier, type, variableId);
        symbols.add(new HashMap.SimpleEntry<>(newlyCreatedSymbol, type));
    }

    @Override
    public void setNewlyCreatedSymbol(ISymbol symbol) {
        newlyCreatedSymbol = symbol;
    }

    private void appendChildrenFromTo(ITSPHPAst source, ITSPHPAst target) {
        int lenght = source.getChildCount();
        for (int i = 0; i < lenght; ++i) {
            target.addChild(AstHelperRegistry.get().copyAst(source.getChild(i)));
        }
    }
}
