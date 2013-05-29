/*
 * Copyright 2013 Robert Stoll <rstoll@tutteli.ch>
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
package ch.tutteli.tsphp.typechecker.utils;

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.CastingDto;
import ch.tutteli.tsphp.typechecker.ICastingMethod;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class AstHelper implements IAstHelper
{

    @Override
    public ITSPHPAst prependCasting(CastingDto dto) {
        ITSPHPAst parent = (ITSPHPAst) dto.actualParameter.getParent();

        //save child index, since it is going to change during rewrite
        int childIndex = dto.actualParameter.getChildIndex();
        ITSPHPAst actualParameter = dto.actualParameter;
        for (ICastingMethod castingMethod : dto.castingMethods) {
            ITSPHPAst cast = castingMethod.createCastAst(actualParameter);
            cast.setEvalType(castingMethod.getType());
            actualParameter = cast;
        }
        actualParameter.setParent(parent);
        parent.replaceChildren(childIndex, childIndex, actualParameter);

        return actualParameter;
    }
}
