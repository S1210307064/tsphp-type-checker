/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.ITSPHPAst;

public class ThisSymbol extends ASymbolWithAccessModifier implements IVariableSymbol
{

    public ThisSymbol(ITSPHPAst definitionAst, String name, IPolymorphicTypeSymbol polymorphicTypeSymbol) {
        super(definitionAst, null, name);
        type = polymorphicTypeSymbol;
        setDefinitionScope(polymorphicTypeSymbol.getDefinitionScope());
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public boolean isAlwaysCasting() {
        return false;
    }

    @Override
    public boolean isFalseable() {
        return false;
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public TypeWithModifiersDto toTypeWithModifiersDto() {
        return new TypeWithModifiersDto(getType(), modifiers);
    }
}
