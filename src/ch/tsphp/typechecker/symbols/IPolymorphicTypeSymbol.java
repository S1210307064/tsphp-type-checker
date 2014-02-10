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
