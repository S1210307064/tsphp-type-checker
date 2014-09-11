/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.symbols.modifiers.ICanBeFalseable;
import ch.tsphp.common.symbols.modifiers.ICanBeNullable;
import ch.tsphp.common.symbols.modifiers.ICanBeStatic;
import ch.tsphp.common.symbols.modifiers.ICanHaveCastModifier;

public interface IVariableSymbol extends ISymbolWithAccessModifier, ICanBeStatic, ICanHaveCastModifier,
        ICanBeFalseable, ICanBeNullable
{
    TypeWithModifiersDto toTypeWithModifiersDto();
}
