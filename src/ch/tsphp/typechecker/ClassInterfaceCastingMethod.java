package ch.tsphp.typechecker;

import ch.tsphp.common.IAstHelper;
import ch.tsphp.common.ITypeSymbol;
import ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker;

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
