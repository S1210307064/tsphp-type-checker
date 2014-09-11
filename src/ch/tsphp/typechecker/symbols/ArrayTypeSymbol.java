/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.symbols.ITypeSymbol;

public class ArrayTypeSymbol extends ANullableTypeSymbol implements IArrayTypeSymbol
{

    private final ITypeSymbol keyTypeSymbol;
    private final ITypeSymbol valueTypeSymbol;
    private final int tokenType;

    @SuppressWarnings("checkstyle:parameternumber")
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
