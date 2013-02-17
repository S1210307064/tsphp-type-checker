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
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.common.exceptions.DefinitionException;
import ch.tutteli.tsphp.common.exceptions.ReferenceException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class ErrorHelper implements IErrorReporter
{

    List<Exception> exceptions = new ArrayList<>();
    IErrorMessageProvider errorMessageProvider;

    public ErrorHelper(IErrorMessageProvider anErrorMessageProvider) {
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
    public DefinitionException aliasForwardReference(ITSPHPAst typeAst, ITSPHPAst useDefinition) {
        return addAndGetDefinitionException("aliasForwardReference", typeAst, useDefinition);
    }
    
     @Override
    public DefinitionException forwardReference(ITSPHPAst typeAst, ITSPHPAst useDefinition) {
        return addAndGetDefinitionException("forwardReference", typeAst, useDefinition);
    }

    private DefinitionException addAndGetDefinitionException(String key,
            ITSPHPAst existingDefintion, ITSPHPAst newDefinition) {

        String errorMessage = errorMessageProvider.getErrorDefinitionMessage(key,
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

    private ReferenceException addAndGetReferenceException(String key,
            ITSPHPAst typeAst) {
        String errorMessage = errorMessageProvider.getErrorReferenceMessage(key,
                new ReferenceErrorDto(typeAst.getText(), typeAst.getLine(), typeAst.getCharPositionInLine()));
        ReferenceException exception = new ReferenceException(errorMessage, typeAst);
        exceptions.add(exception);
        return exception;
    }
}
