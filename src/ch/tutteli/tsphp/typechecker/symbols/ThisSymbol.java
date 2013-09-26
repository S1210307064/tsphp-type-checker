package ch.tutteli.tsphp.typechecker.symbols;

import ch.tutteli.tsphp.common.ITSPHPAst;

public class ThisSymbol extends ASymbolWithAccessModifier implements IVariableSymbol
{

    public ThisSymbol(ITSPHPAst definitionAst, String name, IPolymorphicTypeSymbol polymorphicTypeSymbol) {
        super(definitionAst, null, name);
        type = polymorphicTypeSymbol;
        setDefinitionScope(polymorphicTypeSymbol.getDefinitionScope());
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public boolean isAlwaysCasting() {
        return false;
    }
}
