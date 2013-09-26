package ch.tutteli.tsphp.typechecker.error;

import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.common.exceptions.DefinitionException;
import ch.tutteli.tsphp.common.exceptions.ReferenceException;
import ch.tutteli.tsphp.common.exceptions.UnsupportedOperationException;
import ch.tutteli.tsphp.typechecker.AmbiguousCallException;
import ch.tutteli.tsphp.typechecker.CastingDto;
import ch.tutteli.tsphp.typechecker.symbols.IArrayTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolWithAccessModifier;
import java.util.List;

public interface IErrorReporter extends ch.tutteli.tsphp.common.IErrorReporter
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

    DefinitionException forwardReference(ITSPHPAst typeAst, ITSPHPAst useDefinition);

    DefinitionException methodNotDefined(ITSPHPAst callee, ITSPHPAst id);

    DefinitionException memberNotDefined(ITSPHPAst accessor, ITSPHPAst id);

    DefinitionException variableDefinedInOtherConditionalScope(ITSPHPAst definitionAst, ITSPHPAst variable);

    DefinitionException variableDefinedInConditionalScope(ITSPHPAst definitionAst, ITSPHPAst variable);

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

    ReferenceException wrongTypeSwitchCase(ITSPHPAst switchCase, ITSPHPAst expression, ITypeSymbol typeSymbol);

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

    ReferenceException returnValueExpected(ITSPHPAst returnRoot, ITSPHPAst expression, ITypeSymbol typeSymbol);

    ReferenceException wrongTypeReturn(ITSPHPAst returnRoot, ITSPHPAst expression, ITypeSymbol typeSymbol);

    ReferenceException wrongTypeTernaryCondition(ITSPHPAst operator, ITSPHPAst condition, ITypeSymbol typeExpected);

    ReferenceException wrongTypeTernaryCases(ITSPHPAst caseTrue, ITSPHPAst caseFalse);

    ReferenceException onlySingleValue(ITSPHPAst variableId, ITSPHPAst expression);

    ReferenceException onlyConstantValue(ITSPHPAst variableId, ITSPHPAst expression);

    ReferenceException wrongTypeClone(ITSPHPAst clone, ITSPHPAst expression);

    ReferenceException wrongTypeMethodCall(ITSPHPAst callee);

    ReferenceException wrongTypeInstanceof(ITSPHPAst expression);

    ReferenceException wrongTypeClassMemberAccess(ITSPHPAst expression);

    ReferenceException visibilityViolationClassMemberAccess(ITSPHPAst identifier, ISymbolWithAccessModifier symbol,
            int accessedFrom);

    ReferenceException visibilityViolationStaticClassMemberAccess(ITSPHPAst identifier,
            ISymbolWithAccessModifier symbol, int accessedFrom);

    ReferenceException visibilityViolationClassConstantAccess(ITSPHPAst identifier,
            ISymbolWithAccessModifier symbol, int accessedFrom);

    ReferenceException visibilityViolationMethodCall(ITSPHPAst identifier, ISymbolWithAccessModifier symbol,
            int accessedFrom);
}
