/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ISymbol;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.LowerCaseStringMap;
import ch.tsphp.typechecker.scopes.IScopeHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Adopted from the book "Language Implementation Patterns" by Terence Parr.
 */
public abstract class AScopedSymbol extends ASymbolWithModifier implements IScope
{
    //Warning! start code duplication - same as in AScope
    protected final IScopeHelper scopeHelper;
    protected final IScope enclosingScope;
    protected final Map<String, List<ISymbol>> symbols = new LowerCaseStringMap<>();
    protected final Map<String, Boolean> initialisedSymbols = new HashMap<>();
    //Warning! start code duplication - same as in AScope

    public AScopedSymbol(
            IScopeHelper theScopeHelper
            , ITSPHPAst definitionAst
            , Set<Integer> modifiers
            , String name
            , IScope theEnclosingScope) {
        super(definitionAst, modifiers, name);
        enclosingScope = theEnclosingScope;
        scopeHelper = theScopeHelper;
    }

    @Override
    public void define(ISymbol symbol) {
        scopeHelper.define(this, symbol);
    }

    @Override
    public ISymbol resolve(ITSPHPAst ast) {
        return scopeHelper.resolve(this, ast);
    }

    @Override
    public boolean doubleDefinitionCheck(ISymbol symbol) {
        return scopeHelper.checkIsNotDoubleDefinition(symbols, symbol);
    }

    //Warning! start code duplication - same as in AScope
    @Override
    public IScope getEnclosingScope() {
        return enclosingScope;
    }

    @Override
    public String getScopeName() {
        return name;
    }

    @Override
    public Map<String, List<ISymbol>> getSymbols() {
        return symbols;
    }
    //Warning! end code duplication - same as in AScope

    //Warning! start code duplication - same as in AScope
    @Override
    public void addToInitialisedSymbols(ISymbol symbol, boolean isFullyInitialised) {
        String symbolName = symbol.getName();
        if (!initialisedSymbols.containsKey(symbolName) || !initialisedSymbols.get(symbolName)) {
            initialisedSymbols.put(symbol.getName(), isFullyInitialised);
        }
    }

    @Override
    public Map<String, Boolean> getInitialisedSymbols() {
        return initialisedSymbols;
    }
    //Warning! end code duplication - same as in AScope
}
