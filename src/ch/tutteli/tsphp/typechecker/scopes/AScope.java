package ch.tutteli.tsphp.typechecker.scopes;

import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Adopted from the book "Language Implementation Patterns" by Terence Parr.
 */
public abstract class AScope implements IScope
{
    protected final IScopeHelper scopeHelper;
    protected final String scopeName;
    protected final IScope enclosingScope;
    protected final Map<String, List<ISymbol>> symbols = new LinkedHashMap<>();

    public AScope(IScopeHelper theScopeHelper, String theScopeName, IScope theEnclosingScope) {
        scopeHelper = theScopeHelper;
        scopeName = theScopeName;
        enclosingScope = theEnclosingScope;
    }

    @Override
    public boolean doubleDefinitionCheck(ISymbol symbol) {
        return scopeHelper.doubleDefinitionCheck(symbols, symbol);
    }

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
}
