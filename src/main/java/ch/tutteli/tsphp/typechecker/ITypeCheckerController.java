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

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IScalarTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IVariableSymbol;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public interface ITypeCheckerController
{

    IDefiner getDefiner();

    ISymbolTable getSymbolTable();

    boolean checkIsInterface(ITSPHPAst typeAst, ITypeSymbol symbol);

    boolean checkIsClass(ITSPHPAst typeAst, ITypeSymbol symbol);

    boolean checkForwardReference(ITSPHPAst ast);

    boolean checkOutOfConditionalScope(ITSPHPAst ast);

    IVariableSymbol resolveConstant(ITSPHPAst ast);

    IMethodSymbol resolveStaticMethod(ITSPHPAst callee, ITSPHPAst id);

    IVariableSymbol resolveStaticMember(ITSPHPAst accessor, ITSPHPAst id);

    IVariableSymbol resolveClassConstant(ITSPHPAst accessor, ITSPHPAst id);

    IMethodSymbol resolveFunction(ITSPHPAst ast);

    IMethodSymbol resolveMethod(ITSPHPAst callee, ITSPHPAst id);

    IVariableSymbol resolveThisSelf(ITSPHPAst $this);

    IVariableSymbol resolveParent(ITSPHPAst $this);

    IVariableSymbol resolveVariable(ITSPHPAst ast);

    ITypeSymbol resolveUseType(ITSPHPAst typeAst, ITSPHPAst alias);

    /**
     * Try to resolve the type for the given typeAst and returns an {@link TSPHPErroneusTypeSymbol} if the type could
     * not be found.
     *
     * @param typeAst The AST node which contains the type name. For instance, int, MyClass, \Exception etc.
     * @return The corresponding type or a {@link TSPHPErroneusTypeSymbol} if could not be found.
     */
    ITypeSymbol resolveType(ITSPHPAst typeAst);

    IScalarTypeSymbol resolveScalarType(ITSPHPAst typeAst, boolean isNullable);

    ITypeSymbol resolvePrimitiveType(ITSPHPAst typeASt);

    void checkBreakContinueLevel(ITSPHPAst root, ITSPHPAst expression);

    ITypeSymbol resolveBinaryOperatorEvalType(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right);

    ITypeSymbol resolveUnaryOperatorEvalType(ITSPHPAst operator, ITSPHPAst expression);

    ITypeSymbol resolveTernaryOperatorEvalType(ITSPHPAst operator, ITSPHPAst condition,
            ITSPHPAst caseTrue, ITSPHPAst caseFalse);

    ITypeSymbol resolveReturnTypeArrayAccess(ITSPHPAst statement, ITSPHPAst expression, ITSPHPAst index);

    ITypeSymbol resolveReturnTypeClassMemberAccess(ITSPHPAst statement, ITSPHPAst expression, ITSPHPAst identifier);
 
    void checkFunctionCall(ITSPHPAst identifier, ITSPHPAst arguments);
    
    void checkMethodCall(ITSPHPAst identifier, ITSPHPAst arguments);
    
    void checkEquality(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right);

    void checkIdentity(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right);

    void checkAssignment(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right);

    void checkPrePostIncrementDecrement(ITSPHPAst operator, ITSPHPAst expression);

    void checkCast(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right);

    void checkCastAssignment(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right);

    void checkIf(ITSPHPAst statement, ITSPHPAst expression);

    void checkSwitch(ITSPHPAst statement, ITSPHPAst expression);

    void checkSwitchCase(ITSPHPAst statement, ITSPHPAst expression);

    void checkFor(ITSPHPAst statement, ITSPHPAst expression);

    void checkForeach(ITSPHPAst foreachRoot, ITSPHPAst array, ITSPHPAst keyVariableId, ITSPHPAst valueVariableId);

    void checkWhile(ITSPHPAst whileRoot, ITSPHPAst expression);

    void checkDoWhile(ITSPHPAst doWhileRoot, ITSPHPAst expression);

    void checkThrow(ITSPHPAst throwRoot, ITSPHPAst expression);

    void checkCatch(ITSPHPAst castRoot, ITSPHPAst variableId);

    void checkReturn(ITSPHPAst returnRoot, ITSPHPAst expression);

    void checkInitialValue(ITSPHPAst variableId, ITSPHPAst expression);

    void checkConstantInitialValue(ITSPHPAst variableId, ITSPHPAst expression);

    void checkEcho(ITSPHPAst expression);

    void checkClone(ITSPHPAst clone, ITSPHPAst expression);

    void checkInstanceof(ITSPHPAst operator, ITSPHPAst expression, ITSPHPAst typeAst);
   
}