package ch.tutteli.tsphp.typechecker.symbols;

import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;

public interface IPolymorphicTypeSymbol extends ITypeSymbol, ISymbolWithModifier, IScope
{

    ISymbol resolveWithFallbackToParent(ITSPHPAst ast);

    void addParentTypeSymbol(IPolymorphicTypeSymbol aParent);
}
