/*
 * Copyright 2013 Robert Stoll <rstoll@tutteli.ch>
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 *
 * Adopted from the book Language Implementation Patterns by Terence Parr
 */
public abstract class AScope implements IScope
{

    protected String scopeName;
    protected IScope enclosingScope;
    protected Map<String, List<ISymbol>> symbols = new LinkedHashMap<>();

    public AScope(String theScopeName, IScope theEnclosingScope) {
        scopeName = theScopeName;
        enclosingScope = theEnclosingScope;
    }

    @Override
    public void define(ISymbol symbol) {
        ScopeHelperRegistry.get().define(this, symbol);
    }

    @Override
    public boolean doubleDefinitionCheck(ISymbol symbol) {
       return ScopeHelperRegistry.get().doubleDefinitionCheck(symbols, symbol);
    }

    @Override
    public ISymbol resolve(ITSPHPAst typeAst) {
        return ScopeHelperRegistry.get().resolve(this, typeAst);
    }

    @Override
    public IScope getEnclosingScope() {
        return enclosingScope;
    }

    @Override
    public String getScopeName() {
        return scopeName;
    }

    @Override
    public Map<String, List<ISymbol>> getSymbols() {
        return symbols;
    }

    @Override
    public String toString() {
        return scopeName + ":" + symbols.keySet().toString();
    }
}
