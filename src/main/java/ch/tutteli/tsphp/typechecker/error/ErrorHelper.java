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
import ch.tutteli.tsphp.common.TSPHPAst;
import ch.tutteli.tsphp.common.exceptions.DefinitionException;
import ch.tutteli.tsphp.common.exceptions.ReferenceException;
import java.util.ArrayList;
import java.util.List;
import org.antlr.runtime.Token;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class ErrorHelper implements IErrorHelper
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
    public void determineAlreadyDefinedException(ISymbol symbol1, ISymbol symbol2) {
        determineAlreadyDefinedException(symbol1.getDefinitionAst(), symbol2.getDefinitionAst());

    }

    @Override
    public void determineAlreadyDefinedException(ITSPHPAst ast1, ITSPHPAst ast2) {
        if (ast1.isDefinedEarlierThan(ast2)) {
            addAlreadyDefinedException(ast1, ast2);
        } else {
            addAlreadyDefinedException(ast2, ast1);
        }

    }

    @Override
    public void addAlreadyDefinedException(ISymbol existingSymbol, ISymbol newSymbol) {
        addAlreadyDefinedException(existingSymbol.getDefinitionAst(), newSymbol.getDefinitionAst());
    }

    @Override
    public void addAlreadyDefinedException(ITSPHPAst existingDefintion, ITSPHPAst newDefinition) {


        String errorMessage = errorMessageProvider.getErrorDefinitionMessage("alreadyDefined",
                new DefinitionErrorDto(newDefinition.getText(),
                existingDefintion.getLine(), existingDefintion.getCharPositionInLine(),
                newDefinition.getLine(), newDefinition.getCharPositionInLine()));

        exceptions.add(new DefinitionException(errorMessage, existingDefintion, newDefinition));
    }

    @Override
    public DefinitionException addAndGetUseForwardReferenceException(ITSPHPAst typeAst, ITSPHPAst useDefinition) {

        String errorMessage = errorMessageProvider.getErrorDefinitionMessage("",
                new DefinitionErrorDto(typeAst.getText(),
                useDefinition.getLine(), useDefinition.getCharPositionInLine(),
                typeAst.getLine(), typeAst.getCharPositionInLine()));

        DefinitionException exception = new DefinitionException(errorMessage, typeAst, useDefinition);
        exceptions.add(exception);
        return exception;
    }

    @Override
    public ReferenceException addAndGetUnkownTypeException(ITSPHPAst typeAst) {
        String errorMessage = errorMessageProvider.getErrorReferenceMessage("unkownType",
                new ReferenceErrorDto(typeAst.getText(), typeAst.getLine(), typeAst.getCharPositionInLine()));
        ReferenceException exception = new ReferenceException(errorMessage, typeAst);
        exceptions.add(exception);
        return exception;
    }
}
