package ch.tutteli.tsphp.typechecker.symbols;

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;

public class AliasTypeSymbol extends ATypeSymbol implements IAliasTypeSymbol
{

    public AliasTypeSymbol(ITSPHPAst definitionAst, String name, ITypeSymbol parentTypeSymbol) {
        super(definitionAst, name, parentTypeSymbol);
    }

    @Override
    public boolean isNullable() {
        return true;
    }

    @Override
    public ITSPHPAst getDefaultValue() {
        throw new UnsupportedOperationException("AliasTypeSymbol does not have an default value.");
    }
}
