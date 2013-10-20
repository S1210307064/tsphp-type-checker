package ch.tutteli.tsphp.typechecker;

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IScalarTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IVariableSymbol;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.ErroneousTypeSymbol;

public interface ITypeCheckerController
{

    IDefiner getDefiner();

    ITypeSystem getTypeSystem();

    boolean checkIsInterface(ITSPHPAst typeAst, ITypeSymbol symbol);

    boolean checkIsClass(ITSPHPAst typeAst, ITypeSymbol symbol);

    boolean checkIsForwardReference(ITSPHPAst ast);

    boolean checkIsOutOfConditionalScope(ITSPHPAst ast);

    IVariableSymbol resolveConstant(ITSPHPAst ast);

    IVariableSymbol resolveStaticMember(ITSPHPAst accessor, ITSPHPAst id);

    IVariableSymbol resolveClassConstant(ITSPHPAst accessor, ITSPHPAst id);

    IVariableSymbol resolveClassMemberAccess(ITSPHPAst expression, ITSPHPAst identifier);

    IMethodSymbol resolveFunctionCall(ITSPHPAst identifier, ITSPHPAst arguments);

    IMethodSymbol resolveMethodCall(ITSPHPAst callee, ITSPHPAst identifier, ITSPHPAst arguments);

    IMethodSymbol resolveStaticMethodCall(ITSPHPAst callee, ITSPHPAst identifier, ITSPHPAst arguments);

    IVariableSymbol resolveThisSelf(ITSPHPAst $this);

    IVariableSymbol resolveParent(ITSPHPAst $this);

    IVariableSymbol resolveVariable(ITSPHPAst ast);

    ITypeSymbol resolveUseType(ITSPHPAst typeAst, ITSPHPAst alias);

    /**
     * Try to resolve the type for the given typeAst and returns an {@link ErroneousTypeSymbol} if the type could
     * not be found.
     *
     * @param typeAst The AST node which contains the type name. For instance, int, MyClass, \Exception etc.
     * @return The corresponding type or a {@link ErroneousTypeSymbol} if could not be found.
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

    void checkCatch(ITSPHPAst castRoot, ITSPHPAst variableId);

    void checkReturn(ITSPHPAst returnRoot, ITSPHPAst expression);

    void checkInitialValue(ITSPHPAst variableId, ITSPHPAst expression);

    void checkConstantInitialValue(ITSPHPAst variableId, ITSPHPAst expression);

    void checkEcho(ITSPHPAst expression);

    void checkClone(ITSPHPAst clone, ITSPHPAst expression);

    void checkInstanceof(ITSPHPAst operator, ITSPHPAst expression, ITSPHPAst typeAst);

    void checkNew(ITSPHPAst identifier, ITSPHPAst arguments);

    void addDefaultValue(ITSPHPAst variableId);
}