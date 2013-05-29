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
package ch.tutteli.tsphp.typechecker.symbols;

import ch.tutteli.tsphp.common.AstHelperRegistry;
import ch.tutteli.tsphp.common.ILowerCaseStringMap;
import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.common.LowerCaseStringMap;
import ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tutteli.tsphp.typechecker.scopes.ICaseInsensitiveScope;
import ch.tutteli.tsphp.typechecker.scopes.ScopeHelperRegistry;
import ch.tutteli.tsphp.typechecker.utils.IAstHelper;
import ch.tutteli.tsphp.typechecker.utils.MapHelper;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public abstract class AScopedTypeSymbol extends AScopedSymbol implements ICaseInsensitiveScope, IPolymorphicTypeSymbol
{

    protected Set<ITypeSymbol> parentTypeSymbols = new HashSet<>();
    protected ILowerCaseStringMap<List<ISymbol>> symbolsCaseInsensitive = new LowerCaseStringMap<>();
    private boolean isObjectTheParentTypeSymbol = false;

    public AScopedTypeSymbol(ITSPHPAst definitionAst, Set<Integer> modifiers, String name, IScope enclosingScope,
            ITypeSymbol theParentTypeSymbol) {
        super(definitionAst, modifiers, name, enclosingScope);
        parentTypeSymbols.add(theParentTypeSymbol);
        isObjectTheParentTypeSymbol = theParentTypeSymbol.getName().equals("object");
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
        if (symbol == null && !parentTypeSymbols.isEmpty()) {
            for (ITypeSymbol parentTypeSymbol : parentTypeSymbols) {
                if (parentTypeSymbol instanceof IPolymorphicTypeSymbol) {
                    symbol = ((IPolymorphicTypeSymbol) parentTypeSymbol).resolveWithFallbackToParent(ast);
                    if (symbol != null) {
                        break;
                    }
                }
            }
        }
        return symbol;
    }

    @Override
    public Set<ITypeSymbol> getParentTypeSymbols() {
        return parentTypeSymbols;
    }

    @Override
    public void addParentTypeSymbol(IPolymorphicTypeSymbol aParent) {
        if (isObjectTheParentTypeSymbol) {
            parentTypeSymbols = new HashSet<>();
            isObjectTheParentTypeSymbol = false;
        }
        parentTypeSymbols.add(aParent);
    }

    @Override
    public boolean isNullable() {
        return true;
    }

    @Override
    public ITSPHPAst getDefaultValue() {
        return AstHelperRegistry.get().createAst(TSPHPDefinitionWalker.Null, "null");
    }
}
