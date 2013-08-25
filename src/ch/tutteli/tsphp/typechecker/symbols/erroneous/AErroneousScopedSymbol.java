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
package ch.tutteli.tsphp.typechecker.symbols.erroneous;

import ch.tutteli.tsphp.common.ASymbol;
import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.exceptions.TypeCheckerException;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolWithModifier;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public abstract class AErroneousScopedSymbol extends ASymbol implements IErroneousSymbol, IScope, ISymbolWithModifier
{

    private TypeCheckerException exception;

    public AErroneousScopedSymbol(ITSPHPAst ast, TypeCheckerException theException) {
        super(ast, ast.getText());
    }

    @Override
    public TypeCheckerException getException() {
        return exception;
    }

    @Override
    public String getScopeName() {
        throw new UnsupportedOperationException("AErroneousScopedSymbol is not a real scope.");
    }

    @Override
    public IScope getEnclosingScope() {
        throw new UnsupportedOperationException("AErroneousScopedSymbol is not a real scope.");
    }

    @Override
    public void define(ISymbol is) {
        throw new UnsupportedOperationException("AErroneousScopedSymbol is not a real scope.");
    }

    @Override
    public boolean doubleDefinitionCheck(ISymbol is) {
        throw new UnsupportedOperationException("AErroneousScopedSymbol is not a real scope.");
    }

    @Override
    public ISymbol resolve(ITSPHPAst itsphp) {
        throw new UnsupportedOperationException("AErroneousScopedSymbol is not a real scope.");
    }

    @Override
    public Map<String, List<ISymbol>> getSymbols() {
        throw new UnsupportedOperationException("AErroneousScopedSymbol is not a real scope.");
    }

    @Override
    public void addModifier(Integer modifier) {
        throw new UnsupportedOperationException("AErroneousScopedSymbol is not a real symbol with modifier.");
    }

    @Override
    public boolean removeModifier(Integer modifier) {
        throw new UnsupportedOperationException("AErroneousScopedSymbol is not a real symbol with modifier.");
    }

    @Override
    public Set<Integer> getModifiers() {
        throw new UnsupportedOperationException("AErroneousScopedSymbol is not a real symbol with modifier.");
    }

    @Override
    public void setModifiers(Set<Integer> modifier) {
        throw new UnsupportedOperationException("AErroneousScopedSymbol is not a real symbol with modifier.");
    }
}
