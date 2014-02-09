package ch.tutteli.tsphp.typechecker.symbols;

import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.typechecker.scopes.ICaseInsensitiveScope;

import java.util.Set;

public interface IPolymorphicTypeSymbol extends ITypeSymbol, ISymbolWithModifier, ICaseInsensitiveScope, ICanBeAbstract
{

    ISymbol resolveWithFallbackToParent(ITSPHPAst ast);

    void addParentTypeSymbol(IPolymorphicTypeSymbol aParent);

    Set<ISymbol> getAbstractSymbols();

}
