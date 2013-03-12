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
package ch.tutteli.tsphp.typechecker.utils;

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.CastingDto;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public interface IAstHelper
{

    ITSPHPAst createAst(ITSPHPAst ast);

    ITSPHPAst createAst(int tokenType, String name);

    /**
     * Create a function call or static method call AST node and use the given expression as actual parameter.
     *
     * @return The create node
     */
    ITSPHPAst prependCasting(CastingDto parameterPromotionDto);

    void prependAst(ITSPHPAst ast, ITSPHPAst target);
}
