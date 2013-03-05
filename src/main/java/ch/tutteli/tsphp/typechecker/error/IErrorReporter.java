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
import ch.tutteli.tsphp.common.exceptions.TypeCheckerException;
import ch.tutteli.tsphp.common.exceptions.UnsupportedOperationException;
import ch.tutteli.tsphp.typechecker.AmbiguousCallException;
import ch.tutteli.tsphp.typechecker.CastingDto;
import ch.tutteli.tsphp.typechecker.OverloadDto;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import java.util.List;

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

    DefinitionException definedInOuterScope(ISymbol firstDefinition, ISymbol symbolToCheck);

    DefinitionException aliasForwardReference(ITSPHPAst typeAst, ITSPHPAst useDefinition);

    DefinitionException forwardReference(ITSPHPAst typeAst, ITSPHPAst useDefinition);

    DefinitionException methodNotDefined(ITSPHPAst callee, ITSPHPAst id);

    DefinitionException memberNotDefined(ITSPHPAst callee, ITSPHPAst id);

    DefinitionException objectExpected(ITSPHPAst callee, ITSPHPAst definitionAst);

    ReferenceException unkownType(ITSPHPAst typeAst);

    ReferenceException interfaceExpected(ITSPHPAst typeAst);

    ReferenceException classExpected(ITSPHPAst typeAst);

    ReferenceException variableExpected(ITSPHPAst leftHandSide);

    ReferenceException noParentClass(ITSPHPAst ast);

    ReferenceException notInClass(ITSPHPAst ast);

    ReferenceException notDefined(ITSPHPAst ast);

    ReferenceException notStatic(ITSPHPAst callee);

    UnsupportedOperationException unsupportedOperator(ITSPHPAst operator);

    ReferenceException ambiguousUnaryOperatorUsage(ITSPHPAst operator, ITSPHPAst expression,
            AmbiguousCallException ex);

    ReferenceException ambiguousBinaryOperatorUsage(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right,
            AmbiguousCallException ex);

    ReferenceException operatorAmbiguousCasts(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right,
            List<CastingDto> leftAmbiguouities, List<CastingDto> rightAmbiguouties);

    ReferenceException ambiguousCasts(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right,
            List<CastingDto> ambiguousCastings);

    ReferenceException wrongBinaryOperatorUsage(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right,
            List<IMethodSymbol> existingMethodOverloads);

    ReferenceException wrongUnaryOperatorUsage(ITSPHPAst operator, ITSPHPAst expression,
            List<IMethodSymbol> existingMethodOverloads);

    ReferenceException wrongEqualityUsage(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right);

    ReferenceException wrongIdentityUsage(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right);

    ReferenceException wrongAssignment(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right);
}
