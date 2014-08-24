/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.error;

import ch.tsphp.common.ISymbol;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.ITypeSymbol;
import ch.tsphp.common.exceptions.DefinitionException;
import ch.tsphp.common.exceptions.ReferenceException;
import ch.tsphp.common.exceptions.UnsupportedOperationException;
import ch.tsphp.typechecker.AmbiguousCallException;
import ch.tsphp.typechecker.CastingDto;
import ch.tsphp.typechecker.symbols.IArrayTypeSymbol;
import ch.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tsphp.typechecker.symbols.ISymbolWithAccessModifier;

import java.util.List;
import java.util.Set;

/**
 * Represents the type checker's error reporter.
 * <p/>
 * What ever error occurs in the definition, reference or type checking phase will be reported using an instance of
 * this interface.
 */
@SuppressWarnings("checkstyle:methodcount")
public interface ITypeCheckerErrorReporter extends ch.tsphp.common.IErrorReporter
{

    /**
     * Determine which AST was defined earlier and call the method alreadyDefined correspondingly.
     */
    DefinitionException determineAlreadyDefined(ITSPHPAst ast1, ITSPHPAst ast2);

    /**
     * Determine which symbol was defined earlier and call the method alreadyDefined correspondingly.
     */
    DefinitionException determineAlreadyDefined(ISymbol symbolEnclosingScope, ISymbol symbol);

    DefinitionException alreadyDefined(ISymbol existingSymbol, ISymbol newSymbol);

    DefinitionException alreadyDefined(ITSPHPAst existingDefinition, ITSPHPAst newDefinition);

    DefinitionException definedInOuterScope(ISymbol firstDefinition, ISymbol symbolToCheck);

    DefinitionException aliasForwardReference(ITSPHPAst typeAst, ITSPHPAst useDefinition);

    DefinitionException forwardReference(ITSPHPAst definitionAst, ITSPHPAst identifier);

    DefinitionException methodNotDefined(ITSPHPAst callee, ITSPHPAst id);

    DefinitionException memberNotDefined(ITSPHPAst accessor, ITSPHPAst id);

    DefinitionException variableDefinedInOtherConditionalScope(ITSPHPAst definitionAst, ITSPHPAst variableId);

    DefinitionException variableDefinedInConditionalScope(ITSPHPAst definitionAst, ITSPHPAst variableId);

    DefinitionException variablePartiallyInitialised(ITSPHPAst definitionAst, ITSPHPAst variableId);

    DefinitionException variableNotInitialised(ITSPHPAst definitionAst, ITSPHPAst variableId);

    ReferenceException unknownType(ITSPHPAst typeAst);

    ReferenceException interfaceExpected(ITSPHPAst typeAst);

    ReferenceException classExpected(ITSPHPAst typeAst);

    ReferenceException variableExpected(ITSPHPAst leftHandSide);

    ReferenceException noParentClass(ITSPHPAst ast);

    ReferenceException notInClass(ITSPHPAst ast);

    ReferenceException notInMethod(ITSPHPAst ast);

    ReferenceException notDefined(ITSPHPAst ast);

    ReferenceException notStatic(ITSPHPAst callee);

    ReferenceException toManyBreakContinueLevels(ITSPHPAst root);

    ReferenceException breakContinueLevelZeroNotAllowed(ITSPHPAst root);

    ReferenceException partialReturnFromFunction(ITSPHPAst identifier);

    ReferenceException noReturnFromFunction(ITSPHPAst identifier);

    ReferenceException partialReturnFromMethod(ITSPHPAst identifier);

    ReferenceException noReturnFromMethod(ITSPHPAst identifier);

    UnsupportedOperationException unsupportedOperator(ITSPHPAst operator);

    ReferenceException ambiguousUnaryOperatorUsage(ITSPHPAst operator, ITSPHPAst expression,
            AmbiguousCallException ex);

    ReferenceException ambiguousBinaryOperatorUsage(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right,
            AmbiguousCallException ex);

    ReferenceException ambiguousCall(ITSPHPAst identifier, AmbiguousCallException exception,
            List<ITSPHPAst> actualParameters);

    ReferenceException operatorAmbiguousCasts(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right,
            CastingDto leftToRightCasts, CastingDto rightToLeftCasts,
            List<CastingDto> leftAmbiguities, List<CastingDto> rightAmbiguities);

    ReferenceException ambiguousCasts(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right,
            List<CastingDto> ambiguousCasts);

    ReferenceException wrongBinaryOperatorUsage(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right,
            List<IMethodSymbol> existingMethodOverloads);

