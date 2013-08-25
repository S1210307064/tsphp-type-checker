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
package ch.tutteli.tsphp.typechecker;

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public interface ICastingMethod
{

    ITSPHPAst createCastAst(ITSPHPAst ast);

    ITypeSymbol getType();

    /**
     * Return the parent type which provided the casting method or null if the casting method is defined on the type to
     * be cast.
     *
     * For instance, if B $b; A $a = (A) $b; and not B itself contains the cast to A but C (parent type of B) then this
     * method will return the ITypeSymbol which represents the type C. However, if B contains the casting then null will
     * be returned.
     *
     * @return
     */
    ITypeSymbol getParentTypeWhichProvidesCast();

    void setParentTypeWhichProvidesCast(ITypeSymbol parentTypeSymbol);
}
