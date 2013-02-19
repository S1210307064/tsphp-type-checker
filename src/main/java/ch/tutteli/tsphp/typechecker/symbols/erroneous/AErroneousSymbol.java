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
package ch.tutteli.tsphp.typechecker.symbols.erroneous;

import ch.tutteli.tsphp.common.ASymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.exceptions.TypeCheckerException;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public abstract class AErroneousSymbol extends ASymbol implements IErroneousSymbol
{

    private TypeCheckerException exception;

    public AErroneousSymbol(ITSPHPAst ast, TypeCheckerException theException) {
        super(ast, ast.getText());
    }
    
    @Override
    public TypeCheckerException getException() {
        return exception;
    }
}