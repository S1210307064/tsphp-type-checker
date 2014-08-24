/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker;

import ch.tsphp.typechecker.symbols.IMethodSymbol;

import java.io.Serializable;
import java.util.List;

/**
 * Represents meta-data of an overload, e.g. method overload etc.
 */
public class OverloadDto implements Serializable
{

    public IMethodSymbol methodSymbol;

    /**
     * Count which tells how many parameters fit since they can be promoted.
     * <p/>
     * Promotion is happening when for instance Exception is required and ErrorException provided (this corresponds
     * to one promotion level)
     */
    public int parameterPromotedCount;

    /**
     * Summation of promotion levels
     */
    public int promotionsTotal;

    /**
     * All the parameters which need casting
     */
    public List<CastingDto> parametersNeedCasting;

    public OverloadDto(IMethodSymbol theMethodSymbol, int howManyParameterWerePromoted, int thePromotionsInTotal,
            List<CastingDto> theParametersNeedCasting) {
        methodSymbol = theMethodSymbol;
        parameterPromotedCount = howManyParameterWerePromoted;
        promotionsTotal = thePromotionsInTotal;
        parametersNeedCasting = theParametersNeedCasting;
    }
}
