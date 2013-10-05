package ch.tutteli.tsphp.typechecker.scopes;

import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Adopted from the book "Language Implementation Patterns" by Terence Parr
 */
public abstract class AScope implements IScope
{
    protected final IScopeHelper scopeHelper;
    protected String scopeName;
    protected IScope enclosingScope;
    protected Map<String, List<ISymbol>> symbols = new LinkedHashMap<>();

    public AScope(IScopeHelper theScopeHelper, String theScopeName, IScope theEnclosingScope) {
        scopeHelper = theScopeHelper;
        scopeName = theScopeName;
        enclosingScope = theEnclosingScope;
    }

    @Override
    public void define(ISymbol symbol) {
        scopeHelper.define(this, symbol);
    }

    @Override
    public boolean doubleDefinitionCheck(ISymbol symbol) {
       return scopeHelper.doubleDefinitionCheck(symbols, symbol);
    }

    @Override
    public ISymbol resolve(ITSPHPAst typeAst) {
        return scopeHelper.resolve(this, typeAst);
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

    @Override
    public String toString() {
        return scopeName + ":" + symbols.keySet().toString();
    }
}
