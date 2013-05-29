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
package ch.tutteli.tsphp.typechecker;

import ch.tutteli.tsphp.common.IAstHelper;
import ch.tutteli.tsphp.typechecker.symbols.ITypeSymbolWithPHPBuiltInCasting;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class BuiltInCastingMethod extends ACastingMethod implements ICastingMethod
{

    public BuiltInCastingMethod(IAstHelper theAstHelper, ITypeSymbolWithPHPBuiltInCasting theType) {
        super(theAstHelper, theType);
    }

    @Override
    protected int getTokenType() {
        return ((ITypeSymbolWithPHPBuiltInCasting) typeSymbol).getTokenTypeForCasting();
    }
}
