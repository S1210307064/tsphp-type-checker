/*
 * Copyright 2012 Robert Stoll <rstoll@tutteli.ch>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package ch.tutteli.tsphp.typechecker;

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.INullTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IVariableSymbol;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class OverloadResolver implements IOverloadResolver
{

    private Map<ITypeSymbol, Map<ITypeSymbol, ICastingMethod>> explicitCasts;
    private ITypeSystem typeSystem;

    /**
     *
     * @param theScalarCastMethod The method used to cast a scalar type to another scalar type
     */
    public OverloadResolver(ITypeSystem theTypeSystem) {
        typeSystem = theTypeSystem;
        explicitCasts = theTypeSystem.getExplicitCastings();
    }

    @Override
    public List<OverloadDto> getApplicableOverloads(List<IMethodSymbol> methods,
            List<ITSPHPAst> actualParameters) {

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
        if (formalParameters.size() == actualParameters.size()) {
            if (!formalParameters.isEmpty()) {
                methodDto = getApplicableOverload(method, formalParameters, actualParameters);
            } else {
                methodDto = new OverloadDto(method, 0, 0, null);
            }
        }
        return methodDto;
    }

    private OverloadDto getApplicableOverload(IMethodSymbol method, List<IVariableSymbol> formalParameters,
            List<ITSPHPAst> actualParameters) {

        OverloadDto methodDto = null;

        int parameterCount = formalParameters.size();
        int promotionTotalCount = 0;
        int promotionParameterCount = 0;
        CastingDto castingDto = null;
        List<CastingDto> parameters = new ArrayList<>();
        for (int i = 0; i < parameterCount; ++i) {
            castingDto = getCastingDto(formalParameters.get(i), actualParameters.get(i));
            if (castingDto != null) {
                if (castingDto.promotionLevel != 0) {
                    ++promotionParameterCount;
                    promotionTotalCount += castingDto.promotionLevel;
                }
                if (castingDto.castingMethods != null) {
                    parameters.add(castingDto);
                }
            } else {
                break;
            }
        }
        if (castingDto != null) {
            methodDto = new OverloadDto(method, promotionParameterCount, promotionTotalCount, parameters);
        }
        return methodDto;
    }

    @Override
    public CastingDto getCastingDto(IVariableSymbol formalParameter,
            ITSPHPAst actualParameter) {

        CastingDto castingDto = null;

        if (!formalParameter.isAlwaysCasting()) {
            int promotionLevel = getPromotionLevelFromToConsiderNull(actualParameter, formalParameter);
            if (isSameOrParentType(promotionLevel)) {
                castingDto = new CastingDto(promotionLevel, 0, null, actualParameter);
            }
        } else {
            castingDto = getCastingDtoAlwaysCasting(formalParameter, actualParameter);
        }

        return castingDto;
    }

    @Override
    public CastingDto getCastingDtoAlwaysCasting(IVariableSymbol formalParameter, ITSPHPAst actualParameter) {
        CastingDto castingDto;

        ITypeSymbol formalParameterType = formalParameter.getType();

        int promotionLevel = getPromotionLevelFromToConsiderNull(actualParameter, formalParameter);
        if (isSameOrParentType(promotionLevel)) {
            castingDto = new CastingDto(promotionLevel, 0, null, actualParameter);
        } else if (actualParameter.getEvalType() instanceof INullTypeSymbol) {
            //null is castable to everything with the standard casting operator
            castingDto = getStandardCastingDto(actualParameter, formalParameterType, 0, 1);
        } else {
            //check if actual parameter type is parent of formal parameter type
            promotionLevel = getPromotionLevelFromTo(formalParameterType, actualParameter.getEvalType());
            if (isSameOrParentType(promotionLevel)) {
                castingDto = getStandardCastingDto(actualParameter, formalParameterType, promotionLevel, 0);
            } else {
                castingDto = getCastingDtoFromExplicitCasting(formalParameter, actualParameter);
            }
        }
        return castingDto;
    }

    private CastingDto getStandardCastingDto(ITSPHPAst actualParameter, ITypeSymbol formalParameterType,
            int promotionLevel, int explicitCastingLevel) {
        List<ICastingMethod> castingMethods = new ArrayList<>();
        castingMethods.add(typeSystem.getStandardCastingMethod(formalParameterType));
        return new CastingDto(promotionLevel, explicitCastingLevel, castingMethods, actualParameter);
    }

    /**
     * Return how many promotions have to be applied to the actualType to reach the formalType whereby -1 is returned in
     * the case where formalType is not the actualType or one of its parent types.
     */
    @Override
    public int getPromotionLevelFromTo(ITypeSymbol fromType, ITypeSymbol toType) {
        int count = 0;
        if (fromType != toType) {
            count = -1;
            Set<ITypeSymbol> parentTypes = fromType.getParentTypeSymbols();
            for (ITypeSymbol parentType : parentTypes) {
                if (parentType != null) {
                    int tmp = getPromotionLevelFromTo(parentType, toType);
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
    public boolean isSameOrParentTypeConsiderNull(IVariableSymbol formalType, ITSPHPAst actualType) {
        return isSameOrParentType(getPromotionLevelFromToConsiderNull(actualType, formalType));
    }

    @Override
    public boolean isSameOrParentTypeConsiderNull(ITypeSymbol formalParameterType, ITypeSymbol actualParameterType) {
        return isSameOrParentType(getPromotionLevelFromToConsiderNull(actualParameterType, formalParameterType));
    }

    @Override
    public boolean isSameOrParentType(int promotionLevel) {
        return promotionLevel != -1;
    }

    private CastingDto getCastingDtoFromExplicitCasting(IVariableSymbol formalParameter,
            ITSPHPAst actualParameter) {

        List<CastingDto> dtos = new ArrayList<>();
        Map<ITypeSymbol, PromotionExplicitCastingLevelDto> visitedTypes = new HashMap<>();
        ExplicitCastingDto dto = new ExplicitCastingDto(0, 0, formalParameter, dtos,
                actualParameter.getEvalType(), visitedTypes);

        CastingDto castingDto = getCastingDtoFromExplicitCasting(dto);
        if (castingDto != null) {
            castingDto.actualParameter = actualParameter;
        }

        return castingDto;
    }

    private CastingDto getCastingDtoFromExplicitCasting(ExplicitCastingDto explicitCastingDto) {
        CastingDto castingDto = null;

        //to prevent reentrance
        explicitCastingDto.visitedTypes.put(explicitCastingDto.actualParameterType, null);

        ITypeSymbol formalType = explicitCastingDto.formalParameter.getType();
        Map<ITypeSymbol, ICastingMethod> casts = explicitCasts.get(explicitCastingDto.actualParameterType);

        if (casts != null && casts.containsKey(formalType)) {
            castingDto = createCastingDtoFromExplicitCasting(casts.get(formalType), explicitCastingDto.promotionLevel,
                    explicitCastingDto.explicitCastingLevel + 1);
        } else {
            List<CastingDto> castingDtos = new ArrayList<>();

            addParentExplicitCastings(castingDtos, explicitCastingDto);
            if (casts != null) {
                addExplicitCastingsOfExplicitCastings(castingDtos, casts, explicitCastingDto);
            }

            if (!castingDtos.isEmpty()) {
                List<CastingDto> castings = getMostSpecificExplicitCast(castingDtos);
                castingDto = castings.get(0);
                if (castings.size() != 1) {
                    castingDto.ambiguousCasts = castings;
                }
            }


        }
        explicitCastingDto.visitedTypes.put(explicitCastingDto.actualParameterType, castingDto);

        return castingDto;
    }

    private CastingDto createCastingDtoFromExplicitCasting(ICastingMethod castingMethod,
            int promotionLevel, int explicitCastingLevel) {
        List<ICastingMethod> castingMethods = new ArrayList<>();
        castingMethods.add(castingMethod);
        return new CastingDto(promotionLevel, explicitCastingLevel, castingMethods);
    }

    private void addParentExplicitCastings(List<CastingDto> castingDtos,
            ExplicitCastingDto explicitCastingDto) {
        for (ITypeSymbol typeSymbol : explicitCastingDto.actualParameterType.getParentTypeSymbols()) {
            if (typeSymbol != null) {
                ExplicitCastingDto newDto = new ExplicitCastingDto(explicitCastingDto);
                newDto.actualParameterType = typeSymbol;
                ++newDto.promotionLevel;
                if (isNotYetVisitedOrHasBetterPath(newDto)) {
                    CastingDto castingDto = getCastingDtoFromExplicitCasting(newDto);
                    if (castingDto != null) {
                        castingDto.castingMethods.get(0).setParentTypeWhichProvidesCast(typeSymbol);
                        castingDtos.add(castingDto);
                    }
                }
            }
        }
    }

    private boolean isNotYetVisitedOrHasBetterPath(ExplicitCastingDto dto) {

        boolean hasBetterPath = false;
        boolean isVisited = dto.visitedTypes.containsKey(dto.actualParameterType);
        if (isVisited) {
            PromotionExplicitCastingLevelDto bestDto = dto.visitedTypes.get(dto.actualParameterType);
            //dto is null if typeSymbol was visited already but has no valid path to the goal type or it is still 
            //calculating and it should not be entered again
            hasBetterPath = bestDto != null && (bestDto.explicitCastingLevel > dto.explicitCastingLevel
                    || bestDto.explicitCastingLevel == dto.explicitCastingLevel
                    && bestDto.promotionLevel > dto.promotionLevel);
        }
        return !isVisited || hasBetterPath;
    }

    private void addExplicitCastingsOfExplicitCastings(List<CastingDto> castingDtos,
            Map<ITypeSymbol, ICastingMethod> casts, ExplicitCastingDto explicitCastingDto) {

        for (ITypeSymbol castedType : casts.keySet()) {
            ITypeSymbol formalParameterType = explicitCastingDto.formalParameter.getType();
            int promotionLevel = getPromotionLevelFromTo(castedType, formalParameterType);
            if (isSameOrParentType(promotionLevel)) {
                castingDtos.add(createCastingDtoFromExplicitCasting(casts.get(castedType),
                        explicitCastingDto.promotionLevel + promotionLevel,
                        explicitCastingDto.explicitCastingLevel + 1));
            } else {
                addExplicitCastingsOfExplicitCasting(castingDtos, castedType,
                        casts.get(castedType), explicitCastingDto);
            }
        }
    }

    private void addExplicitCastingsOfExplicitCasting(List<CastingDto> castingDtos,
            ITypeSymbol castedType, ICastingMethod castingMethod, ExplicitCastingDto explicitCastingDto) {
        ExplicitCastingDto newDto = new ExplicitCastingDto(explicitCastingDto);
        newDto.actualParameterType = castedType;
        ++newDto.explicitCastingLevel;
        if (isNotYetVisitedOrHasBetterPath(newDto)) {
            CastingDto castingDto = getCastingDtoFromExplicitCasting(newDto);
            if (castingDto != null) {
                castingDtos.add(chainUpCastingMethods(castingMethod, castingDto));
            }
        }
    }

    private List<CastingDto> getMostSpecificExplicitCast(List<CastingDto> castingDtos) {

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
        return mostSpecificDto.explicitCastingLevel > newDto.explicitCastingLevel
                || mostSpecificDto.explicitCastingLevel == newDto.explicitCastingLevel
                && mostSpecificDto.promotionLevel > newDto.promotionLevel;
    }

    private boolean isSecondEqual(CastingDto mostSpecificDto, CastingDto newDto) {
        return mostSpecificDto.explicitCastingLevel > newDto.explicitCastingLevel
                || mostSpecificDto.explicitCastingLevel == newDto.explicitCastingLevel
                && mostSpecificDto.promotionLevel == newDto.promotionLevel;
    }

    @Override
    public OverloadDto getMostSpecificApplicableOverload(List<OverloadDto> methods)
            throws AmbiguousCallException {

        List<OverloadDto> ambiguousMethodDtos = new ArrayList<>();

        OverloadDto mostSpecificMethodDto = methods.get(0);

        int methodsSize = methods.size();
        for (int i = 1; i < methodsSize; ++i) {
            OverloadDto methodDto = methods.get(i);
            if (isSecondBetter(mostSpecificMethodDto, methodDto)) {
                mostSpecificMethodDto = methodDto;
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
        return mostSpecificMethodDto.parameterPromotedCount > methodDto.parameterPromotedCount
                || mostSpecificMethodDto.parameterPromotedCount == methodDto.parameterPromotedCount
                && mostSpecificMethodDto.promotionsTotal > methodDto.promotionsTotal;
    }

    private boolean isSecondEqual(OverloadDto mostSpecificMethodDto, OverloadDto methodDto) {
        return mostSpecificMethodDto.parameterPromotedCount == methodDto.parameterPromotedCount
                && mostSpecificMethodDto.promotionsTotal == methodDto.promotionsTotal;
    }

    private int getPromotionLevelFromToConsiderNull(ITSPHPAst actualParameter, IVariableSymbol formalParameter) {
        ITypeSymbol formalParameterType = formalParameter.getType();
        ITypeSymbol actualParameterType = actualParameter.getEvalType();
        return getPromotionLevelFromToConsiderNull(actualParameterType, formalParameterType);
    }

    @Override
    public int getPromotionLevelFromToConsiderNull(ITypeSymbol actualParameterType, ITypeSymbol formalParameterType) {
        int promotionLevel;
        if (!(actualParameterType instanceof INullTypeSymbol)) {
            promotionLevel = getPromotionLevelFromTo(actualParameterType, formalParameterType);
        } else if (formalParameterType.isNullable()) {
            //same type if actual parameter is null and formal parameter type is nullable
            promotionLevel = 0;
        } else {
            //if actual is null and formal parameter type is not nullable then we do not have a promotion.
            promotionLevel = -1;
        }
        return promotionLevel;
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

    private class ExplicitCastingDto extends PromotionExplicitCastingLevelDto
    {

        public IVariableSymbol formalParameter;
        public ITypeSymbol actualParameterType;
        public Map<ITypeSymbol, PromotionExplicitCastingLevelDto> visitedTypes;

        private ExplicitCastingDto(int thePromotionLevel, int theExplicitCastingLevel,
                IVariableSymbol theFormalParameter, List<CastingDto> theListToAddTheDtos,
                ITypeSymbol theActualParameterType,
                Map<ITypeSymbol, PromotionExplicitCastingLevelDto> theVisitedTypes) {
            super(thePromotionLevel, theExplicitCastingLevel);
            formalParameter = theFormalParameter;
            actualParameterType = theActualParameterType;
            visitedTypes = theVisitedTypes;
        }

        private ExplicitCastingDto(ExplicitCastingDto explicitCastingDto) {
            super(explicitCastingDto.promotionLevel, explicitCastingDto.explicitCastingLevel);
            formalParameter = explicitCastingDto.formalParameter;
            actualParameterType = explicitCastingDto.actualParameterType;
            visitedTypes = explicitCastingDto.visitedTypes;
        }
    }
}
