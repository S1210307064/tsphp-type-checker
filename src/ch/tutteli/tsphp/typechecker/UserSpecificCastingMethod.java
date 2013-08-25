/*
 * Copyright 2013 Robert Stoll <rstoll@tutteli.ch>
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

import ch.tutteli.tsphp.common.IAstHelper;
import ch.tutteli.tsphp.common.ITSPHPAst;
import static ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker.ACTUAL_PARAMETERS;
import static ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker.Identifier;
import static ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker.METHOD_CALL_STATIC;
import static ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker.TYPE_NAME;
import ch.tutteli.tsphp.typechecker.scopes.INamespaceScope;
import ch.tutteli.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.ITypeSymbolWithPHPBuiltInCasting;
import org.antlr.runtime.Token;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class UserSpecificCastingMethod extends ACastingMethod implements ICastingMethod
{

    private IMethodSymbol methodSymbol;

    public UserSpecificCastingMethod(IAstHelper theAstHelper, ITypeSymbolWithPHPBuiltInCasting theType,
            IMethodSymbol theMethodSymbol) {
        super(theAstHelper, theType);
        methodSymbol = theMethodSymbol;
    }

    @Override
    protected int getTokenType() {
        return ((ITypeSymbolWithPHPBuiltInCasting) typeSymbol).getTokenTypeForCasting();
    }

    @Override
    public ITSPHPAst createCastAst(ITSPHPAst ast) {
        //create the cast based on the given (to take the given position etc.)
        ITSPHPAst cast = astHelper.createAst(ast);

        //^(METHOD_CALL_STATIC TYPE_NAME Identifier ^(ACTUAL_PARAMETERS)) 
        Token token = ast.getToken();
        token.setType(METHOD_CALL_STATIC);
        token.setText("smCall");
        ITSPHPAst typeName = astHelper.createAst(TYPE_NAME, getAbsoluteClassName(methodSymbol));
        cast.addChild(typeName);
        ITSPHPAst identifier = astHelper.createAst(Identifier, methodSymbol.getName() + "()");
        cast.addChild(identifier);
        ITSPHPAst actualParameters = astHelper.createAst(ACTUAL_PARAMETERS, "args");
        actualParameters.addChild(ast);
        cast.addChild(actualParameters);
        return cast;
    }

    private String getAbsoluteClassName(IMethodSymbol castingMethod) {
        IClassTypeSymbol classSymbol = (IClassTypeSymbol) castingMethod.getEnclosingScope();
        INamespaceScope namespaceScope = (INamespaceScope) classSymbol.getEnclosingScope();
        return namespaceScope.getScopeName() + classSymbol.getName();
    }
}
