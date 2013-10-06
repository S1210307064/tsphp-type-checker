package ch.tutteli.tsphp.typechecker.symbols;

import ch.tutteli.tsphp.common.AstHelperRegistry;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import java.util.Set;

public abstract class ANullableTypeSymbol extends ATypeSymbol
{

    public ANullableTypeSymbol(String name, ITypeSymbol parentTypeSymbol) {
        super(null, name, parentTypeSymbol);
    }

    public ANullableTypeSymbol(String name, Set<ITypeSymbol> parentTypeSymbols) {
        super(null, name, parentTypeSymbols);
    }

    @Override
    public boolean isNullable() {
        return true;
    }

    @Override
    public ITSPHPAst getDefaultValue() {
        return AstHelperRegistry.get().createAst(TSPHPDefinitionWalker.Null, "null");
    }
}
