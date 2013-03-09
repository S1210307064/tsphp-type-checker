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
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public abstract class ASymbolWithModifier extends ASymbol implements ISymbol
{

    protected Set<Integer> modifiers;

    public ASymbolWithModifier(ITSPHPAst definitionAst, Set<Integer> theModifiers, String name) {
        super(definitionAst, name);
        modifiers = theModifiers;
    }

    @Override
    public String toString() {
        return super.toString() + ModifierHelper.getModifiers(new TreeSet<>(modifiers));
    }

//    @Override
//    public void addModifier(Integer modifier) {
//        modifiers.add(modifier);
//    }
//
//    @Override
//    public Set<Integer> getModifiers() {
//        return modifiers;
//    }
//
//    @Override
//    public void setModifiers(Set<Integer> newModifiers) {
//        modifiers = newModifiers;
//    }
}
