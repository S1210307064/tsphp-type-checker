package ch.tutteli.tsphp.typechecker.scopes;

import ch.tutteli.tsphp.common.ILowerCaseStringMap;
import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.error.ErrorReporterRegistry;
import ch.tutteli.tsphp.typechecker.utils.MapHelper;
import java.util.List;
import java.util.Map;

public class ScopeHelper implements IScopeHelper
{

    @Override
    public void define(IScope definitionScope, ISymbol symbol) {
        MapHelper.addToListMap(definitionScope.getSymbols(), symbol.getName(), symbol);
        symbol.setDefinitionScope(definitionScope);
    }

    @Override
    public boolean doubleDefinitionCheck(Map<String, List<ISymbol>> symbols, ISymbol symbol) {
        return doubleDefinitionCheck(symbols.get(symbol.getName()).get(0), symbol);
    }

    @Override
    public boolean doubleDefinitionCheck(Map<String, List<ISymbol>> symbols, ISymbol symbol,
            IAlreadyDefinedMethodCaller errorMethodCaller) {
        return doubleDefinitionCheck(symbols.get(symbol.getName()).get(0), symbol, errorMethodCaller);
    }

    @Override
    public boolean doubleDefinitionCheck(ISymbol firstDefinition, ISymbol symbolToCheck) {
        return doubleDefinitionCheck(firstDefinition, symbolToCheck, new StandardAlreadyDefinedMethodCaller());
    }

    @Override
    public boolean doubleDefinitionCheck(ISymbol firstDefinition, ISymbol symbolToCheck,
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
     * Represents a delegate which calls the appropriate method on ErrorReporter.
     */
    private class StandardAlreadyDefinedMethodCaller implements IAlreadyDefinedMethodCaller
    {

        @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
        @Override
        public void callAccordingAlreadyDefinedMethod(ISymbol firstDefinition, ISymbol symbolToCheck) {
            ErrorReporterRegistry.get().alreadyDefined(firstDefinition, symbolToCheck);
        }
    }
}
