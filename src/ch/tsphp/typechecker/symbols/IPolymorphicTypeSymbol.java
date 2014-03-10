/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.ISymbol;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.ITypeSymbol;
import ch.tsphp.typechecker.scopes.ICaseInsensitiveScope;

import java.util.Set;

public interface IPolymorphicTypeSymbol extends ITypeSymbol, ISymbolWithModifier, ICaseInsensitiveScope, ICanBeAbstract
{

    ISymbol resolveWithFallbackToParent(ITSPHPAst ast);

    void addParentTypeSymbol(IPolymorphicTypeSymbol aParent);

    Set<ISymbol> getAbstractSymbols();

}
