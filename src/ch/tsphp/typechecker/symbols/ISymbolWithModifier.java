/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.ISymbol;

import java.util.Set;

public interface ISymbolWithModifier extends ISymbol
{

    void addModifier(Integer modifier);

    boolean removeModifier(Integer modifier);

    Set<Integer> getModifiers();

    void setModifiers(Set<Integer> modifier);
}
