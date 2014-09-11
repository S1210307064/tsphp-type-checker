/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ASymbol;
import ch.tsphp.common.symbols.ISymbolWithModifier;
import ch.tsphp.common.symbols.modifiers.IModifierSet;

import java.util.TreeSet;

public abstract class ASymbolWithModifier extends ASymbol implements ISymbolWithModifier
{

    protected IModifierSet modifiers;

    public ASymbolWithModifier(ITSPHPAst definitionAst, IModifierSet theModifiers, String name) {
        super(definitionAst, name);
        modifiers = theModifiers;
    }

    @Override
    public String toString() {
        return super.toString() + ch.tsphp.typechecker.utils.ModifierHelper.getModifiersAsString(
                new TreeSet<>(modifiers));
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
    public IModifierSet getModifiers() {
        return modifiers;
    }

    @Override
    public void setModifiers(IModifierSet newModifiers) {
        modifiers = newModifiers;
    }
}
