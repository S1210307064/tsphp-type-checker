/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.symbols.ISymbolWithModifier;
import ch.tsphp.common.symbols.modifiers.ICanBePrivate;
import ch.tsphp.common.symbols.modifiers.ICanBeProtected;
import ch.tsphp.common.symbols.modifiers.ICanBePublic;

public interface ISymbolWithAccessModifier extends ISymbolWithModifier, ICanBePublic, ICanBeProtected, ICanBePrivate
{
    boolean canBeAccessedFrom(int type);
}
