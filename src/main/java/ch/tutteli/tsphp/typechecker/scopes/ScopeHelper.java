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
package ch.tutteli.tsphp.typechecker.scopes;

import ch.tutteli.tsphp.common.ILowerCaseStringMap;
import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.error.ErrorReporterRegistry;
import ch.tutteli.tsphp.typechecker.utils.MapHelper;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
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
        int lastBackslashPosition = typeName.lastIndexOf("\\") + 1;
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

    /**
     * Represents a delegate which calls the appropriate method on ErrorReporter.
     */
    private class StandardAlreadyDefinedMethodCaller implements IAlreadyDefinedMethodCaller
    {

        @Override
        public void callAccordingAlreadyDefinedMethod(ISymbol firstDefinition, ISymbol symbolToCheck) {
            ErrorReporterRegistry.get().alreadyDefined(firstDefinition, symbolToCheck);
        }
    }
}
