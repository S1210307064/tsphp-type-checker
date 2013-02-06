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
package ch.tutteli.tsphp.typechecker;

import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.TSPHPAst;
import ch.tutteli.tsphp.typechecker.symbols.IClassSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tutteli.tsphp.typechecker.symbols.IVariableSymbol;
import org.antlr.runtime.CommonToken;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class DefinitionHelper implements IDefinitionHelper
{

    private ISymbolFactory symbolFactory;

    public DefinitionHelper(ISymbolFactory aSymbolFactory) {
        symbolFactory = aSymbolFactory;
    }

    @Override
    public IScope defineClass(IScope currentScope, TSPHPAst modifier, TSPHPAst identifier, TSPHPAst extendsIds, TSPHPAst implementsIds) {
        assignScopeToIdentifiers(currentScope, extendsIds);
        assignScopeToIdentifiers(currentScope, implementsIds);
        IClassSymbol classSymbol = symbolFactory.createClassSymbol(modifier, identifier, currentScope);
        identifier.symbol = classSymbol;
        currentScope.define(classSymbol);
        return classSymbol;
    }

    @Override
    public IScope defineMethod(IScope currentScope, TSPHPAst methodModifier,
            TSPHPAst returnTypeModifier, TSPHPAst returnType, TSPHPAst identifier) {
        returnType.scope = currentScope;
        IMethodSymbol methodSymbol = symbolFactory.createMethodSymbol(methodModifier, returnTypeModifier, identifier, currentScope);
        identifier.symbol = methodSymbol;
        currentScope.define(methodSymbol);
        return methodSymbol;
    }

    @Override
    public void defineConstant(IScope currentScope, TSPHPAst type, TSPHPAst identifier) {
        TSPHPAst modifier = new TSPHPAst();
        modifier.addChild(new TSPHPAst(new CommonToken(TSPHPTypeCheckerDefinition.Final)));
        defineVariable(currentScope, modifier, type, identifier);
    }

    @Override
    public void defineVariable(IScope currentScope, TSPHPAst modifier, TSPHPAst type, TSPHPAst variableId) {
        type.scope = currentScope;
        IVariableSymbol variableSymbol = symbolFactory.createVariableSymbol(modifier, variableId);
        variableId.symbol = variableSymbol;
        currentScope.define(variableSymbol);
    }

    private void assignScopeToIdentifiers(IScope currentScope, TSPHPAst identifierList) {
        int lenght = identifierList.getChildCount();
        for (int i = 0; i < lenght; ++i) {
            TSPHPAst ast = (TSPHPAst) identifierList.getChild(i);
            ast.scope = currentScope;
        }
    }
}
