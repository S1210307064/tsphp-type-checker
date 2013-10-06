package ch.tutteli.tsphp.typechecker.symbols;

import ch.tutteli.tsphp.common.ITypeSymbol;

import java.util.HashSet;

public class NullTypeSymbol extends ANullableTypeSymbol implements INullTypeSymbol
{

    public NullTypeSymbol() {
        super("null", new HashSet<ITypeSymbol>());
    }
}
