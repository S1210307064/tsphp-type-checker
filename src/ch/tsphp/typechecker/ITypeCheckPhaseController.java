/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tsphp.typechecker.symbols.erroneous.IErroneousTypeSymbol;

/**
 * Represents the interface between the TSPHPTypeCheckWalker (ANTLR generated) and the logic.
 */
public interface ITypeCheckPhaseController
{
    IMethodSymbol resolveFunctionCall(ITSPHPAst identifier, ITSPHPAst arguments);

    IMethodSymbol resolveMethodCall(ITSPHPAst callee, ITSPHPAst identifier, ITSPHPAst arguments);

    IMethodSymbol resolveStaticMethodCall(ITSPHPAst callee, ITSPHPAst identifier, ITSPHPAst arguments);

    ITypeSymbol resolveBinaryOperatorEvalType(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right);

    ITypeSymbol resolveUnaryOperatorEvalType(ITSPHPAst operator, ITSPHPAst expression);

    ITypeSymbol resolveTernaryOperatorEvalType(ITSPHPAst operator, ITSPHPAst condition,
            ITSPHPAst caseTrue, ITSPHPAst caseFalse);

    ITypeSymbol resolveReturnTypeArrayAccess(ITSPHPAst statement, ITSPHPAst expression, ITSPHPAst index);

    void checkEquality(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right);

    void checkIdentity(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right);

    void checkAssignment(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right);

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

    void checkCatch(ITSPHPAst catchRoot, ITSPHPAst variableId);

    void checkReturn(ITSPHPAst returnRoot, ITSPHPAst expression);

    void checkInitialValue(ITSPHPAst variableId, ITSPHPAst expression);

    void checkConstantInitialValue(ITSPHPAst variableId, ITSPHPAst expression);

    void checkClassMemberInitialValue(ITSPHPAst variableId, ITSPHPAst expression);

    void checkEcho(ITSPHPAst expression);

    void checkClone(ITSPHPAst clone, ITSPHPAst expression);

    void checkInstanceof(ITSPHPAst operator, ITSPHPAst expression, ITSPHPAst typeAst);

    void checkNew(ITSPHPAst identifier, ITSPHPAst arguments);

    void checkPolymorphism(ITSPHPAst identifier);

    void addDefaultValue(ITSPHPAst variableId);

    IErroneousTypeSymbol createErroneousTypeForMissingSymbol(ITSPHPAst identifier);
}