package ch.tutteli.tsphp.typechecker.symbols;

import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.typechecker.utils.IAstHelper;
import java.util.HashSet;

public class NullTypeSymbol extends ANullableTypeSymbol implements INullTypeSymbol
{

    public NullTypeSymbol() {
        super(null, "null", new HashSet<ITypeSymbol>());
    }
}
