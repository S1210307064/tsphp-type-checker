/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker;

import ch.tsphp.typechecker.symbols.IMethodSymbol;

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
