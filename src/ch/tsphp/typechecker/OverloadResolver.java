/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tsphp.typechecker.symbols.IVariableSymbol;
import ch.tsphp.typechecker.symbols.TypeWithModifiersDto;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OverloadResolver implements IOverloadResolver
{

    private final Map<ITypeSymbol, Map<ITypeSymbol, ICastingMethod>> explicitCasts;
    private final Map<ITypeSymbol, Map<ITypeSymbol, ICastingMethod>> implicitCasts;
    private final ITypeSystem typeSystem;

    public OverloadResolver(ITypeSystem theTypeSystem) {
        typeSystem = theTypeSystem;
        implicitCasts = typeSystem.getImplicitCasting();
        explicitCasts = theTypeSystem.getExplicitCastings();
    }

    @Override
    public List<OverloadDto> getApplicableOverloads(List<IMethodSymbol> methods, List<ITSPHPAst> actualParameters) {

        List<OverloadDto> applicableMethods = new ArrayList<>();
        for (IMethodSymbol method : methods) {

            OverloadDto methodDto = getApplicableOverload(method, actualParameters);

            if (methodDto != null) {
                applicableMethods.add(methodDto);
            }
        }
        return applicableMethods;
    }

    private OverloadDto getApplicableOverload(IMethodSymbol method, List<ITSPHPAst> actualParameters) {
        OverloadDto methodDto = null;
        List<IVariableSymbol> formalParameters = method.getParameters();
        //Todo rstoll TSPHP-525 optional parameters and OverloadResolver
        if (formalParameters.size() == actualParameters.size()) {
            if (!formalParameters.isEmpty()) {
                methodDto = getApplicableOverload(method, actualParameters, formalParameters);
            } else {
                methodDto = new OverloadDto(method, 0, 0, null);
            }
        }
        return methodDto;
    }

    private OverloadDto getApplicableOverload(
            IMethodSymbol method, List<ITSPHPAst> actualParameters, List<IVariableSymbol> formalParameters) {

        OverloadDto methodDto = null;

        int parameterCount = formalParameters.size();
        int promotionTotalCount = 0;
        int promotionParameterCount = 0;
        CastingDto castingDto = null;
        List<CastingDto> parametersNeedCasting = new ArrayList<>();
        for (int i = 0; i < parameterCount; ++i) {
            castingDto = getCastingDto(actualParameters.get(i), formalParameters.get(i).toTypeWithModifiersDto());
            if (castingDto != null) {
                if (castingDto.promotionLevel != 0) {
                    ++promotionParameterCount;
                    promotionTotalCount += castingDto.promotionLevel;
                }
                if (castingDto.castingMethods != null) {
                    parametersNeedCasting.add(castingDto);
                }
            } else {
                break;
            }
        }
        if (castingDto != null) {
            methodDto = new OverloadDto(method, promotionParameterCount, promotionTotalCount, parametersNeedCasting);
        }
        return methodDto;
    }

    @Override
    public CastingDto getCastingDto(ITSPHPAst actualParameter, TypeWithModifiersDto formalParameter) {
        return !formalParameter.modifiers.isAlwaysCasting()
                ? getCastingDtoInNormalMode(actualParameter, formalParameter)
                : getCastingDtoInAlwaysCastingMode(actualParameter, formalParameter);
    }

    private CastingDto getCastingDtoInNormalMode(ITSPHPAst actualParameter,
            TypeWithModifiersDto formalParameter) {
        CastingDto castingDto;
        int promotionLevelActualToFormal = getPromotionLevelFromToConsiderFalseAndNull(
                actualParameter.getEvalType(), actualParameter.getText(), formalParameter);

        if (isSameOrSubType(promotionLevelActualToFormal)) {
            castingDto = new CastingDto(promotionLevelActualToFormal, 0, null, actualParameter);
        } else if (isFormalCorrespondingFalseableOrNullableType(
                actualParameter.getEvalType(), formalParameter.typeSymbol)) {
            castingDto = new CastingDto(promotionLevelActualToFormal, 1, null, actualParameter);
        } else {
            castingDto = getImplicitCastingDto(actualParameter, formalParameter);
        }
        return castingDto;
    }

    @Override
    public boolean isFormalCorrespondingFalseableOrNullableType(ITypeSymbol actualType, ITypeSymbol formalType) {
        boolean isIt = false;
        if (actualType.getDefinitionScope().equals(formalType.getDefinitionScope())) {
            String actualTypeName = actualType.getName();
            String formalTypeName = formalType.getName();

            if (!actualType.isFalseable() && !actualType.isNullable()) {
                isIt = formalTypeName.equals(actualTypeName + "!")
                        || formalTypeName.equals(actualTypeName + "?")
                        || formalTypeName.equals(actualTypeName + "!?");
            } else {
                if (actualTypeName.endsWith("!") || actualTypeName.endsWith("?")) {
                    actualTypeName = actualTypeName.substring(0, actualTypeName.length() - 1);
                }
                if (isNullableWithoutTypeModifier(actualType)) {
                    isIt = formalTypeName.equals(actualTypeName + "!")
                            || formalTypeName.equals(actualTypeName + "!?");
                } else {
                    isIt = formalTypeName.equals(actualTypeName + "!?");
                }
            }
        }
        return isIt;
    }

    private boolean isNullableWithoutTypeModifier(ITypeSymbol actualType) {
        return actualType.isNullable() && !actualType.getName().endsWith("?");
    }

    private CastingDto getImplicitCastingDto(
            ITSPHPAst actualParameter, TypeWithModifiersDto formalParameter) {

        Map<ITypeSymbol, PromotionAndCastingLevelDto> visitedTypes = new HashMap<>();
        int promotionLevel = 0;
        int castingLevel = 0;
        FindCastingDto dto = new FindCastingDto(
                implicitCasts,
                promotionLevel,
                castingLevel,
                formalParameter,
                actualParameter.getEvalType(),
                visitedTypes);

        CastingDto castingDto = findCasting(dto);
        if (castingDto != null) {
            castingDto.actualParameter = actualParameter;
        }

        return castingDto;
    }

    @Override
    public CastingDto getCastingDtoInAlwaysCastingMode(
            ITSPHPAst actualParameter, TypeWithModifiersDto formalParameter) {

        CastingDto castingDto = getCastingDtoInNormalMode(actualParameter, formalParameter);
        if (castingDto == null) {
            ITypeSymbol formalParameterType = formalParameter.typeSymbol;
            ITypeSymbol actualParameterType = actualParameter.getEvalType();
            if (isNull(actualParameterType)) {
                //null can be casted to everything with the standard casting operator
                castingDto = getStandardCastingDto(actualParameter, formalParameterType, 0, 1);
            } else {
                //check if actualParameter type is parent of formalParameter type -> down cast
                int promotionLevelFormalToActual = getPromotionLevelFromTo(
                        formalParameterType, actualParameter.getEvalType());

                if (isSameOrSubType(promotionLevelFormalToActual)) {
                    castingDto = getStandardCastingDto(
                            actualParameter, formalParameterType, promotionLevelFormalToActual, 0);
                } else {
                    castingDto = getExplicitCastingDto(actualParameter, formalParameter);
                }
            }
        }
        return castingDto;
    }

    private CastingDto getStandardCastingDto(
            ITSPHPAst actualParameter, ITypeSymbol formalParameterType, int promotionLevel, int castingLevel) {

        List<ICastingMethod> castingMethods = new ArrayList<>();
        castingMethods.add(typeSystem.getStandardCastingMethod(formalParameterType));
        return new CastingDto(promotionLevel, castingLevel, castingMethods, actualParameter);
    }

    private CastingDto getExplicitCastingDto(ITSPHPAst actualParameter, TypeWithModifiersDto formalParameter) {

        Map<ITypeSymbol, PromotionAndCastingLevelDto> visitedTypes = new HashMap<>();
        int promotionLevel = 0;
        int castingLevel = 0;
        FindCastingDto dto = new FindCastingDto(
                explicitCasts,
                promotionLevel,
                castingLevel,
                formalParameter,
                actualParameter.getEvalType(),
                visitedTypes);

        CastingDto castingDto = findCasting(dto);
        if (castingDto != null) {
            castingDto.actualParameter = actualParameter;
        }

        return castingDto;
    }

    private CastingDto findCasting(FindCastingDto findCastingDto) {
        CastingDto castingDto = null;

        //to prevent re-entrance
        findCastingDto.visitedTypes.put(findCastingDto.actualParameterType, null);

        ITypeSymbol formalType = findCastingDto.formalParameter.typeSymbol;
        Map<ITypeSymbol, ICastingMethod> casts = findCastingDto.castings.get(findCastingDto.actualParameterType);

        if (casts != null && casts.containsKey(formalType)) {
            castingDto = createCastingDto(
                    casts.get(formalType), findCastingDto.promotionLevel, findCastingDto.castingLevel + 1);
        } else {
            List<CastingDto> castingDtos = new ArrayList<>();

            castingDtos.addAll(findParentCastings(findCastingDto));
            if (casts != null) {
                //TODO rstoll TSPHP-759 casts not over multiple levels
                castingDtos.addAll(findCastingsOfCastings(casts, findCastingDto));
            }

            if (!castingDtos.isEmpty()) {
                List<CastingDto> castings = getMostSpecificCast(castingDtos);
                castingDto = castings.get(0);
                if (castings.size() != 1) {
                    castingDto.ambiguousCasts = castings;
                }
            }
        }
        findCastingDto.visitedTypes.put(findCastingDto.actualParameterType, castingDto);

        return castingDto;
    }

    private CastingDto createCastingDto(ICastingMethod castingMethod, int promotionLevel, int castingLevel) {
        List<ICastingMethod> castingMethods = new ArrayList<>();
        castingMethods.add(castingMethod);
        return new CastingDto(promotionLevel, castingLevel, castingMethods);
    }

    private Collection<CastingDto> findParentCastings(FindCastingDto findCastingDto) {
        Collection<CastingDto> castingDtos = new ArrayDeque<>();

        for (ITypeSymbol typeSymbol : findCastingDto.actualParameterType.getParentTypeSymbols()) {
            if (typeSymbol != null) {
                FindCastingDto newDto = new FindCastingDto(findCastingDto);
                newDto.actualParameterType = typeSymbol;
                ++newDto.promotionLevel;
                if (isNotYetVisitedOrHasBetterPath(newDto)) {
                    CastingDto castingDto = findCasting(newDto);
                    if (castingDto != null) {
                        for (ICastingMethod castingMethod : castingDto.castingMethods) {
                            castingMethod.setParentTypeWhichProvidesCast(typeSymbol);
                        }
                        castingDtos.add(castingDto);
                    }
                }
            }
        }
        return castingDtos;
    }

    private boolean isNotYetVisitedOrHasBetterPath(FindCastingDto dto) {

        boolean hasBetterPath = false;
        boolean isVisited = dto.visitedTypes.containsKey(dto.actualParameterType);
        if (isVisited) {
            PromotionAndCastingLevelDto bestDto = dto.visitedTypes.get(dto.actualParameterType);
            //dto is null if typeSymbol was visited already but has no valid path to the goal type or it is still 
            //calculating and it should not be entered again
            hasBetterPath = bestDto != null && (bestDto.castingLevel > dto.castingLevel
                    || bestDto.castingLevel == dto.castingLevel
                    && bestDto.promotionLevel > dto.promotionLevel);
        }
        return !isVisited || hasBetterPath;
    }

    private List<CastingDto> findCastingsOfCastings(Map<ITypeSymbol, ICastingMethod> casts,
            FindCastingDto findCastingDto) {
        /*
        todo rstoll TSPHP-759 casts not over multiple levels
        it would be necessary to add implicit castings as well. However, since TSPHP-759 will forbid
        castings over multiple levels anyway it does not make sense to invest more time in this method.
         */

        List<CastingDto> castingDtos = new ArrayList<>();
        for (ITypeSymbol castedType : casts.keySet()) {
            ITypeSymbol formalParameterType = findCastingDto.formalParameter.typeSymbol;
            int promotionLevel = getPromotionLevelFromTo(castedType, formalParameterType);
            if (isSameOrSubType(promotionLevel)) {
                castingDtos.add(createCastingDto(casts.get(castedType),
                        findCastingDto.promotionLevel + promotionLevel,
                        findCastingDto.castingLevel + 1));
            } else {
                castingDtos.addAll(findExplicitCastingsOfCasting(castedType, casts.get(castedType), findCastingDto));
            }
        }
        return castingDtos;
    }

    private List<CastingDto> findExplicitCastingsOfCasting(
            ITypeSymbol castedType, ICastingMethod castingMethod, FindCastingDto findCastingDto) {

        List<CastingDto> castingDtos = new ArrayList<>();
        FindCastingDto newDto = new FindCastingDto(findCastingDto);
        newDto.actualParameterType = castedType;
        ++newDto.castingLevel;
        if (isNotYetVisitedOrHasBetterPath(newDto)) {
            CastingDto castingDto = findCasting(newDto);
            if (castingDto != null) {
                castingDtos.add(chainUpCastingMethods(castingMethod, castingDto));
            }
        }
        return castingDtos;
    }

    private List<CastingDto> getMostSpecificCast(List<CastingDto> castingDtos) {

        List<CastingDto> castings = new ArrayList<>();

        CastingDto mostSpecificDto = castingDtos.get(0);

        int castingsSize = castingDtos.size();
        for (int i = 1; i < castingsSize; ++i) {
            CastingDto newDto = castingDtos.get(i);
            if (isSecondBetter(mostSpecificDto, newDto)) {
                mostSpecificDto = newDto;
                castings = new ArrayList<>();
            } else if (isSecondEqual(mostSpecificDto, newDto)) {
                castings.add(newDto);
            }
        }
        castings.add(mostSpecificDto);
        return castings;
    }

    private boolean isSecondBetter(CastingDto mostSpecificDto, CastingDto newDto) {
        return mostSpecificDto.castingLevel > newDto.castingLevel
                || mostSpecificDto.castingLevel == newDto.castingLevel
                && mostSpecificDto.promotionLevel > newDto.promotionLevel;
    }

    private boolean isSecondEqual(CastingDto mostSpecificDto, CastingDto newDto) {
        return mostSpecificDto.castingLevel > newDto.castingLevel
                || mostSpecificDto.castingLevel == newDto.castingLevel
                && mostSpecificDto.promotionLevel == newDto.promotionLevel;
    }

    /**
     * Return how many promotions have to be applied to the actualType to reach the formalType whereby -1 is returned in
     * the case where formalType is not the actualType or one of its parent types.
     */
    @Override
    public int getPromotionLevelFromTo(ITypeSymbol actualParameterType, ITypeSymbol formalParameterType) {
        int count = 0;
        if (actualParameterType != formalParameterType) {
            count = -1;
            Set<ITypeSymbol> parentTypes = actualParameterType.getParentTypeSymbols();
            for (ITypeSymbol parentType : parentTypes) {
                if (parentType != null) {
                    int tmp = getPromotionLevelFromTo(parentType, formalParameterType);
                    if (tmp != -1) {
                        count += tmp + 2;
                        break;
                    }
                }
            }
        }
        return count;
    }

    @Override
    public boolean isSameOrSubTypeConsiderFalseAndNull(
            ITypeSymbol actualSymbol, String actualParameterValue, TypeWithModifiersDto formalParameter) {
        return isSameOrSubType(
                getPromotionLevelFromToConsiderFalseAndNull(actualSymbol, actualParameterValue, formalParameter));
    }

    @Override
    public boolean isSameOrSubType(int promotionLevel) {
        return promotionLevel != -1;
    }

    @Override
    public OverloadDto getMostSpecificApplicableOverload(List<OverloadDto> methods) throws AmbiguousCallException {

        List<OverloadDto> ambiguousMethodDtos = new ArrayList<>();

        OverloadDto mostSpecificMethodDto = methods.get(0);

        int methodsSize = methods.size();
        for (int i = 1; i < methodsSize; ++i) {
            OverloadDto methodDto = methods.get(i);
            if (isSecondBetter(mostSpecificMethodDto, methodDto)) {
                mostSpecificMethodDto = methodDto;
                if (ambiguousMethodDtos.size() > 0) {
                    ambiguousMethodDtos = new ArrayList<>();
                }
            } else if (isSecondEqual(mostSpecificMethodDto, methodDto)) {
                ambiguousMethodDtos.add(methodDto);
            }
        }
        if (!ambiguousMethodDtos.isEmpty()) {
            ambiguousMethodDtos.add(mostSpecificMethodDto);
            throw new AmbiguousCallException(ambiguousMethodDtos);
        }

        return mostSpecificMethodDto;
    }

    private boolean isSecondBetter(OverloadDto mostSpecificMethodDto, OverloadDto methodDto) {

        int mostSpecificCastingSize = mostSpecificMethodDto.parametersNeedCasting.size();
        int challengerCastingSize = methodDto.parametersNeedCasting.size();
        boolean isSecondBetter = mostSpecificCastingSize > challengerCastingSize;
        if (!isSecondBetter && mostSpecificCastingSize == challengerCastingSize) {
            int mostSpecificParameterCount = mostSpecificMethodDto.parameterPromotedCount;
            int challengerParameterCount = methodDto.parameterPromotedCount;
            isSecondBetter = mostSpecificParameterCount > challengerParameterCount
                    || (mostSpecificParameterCount == challengerParameterCount
                    && mostSpecificMethodDto.promotionsTotal > methodDto.promotionsTotal);
        }
        return isSecondBetter;
    }

    private boolean isSecondEqual(OverloadDto mostSpecificMethodDto, OverloadDto methodDto) {
        return mostSpecificMethodDto.parametersNeedCasting.size() == methodDto.parametersNeedCasting.size()
                && mostSpecificMethodDto.parameterPromotedCount == methodDto.parameterPromotedCount
                && mostSpecificMethodDto.promotionsTotal == methodDto.promotionsTotal;
    }

    @Override
    public int getPromotionLevelFromToConsiderFalseAndNull(
            ITypeSymbol actualParameterType, String actualParameterValue, TypeWithModifiersDto formalParameter) {

        int promotionLevel = getPromotionLevelFromTo(actualParameterType, formalParameter.typeSymbol);
        if (promotionLevel == -1) {
            if (isActualFalseAndFormalBool(actualParameterType, actualParameterValue, formalParameter)
                    || isActualFalseAndFormalFalseable(actualParameterType, actualParameterValue, formalParameter)
                    || isActualNullAndFormalNullable(actualParameterType, formalParameter)) {
                promotionLevel = 0;
            }
        }
        return promotionLevel;
    }

    private boolean isNull(ITypeSymbol typeSymbol) {
        return typeSymbol.equals(typeSystem.getNullTypeSymbol());
    }

    private boolean isFalse(ITypeSymbol typeSymbol, String actualParameterValue) {
        return typeSymbol.equals(typeSystem.getBoolTypeSymbol()) && actualParameterValue.equals("false");
    }

    private boolean isActualFalseAndFormalBool(
            ITypeSymbol actualParameterType, String actualParameterValue, TypeWithModifiersDto formalParameter) {
        return isFalse(actualParameterType, actualParameterValue) && formalParameter.typeSymbol.equals(typeSystem
                .getBoolTypeSymbol());
    }

    private boolean isActualFalseAndFormalFalseable(
            ITypeSymbol actualParameterType, String actualParameterValue, TypeWithModifiersDto formalParameter) {
        return isFalse(actualParameterType, actualParameterValue) && formalParameter.modifiers.isFalseable();
    }

    private boolean isActualNullAndFormalNullable(
            ITypeSymbol actualParameterType, TypeWithModifiersDto formalParameter) {
        return isNull(actualParameterType) && (formalParameter.modifiers.isNullable()
                || formalParameter.typeSymbol.isNullable());
    }

    private CastingDto chainUpCastingMethods(ICastingMethod castingMethod, CastingDto castingDto) {

        if (castingDto.ambiguousCasts != null) {
            for (CastingDto ambiguousDto : castingDto.ambiguousCasts) {
                List<ICastingMethod> castingMethods = new ArrayList<>();
                castingMethods.add(castingMethod);
                castingMethods.addAll(ambiguousDto.castingMethods);
                ambiguousDto.castingMethods = castingMethods;
            }
        } else {
            List<ICastingMethod> castingMethods = new ArrayList<>();
            castingMethods.add(castingMethod);
            castingMethods.addAll(castingDto.castingMethods);
            castingDto.castingMethods = castingMethods;
        }

        return castingDto;
    }

    //CHECKSTYLE:OFF:VisibilityModifier|ParameterNumber
    private static final class FindCastingDto extends PromotionAndCastingLevelDto
    {
        public Map<ITypeSymbol, Map<ITypeSymbol, ICastingMethod>> castings;
        public TypeWithModifiersDto formalParameter;
        public ITypeSymbol actualParameterType;
        public Map<ITypeSymbol, PromotionAndCastingLevelDto> visitedTypes;

        private FindCastingDto(
                Map<ITypeSymbol, Map<ITypeSymbol, ICastingMethod>> theCastings,
                int thePromotionLevel,
                int theCastingLevel,
                TypeWithModifiersDto theFormalParameter,
                ITypeSymbol theActualParameterType,
                Map<ITypeSymbol, PromotionAndCastingLevelDto> theVisitedTypes) {
            super(thePromotionLevel, theCastingLevel);
            castings = theCastings;
            formalParameter = theFormalParameter;
            actualParameterType = theActualParameterType;
            visitedTypes = theVisitedTypes;
        }

        private FindCastingDto(FindCastingDto findCastingDto) {
            super(findCastingDto.promotionLevel, findCastingDto.castingLevel);
            castings = findCastingDto.castings;
            formalParameter = findCastingDto.formalParameter;
            actualParameterType = findCastingDto.actualParameterType;
            visitedTypes = findCastingDto.visitedTypes;
        }
    }
    //CHECKSTYLE:OFF:VisibilityModifier|ParameterNumber
}
