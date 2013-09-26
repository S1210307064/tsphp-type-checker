package ch.tutteli.tsphp.typechecker.symbols;

import ch.tutteli.tsphp.common.ITypeSymbol;

public interface ITypeSymbolWithPHPBuiltInCasting extends ITypeSymbol
{
    int getTokenTypeForCasting();
}
