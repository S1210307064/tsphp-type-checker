package ch.tutteli.tsphp.typechecker.symbols;

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import java.util.Set;

public class VoidTypeSymbol extends ATypeSymbol implements IVoidTypeSymbol
{

    public VoidTypeSymbol() {
        super(null, "void", (Set<ITypeSymbol>) null);
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public ITSPHPAst getDefaultValue() {
        throw new UnsupportedOperationException("Void has no default value and should not be used as type"
                + " other than return type of a function/method.");
    }
}
