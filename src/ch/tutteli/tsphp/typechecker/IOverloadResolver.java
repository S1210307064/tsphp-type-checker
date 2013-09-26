package ch.tutteli.tsphp.typechecker;

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IVariableSymbol;
import java.util.List;

public interface IOverloadResolver
{

    List<OverloadDto> getApplicableOverloads(List<IMethodSymbol> methods,
            List<ITSPHPAst> actualParameterTypes);

    OverloadDto getMostSpecificApplicableOverload(List<OverloadDto> methods)
            throws AmbiguousCallException;

    CastingDto getCastingDto(IVariableSymbol formalParameter, ITSPHPAst actualParameter);

    CastingDto getCastingDtoAlwaysCasting(IVariableSymbol formalParameter, ITSPHPAst actualParameter);

    /**
     * Return how many promotions have to be applied to the actualType to reach the formalType whereby -1 is returned in
     * the case where formalType is not the actualType or one of its parent types.
     */
    int getPromotionLevelFromTo(ITypeSymbol fromType, ITypeSymbol toType);

    int getPromotionLevelFromToConsiderNull(ITypeSymbol actualParameterType, ITypeSymbol formalParameterType);

    boolean isSameOrParentType(int promotionLevel);

    boolean isSameOrParentTypeConsiderNull(IVariableSymbol formalType, ITSPHPAst actualType);

    boolean isSameOrParentTypeConsiderNull(ITypeSymbol formalParameterType, ITypeSymbol actualParameterType);
}
