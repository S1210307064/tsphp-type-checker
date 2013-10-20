package ch.tutteli.tsphp.typechecker;

import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;

import java.io.Serializable;
import java.util.List;

public class OverloadDto implements Serializable
{

    public IMethodSymbol methodSymbol;
    public int parameterPromotedCount;
    public int promotionsTotal;
    public List<CastingDto> parametersNeedCasting;

    public OverloadDto(IMethodSymbol theMethodSymbol, int howManyParameterWerePromoted, int thePromotionsInTotal,
            List<CastingDto> theParametersNeedCasting) {
        methodSymbol = theMethodSymbol;
        parameterPromotedCount = howManyParameterWerePromoted;
        promotionsTotal = thePromotionsInTotal;
        parametersNeedCasting = theParametersNeedCasting;
    }
}
