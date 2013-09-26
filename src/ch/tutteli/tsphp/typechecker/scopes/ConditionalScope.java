package ch.tutteli.tsphp.typechecker.scopes;

import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.error.ErrorReporterRegistry;

public class ConditionalScope extends AScope implements IConditionalScope
{

    public ConditionalScope(IScope enclosingScope) {
        super("cScope", enclosingScope);
    }

    @Override
    public void define(ISymbol symbol) {
        enclosingScope.define(symbol);
        symbol.setDefinitionScope(this);
    }

    @Override
    public boolean doubleDefinitionCheck(ISymbol symbol) {
        IScope scope = getEnclosingNonConditionalScope(symbol);
        if (scope instanceof INamespaceScope) {
            scope = scope.getEnclosingScope();
        }
        return ScopeHelperRegistry.get().doubleDefinitionCheck(scope.getSymbols(), symbol,
                new IAlreadyDefinedMethodCaller()
        {
            @Override
            public void callAccordingAlreadyDefinedMethod(ISymbol firstDefinition, ISymbol symbolToCheck) {
                ErrorReporterRegistry.get().definedInOuterScope(firstDefinition, symbolToCheck);
            }
        });
    }

    private IScope getEnclosingNonConditionalScope(ISymbol symbol) {
        IScope scope = symbol.getDefinitionAst().getScope();
        while (scope != null && scope instanceof IConditionalScope) {
            scope = scope.getEnclosingScope();
        }
        return scope;
    }

    @Override
    public ISymbol resolve(ITSPHPAst ast) {
        return enclosingScope.resolve(ast);
    }
}
