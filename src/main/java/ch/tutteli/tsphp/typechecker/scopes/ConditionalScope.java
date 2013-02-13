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
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.error.ErrorHelperRegistry;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class ConditionalScope extends AScope implements IConditionalScope
{

    public ConditionalScope(IScope enclosingScope) {
        super("cScope", enclosingScope);
    }

    @Override
    public void definitionCheck(ISymbol symbol) {
        super.definitionCheck(symbol);

        //Check if already defined in enclosing scope
        ISymbol symbolEnclosingScope = enclosingScope.resolve(symbol.getDefinitionAst());
        if (symbolEnclosingScope != null) {
            generateAlreadyDefinedException(symbolEnclosingScope, symbol);
        }
    }

    @Override
    public ISymbol resolve(ITSPHPAst ast) {
        ISymbol symbol = super.resolve(ast);
        if (symbol == null) {
            symbol = enclosingScope.resolve(ast);
        }
        return symbol;
    }

    private void generateAlreadyDefinedException(ISymbol symbolEnclosingScope, ISymbol symbol) {
        List<ITSPHPAst> definitions = new ArrayList<>();
        definitions.add(symbolEnclosingScope.getDefinitionAst());
        definitions.add(symbol.getDefinitionAst());
        ErrorHelperRegistry.get().addAlreadyDefinedException(definitions);
    }
}
