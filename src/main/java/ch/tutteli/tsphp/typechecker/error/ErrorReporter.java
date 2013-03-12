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

import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.common.exceptions.DefinitionException;
import ch.tutteli.tsphp.common.exceptions.ReferenceException;
import ch.tutteli.tsphp.common.exceptions.UnsupportedOperationException;
import ch.tutteli.tsphp.typechecker.AmbiguousCallException;
import ch.tutteli.tsphp.typechecker.CastingDto;
import ch.tutteli.tsphp.typechecker.OverloadDto;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IScalarTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IVariableSymbol;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class ErrorReporter implements IErrorReporter
{

    List<Exception> exceptions = new ArrayList<>();
    IErrorMessageProvider errorMessageProvider;

    public ErrorReporter(IErrorMessageProvider anErrorMessageProvider) {
        errorMessageProvider = anErrorMessageProvider;
    }

    @Override
    public boolean hasFoundError() {
        return !exceptions.isEmpty();
    }

    @Override
    public List<Exception> getExceptions() {
        return exceptions;
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
    public DefinitionException objectExpected(ITSPHPAst callee, ITSPHPAst actualType) {
        return addAndGetDefinitionException("objectExpected", callee, actualType);
    }

    private DefinitionException addAndGetDefinitionException(String key,
            ITSPHPAst existingDefintion, ITSPHPAst newDefinition) {

        String errorMessage = errorMessageProvider.getDefinitionErrorMessage(key,
                new DefinitionErrorDto(
                existingDefintion.getText(), existingDefintion.getLine(), existingDefintion.getCharPositionInLine(),
                newDefinition.getText(), newDefinition.getLine(), newDefinition.getCharPositionInLine()));

        DefinitionException exception = new DefinitionException(errorMessage, existingDefintion, newDefinition);
        exceptions.add(exception);
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
    public ReferenceException notDefined(ITSPHPAst ast) {
        return addAndGetReferenceException("notDefined", ast);
    }

    @Override
    public ReferenceException notStatic(ITSPHPAst callee) {
        return addAndGetReferenceException("notStatic", (ITSPHPAst) callee.getParent());
    }

    private ReferenceException addAndGetReferenceException(String key,
            ITSPHPAst typeAst) {
        String errorMessage = errorMessageProvider.getReferenceErrorMessage(key,
                new ReferenceErrorDto(typeAst.getText(), typeAst.getLine(), typeAst.getCharPositionInLine()));
        ReferenceException exception = new ReferenceException(errorMessage, typeAst);
        exceptions.add(exception);
        return exception;
    }

    @Override
    public ReferenceException operatorAmbiguousCasts(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right,
            List<CastingDto> leftAmbiguouities, List<CastingDto> rightAmbiguouties) {
        return addAndGetOperatorAmbiguousCastsException("operatorAmbiguousCasting",
                operator, leftAmbiguouities, rightAmbiguouties, left, right);
    }

    @Override
    public ReferenceException ambiguousCasts(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right, List<CastingDto> ambiguousCastings) {
        return addAndGetOperatorAmbiguousCastsException("ambiguousCasting", operator, null, ambiguousCastings, left, right);
    }

    private ReferenceException addAndGetOperatorAmbiguousCastsException(String key, ITSPHPAst operator,
            List<CastingDto> leftAmbiguouities, List<CastingDto> rightAmbiguouities, ITSPHPAst left, ITSPHPAst right) {

        String leftType = getAbsoluteTypeName(left.getEvalType());
        List<String[]> leftReturnTypes = new ArrayList<>();
        if (leftAmbiguouities != null) {

            for (CastingDto castingDto : leftAmbiguouities) {
                int castingMethodsSize = castingDto.castingMethods.size();
                String[] types = new String[castingMethodsSize + 1];
                types[0] = leftType;
                for (int i = 0; i < castingMethodsSize; ++i) {
                    types[i + 1] = getAbsoluteTypeName(castingDto.castingMethods.get(i).getType());
                }
                leftReturnTypes.add(types);
            }
        }

        String rightType = getAbsoluteTypeName(right.getEvalType());
        List<String[]> rightReturnTypes = new ArrayList<>();
        if (rightAmbiguouities != null) {

            for (CastingDto castingDto : rightAmbiguouities) {
                int castingMethodsSize = castingDto.castingMethods.size();
                String[] types = new String[castingMethodsSize + 1];
                types[0] = rightType;
                for (int i = 0; i < castingMethodsSize; ++i) {
                    types[i + 1] = getAbsoluteTypeName(castingDto.castingMethods.get(i).getType());
                }
                rightReturnTypes.add(types);
            }
        }

        String errorMessage = errorMessageProvider.getOperatorAmbiguousCastingErrorMessage(key,
                new AmbiguousCastingErrorDto(operator.getText(), operator.getLine(), operator.getCharPositionInLine(),
                leftType, rightType, leftReturnTypes, rightReturnTypes));
        ReferenceException exception = new ReferenceException(errorMessage, operator);
        exceptions.add(exception);
        return exception;
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

        List<String[]> existingOverloads = new ArrayList<>();

        for (IMethodSymbol method : existingMethodOverloads) {
            existingOverloads.add(getFormalParameters(method.getParameters()));
        }
        String errorMessage = errorMessageProvider.getWrongArgumentTypeErrorMessage(key,
                new WrongArgumentTypeErrorDto(call.getText(), call.getLine(), call.getCharPositionInLine(),
                actualParameterTypes, existingOverloads));
        ReferenceException exception = new ReferenceException(errorMessage, call);
        exceptions.add(exception);
        return exception;
    }

    private String getAbsoluteTypeName(ITypeSymbol typeSymbol) {
        IScope definitionScope = typeSymbol.getDefinitionScope();
        return definitionScope.getScopeName() + typeSymbol.getName();
    }

    private String[] getFormalParameters(List<IVariableSymbol> formalParameters) {
        String[] formalParameterTypes = new String[formalParameters.size()];
        for (int i = 0; i < formalParameterTypes.length; ++i) {
            formalParameterTypes[i] = getAbsoluteTypeName(formalParameters.get(i).getType());
        }
        return formalParameterTypes;
    }

    @Override
    public UnsupportedOperationException unsupportedOperator(ITSPHPAst operator) {
        UnsupportedOperationException exception = new UnsupportedOperationException(
                "Unsupported operator exception occured. Please report bug to http://tsphp.tutteli.ch\nException "
                + "was caused by operator \"" + operator.getText()
                + " on line " + operator.getLine() + "|" + operator.getCharPositionInLine(), operator);
        exceptions.add(exception);
        return exception;
    }

    @Override
    public ReferenceException wrongEqualityUsage(ITSPHPAst statement, ITSPHPAst left, ITSPHPAst right) {
        return addAndGetTypeCheckErrorMessage("equalityOperator", statement, left, right);
    }

    @Override
    public ReferenceException wrongIdentityUsage(ITSPHPAst statement, ITSPHPAst left, ITSPHPAst right) {
        return addAndGetTypeCheckErrorMessage("identityOperator", statement, left, right);
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
        exceptions.add(exception);
        return exception;
    }
}
