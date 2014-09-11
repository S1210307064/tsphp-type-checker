/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;

import java.util.HashSet;
import java.util.Set;

public abstract class ATypeSymbol extends ASymbolWithModifier implements ITypeSymbol
{

    private final Set<ITypeSymbol> parentTypeSymbols;

    @SuppressWarnings("checkstyle:parameternumber")
    public ATypeSymbol(final ITSPHPAst theDefinitionAst, final String theName, final ITypeSymbol theParentTypeSymbol) {
        super(theDefinitionAst, new ModifierSet(), theName);
        parentTypeSymbols = new HashSet<>(1);
        parentTypeSymbols.add(theParentTypeSymbol);
    }

    @SuppressWarnings("checkstyle:parameternumber")
    public ATypeSymbol(ITSPHPAst theDefinitionAst, String theName, Set<ITypeSymbol> theParentTypeSymbols) {
        super(theDefinitionAst, new ModifierSet(), theName);
        parentTypeSymbols = theParentTypeSymbols;
    }

    @Override
    public Set<ITypeSymbol> getParentTypeSymbols() {
        return parentTypeSymbols;
    }

    @Override
    public boolean isFalseable() {
        return modifiers.isFalseable();
    }

    @Override
    public boolean isNullable() {
        return modifiers.isNullable();
    }
}
