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
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.common.TSPHPAst;
import ch.tutteli.tsphp.common.exceptions.TypeCheckerException;
import ch.tutteli.tsphp.typechecker.utils.MapHelper;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class NamespaceScope extends AScope implements INamespaceScope
{

    private Map<String, List<TSPHPAst>> uses = new LinkedHashMap<>();

    public NamespaceScope(String scopeName, IScope globalNamespaceScope) {
        super(scopeName, globalNamespaceScope);
    }

    @Override
    public void define(ISymbol symbol) {
        //we define symbols in the corresponding global namespace scope in order that it can be found from other
        //namespaces as well
        enclosingScope.define(symbol);
        //However, definition scope is this one, is used for alias resolving and name clashes
        symbol.setDefinitionScope(this);
    }

    @Override
    public ISymbol resolve(String name) {
        //we resolve from the corresponding global namespace scope 
        return enclosingScope.resolve(name);
    }

    @Override
    public ITypeSymbol resolveType(TSPHPAst typeAst) {
        return ScopeHelperRegistry.get().resolveType(this, typeAst);
    }

    @Override
    public void addUse(String alias, TSPHPAst type) {
        MapHelper.addToListMap(uses, alias, type);
    }

    @Override
    public List<TSPHPAst> getUse(String alias) {
        return uses.get(alias);
    }

    @Override
    public TSPHPAst getOneUse(String alias) {
        return uses.containsKey(alias) ? uses.get(alias).get(0) : null;
    }
}
