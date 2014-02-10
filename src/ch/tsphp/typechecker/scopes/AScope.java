package ch.tsphp.typechecker.scopes;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ISymbol;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Adopted from the book "Language Implementation Patterns" by Terence Parr.
 */
public abstract class AScope implements IScope
{
    protected final String scopeName;

    //Warning! start code duplication - same as in AScopedSymbol
    protected final IScopeHelper scopeHelper;
    protected final IScope enclosingScope;
    protected final Map<String, List<ISymbol>> symbols = new LinkedHashMap<>();
    protected final Map<String, Boolean> initialisedSymbols = new HashMap<>();
    //Warning! end code duplication - same as in AScopedSymbol

    public AScope(IScopeHelper theScopeHelper, String theScopeName, IScope theEnclosingScope) {
        scopeHelper = theScopeHelper;
        scopeName = theScopeName;
        enclosingScope = theEnclosingScope;
    }

    //Warning! start code duplication - same as in AScopedSymbol
    @Override
    public IScope getEnclosingScope() {
        return enclosingScope;
    }

    @Override
    public String getScopeName() {
        return scopeName;
    }

    @Override
    public Map<String, List<ISymbol>> getSymbols() {
        return symbols;
    }
    //Warning! end code duplication - same as in AScopedSymbol

    //Warning! start code duplication - same as in AScopedSymbol
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
    //Warning! end code duplication - same as in AScopedSymbol
}
