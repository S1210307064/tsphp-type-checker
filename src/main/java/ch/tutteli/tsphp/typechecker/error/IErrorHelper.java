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

import ch.tutteli.tsphp.common.IErrorReporter;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.TSPHPAst;
import ch.tutteli.tsphp.common.exceptions.DefinitionException;
import ch.tutteli.tsphp.common.exceptions.ReferenceException;
import java.util.List;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public interface IErrorHelper extends IErrorReporter
{

    TSPHPAst recoverFromTypeClash(TSPHPAst ast1, TSPHPAst ast2);

    TSPHPAst addAlreadyDefinedExceptionAndRecover(TSPHPAst ast1, TSPHPAst ast2);

    void addAlreadyDefinedException(ISymbol existingSymbol, ISymbol newSymbol);

    void addAlreadyDefinedException(TSPHPAst existingDefintion, TSPHPAst newDefinition);

    void addAlreadyDefinedException(List<TSPHPAst> definitionAsts);

    DefinitionException addUseForwardReferenceException(TSPHPAst typeAst, TSPHPAst useDefinition);

    public ReferenceException addAndGetUnkownTypeException(TSPHPAst typeAst);
}
