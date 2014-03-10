/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.ASymbol;
import ch.tsphp.common.ITSPHPAst;

import java.util.Set;
import java.util.TreeSet;

public abstract class ASymbolWithModifier extends ASymbol implements ISymbolWithModifier
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

    @Override
    public void addModifier(Integer modifier) {
        modifiers.add(modifier);
    }

    @Override
    public boolean removeModifier(Integer modifier) {
        return modifiers.remove(modifier);
    }

    @Override
    public Set<Integer> getModifiers() {
        return modifiers;
    }

    @Override
    public void setModifiers(Set<Integer> newModifiers) {
        modifiers = newModifiers;
    }
}
