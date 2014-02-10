package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.ITypeSymbol;

public interface IArrayTypeSymbol extends ITypeSymbolWithPHPBuiltInCasting
{
    ITypeSymbol getKeyTypeSymbol();

    ITypeSymbol getValueTypeSymbol();
}
