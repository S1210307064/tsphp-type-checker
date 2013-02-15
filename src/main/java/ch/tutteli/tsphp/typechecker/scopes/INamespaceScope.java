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
package ch.tutteli.tsphp.typechecker.scopes;

import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.typechecker.symbols.IAliasSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IInterfaceTypeSymbol;
import java.util.List;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public interface INamespaceScope extends IScope
{

    void defineUse(IAliasSymbol symbol);

    void useDefinitionCheck(IAliasSymbol symbol);

    void interfaceDefinitionCheck(IInterfaceTypeSymbol symbol);

    void classDefinitionCheck(IClassTypeSymbol classTypeSymbol);

    /**
     * Return one or more AST which contains the use declaration for the alias or null if the alias could not be found.
     *
     * @param alias The alias which shall be found
     * @return A list of ASTs or null
     */
    List<IAliasSymbol> getUse(String alias);
}
