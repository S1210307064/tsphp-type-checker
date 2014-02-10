package ch.tsphp.typechecker;

import ch.tsphp.common.IAstHelper;
import ch.tsphp.typechecker.symbols.ITypeSymbolWithPHPBuiltInCasting;

public class BuiltInCastingMethod extends ACastingMethod
{

    public BuiltInCastingMethod(IAstHelper theAstHelper, ITypeSymbolWithPHPBuiltInCasting theType) {
        super(theAstHelper, theType);
    }

    @Override
    protected int getTokenType() {
        return ((ITypeSymbolWithPHPBuiltInCasting) typeSymbol).getTokenTypeForCasting();
    }
}
