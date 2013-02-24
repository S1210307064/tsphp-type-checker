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
import ch.tutteli.tsphp.typechecker.symbols.IScalarTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.ITypeSymbolWithPHPBuiltInCasting;
import ch.tutteli.tsphp.typechecker.symbols.IVariableSymbol;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class OverloadResolver implements IOverloadResolver
{

    private Map<ITypeSymbol, Map<ITypeSymbol, IMethodSymbol>> explicitCasts;
    private ITypeSystemInitialiser symbolTable;

    /**
     *
     * @param theScalarCastMethod The method used to cast a scalar type to another scalar type
     */
    public OverloadResolver(ITypeSystemInitialiser theSymbolTable) {
        symbolTable = theSymbolTable;
        explicitCasts = theSymbolTable.getExplicitCastings();
    }

    @Override
    public List<OverloadDto> getApplicableMethods(List<IMethodSymbol> methods,
            List<ITSPHPAst> actualParameters) {

        List<OverloadDto> applicableMethods = new ArrayList<>();
        for (IMethodSymbol method : methods) {

            OverloadDto methodDto = getApplicableMethodDto(method, actualParameters);

            if (methodDto != null) {
                applicableMethods.add(methodDto);
            }
        }
        return applicableMethods;
    }

    private OverloadDto getApplicableMethodDto(IMethodSymbol method, List<ITSPHPAst> actualParameters) {
        OverloadDto methodDto = null;
        List<IVariableSymbol> formalParameters = method.getParameters();
        if (formalParameters.size() == actualParameters.size()) {
            if (!formalParameters.isEmpty()) {
                methodDto = getApplicableMethodDto(method, formalParameters, actualParameters);
            } else {
                methodDto = new OverloadDto(method, 0, 0, null);
            }
        }
        return methodDto;
    }

    private OverloadDto getApplicableMethodDto(IMethodSymbol method, List<IVariableSymbol> formalParameters,
            List<ITSPHPAst> actualParameters) {

        OverloadDto methodDto = null;

        int parameterCount = formalParameters.size();
        int promotionTotalCount = 0;
        int promotionParameterCount = 0;
        ParameterPromotionDto parameterDto = null;
        List<ParameterPromotionDto> parameters = new ArrayList<>();
        for (int i = 0; i < parameterCount; ++i) {
            parameterDto = getParameterPromotionDto(formalParameters.get(i), actualParameters.get(i));
            if (parameterDto != null) {
                if (parameterDto.promotionLevel != 0) {
                    ++promotionParameterCount;
                    promotionTotalCount += parameterDto.promotionLevel;
                }
                if(parameterDto.castingMethods!=null){
                    parameters.add(parameterDto);
                }
            } else {
                break;
            }
        }
        if (parameterDto != null) {
            methodDto = new OverloadDto(method, promotionParameterCount, promotionTotalCount, parameters);
        }
        return methodDto;
    }

    private ParameterPromotionDto getParameterPromotionDto(IVariableSymbol formalParameter,
            ITSPHPAst actualParameter) {
        ParameterPromotionDto parameterDto = null;

        ITypeSymbol formalParameterType = formalParameter.getType();

        int promotionCount = getPromotionCountFromTo(actualParameter.getEvalType(), formalParameterType);

        if (isSameOrParentType(promotionCount)) {
            List<IMethodSymbol> castings = null;
            if (promotionCount!=0 && actualParameter.getEvalType() instanceof IScalarTypeSymbol) {
                castings = new ArrayList<>();
                castings.add(symbolTable.createPHPInBuiltCastingMethod(
                        (ITypeSymbolWithPHPBuiltInCasting) formalParameterType));
            }
            parameterDto = new ParameterPromotionDto(promotionCount, 0, actualParameter, castings);
        } else if (formalParameter.isAlwaysCasting()) {

            parameterDto = getExplicitCastParameterPromotionDto(formalParameter, actualParameter);
        }

        return parameterDto;
    }

    /**
     * Return how many promotions have to be applied to the actualType to reach the formalType whereby -1 is returned in
     * the case where formalType is not the actualType or one of its parent types.
     */
    private int getPromotionCountFromTo(ITypeSymbol actualType, ITypeSymbol formalType) {
        int count = 0;
        while (actualType != null && actualType != formalType) {
            actualType = actualType.getParentTypeSymbol();
            ++count;
        }
        if (actualType == null) {
            count = -1;
        }
        return count;
    }

    private boolean isSameOrParentType(int promotionCount) {
        return promotionCount != -1;
    }

    private ParameterPromotionDto getExplicitCastParameterPromotionDto(IVariableSymbol theFormalParameter,
            ITSPHPAst actualParameter) {

        List<ParameterPromotionDto> dtos = new ArrayList<>();
        Map<ITypeSymbol, PromotionExplicitCastingLevelDto> visitedTypes = new HashMap<>();
        ExplicitCastingDto dto = new ExplicitCastingDto(0, 0, theFormalParameter, dtos, actualParameter.getEvalType(), visitedTypes);

        ParameterPromotionDto promotionDto = getExplicitCastParameterPromotionDto(dto);
        promotionDto.actualParameter = actualParameter;

        return promotionDto;
    }

    private ParameterPromotionDto getExplicitCastParameterPromotionDto(ExplicitCastingDto explicitCastingDto) {
        ParameterPromotionDto parameterDto = null;

        Map<ITypeSymbol, IMethodSymbol> casts = explicitCasts.get(explicitCastingDto.actualParameterType);
        if (casts != null) {
            ITypeSymbol formalType = explicitCastingDto.formalParameter.getType();
            if (casts.containsKey(formalType)) {
                List<IMethodSymbol> castingMethods = new ArrayList<>();
                castingMethods.add(casts.get(formalType));
                parameterDto = new ParameterPromotionDto(explicitCastingDto.promotionLevel,
                        explicitCastingDto.explicitCastingLevel + 1, castingMethods);

                explicitCastingDto.visitedTypes.put(explicitCastingDto.actualParameterType, parameterDto);
                explicitCastingDto.parameterDto.add(parameterDto);
            } else {
                List<ParameterPromotionDto> castingDtos = new ArrayList<>();

                addParentExplicitCastings(explicitCastingDto);

                addExplicitCastingsOfExplicitCastings(casts, castingDtos, explicitCastingDto);

                if (!castingDtos.isEmpty()) {
                    List<ParameterPromotionDto> castings = getMostSpecificExplicitCasting(castingDtos);
                    parameterDto = castings.get(0);
                    if (castings.size() != 1) {
                        parameterDto.ambigousCastings = castings;
                    }
                }
                explicitCastingDto.visitedTypes.put(explicitCastingDto.actualParameterType, parameterDto);
            }
        } else {
            explicitCastingDto.visitedTypes.put(explicitCastingDto.actualParameterType, null);
        }

        return parameterDto;
    }

    private void addParentExplicitCastings(ExplicitCastingDto explicitCastingDto) {
        ExplicitCastingDto newDto = new ExplicitCastingDto(explicitCastingDto);
        newDto.actualParameterType = explicitCastingDto.actualParameterType.getParentTypeSymbol();
        ++newDto.promotionLevel;
        if (newDto.actualParameterType != null && isNotYetVisitedOrHasBetterPath(newDto)) {
            getExplicitCastParameterPromotionDto(newDto);
        }
    }

    private boolean isNotYetVisitedOrHasBetterPath(ExplicitCastingDto dto) {

        boolean betterPathFound = dto.visitedTypes.containsKey(dto.actualParameterType);
        if (!betterPathFound) {
            PromotionExplicitCastingLevelDto bestDto = dto.visitedTypes.get(dto.actualParameterType);
            //dto is null if typeSymbol was visited already but has no valid path to the goal type 
            betterPathFound = bestDto != null && (bestDto.explicitCastingLevel > dto.explicitCastingLevel
                    || bestDto.explicitCastingLevel == dto.explicitCastingLevel
                    && bestDto.promotionLevel > dto.promotionLevel);
        }
        return betterPathFound;
    }

    private void addExplicitCastingsOfExplicitCastings(Map<ITypeSymbol, IMethodSymbol> casts,
            List<ParameterPromotionDto> parameterDto, ExplicitCastingDto explicitCastingDto) {

        for (ITypeSymbol castedType : casts.keySet()) {
            ExplicitCastingDto newDto = new ExplicitCastingDto(explicitCastingDto);
            newDto.actualParameterType = castedType;
            ++newDto.explicitCastingLevel;
            newDto.parameterDto = parameterDto;
            if (isNotYetVisitedOrHasBetterPath(newDto)) {
                getExplicitCastParameterPromotionDto(newDto);
            }
        }
    }

    private List<ParameterPromotionDto> getMostSpecificExplicitCasting(List<ParameterPromotionDto> castingDtos) {

        List<ParameterPromotionDto> castings = new ArrayList<>();;

        ParameterPromotionDto mostSpecificDto = castingDtos.get(0);

        int castingsSize = castingDtos.size();
        for (int i = 0; i < castingsSize; ++i) {
            ParameterPromotionDto newDto = castingDtos.get(i);
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

    private boolean isSecondBetter(ParameterPromotionDto mostSpecificDto, ParameterPromotionDto newDto) {
        return mostSpecificDto.explicitCastingLevel > newDto.explicitCastingLevel
                || mostSpecificDto.explicitCastingLevel == newDto.explicitCastingLevel
                && mostSpecificDto.promotionLevel > newDto.promotionLevel;
    }

    private boolean isSecondEqual(ParameterPromotionDto mostSpecificDto, ParameterPromotionDto newDto) {
        return mostSpecificDto.explicitCastingLevel > newDto.explicitCastingLevel
                || mostSpecificDto.explicitCastingLevel == newDto.explicitCastingLevel
                && mostSpecificDto.promotionLevel == newDto.promotionLevel;
    }

    @Override
    public OverloadDto getMostSpecificApplicableMethod(List<OverloadDto> methods)
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

    private class ExplicitCastingDto extends PromotionExplicitCastingLevelDto
    {

        public IVariableSymbol formalParameter;
        public List<ParameterPromotionDto> parameterDto;
        public ITypeSymbol actualParameterType;
        public Map<ITypeSymbol, PromotionExplicitCastingLevelDto> visitedTypes;

        private ExplicitCastingDto(int thePromotionLevel, int theExplicitCastingLevel,
                IVariableSymbol theFormalParameter, List<ParameterPromotionDto> theListToAddTheDtos,
                ITypeSymbol theActualParameterType,
                Map<ITypeSymbol, PromotionExplicitCastingLevelDto> theVisitedTypes) {
            super(thePromotionLevel, theExplicitCastingLevel);
            formalParameter = theFormalParameter;
            parameterDto = theListToAddTheDtos;
            actualParameterType = theActualParameterType;
            visitedTypes = theVisitedTypes;
        }

        private ExplicitCastingDto(ExplicitCastingDto explicitCastingDto) {
            super(explicitCastingDto.promotionLevel, explicitCastingDto.explicitCastingLevel);
            formalParameter = explicitCastingDto.formalParameter;
            parameterDto = explicitCastingDto.parameterDto;
            actualParameterType = explicitCastingDto.actualParameterType;
            visitedTypes = explicitCastingDto.visitedTypes;
        }
    }
}
