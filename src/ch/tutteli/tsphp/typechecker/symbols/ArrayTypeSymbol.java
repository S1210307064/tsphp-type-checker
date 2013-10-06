package ch.tutteli.tsphp.typechecker.symbols;

import ch.tutteli.tsphp.common.ITypeSymbol;

public class ArrayTypeSymbol extends ANullableTypeSymbol implements IArrayTypeSymbol
{

    private final ITypeSymbol keyTypeSymbol;
    private final ITypeSymbol valueTypeSymbol;
    private final int tokenType;

    public ArrayTypeSymbol(String name, int theTokenType, ITypeSymbol theKeyTypeSymbol, ITypeSymbol theValueTypeSymbol,
            ITypeSymbol parentTypeSymbol) {
        super(name, parentTypeSymbol);
        tokenType = theTokenType;
        keyTypeSymbol = theKeyTypeSymbol;
        valueTypeSymbol = theValueTypeSymbol;
    }

    @Override
    public int getTokenTypeForCasting() {
        return tokenType;
    }

    @Override
    public ITypeSymbol getKeyTypeSymbol() {
        return keyTypeSymbol;
    }

    @Override
    public ITypeSymbol getValueTypeSymbol() {
        return valueTypeSymbol;
    }
}
