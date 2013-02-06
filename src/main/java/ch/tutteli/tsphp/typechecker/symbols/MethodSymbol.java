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
package ch.tutteli.tsphp.typechecker.symbols;

import ch.tutteli.tsphp.common.ASymbol;
import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.TSPHPAst;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class MethodSymbol extends AScopedSymbol implements IMethodSymbol
{

    private Map<String, ASymbol> orderedArgs = new LinkedHashMap<>();
    private Set<Integer> returnTypeModifier;

    public MethodSymbol(TSPHPAst definitionAst, Set<Integer> methodModifiers, Set<Integer> theReturnTypeModifier,
            String name, IScope enclosingScope) {
        super(definitionAst, methodModifiers, name, enclosingScope);
        returnTypeModifier = theReturnTypeModifier;
    }

    @Override
    public String getName() {
        return name + "(" + stripBrackets(orderedArgs.keySet().toString()) + ")";
    }

    @Override
    public String toString() {
        return super.toString() + ModifierHelper.getModifiers(new TreeSet(returnTypeModifier));
    }
}
