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
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.common.TSPHPAst;
import ch.tutteli.tsphp.common.exceptions.TypeCheckerException;
import ch.tutteli.tsphp.typechecker.error.ErrorHelperRegistry;
import ch.tutteli.tsphp.typechecker.scopes.ScopeHelperRegistry;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 *
 * Adopted from the book Language Implementation Patterns by Terence Parr
 */
public abstract class AScopedSymbol extends ASymbolWithModifier implements IScope
{

    protected IScope enclosingScope;
    protected Map<String, List<ISymbol>> members = new LinkedHashMap<>();

    public AScopedSymbol(TSPHPAst definitionAst, Set<Integer> modifiers, String name, IScope theEnclosingScope) {
        super(definitionAst, modifiers, name);
        enclosingScope = theEnclosingScope;
    }

    @Override
    public Map<String, List<ISymbol>> getSymbols() {
        return members;
    }

    @Override
    public void define(ISymbol symbol) {
        ScopeHelperRegistry.get().define(this, symbol);
    }

    @Override
    public void definitionCheck(ISymbol symbol) {
        ScopeHelperRegistry.get().definitionCheck(this, symbol);
    }

    @Override
    public ISymbol resolve(String name) {
        return ScopeHelperRegistry.get().resolve(this, name);
    }

    @Override
    public ITypeSymbol resolveType(TSPHPAst typeAst) {
        //only INamespaceScope define types.
        return enclosingScope.resolveType(typeAst);
    }

    @Override
    public IScope getParentScope() {
        return getEnclosingScope();
    }

    @Override
    public IScope getEnclosingScope() {
        return enclosingScope;
    }

    @Override
    public String getScopeName() {
        return name;
    }
}
