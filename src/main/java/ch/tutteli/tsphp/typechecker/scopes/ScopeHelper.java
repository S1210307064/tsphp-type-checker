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

import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.exceptions.TypeCheckerException;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class ScopeHelper
{

    private ScopeHelper() {
    }

    public static void define(IScope definitionScope, ISymbol symbol) {
        definitionScope.getSymbols().put(symbol.getName(), symbol);
        symbol.setDefinitionScope(definitionScope);
    }

    public static ISymbol resolve(IScope scope, String name) throws TypeCheckerException {
        ISymbol symbol = scope.getSymbols().get(name);
// if (!symbols.containsKey(symbolName)) {
//        } else {
//            ISymbol definedSymbol = symbols.get(symbolName);
//            Token token = definedSymbol.getDefinitionAst().getToken();
//            throw new DefinitionException(symbolName + " was already defined in line "
//                    + token.getLine() + " position " + token.getCharPositionInLine(), definedSymbol, symbol);
//        }
        // check in parent scope if it couldn't be found here
        if (symbol == null) {
            IScope parent = scope.getParentScope();
            if (parent != null) {
                symbol = parent.resolve(name);
            }
        }
        return symbol;
    }
}
