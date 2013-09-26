
package ch.tutteli.tsphp.typechecker.symbols;

import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.LowerCaseStringMap;
import ch.tutteli.tsphp.typechecker.scopes.ScopeHelperRegistry;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Adopted from the book "Language Implementation Patterns" by Terence Parr
 */
public abstract class AScopedSymbol extends ASymbolWithModifier implements IScope
{

    protected IScope enclosingScope;
    protected Map<String, List<ISymbol>> members = new LowerCaseStringMap<>();

    public AScopedSymbol(ITSPHPAst definitionAst, Set<Integer> modifiers, String name, IScope theEnclosingScope) {
        super(definitionAst, modifiers, name);
        enclosingScope = theEnclosingScope;
    }

    @Override
    public Map<String, List<ISymbol>> getSymbols() {
        return members;
    }

    @Override
    public void define(ISymbol symbol) {
        ScopeHelperRegistry.get().define(this, symbol);
    }

    @Override
    public boolean doubleDefinitionCheck(ISymbol symbol) {
        return ScopeHelperRegistry.get().doubleDefinitionCheck(members, symbol);
    }

    @Override
    public ISymbol resolve(ITSPHPAst ast) {
        return ScopeHelperRegistry.get().resolve(this, ast);
    }

    @Override
    public IScope getEnclosingScope() {
        return enclosingScope;
    }

    @Override
    public String getScopeName() {
        return name;
    }
}
