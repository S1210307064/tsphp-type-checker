package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.ITypeSymbol;

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
