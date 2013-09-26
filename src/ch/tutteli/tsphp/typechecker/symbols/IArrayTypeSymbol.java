package ch.tutteli.tsphp.typechecker.symbols;

import ch.tutteli.tsphp.common.ITypeSymbol;

public interface IArrayTypeSymbol extends ITypeSymbolWithPHPBuiltInCasting
{
    ITypeSymbol getKeyTypeSymbol();
    ITypeSymbol getValueTypeSymbol();
}
