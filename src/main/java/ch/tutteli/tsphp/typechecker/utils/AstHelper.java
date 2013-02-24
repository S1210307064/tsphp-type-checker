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
package ch.tutteli.tsphp.typechecker.utils;

import ch.tutteli.tsphp.common.AstHelperRegistry;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITSPHPAstAdaptor;
import ch.tutteli.tsphp.typechecker.ParameterPromotionDto;
import static ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker.*;
import ch.tutteli.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.INamespaceScope;
import ch.tutteli.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.ITypeSymbolWithPHPBuiltInCasting;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class AstHelper implements IAstHelper
{

    private ITSPHPAstAdaptor astAdaptor;

    public AstHelper(ITSPHPAstAdaptor theAstAdaptor) {
        astAdaptor = theAstAdaptor;
    }

    @Override
    public ITSPHPAst createAst(int tokenType, String name) {
        return (ITSPHPAst) astAdaptor.create(tokenType, new CommonToken(tokenType, name));
    }

    @Override
    public ITSPHPAst prependCasting(ParameterPromotionDto dto) {
        ITSPHPAst parent = (ITSPHPAst) dto.actualParameter.getParent();

        //save child index, since it is going to change during rewrite
        int childIndex = dto.actualParameter.getChildIndex();
        ITSPHPAst actualParameter = dto.actualParameter;
        for (IMethodSymbol methodSymbol : dto.castingMethods) {
            ITSPHPAst call = astAdaptor.create(actualParameter);
            if (methodSymbol.getEnclosingScope() instanceof IGlobalNamespaceScope) {
                rewriteToCast(call, actualParameter, methodSymbol);
            } else {
                rewriteToStaticMethodCall(call, actualParameter, methodSymbol);
            }
            call.setEvalType(methodSymbol.getType());
            actualParameter = call;
        }
        actualParameter.setParent(parent);
        parent.replaceChildren(childIndex, childIndex, actualParameter);

        return actualParameter;
    }

    private void rewriteToCast(ITSPHPAst call, ITSPHPAst actualParameter, IMethodSymbol castingMethod) {
        //^(CASTING Type expression)
        ITypeSymbolWithPHPBuiltInCasting typeSymbol = (ITypeSymbolWithPHPBuiltInCasting) castingMethod.getType();

        Token token = call.getToken();
        token.setType(CASTING);
        token.setText("casting");
        ITSPHPAst type = createAst(typeSymbol.getTokenTypeForCasting(), typeSymbol.getName());
        call.addChild(type);
        call.addChild(actualParameter);
    }

    private void rewriteToStaticMethodCall(ITSPHPAst call, ITSPHPAst expression, IMethodSymbol castingMethod) {
        //^(METHOD_CALL_STATIC TYPE_NAME Identifier ^(ACTUAL_PARAMETERS)) 
        Token token = call.getToken();
        token.setType(METHOD_CALL_STATIC);
        token.setText("smCall");
        ITSPHPAst typeName = createAst(TYPE_NAME, getAbsoluteClassName(castingMethod));
        call.addChild(typeName);
        ITSPHPAst identifier = createAst(Identifier, castingMethod.getName() + "()");
        call.addChild(identifier);
        ITSPHPAst actualParameters = createAst(ACTUAL_PARAMETERS, "args");
        actualParameters.addChild(expression);
        call.addChild(actualParameters);
    }

    private String getAbsoluteClassName(IMethodSymbol castingMethod) {
        IClassTypeSymbol classSymbol = (IClassTypeSymbol) castingMethod.getEnclosingScope();
        INamespaceScope namespaceScope = (INamespaceScope) classSymbol.getEnclosingScope();
        return "\\" + namespaceScope.getScopeName() + "\\" + classSymbol.getName();

    }
}
