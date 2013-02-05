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

import ch.tutteli.tsphp.common.ASymbol;
import ch.tutteli.tsphp.common.IType;
import ch.tutteli.tsphp.common.TSPHPAst;
import java.util.Set;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class ASymbolWithModifier extends ASymbol
{

    protected Set<Integer> modifiers;

    public ASymbolWithModifier(String name, TSPHPAst definitionAst, Set<Integer> theModifiers) {
        super(name, definitionAst);
        modifiers = theModifiers;
    }

    public ASymbolWithModifier(String name, TSPHPAst definitionAst, Set<Integer> theModifiers, IType type) {
        super(name, definitionAst, type);
        modifiers = theModifiers;
    }

    @Override
    public String toString() {
        return super.toString() + (!modifiers.isEmpty() ? "|" + getModifiersAsString() : "");
    }

    private String getModifiersAsString() {
        StringBuilder stringBuilder = new StringBuilder();
        boolean firstModifier = true;
        for (Integer modifier : modifiers) {
            if (!firstModifier) {
                stringBuilder.append(',');
            }
            firstModifier = false;
            stringBuilder.append(modifier);
        }
        return stringBuilder.toString();
    }
}
