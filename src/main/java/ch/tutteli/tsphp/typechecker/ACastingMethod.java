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
package ch.tutteli.tsphp.typechecker;

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tutteli.tsphp.typechecker.utils.IAstHelper;
import org.antlr.runtime.Token;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public abstract class ACastingMethod implements ICastingMethod
{

    protected IAstHelper astHelper;
    protected ITypeSymbol typeSymbol;

    protected abstract int getTokenType();

    public ACastingMethod(IAstHelper theAstHelper, ITypeSymbol theType) {
        astHelper = theAstHelper;
        typeSymbol = theType;
    }

    @Override
    public ITypeSymbol getType() {
        return typeSymbol;
    }

    @Override
    public ITSPHPAst createCastAst(ITSPHPAst ast) {
        //create the cast based on the given (to take the given position etc.)
        ITSPHPAst cast = astHelper.createAst(ast);

        //^(CASTING Type expression
        Token token = cast.getToken();
        token.setType(TSPHPDefinitionWalker.CASTING);
        token.setText("casting");
        ITSPHPAst type = astHelper.createAst(getTokenType(), typeSymbol.getName());
        cast.addChild(type);
        cast.addChild(ast);
        return cast;
    }
}
