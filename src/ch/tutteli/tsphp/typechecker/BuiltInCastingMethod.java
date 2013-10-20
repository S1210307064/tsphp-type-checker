package ch.tutteli.tsphp.typechecker;

import ch.tutteli.tsphp.common.IAstHelper;
import ch.tutteli.tsphp.typechecker.symbols.ITypeSymbolWithPHPBuiltInCasting;

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
