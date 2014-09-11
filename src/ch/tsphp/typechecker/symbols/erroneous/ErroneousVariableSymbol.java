/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.symbols.erroneous;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.typechecker.symbols.ModifierSet;
import ch.tsphp.typechecker.symbols.TypeWithModifiersDto;

public class ErroneousVariableSymbol extends AErroneousSymbol implements IErroneousVariableSymbol
{

    public static final String ERROR_MESSAGE = "ErroneousVariableSymbol is not a real variable symbol.";

    public ErroneousVariableSymbol(ITSPHPAst ast, TSPHPException exception) {
        super(ast, exception);
    }

    @Override
    public boolean isStatic() {
        return true;
    }

    @Override
    public boolean isAlwaysCasting() {
        return true;
    }

    @Override
    public void addModifier(Integer modifier) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public boolean removeModifier(Integer modifier) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public IModifierSet getModifiers() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public void setModifiers(IModifierSet modifiers) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public boolean canBeAccessedFrom(int type) {
        return true;
    }

    @Override
    public boolean isPublic() {
        return true;
    }

    @Override
    public boolean isProtected() {
        return false;
    }

    @Override
    public boolean isPrivate() {
        return false;
    }

    @Override
    public boolean isFalseable() {
        return true;
    }

    @Override
    public boolean isNullable() {
        return true;
    }

    @Override
    public TypeWithModifiersDto toTypeWithModifiersDto() {
        return new TypeWithModifiersDto(getType(), new ModifierSet());
    }
}
