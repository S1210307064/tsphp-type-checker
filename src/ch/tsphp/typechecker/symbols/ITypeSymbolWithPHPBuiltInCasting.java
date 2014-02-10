package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.ITypeSymbol;

public interface ITypeSymbolWithPHPBuiltInCasting extends ITypeSymbol
{
    int getTokenTypeForCasting();
}
