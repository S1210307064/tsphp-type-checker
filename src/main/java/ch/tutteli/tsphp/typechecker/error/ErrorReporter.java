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

import ch.tutteli.tsphp.common.IErrorLogger;
import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.common.exceptions.DefinitionException;
import ch.tutteli.tsphp.common.exceptions.ReferenceException;
import ch.tutteli.tsphp.common.exceptions.TypeCheckerException;
import ch.tutteli.tsphp.common.exceptions.UnsupportedOperationException;
import ch.tutteli.tsphp.typechecker.AmbiguousCallException;
import ch.tutteli.tsphp.typechecker.CastingDto;
import ch.tutteli.tsphp.typechecker.ICastingMethod;
import ch.tutteli.tsphp.typechecker.OverloadDto;
import ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tutteli.tsphp.typechecker.symbols.IArrayTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IPolymorphicTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolWithAccessModifier;
import ch.tutteli.tsphp.typechecker.symbols.IVariableSymbol;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class ErrorReporter implements IErrorReporter
{

    private IErrorMessageProvider errorMessageProvider;
    private Collection<IErrorLogger> errorLoggers = new ArrayDeque<>();
    private boolean hasFoundError;

    public ErrorReporter(IErrorMessageProvider anErrorMessageProvider) {
        errorMessageProvider = anErrorMessageProvider;
    }

    @Override
    public boolean hasFoundError() {
        return hasFoundError;
    }

    private void reportError(TypeCheckerException exception) {
        hasFoundError = true;
        for (IErrorLogger logger : errorLoggers) {
            logger.log(exception);
        }
    }

    @Override
    public void addErrorLogger(IErrorLogger errorLogger) {
        errorLoggers.add(errorLogger);
    }

    @Override
    public void reset() {
        hasFoundError = false;
    }

    @Override
    public DefinitionException determineAlreadyDefined(ISymbol symbol1, ISymbol symbol2) {
        return determineAlreadyDefined(symbol1.getDefinitionAst(), symbol2.getDefinitionAst());

    }

    @Override
    public DefinitionException determineAlreadyDefined(ITSPHPAst ast1, ITSPHPAst ast2) {
        return ast1.isDefinedEarlierThan(ast2)
                ? alreadyDefined(ast1, ast2)
                : alreadyDefined(ast2, ast1);

    }

    @Override
    public DefinitionException alreadyDefined(ISymbol existingSymbol, ISymbol newSymbol) {
        return alreadyDefined(existingSymbol.getDefinitionAst(), newSymbol.getDefinitionAst());
    }

    @Override
    public DefinitionException alreadyDefined(ITSPHPAst existingDefintion, ITSPHPAst newDefinition) {
        return addAndGetDefinitionException("alreadyDefined", existingDefintion, newDefinition);
    }

    @Override
    public DefinitionException definedInOuterScope(ISymbol firstDefinition, ISymbol symbolToCheck) {
        return addAndGetDefinitionException("definedInOuterScope", firstDefinition.getDefinitionAst(),
                symbolToCheck.getDefinitionAst());
    }

    @Override
    public DefinitionException aliasForwardReference(ITSPHPAst typeAst, ITSPHPAst useDefinition) {
        return addAndGetDefinitionException("aliasForwardReference", typeAst, useDefinition);
    }

    @Override
    public DefinitionException forwardReference(ITSPHPAst typeAst, ITSPHPAst useDefinition) {
        return addAndGetDefinitionException("forwardReference", typeAst, useDefinition);
    }

    @Override
    public DefinitionException methodNotDefined(ITSPHPAst callee, ITSPHPAst id) {
        return addAndGetDefinitionException("methodNotDefined", callee.getSymbol().getDefinitionAst(), id);
    }

    @Override
    public DefinitionException memberNotDefined(ITSPHPAst callee, ITSPHPAst id) {
        return addAndGetDefinitionException("memberNotDefined", callee.getSymbol().getDefinitionAst(), id);
    }

    @Override
    public DefinitionException variableDefinedInOtherConditionalScope(ITSPHPAst definitionAst, ITSPHPAst variable) {
        return addAndGetDefinitionException("variableDefinedInOtherConditionalScope", definitionAst, variable);
    }

    @Override
    public DefinitionException variableDefinedInConditionalScope(ITSPHPAst definitionAst, ITSPHPAst variable) {
        return addAndGetDefinitionException("variableDefinedInConditionalScope", definitionAst, variable);
    }

    private DefinitionException addAndGetDefinitionException(String key,
            ITSPHPAst existingDefintion, ITSPHPAst newDefinition) {

        String errorMessage = errorMessageProvider.getDefinitionErrorMessage(key,
                new DefinitionErrorDto(
                existingDefintion.getText(), existingDefintion.getLine(), existingDefintion.getCharPositionInLine(),
                newDefinition.getText(), newDefinition.getLine(), newDefinition.getCharPositionInLine()));

        DefinitionException exception = new DefinitionException(errorMessage, existingDefintion, newDefinition);
        reportError(exception);
        return exception;
    }

    @Override
    public ReferenceException unkownType(ITSPHPAst typeAst) {
        return addAndGetReferenceException("unkownType", typeAst);
    }

    @Override
    public ReferenceException interfaceExpected(ITSPHPAst typeAst) {
        return addAndGetReferenceException("interfaceExpected", typeAst);
    }

    @Override
    public ReferenceException classExpected(ITSPHPAst typeAst) {
        return addAndGetReferenceException("classExpected", typeAst);
    }

    @Override
    public ReferenceException variableExpected(ITSPHPAst leftHandSide) {
        return addAndGetReferenceException("variableExpected", leftHandSide);
    }

    @Override
    public ReferenceException noParentClass(ITSPHPAst ast) {
        return addAndGetReferenceException("noParentClass", ast);
    }

    @Override
    public ReferenceException notInClass(ITSPHPAst ast) {
        return addAndGetReferenceException("notInClass", ast);
    }

    @Override
    public ReferenceException notInMethod(ITSPHPAst ast) {
        return addAndGetReferenceException("notInMethod", ast);
    }

    @Override
    public ReferenceException notDefined(ITSPHPAst ast) {
        return addAndGetReferenceException("notDefined", ast);
    }

    @Override
    public ReferenceException notStatic(ITSPHPAst callee) {
        return addAndGetReferenceException("notStatic", (ITSPHPAst) callee.getParent());
    }

    @Override
    public ReferenceException toManyBreakContinueLevels(ITSPHPAst root) {
        return addAndGetReferenceException("toManyBreakContinueLevels", root);
    }

    private ReferenceException addAndGetReferenceException(String key,
            ITSPHPAst typeAst) {
        String errorMessage = errorMessageProvider.getReferenceErrorMessage(key,
                new ReferenceErrorDto(typeAst.getText(), typeAst.getLine(), typeAst.getCharPositionInLine()));
        ReferenceException exception = new ReferenceException(errorMessage, typeAst);
        reportError(exception);
        return exception;
    }

    @Override
    public ReferenceException operatorAmbiguousCasts(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right,
            CastingDto leftToRightCasts, CastingDto rightToLeftCasts,
            List<CastingDto> leftAmbiguouities, List<CastingDto> rightAmbiguouties) {

        ReferenceException exception;
        if (noAmbiguousCasts(leftAmbiguouities, rightAmbiguouties)) {
            exception = addAndGetOperatorAmbiguousCastsException("operatorBothSideCast",
                    operator, leftToRightCasts, rightToLeftCasts,
                    leftAmbiguouities, rightAmbiguouties, left, right, true);
        } else {
            exception = addAndGetOperatorAmbiguousCastsException("operatorAmbiguousCasts",
                    operator, leftToRightCasts, rightToLeftCasts,
                    leftAmbiguouities, rightAmbiguouties, left, right, true);
        }

        return exception;
    }

    private boolean noAmbiguousCasts(List<CastingDto> leftAmbiguouities, List<CastingDto> rightAmbiguouties) {
        return leftAmbiguouities == null || leftAmbiguouities.isEmpty()
                && rightAmbiguouties == null || rightAmbiguouties.isEmpty();
    }

    @Override
    public ReferenceException ambiguousCasts(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right,
            List<CastingDto> ambiguousCastings) {
        return addAndGetOperatorAmbiguousCastsException("ambiguousCasts", operator, null, null, null, ambiguousCastings,
                left, right, false);
    }

    private ReferenceException addAndGetOperatorAmbiguousCastsException(String key, ITSPHPAst operator,
            CastingDto leftToRightCasts, CastingDto rightToLeftCasts,
            List<CastingDto> leftAmbiguouities, List<CastingDto> rightAmbiguouities, ITSPHPAst left, ITSPHPAst right,
            boolean doBothSideCast) {

        String leftType = getAbsoluteTypeName(left.getEvalType());
        String rightType = getAbsoluteTypeName(right.getEvalType());

        List<String> leftToRightReturnTypes;
        if (doBothSideCast) {
            leftToRightReturnTypes = getReturnTypes(leftToRightCasts, leftType, rightType);
        } else {
            leftToRightReturnTypes = new ArrayList<>();
            leftToRightReturnTypes.add(leftType);
        }
        List<List<String>> leftReturnTypes = new ArrayList<>();
        if (leftAmbiguouities != null) {
            addReturnTypes(leftReturnTypes, leftAmbiguouities, leftType, rightType);
        }

        List<String> rightToLeftReturnTypes;
        if (doBothSideCast) {
            rightToLeftReturnTypes = getReturnTypes(rightToLeftCasts, rightType, leftType);
        } else {
            rightToLeftReturnTypes = new ArrayList<>();
            rightToLeftReturnTypes.add(rightType);
        }
        List<List<String>> rightReturnTypes = new ArrayList<>();
        if (rightAmbiguouities != null) {
            addReturnTypes(rightReturnTypes, rightAmbiguouities, rightType, leftType);
        }

        String errorMessage = errorMessageProvider.getOperatorAmbiguousCastingErrorMessage(key,
                new AmbiguousCastsErrorDto(operator.getText(), operator.getLine(), operator.getCharPositionInLine(),
                leftToRightReturnTypes, rightToLeftReturnTypes, leftReturnTypes, rightReturnTypes));
        ReferenceException exception = new ReferenceException(errorMessage, operator);
        reportError(exception);
        return exception;
    }

    private void addReturnTypes(List<List<String>> returnTypes, List<CastingDto> castingDtos,
            String startType, String endType) {
        for (CastingDto castingDto : castingDtos) {
            returnTypes.add(getReturnTypes(castingDto, startType, endType));
        }
    }

    private List<String> getReturnTypes(CastingDto castingDto, String startType, String endType) {
        List<String> returnTypes = new ArrayList<>();
        returnTypes.add(startType);
        for (ICastingMethod castingMethod : castingDto.castingMethods) {
            ITypeSymbol parentTypeSymbol = castingMethod.getParentTypeWhichProvidesCast();
            if (parentTypeSymbol != null) {
                returnTypes.add(getAbsoluteTypeName(parentTypeSymbol));
            }
            returnTypes.add(getAbsoluteTypeName(castingMethod.getType()));
        }
        if (!returnTypes.get(returnTypes.size() - 1).equals(endType)) {
            returnTypes.add(endType);
        }
        return returnTypes;
    }

    @Override
    public ReferenceException ambiguousUnaryOperatorUsage(ITSPHPAst operator, ITSPHPAst expression,
            AmbiguousCallException ex) {

        return addAndGetAmbiguousCallException("ambiguousOperatorUsage", operator, ex, expression);
    }

    @Override
    public ReferenceException ambiguousBinaryOperatorUsage(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right,
            AmbiguousCallException ex) {

        return addAndGetAmbiguousCallException("ambiguousOperatorUsage", operator, ex, left, right);
    }

    private ReferenceException addAndGetAmbiguousCallException(String key, ITSPHPAst call, AmbiguousCallException ex,
            ITSPHPAst... actualParameters) {
        List<IMethodSymbol> methods = new ArrayList<>();
        List<OverloadDto> overloads = ex.getAmbiguousOverloads();
        for (OverloadDto overload : overloads) {
            methods.add(overload.methodSymbol);
        }
        return addAndGetWrongArgumentTypeException(key, call, methods, actualParameters);
    }

    @Override
    public ReferenceException wrongBinaryOperatorUsage(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right,
            List<IMethodSymbol> existingMethodOverloads) {

        return addAndGetWrongArgumentTypeException("wrongOperatorUsage", operator,
                existingMethodOverloads, left, right);

    }

    @Override
    public ReferenceException wrongUnaryOperatorUsage(ITSPHPAst operator, ITSPHPAst expression,
            List<IMethodSymbol> existingMethodOverloads) {
        return addAndGetWrongArgumentTypeException("wrongOperatorUsage", operator, existingMethodOverloads, expression);
    }

    private ReferenceException addAndGetWrongArgumentTypeException(String key, ITSPHPAst call,
            List<IMethodSymbol> existingMethodOverloads, ITSPHPAst... actualParameters) {

        String[] actualParameterTypes = new String[actualParameters.length];
        for (int i = 0; i < actualParameterTypes.length; ++i) {
            actualParameterTypes[i] = getAbsoluteTypeName(actualParameters[i].getEvalType());
        }

        List<List<String>> existingOverloads = new ArrayList<>();

        for (IMethodSymbol method : existingMethodOverloads) {
            existingOverloads.add(getFormalParameters(method.getParameters()));
        }
        String errorMessage = errorMessageProvider.getWrongArgumentTypeErrorMessage(key,
                new WrongArgumentTypeErrorDto(call.getText(), call.getLine(), call.getCharPositionInLine(),
                actualParameterTypes, existingOverloads));
        ReferenceException exception = new ReferenceException(errorMessage, call);
        reportError(exception);
        return exception;
    }

    private String getAbsoluteTypeName(ITypeSymbol typeSymbol) {
        IScope definitionScope = typeSymbol.getDefinitionScope();
        return definitionScope.getScopeName() + typeSymbol.getName();
    }

    private List<String> getFormalParameters(List<IVariableSymbol> formalParameters) {
        List<String> formalParameterTypes = new ArrayList<>(formalParameters.size());
        for (IVariableSymbol variableSymbol : formalParameters) {
            formalParameterTypes.add(getAbsoluteTypeName(variableSymbol.getType()));
        }
        return formalParameterTypes;
    }

    @Override
    public UnsupportedOperationException unsupportedOperator(ITSPHPAst operator) {
        UnsupportedOperationException exception = new UnsupportedOperationException(
                "Unsupported operator exception occured. Please report bug to http://tsphp.tutteli.ch\nException "
                + "was caused by operator \"" + operator.getText()
                + " on line " + operator.getLine() + "|" + operator.getCharPositionInLine(), operator);
        reportError(exception);
        return exception;
    }

    @Override
    public ReferenceException wrongEqualityUsage(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right) {
        return addAndGetTypeCheckErrorMessage("equalityOperator", operator, left, right);
    }

    @Override
    public ReferenceException wrongCast(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right) {
        return addAndGetTypeCheckErrorMessage("wrongCast", operator, left, right);
    }

    @Override
    public ReferenceException wrongIdentityUsage(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right) {
        return addAndGetTypeCheckErrorMessage("identityOperator", operator, left, right);
    }

    @Override
    public ReferenceException wrongAssignment(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right) {
        return addAndGetTypeCheckErrorMessage("wrongAssignment", operator, left, right);
    }

    private ReferenceException addAndGetTypeCheckErrorMessage(String key, ITSPHPAst statement, ITSPHPAst left,
            ITSPHPAst right) {
        String errorMessage = errorMessageProvider.getTypeCheckErrorMessage(key,
                new TypeCheckErrorDto(statement.getText(), statement.getLine(), statement.getCharPositionInLine(),
                getAbsoluteTypeName(left.getEvalType()), getAbsoluteTypeName(right.getEvalType())));
        ReferenceException exception = new ReferenceException(errorMessage, statement);
        reportError(exception);
        return exception;
    }

    @Override
    public ReferenceException notSameOrParentType(ITSPHPAst statement, ITSPHPAst expression, ITypeSymbol typeSymbol) {
        return addAndGetStatementTypeCheckError("notSameOrParentType", statement, expression, typeSymbol);
    }

    @Override
    public ReferenceException wrongTypeIf(ITSPHPAst ifRoot, ITSPHPAst expression, ITypeSymbol typeSymbol) {
        return addAndGetStatementTypeCheckError("wrongTypeIf", ifRoot, expression, typeSymbol);
    }

    @Override
    public ReferenceException wrongTypeSwitch(ITSPHPAst switchRoot, ITSPHPAst expression, ITypeSymbol typeSymbol) {
        return addAndGetStatementTypeCheckError("wrongTypeSwitch", switchRoot, expression, typeSymbol);
    }

    @Override
    public ReferenceException wrongTypeSwitchCase(ITSPHPAst switchCase, ITSPHPAst expression, ITypeSymbol typeSymbol) {
        return addAndGetStatementTypeCheckError("wrongTypeSwitchCase", expression, expression, typeSymbol);
    }

    @Override
    public ReferenceException wrongTypeFor(ITSPHPAst forRoot, ITSPHPAst expression, ITypeSymbol typeSymbol) {
        return addAndGetStatementTypeCheckError("wrongTypeFor", forRoot, expression, typeSymbol);
    }

    @Override
    public ReferenceException wrongTypeForeach(ITSPHPAst foreachRoot, ITSPHPAst array,
            IArrayTypeSymbol arrayTypeSymbol) {
        return addAndGetStatementTypeCheckError("wrongTypForeach", foreachRoot, array, arrayTypeSymbol);
    }

    @Override
    public ReferenceException wrongTypeWhile(ITSPHPAst whileRoot, ITSPHPAst expression, ITypeSymbol typeSymbol) {
        return addAndGetStatementTypeCheckError("wrongTypeWhile", whileRoot, expression, typeSymbol);
    }

    @Override
    public ReferenceException wrongTypeDoWhile(ITSPHPAst doWhileRoot, ITSPHPAst expression, ITypeSymbol typeSymbol) {
        return addAndGetStatementTypeCheckError("wrongTypeDoWhile", doWhileRoot, expression, typeSymbol);
    }

    @Override
    public ReferenceException wrongTypeThrow(ITSPHPAst throwRoot, ITSPHPAst expression, ITypeSymbol typeSymbol) {
        return addAndGetStatementTypeCheckError("wrongTypeThrow", throwRoot, expression, typeSymbol);
    }

    @Override
    public ReferenceException wrongTypeCatch(ITSPHPAst castRoot, ITSPHPAst variableId, ITypeSymbol typeSymbol) {
        return addAndGetStatementTypeCheckError("wrongTypeCatch", castRoot, variableId, typeSymbol);
    }

    @Override
    public ReferenceException wrongTypeEcho(ITSPHPAst expression, ITypeSymbol typeSymbol) {
        return addAndGetStatementTypeCheckError("wrongTypeEcho", expression, expression, typeSymbol);
    }

    @Override
    public ReferenceException wrongTypeArrayAccess(ITSPHPAst expression, IArrayTypeSymbol arrayTypeSymbol) {
        return addAndGetStatementTypeCheckError("arrayExpected", expression, expression, arrayTypeSymbol);
    }

    @Override
    public ReferenceException wrongArrayIndexType(ITSPHPAst expression, ITSPHPAst index, ITypeSymbol typeSymbol) {
        return addAndGetStatementTypeCheckError("wrongArrayIndexType", expression, index, typeSymbol);
    }

    @Override
    public ReferenceException noReturnValueExpected(ITSPHPAst returnRoot, ITSPHPAst expression,
            ITypeSymbol typeSymbol) {
        return addAndGetStatementTypeCheckError("noReturnValueExpected", returnRoot, expression, typeSymbol);
    }

    @Override
    public ReferenceException returnValueExpected(ITSPHPAst returnRoot, ITSPHPAst expression, ITypeSymbol typeSymbol) {
        return addAndGetStatementTypeCheckError("returnValueExpected", returnRoot, expression, typeSymbol);
    }

    @Override
    public ReferenceException wrongTypeReturn(ITSPHPAst returnRoot, ITSPHPAst expression, ITypeSymbol typeSymbol) {
        return addAndGetStatementTypeCheckError("wrongTypeReturn", returnRoot, expression, typeSymbol);
    }

    @Override
    public ReferenceException wrongTypeTernaryCondition(ITSPHPAst operator, ITSPHPAst condition,
            ITypeSymbol typeExpected) {
        return addAndGetStatementTypeCheckError("wrongTypeTernaryCondition", operator, condition, typeExpected);

    }

    @Override
    public ReferenceException wrongTypeTernaryCases(ITSPHPAst caseTrue, ITSPHPAst caseFalse) {
        return addAndGetStatementTypeCheckError("wrongTypeTernaryCases", caseFalse, caseFalse, caseTrue.getEvalType());
    }

    @Override
    public ReferenceException onlySingleValue(ITSPHPAst variableId, ITSPHPAst expression) {
        return addAndGetStatementTypeCheckError("onlySingleValue", variableId, expression, variableId.getEvalType());
    }

    @Override
    public ReferenceException onlyConstantValue(ITSPHPAst variableId, ITSPHPAst expression) {
        return addAndGetStatementTypeCheckError("onlyConstantValue", variableId, expression, variableId.getEvalType());
    }

    private ReferenceException addAndGetStatementTypeCheckError(String key, ITSPHPAst statement,
            ITSPHPAst expression, ITypeSymbol typeSymbol) {

        String errorMessage = errorMessageProvider.getTypeCheckErrorMessage(key,
                new TypeCheckErrorDto(statement.getText(), statement.getLine(), statement.getCharPositionInLine(),
                getAbsoluteTypeName(typeSymbol), getAbsoluteTypeName(expression.getEvalType())));
        ReferenceException exception = new ReferenceException(errorMessage, statement);
        reportError(exception);
        return exception;
    }

    @Override
    public ReferenceException wrongTypeClone(ITSPHPAst clone, ITSPHPAst expression) {
        return addAndGetClassInterfaceExpectedError("wrongTypeClone", clone, expression.getEvalType());
    }

    @Override
    public ReferenceException wrongTypeMethodCall(ITSPHPAst callee) {
        return addAndGetClassInterfaceExpectedError("wrongTypeMethodCall", callee, callee.getSymbol().getType());
    }

    @Override
    public ReferenceException wrongTypeInstanceof(ITSPHPAst expression) {
        return addAndGetClassInterfaceExpectedError("wrongTypeInstanceof", expression, expression.getEvalType());
    }

    @Override
    public ReferenceException wrongTypeClassMemberAccess(ITSPHPAst expression) {
        return addAndGetClassInterfaceExpectedError("wrongTypeClassMemberAccess", expression, expression.getEvalType());
    }

    private ReferenceException addAndGetClassInterfaceExpectedError(String key, ITSPHPAst operator,
            ITypeSymbol typeSymbol) {
        String errorMessage = errorMessageProvider.getTypeCheckErrorMessage(key,
                new TypeCheckErrorDto(operator.getText(), operator.getLine(), operator.getCharPositionInLine(),
                "class-/interface-type", getAbsoluteTypeName(typeSymbol)));
        ReferenceException exception = new ReferenceException(errorMessage, operator);
        reportError(exception);
        return exception;
    }

    @Override
    public ReferenceException visibilityViolationClassMemberAccess(ITSPHPAst identifier,
            ISymbolWithAccessModifier symbol, int accessFrom) {

        return addAndGetVisibilityViolatedError("classMemberAccess", identifier,
                symbol, accessFrom);
    }

    private ReferenceException addAndGetVisibilityViolatedError(String key, ITSPHPAst identifier,
            ISymbolWithAccessModifier symbol, int accessedFrom) {

        String visibility = symbol.isProtected() ? "protected" : "private";
        String wasAccessedFrom;
        switch (accessedFrom) {
            case TSPHPDefinitionWalker.Public:
                wasAccessedFrom = "public";
                break;
            case TSPHPDefinitionWalker.Protected:
                wasAccessedFrom = "protected";
                break;
            case TSPHPDefinitionWalker.Private:
            default:
                wasAccessedFrom = "private";
        }

        String errorMessage = errorMessageProvider.getVisibilityErrorMessage(key,
                new VisbilityErrorDto(symbol.getName(), identifier.getLine(),
                identifier.getCharPositionInLine(), visibility, wasAccessedFrom));
        ReferenceException exception = new ReferenceException(errorMessage, identifier);
        reportError(exception);
        return exception;
    }
}
