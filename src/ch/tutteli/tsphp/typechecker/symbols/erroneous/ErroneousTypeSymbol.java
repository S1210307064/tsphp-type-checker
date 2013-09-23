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

import ch.tutteli.tsphp.common.AstHelperRegistry;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.common.exceptions.TypeCheckerException;
import ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import java.util.Set;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class ErroneousTypeSymbol extends AErroneousSymbol implements IErroneousTypeSymbol
{

    public ErroneousTypeSymbol(ITSPHPAst type, TypeCheckerException exception) {
        super(type, exception);
    }

    @Override
    public Set<ITypeSymbol> getParentTypeSymbols() {
        throw new UnsupportedOperationException("ErroneousTypeSymbol is not a real type.");
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
