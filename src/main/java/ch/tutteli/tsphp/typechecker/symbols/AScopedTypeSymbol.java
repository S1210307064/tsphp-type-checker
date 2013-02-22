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

import ch.tutteli.tsphp.common.ILowerCaseStringMap;
import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.common.LowerCaseStringMap;
import ch.tutteli.tsphp.typechecker.scopes.ICaseInsensitiveScope;
import ch.tutteli.tsphp.typechecker.scopes.ScopeHelperRegistry;
import ch.tutteli.tsphp.typechecker.utils.MapHelper;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class AScopedTypeSymbol extends AScopedSymbol implements ITypeSymbol, ICaseInsensitiveScope, IPolymorphicTypeSymbol
{

    protected IPolymorphicTypeSymbol parent;
    protected ILowerCaseStringMap<List<ISymbol>> symbolsCaseInsensitive = new LowerCaseStringMap<>();

    public AScopedTypeSymbol(ITSPHPAst definitionAst, Set<Integer> modifiers, String name, IScope enclosingScope) {
        super(definitionAst, modifiers, name, enclosingScope);
    }

    @Override
    public void define(ISymbol symbol) {
        super.define(symbol);
        MapHelper.addToListMap(symbolsCaseInsensitive, symbol.getName(), symbol);
    }

    @Override
    public boolean doubleDefinitionCheckCaseInsensitive(ISymbol symbol) {
        return ScopeHelperRegistry.get().doubleDefinitionCheck(symbolsCaseInsensitive, symbol);
    }

    @Override
    public ISymbol resolveWithFallbackToParent(ITSPHPAst ast) {
        ISymbol symbol = ScopeHelperRegistry.get().resolve(this, ast);
        if (symbol == null) {
            symbol = parent.resolveWithFallbackToParent(ast);
        }
        return symbol;
    }

    @Override
    public IPolymorphicTypeSymbol getParent() {
        return parent;
    }

    @Override
    public void setParent(IPolymorphicTypeSymbol newParent) {
        parent = newParent;
    }
}
