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
package ch.tutteli.tsphp.typechecker.symbols.erroneous;

import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.exceptions.TypeCheckerException;
import ch.tutteli.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class ErroneousClassTypeSymbol extends AErroneousScopedSymbol implements IErroneousClassTypeSymbol
{

    private IMethodSymbol construct;

    public ErroneousClassTypeSymbol(ITSPHPAst ast, TypeCheckerException exception, IMethodSymbol theConstruct) {
        super(ast, exception);
        construct = theConstruct;
    }

    @Override
    public ISymbol resolveWithFallbackToParent(ITSPHPAst ast) {
        throw new UnsupportedOperationException("ErroneousClassSymbol is not a real class.");
    }

    @Override
    public void setConstruct(IMethodSymbol newConstruct) {
        throw new UnsupportedOperationException("ErroneousClassSymbol is not a real class.");
    }

    @Override
    public IMethodSymbol getConstruct() {
        return construct;
    }

    @Override
    public IClassTypeSymbol getParent() {
        return this;
    }

    @Override
    public void setParent(IClassTypeSymbol newParent) {
        throw new UnsupportedOperationException("ErroneousClassSymbol is not a real class.");
    }

    @Override
    public boolean doubleDefinitionCheckCaseInsensitive(ISymbol symbol) {
        throw new UnsupportedOperationException("ErroneousClassSymbol is not a real class.");
    }
}
