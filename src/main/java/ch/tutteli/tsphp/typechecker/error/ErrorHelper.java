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
    public void addAlreadyDefinedException(List<TSPHPAst> definitionAsts) {
        TSPHPAst firstDefinition = definitionAsts.get(0);
        int size = definitionAsts.size();
        for (int i = 1; i < size; ++i) {
            addAlreadyDefinedException(firstDefinition, definitionAsts.get(i));
        }
    }

    @Override
    public void addAlreadyDefinedException(ISymbol existingSymbol, ISymbol newSymbol) {
        addAlreadyDefinedException(existingSymbol.getDefinitionAst(), newSymbol.getDefinitionAst());
    }

    @Override
    public void addAlreadyDefinedException(TSPHPAst existingDefintion, TSPHPAst newDefinition) {
        Token newToken = newDefinition.token;
        Token existingToken = existingDefintion.token;

        String errorMessage = errorMessageProvider.getErrorDefinitionMessage("alreadyDefined",
                new DefinitionErrorDto(newDefinition.getText(),
                existingToken.getLine(), existingToken.getCharPositionInLine(),
                newToken.getLine(), newToken.getCharPositionInLine()));

        exceptions.add(new DefinitionException(errorMessage, existingDefintion, newDefinition));
    }

    @Override
    public TSPHPAst addAlreadyDefinedExceptionAndRecover(TSPHPAst ast1, TSPHPAst ast2) {
        TSPHPAst existingDefinition;
        if (isAst1DefinedBeforeAst2(ast1, ast2)) {
            existingDefinition = ast1;
            addAlreadyDefinedException(ast1, ast2);
        } else {
            existingDefinition = ast2;
            addAlreadyDefinedException(ast2, ast1);
        }
        return existingDefinition;
    }

    @Override
    public DefinitionException addUseForwardReferenceException(TSPHPAst typeAst, TSPHPAst useDefinition) {

        String errorMessage = errorMessageProvider.getErrorDefinitionMessage("",
                new DefinitionErrorDto(typeAst.getText(),
                useDefinition.getLine(), useDefinition.getCharPositionInLine(),
                typeAst.getLine(), typeAst.getCharPositionInLine()));

        DefinitionException exception = new DefinitionException(errorMessage, typeAst, useDefinition);
        exceptions.add(exception);
        return exception;
    }

    @Override
    public ReferenceException addAndGetUnkownTypeException(TSPHPAst typeAst) {
        String errorMessage = errorMessageProvider.getErrorReferenceMessage("unkownType",
                new ReferenceErrorDto(typeAst.getText(), typeAst.getLine(), typeAst.getCharPositionInLine()));
        ReferenceException exception = new ReferenceException(errorMessage, typeAst);
        exceptions.add(exception);
        return exception;
    }

    @Override
    public TSPHPAst recoverFromTypeClash(TSPHPAst ast1, TSPHPAst ast2) {
        return isAst1DefinedBeforeAst2(ast1, ast2) ? ast1 : ast2;
    }

    private boolean isAst1DefinedBeforeAst2(TSPHPAst ast1, TSPHPAst ast2) {
        return ast1.token.getLine() < ast2.token.getLine()
                || (ast1.token.getLine() == ast2.token.getLine()
                && ast1.token.getCharPositionInLine() <= ast2.token.getCharPositionInLine());
    }
}