    ReferenceException wrongUnaryOperatorUsage(ITSPHPAst operator, ITSPHPAst expression,
            List<IMethodSymbol> existingMethodOverloads);

    ReferenceException wrongFunctionCall(ITSPHPAst identifier, List<ITSPHPAst> actualParameters,
            List<IMethodSymbol> methods);

    ReferenceException wrongMethodCall(ITSPHPAst identifier, List<ITSPHPAst> actualParameters,
            List<IMethodSymbol> methods);

    ReferenceException wrongEqualityUsage(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right);

    ReferenceException wrongCast(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right);

    ReferenceException wrongIdentityUsage(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right);

    ReferenceException wrongAssignment(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right);

    ReferenceException notSameOrParentType(ITSPHPAst statement, ITSPHPAst expression, ITypeSymbol typeSymbol);

    ReferenceException wrongTypeIf(ITSPHPAst ifRoot, ITSPHPAst expression, ITypeSymbol typeSymbol);

    ReferenceException wrongTypeSwitch(ITSPHPAst switchRoot, ITSPHPAst expression, ITypeSymbol typeSymbol);

    ReferenceException wrongTypeSwitchCase(ITSPHPAst switchRoot, ITSPHPAst switchCase, ITypeSymbol typeSymbol);

    ReferenceException wrongTypeFor(ITSPHPAst forRoot, ITSPHPAst expression, ITypeSymbol typeSymbol);

    ReferenceException wrongTypeForeach(ITSPHPAst foreachRoot, ITSPHPAst array, IArrayTypeSymbol arrayTypeSymbol);

    ReferenceException wrongTypeWhile(ITSPHPAst whileRoot, ITSPHPAst expression, ITypeSymbol typeSymbol);

    ReferenceException wrongTypeDoWhile(ITSPHPAst doWhileRoot, ITSPHPAst expression, ITypeSymbol typeSymbol);

    ReferenceException wrongTypeThrow(ITSPHPAst throwRoot, ITSPHPAst expression, ITypeSymbol typeSymbol);

    ReferenceException wrongTypeCatch(ITSPHPAst castRoot, ITSPHPAst variableId, ITypeSymbol typeSymbol);

    ReferenceException wrongTypeEcho(ITSPHPAst expression, ITypeSymbol typeSymbol);

    ReferenceException wrongTypeArrayAccess(ITSPHPAst expression, IArrayTypeSymbol arrayTypeSymbol);

    ReferenceException wrongArrayIndexType(ITSPHPAst expression, ITSPHPAst index, ITypeSymbol typeSymbol);

    ReferenceException noReturnValueExpected(ITSPHPAst returnRoot, ITSPHPAst expression, ITypeSymbol typeSymbol);

    ReferenceException returnValueExpected(ITSPHPAst returnRoot, ITypeSymbol typeSymbol);

    ReferenceException wrongTypeReturn(ITSPHPAst returnRoot, ITSPHPAst expression, ITypeSymbol typeSymbol);

    ReferenceException wrongTypeTernaryCondition(ITSPHPAst operator, ITSPHPAst condition, ITypeSymbol typeExpected);

    ReferenceException wrongTypeTernaryCases(ITSPHPAst caseTrue, ITSPHPAst caseFalse);

    ReferenceException onlySingleValue(ITSPHPAst variableId, ITSPHPAst expression);

    ReferenceException onlyConstantValue(ITSPHPAst variableId, ITSPHPAst expression);

    ReferenceException wrongClassMemberInitialValue(ITSPHPAst variableId, ITSPHPAst expression, ITypeSymbol evalType);

    ReferenceException wrongTypeClone(ITSPHPAst clone, ITSPHPAst expression);

    ReferenceException wrongTypeMethodCall(ITSPHPAst callee);

    ReferenceException wrongTypeInstanceof(ITSPHPAst expression);

    ReferenceException wrongTypeClassMemberAccess(ITSPHPAst expression);

    ReferenceException visibilityViolationClassMemberAccess(ITSPHPAst identifier,
            ISymbolWithAccessModifier symbol, int accessedFrom);

    ReferenceException visibilityViolationStaticClassMemberAccess(ITSPHPAst identifier,
            ISymbolWithAccessModifier symbol, int accessedFrom);

    ReferenceException visibilityViolationClassConstantAccess(ITSPHPAst identifier,
            ISymbolWithAccessModifier symbol, int accessedFrom);

    ReferenceException visibilityViolationMethodCall(ITSPHPAst identifier,
            ISymbolWithAccessModifier symbol, int accessedFrom);

    ReferenceException missingAbstractImplementations(ITSPHPAst identifier, Set<ISymbol> symbols);
}
