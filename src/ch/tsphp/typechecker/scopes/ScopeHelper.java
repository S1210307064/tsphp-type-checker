/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.scopes;

import ch.tsphp.common.ILowerCaseStringMap;
import ch.tsphp.common.IScope;
import ch.tsphp.common.ISymbol;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.error.ITypeCheckerErrorReporter;
import ch.tsphp.typechecker.utils.MapHelper;

import java.util.List;
import java.util.Map;

public class ScopeHelper implements IScopeHelper
{
    private final ITypeCheckerErrorReporter typeCheckerErrorReporter;

    public ScopeHelper(ITypeCheckerErrorReporter theTypeCheckerErrorReporter) {
        typeCheckerErrorReporter = theTypeCheckerErrorReporter;
    }

    @Override
    public void define(IScope definitionScope, ISymbol symbol) {
        MapHelper.addToListMap(definitionScope.getSymbols(), symbol.getName(), symbol);
        symbol.setDefinitionScope(definitionScope);
    }

    @Override
    public boolean checkIsNotDoubleDefinition(Map<String, List<ISymbol>> symbols, ISymbol symbol) {
        return checkIsNotDoubleDefinition(symbols.get(symbol.getName()).get(0), symbol);
    }

    @Override
    public boolean checkIsNotDoubleDefinition(Map<String, List<ISymbol>> symbols, ISymbol symbol,
            IAlreadyDefinedMethodCaller errorMethodCaller) {
        return checkIsNotDoubleDefinition(symbols.get(symbol.getName()).get(0), symbol, errorMethodCaller);
    }

    @Override
    public boolean checkIsNotDoubleDefinition(ISymbol firstDefinition, ISymbol symbolToCheck) {
        return checkIsNotDoubleDefinition(firstDefinition, symbolToCheck, new StandardAlreadyDefinedMethodCaller());
    }

    @Override
    public boolean checkIsNotDoubleDefinition(ISymbol firstDefinition, ISymbol symbolToCheck,
            IAlreadyDefinedMethodCaller errorMethodCaller) {
        boolean isFirst = firstDefinition.equals(symbolToCheck);
        if (!isFirst) {
            errorMethodCaller.callAccordingAlreadyDefinedMethod(firstDefinition, symbolToCheck);
        }
        return isFirst;
    }

    @Override
    public IGlobalNamespaceScope getCorrespondingGlobalNamespace(
            ILowerCaseStringMap<IGlobalNamespaceScope> globalNamespaceScopes, String typeName) {
        int lastBackslashPosition = typeName.lastIndexOf('\\') + 1;
        String namespaceName = typeName.substring(0, lastBackslashPosition);
        return globalNamespaceScopes.get(namespaceName);
    }

    @Override
    public ISymbol resolve(IScope scope, ITSPHPAst ast) {
        ISymbol symbol = null;
        Map<String, List<ISymbol>> symbols = scope.getSymbols();
        if (symbols.containsKey(ast.getText())) {
            symbol = symbols.get(ast.getText()).get(0);
        }
        return symbol;
    }

    @Override
    public INamespaceScope getEnclosingNamespaceScope(ITSPHPAst ast) {
        INamespaceScope namespaceScope = null;

        IScope scope = ast.getScope();
        while (scope != null && !(scope instanceof INamespaceScope)) {
            scope = scope.getEnclosingScope();
        }
        if (scope != null) {
            namespaceScope = (INamespaceScope) scope;
        }
        return namespaceScope;
    }

    /**
     * Represents a delegate which calls the appropriate method on TypeCheckerErrorReporter.
     */
    private class StandardAlreadyDefinedMethodCaller implements IAlreadyDefinedMethodCaller
    {

        @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
        @Override
        public void callAccordingAlreadyDefinedMethod(ISymbol firstDefinition, ISymbol symbolToCheck) {
            typeCheckerErrorReporter.alreadyDefined(firstDefinition, symbolToCheck);
        }
    }
}
