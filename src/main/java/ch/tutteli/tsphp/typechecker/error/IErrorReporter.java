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
package ch.tutteli.tsphp.typechecker.error;

import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.exceptions.DefinitionException;
import ch.tutteli.tsphp.common.exceptions.ReferenceException;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public interface IErrorReporter extends ch.tutteli.tsphp.common.IErrorReporter
{

    /**
     * Determine which AST was defined earlier and call the method addAlreadyefinedException correspondingly.
     */
    DefinitionException determineAlreadyDefined(ITSPHPAst ast1, ITSPHPAst ast2);

    /**
     * Determine which symbol was defined earlier and call the method addAlreadyefinedException correspondingly.
     */
    DefinitionException determineAlreadyDefined(ISymbol symbolEnclosingScope, ISymbol symbol);

    DefinitionException alreadyDefined(ISymbol existingSymbol, ISymbol newSymbol);

    DefinitionException alreadyDefined(ITSPHPAst existingDefintion, ITSPHPAst newDefinition);

    DefinitionException aliasForwardReference(ITSPHPAst typeAst, ITSPHPAst useDefinition);

    DefinitionException forwardReference(ITSPHPAst typeAst, ITSPHPAst useDefinition);

    ReferenceException unkownType(ITSPHPAst typeAst);

    ReferenceException interfaceExpected(ITSPHPAst typeAst);

    ReferenceException classExpected(ITSPHPAst typeAst);

    ReferenceException noParentClass(ITSPHPAst ast);

    ReferenceException notInClass(ITSPHPAst ast);

    ReferenceException notDefined(ITSPHPAst ast);
}
