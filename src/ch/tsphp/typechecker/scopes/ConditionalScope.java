/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.scopes;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.typechecker.error.ITypeCheckerErrorReporter;


public class ConditionalScope extends AScope implements IConditionalScope
{
    private final ITypeCheckerErrorReporter typeCheckerErrorReporter;

    public ConditionalScope(
            IScopeHelper scopeHelper, IScope enclosingScope, ITypeCheckerErrorReporter theTypeCheckerErrorReporter) {
        super(scopeHelper, "cScope", enclosingScope);
        typeCheckerErrorReporter = theTypeCheckerErrorReporter;
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
        return scopeHelper.checkIsNotDoubleDefinition(scope.getSymbols(), symbol,
                new IAlreadyDefinedMethodCaller()
                {
                    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
                    @Override
                    public void callAccordingAlreadyDefinedMethod(ISymbol firstDefinition, ISymbol symbolToCheck) {
                        typeCheckerErrorReporter.definedInOuterScope(firstDefinition, symbolToCheck);
                    }
                }
        );
    }

    private IScope getEnclosingNonConditionalScope(ISymbol symbol) {
        IScope scope = symbol.getDefinitionAst().getScope();
        while (scope instanceof IConditionalScope) {
            scope = scope.getEnclosingScope();
        }
        return scope;
    }

    @Override
    public ISymbol resolve(ITSPHPAst ast) {
        return enclosingScope.resolve(ast);
    }

    @Override
    public boolean isFullyInitialised(ISymbol symbol) {
        String symbolName = symbol.getName();
        return initialisedSymbols.containsKey(symbolName) && initialisedSymbols.get(symbolName)
                || enclosingScope.isFullyInitialised(symbol);
    }

    @Override
    public boolean isPartiallyInitialised(ISymbol symbol) {
        String symbolName = symbol.getName();
        return initialisedSymbols.containsKey(symbolName) && !initialisedSymbols.get(symbolName)
                || enclosingScope.isPartiallyInitialised(symbol);
    }
}
