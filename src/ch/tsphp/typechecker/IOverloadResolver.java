/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tsphp.typechecker.symbols.TypeWithModifiersDto;

import java.util.List;

/**
 * Responsible to resolve overloads, e.g. method overload, operator overload etc.
 */
public interface IOverloadResolver
{

    List<OverloadDto> getApplicableOverloads(List<IMethodSymbol> methods, List<ITSPHPAst> actualParameterTypes);

    OverloadDto getMostSpecificApplicableOverload(List<OverloadDto> methods) throws AmbiguousCallException;

    CastingDto getCastingDto(ITSPHPAst actualParameter, TypeWithModifiersDto formalParameter);

    CastingDto getCastingDtoInAlwaysCastingMode(ITSPHPAst actualParameter, TypeWithModifiersDto formalParameter);

    /**
     * Return how many promotions have to be applied to the actualParameterType to reach formalParameterType
     * whereby -1 is returned in the case where actualParameterType has not the same type as formalParameterType and
     * is not a sub-type either.
     */
    int getPromotionLevelFromTo(ITypeSymbol actualParameterType, ITypeSymbol formalParameterType);

    /**
     * Uses the same logic as getPromotionLevelFromTo but checks first if actualParameter is null or false.
     * <p/>
     * If actualParameter is null and formalParameter is nullable then 0 is returned. If actualParameter is false and
     * the formalParameter is falseable then 0 is returned as well. If actualParameter is null or false and
     * formalParameter is not nullable, falseable respectively, then -1 is returned.
     */
    int getPromotionLevelFromToConsiderFalseAndNull(
            ITypeSymbol actualParameterType, String actualParameterValue, TypeWithModifiersDto formalParameter);

    boolean isSameOrSubType(int promotionLevel);

    boolean isSameOrSubTypeConsiderFalseAndNull(
            ITypeSymbol actualParameterType, String actualParameterValue, TypeWithModifiersDto formalParameter);

    boolean isFormalCorrespondingFalseableOrNullableType(
            ITypeSymbol actualParameterType, ITypeSymbol formalParameterType);
}
