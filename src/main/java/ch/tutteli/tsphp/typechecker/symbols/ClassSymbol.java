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

import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.TSPHPAst;
import java.util.Set;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 *
 * Adopted from the book Language Implementation Patterns by Terence Parr
 */
public class ClassSymbol extends AScopedSymbol implements IClassSymbol
{

    private IMethodSymbol construct;

    public ClassSymbol(TSPHPAst definitionAst, Set<Integer> modifiers, String name, IScope enclosingScope) {
        super(definitionAst, modifiers, name, enclosingScope);
    }

    @Override
    public void setConstruct(IMethodSymbol newConstruct) {
        construct = newConstruct;
    }

    @Override
    public IMethodSymbol getConstruct() {
        return construct;
    }
}
