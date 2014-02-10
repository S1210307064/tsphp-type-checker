package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.ITypeSymbol;

import java.util.HashSet;

public class NullTypeSymbol extends ANullableTypeSymbol implements INullTypeSymbol
{

    public NullTypeSymbol() {
        super("null", new HashSet<ITypeSymbol>());
    }
}
