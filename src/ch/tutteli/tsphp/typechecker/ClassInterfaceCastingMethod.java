package ch.tutteli.tsphp.typechecker;

import ch.tutteli.tsphp.common.IAstHelper;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker;

class ClassInterfaceCastingMethod extends ACastingMethod
{

    public ClassInterfaceCastingMethod(IAstHelper astHelper, ITypeSymbol typeSymbol) {
        super(astHelper, typeSymbol);
    }

    @Override
    protected int getTokenType() {
        return TSPHPDefinitionWalker.TYPE_NAME;
    }
}
