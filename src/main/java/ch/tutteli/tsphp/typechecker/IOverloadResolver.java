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
import ch.tutteli.tsphp.typechecker.symbols.IVariableSymbol;
import java.util.List;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public interface IOverloadResolver
{

    List<OverloadDto> getApplicableOverloads(List<IMethodSymbol> methods,
            List<ITSPHPAst> actualParameterTypes);

    OverloadDto getMostSpecificApplicableMethod(List<OverloadDto> methods)
            throws AmbiguousCallException;

    CastingDto getCastingDto(IVariableSymbol formalParameter, ITSPHPAst actualParameter);

    CastingDto getCastingDtoAlwaysCasting(IVariableSymbol formalParameter, ITSPHPAst actualParameter);

    /**
     * Return how many promotions have to be applied to the actualType to reach the formalType whereby -1 is returned in
     * the case where formalType is not the actualType or one of its parent types.
     */
    int getPromotionLevelFromTo(ITypeSymbol fromType, ITypeSymbol toType);

    boolean isSameOrParentType(int promotionLevel);

    boolean isSameOrParentTypeConsiderNull(IVariableSymbol formalType, ITSPHPAst actualType);

    boolean isSameOrParentTypeConsiderNull(ITypeSymbol formalParameterType, ITypeSymbol actualParameterType);
}
