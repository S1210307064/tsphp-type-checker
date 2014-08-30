/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker;

import ch.tsphp.common.AstHelperRegistry;
import ch.tsphp.common.ISymbol;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.ITypeSymbol;
import ch.tsphp.common.exceptions.DefinitionException;
import ch.tsphp.common.exceptions.ReferenceException;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.common.exceptions.TypeCheckerException;
import ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tsphp.typechecker.error.ITypeCheckerErrorReporter;
import ch.tsphp.typechecker.symbols.IArrayTypeSymbol;
import ch.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tsphp.typechecker.symbols.IPolymorphicTypeSymbol;
import ch.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tsphp.typechecker.symbols.ISymbolWithAccessModifier;
import ch.tsphp.typechecker.symbols.IVariableSymbol;
import ch.tsphp.typechecker.symbols.IVoidTypeSymbol;
import ch.tsphp.typechecker.symbols.erroneous.IErroneousMethodSymbol;
import ch.tsphp.typechecker.symbols.erroneous.IErroneousSymbol;
import ch.tsphp.typechecker.symbols.erroneous.IErroneousTypeSymbol;
import ch.tsphp.typechecker.symbols.erroneous.IErroneousVariableSymbol;
import ch.tsphp.typechecker.utils.ITypeCheckerAstHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TypeCheckPhaseController implements ITypeCheckPhaseController
{

    private final ISymbolFactory symbolFactory;
    private final ISymbolResolver symbolResolver;
    private final ITypeCheckerErrorReporter typeCheckErrorReporter;
    private final ITypeSystem typeSystem;
    private final IOverloadResolver overloadResolver;
    private final IAccessResolver accessResolver;
    private final ITypeCheckerAstHelper astHelper;

    private Map<Integer, List<IMethodSymbol>> unaryOperators = new HashMap<>();
    private Map<Integer, List<IMethodSymbol>> binaryOperators = new HashMap<>();

    @SuppressWarnings("checkstyle:parameternumber")
    public TypeCheckPhaseController(
            ISymbolFactory theSymbolFactory,
            ISymbolResolver theSymbolResolver,
            ITypeCheckerErrorReporter theTypeCheckerErrorReporter,
            ITypeSystem theTypeSystem,
            IOverloadResolver theMethodResolver,
            IAccessResolver theAccessResolver,
            ITypeCheckerAstHelper theAstHelper) {

        symbolFactory = theSymbolFactory;
        typeSystem = theTypeSystem;
        typeCheckErrorReporter = theTypeCheckerErrorReporter;
        symbolResolver = theSymbolResolver;
        overloadResolver = theMethodResolver;
        accessResolver = theAccessResolver;
        astHelper = theAstHelper;

        unaryOperators = typeSystem.getUnaryOperators();
        binaryOperators = typeSystem.getBinaryOperators();
    }


    private IMethodSymbol getEnclosingMethod(ITSPHPAst ast) {
        IMethodSymbol methodSymbol = symbolResolver.getEnclosingMethod(ast);
        if (methodSymbol == null) {
            ReferenceException ex = typeCheckErrorReporter.notInMethod(ast);
            methodSymbol = symbolFactory.createErroneousMethodSymbol(ast, ex);
        }
        return methodSymbol;
    }

    @Override
    public ITypeSymbol resolveBinaryOperatorEvalType(final ITSPHPAst operator, final ITSPHPAst left,
            final ITSPHPAst right) {

        IAmbiguousCallReporter caller = new IAmbiguousCallReporter()
        {
            @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
            @Override
            public void report(AmbiguousCallException exception) {
                typeCheckErrorReporter.ambiguousBinaryOperatorUsage(operator, left, right, exception);
            }
        };
        IWrongOperatorUsageReporter wrongOperatorUsageCaller = new IWrongOperatorUsageReporter()
        {
            @Override
            public ReferenceException report(List<IMethodSymbol> methods) {
                return typeCheckErrorReporter.wrongBinaryOperatorUsage(operator, left, right, methods);
            }
        };

        return resolveOperatorEvalType(new OperatorResolvingDto(operator, binaryOperators,
                new BinaryActualParameterGetter(left, right),
                new BinaryOperatorErroneousChecker(left.getEvalType(), right.getEvalType()),
                caller,
                wrongOperatorUsageCaller));

    }

    private ITypeSymbol resolveOperatorEvalType(OperatorResolvingDto dto) {
        ITypeSymbol typeSymbol;

        IErroneousTypeSymbol erroneousTypeSymbol = dto.erroneousChecker.getErroneousTypeSymbol();
        if (erroneousTypeSymbol == null) {
            if (dto.operators.containsKey(dto.operator.getToken().getType())) {
                try {
                    typeSymbol = resolveEvalType(dto);
                } catch (TypeCheckerException ex) {
                    typeSymbol = symbolFactory.createErroneousTypeSymbol(dto.operator, ex);
                }
            } else {
                TypeCheckerException exception = typeCheckErrorReporter.unsupportedOperator(dto.operator);
                typeSymbol = symbolFactory.createErroneousTypeSymbol(dto.operator, exception);
            }

        } else {
            typeSymbol = erroneousTypeSymbol;
        }
        return typeSymbol;
    }

    private ITypeSymbol resolveEvalType(OperatorResolvingDto dto) throws TypeCheckerException {

        ITypeSymbol typeSymbol;
        List<IMethodSymbol> methods = dto.operators.get(dto.operator.getToken().getType());
        List<ITSPHPAst> actualParameters = dto.actualParameterGetter.getActualParameters();
        OverloadDto overloadDto = getMostSpecificOverload(methods, actualParameters, dto.ambiguousCallReporter);

        if (overloadDto != null) {
            typeSymbol = overloadDto.methodSymbol.getType();
        } else {
            throw dto.wrongOperatorUsageReporter.report(methods);
        }
        return typeSymbol;
    }

    private OverloadDto getMostSpecificOverload(List<IMethodSymbol> methods, List<ITSPHPAst> actualParameters,
            IAmbiguousCallReporter ambiguousCallReporter) {

        OverloadDto overloadDto = null;
        List<OverloadDto> goodMethods = overloadResolver.getApplicableOverloads(methods, actualParameters);

        if (!goodMethods.isEmpty()) {
            try {
                overloadDto = overloadResolver.getMostSpecificApplicableOverload(goodMethods);
            } catch (AmbiguousCallException ex) {
                ambiguousCallReporter.report(ex);
                overloadDto = ex.getAmbiguousOverloads().get(0);
            }
        }
        if (overloadDto != null) {
            if (overloadDto.parametersNeedCasting != null) {
                addCastingsToAst(overloadDto.parametersNeedCasting);
            }
        }
        return overloadDto;
    }

    private void addCastingsToAst(List<CastingDto> parametersNeedCasting) {
        for (CastingDto parameterPromotionDto : parametersNeedCasting) {
            astHelper.prependCasting(parameterPromotionDto);
        }
    }

    @Override
    public ITypeSymbol resolveUnaryOperatorEvalType(final ITSPHPAst operator, final ITSPHPAst expression) {
        IAmbiguousCallReporter ambiguousCallReporter = new IAmbiguousCallReporter()
        {
            @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
            @Override
            public void report(AmbiguousCallException exception) {
                typeCheckErrorReporter.ambiguousUnaryOperatorUsage(operator, expression, exception);
            }
        };
        IWrongOperatorUsageReporter wrongOperatorUsageReporter = new IWrongOperatorUsageReporter()
        {
            @Override
            public ReferenceException report(List<IMethodSymbol> methods) {
                return typeCheckErrorReporter.wrongUnaryOperatorUsage(operator, expression, methods);
            }
        };

        return resolveOperatorEvalType(new OperatorResolvingDto(
                operator,
                unaryOperators,
                new UnaryActualParameterGetter(expression),
                new UnaryOperatorErroneousChecker(expression.getEvalType()),
                ambiguousCallReporter, wrongOperatorUsageReporter));
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public ITypeSymbol resolveTernaryOperatorEvalType(ITSPHPAst operator, ITSPHPAst condition,
            ITSPHPAst caseTrue, ITSPHPAst caseFalse) {

        checkTernaryCondition(operator, condition);
        ITypeSymbol caseTrueType = caseTrue.getEvalType();
        ITypeSymbol caseFalseType = caseFalse.getEvalType();
        ITypeSymbol typeSymbol = caseTrueType;

        if (areNotErroneousTypes(caseTrueType, caseFalseType)) {
            if (areNotSameAndNoneIsSubType(caseTrue, caseFalse)) {
                typeCheckErrorReporter.wrongTypeTernaryCases(caseTrue, caseFalse);
            } else {
                int promotionLevel = overloadResolver.getPromotionLevelFromTo(caseTrueType, caseFalseType);
                if (caseFalseTypeIsParentType(promotionLevel)) {
                    typeSymbol = caseFalseType;
                }
            }
        } else if (!(caseFalseType instanceof IErroneousTypeSymbol)) {
            typeSymbol = caseFalseType;
        }
        return typeSymbol;
    }

    private boolean caseFalseTypeIsParentType(int promotionLevel) {
        return promotionLevel > 0;
    }

    private void checkTernaryCondition(final ITSPHPAst operator, final ITSPHPAst condition) {
        final ITypeSymbol typeExpected = typeSystem.getBoolTypeSymbol();
        checkIsSameOrSubType(condition, typeExpected, new IErrorReporterCaller()
        {
            @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
            @Override
            public void callAppropriateMethod() {
                typeCheckErrorReporter.wrongTypeTernaryCondition(operator, condition, typeExpected);
            }
        });
    }

    @Override
    public ITypeSymbol resolveReturnTypeArrayAccess(ITSPHPAst statement, final ITSPHPAst expression,
            final ITSPHPAst index) {

        ITypeSymbol returnTypeArrayAccess;

        ITypeSymbol keyTypeSymbol = null;
        ITypeSymbol evalType = expression.getEvalType();
        if (!(evalType instanceof IErroneousSymbol)) {
            IArrayTypeSymbol arrayTypeSymbol = typeSystem.getArrayTypeSymbol();
            int promotionCount = overloadResolver.getPromotionLevelFromTo(evalType, arrayTypeSymbol);
            if (overloadResolver.isSameOrParentType(promotionCount)) {
                IArrayTypeSymbol arrayType = (IArrayTypeSymbol) evalType;
                keyTypeSymbol = arrayType.getKeyTypeSymbol();
                returnTypeArrayAccess = arrayType.getValueTypeSymbol();
            } else {
                ReferenceException exception = typeCheckErrorReporter.wrongTypeArrayAccess(
                        expression, arrayTypeSymbol);
                returnTypeArrayAccess = symbolFactory.createErroneousTypeSymbol(statement, exception);
            }
        } else {
            returnTypeArrayAccess = evalType;
        }

        if (keyTypeSymbol == null) {
            keyTypeSymbol = typeSystem.getStringTypeSymbol();
        }

        final ITypeSymbol typeSymbol = keyTypeSymbol;
        checkIsSameOrSubType(index, typeSymbol, new IErrorReporterCaller()
        {
            @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
            @Override
            public void callAppropriateMethod() {
                typeCheckErrorReporter.wrongArrayIndexType(expression, index, typeSymbol);
            }
        });

        return returnTypeArrayAccess;
    }

    @Override
    public IMethodSymbol resolveFunctionCall(ITSPHPAst identifier, ITSPHPAst arguments) {

        IMethodSymbol methodSymbol = (IMethodSymbol) symbolResolver.resolveGlobalIdentifierWithFallback(identifier);
        IWrongCallReporter wrongCallReporter = new IWrongCallReporter()
        {
            @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
            @Override
            public void report(ITSPHPAst identifier, List<ITSPHPAst> actualParameters, List<IMethodSymbol> methods) {
                typeCheckErrorReporter.wrongFunctionCall(identifier, actualParameters, methods);
            }
        };

        if (methodSymbol != null) {
            resolveCallOverload(identifier, arguments, methodSymbol, wrongCallReporter);
        } else {
            ReferenceException exception = typeCheckErrorReporter.notDefined(identifier);
            ITypeSymbol typeSymbol = symbolFactory.createErroneousTypeSymbol(identifier, exception);
            methodSymbol = symbolFactory.createErroneousMethodSymbol(identifier, exception);
            methodSymbol.setType(typeSymbol);
        }

        return methodSymbol;
    }

    private IMethodSymbol resolveCallOverload(final ITSPHPAst identifier, ITSPHPAst arguments,
            IMethodSymbol methodSymbol, IWrongCallReporter wrongCallReporter) {

        IMethodSymbol resolveMethodSymbol = methodSymbol;
        List<IMethodSymbol> methods = new ArrayList<>();
        methods.add(methodSymbol);
        OverloadDto overloadDto = resolveCallOverload(identifier, arguments, methods, wrongCallReporter);
        //error reporting if overloadDto == null happens in resolveCallOverload
        if (overloadDto != null) {
            resolveMethodSymbol = overloadDto.methodSymbol;
        }
        return resolveMethodSymbol;
    }

    @Override
    public IMethodSymbol resolveMethodCall(ITSPHPAst callee, ITSPHPAst identifier, ITSPHPAst arguments) {
        IMethodSymbol methodSymbol;

        ITypeSymbol typeSymbol = callee.getEvalType();
        if (!(typeSymbol instanceof IErroneousTypeSymbol)) {
            methodSymbol = checkCalleeAndResolveCall(typeSymbol, callee, identifier, arguments);
        } else {
            TSPHPException exception = ((IErroneousTypeSymbol) typeSymbol).getException();
            methodSymbol = symbolFactory.createErroneousMethodSymbol(identifier, exception);
            methodSymbol.setType(typeSymbol);
        }
        return methodSymbol;
    }

    private IMethodSymbol checkCalleeAndResolveCall(ITypeSymbol typeSymbol, ITSPHPAst callee,
            ITSPHPAst identifier, ITSPHPAst arguments) {
        IMethodSymbol methodSymbol;
        if (typeSymbol instanceof IPolymorphicTypeSymbol) {
            IPolymorphicTypeSymbol polymorphicTypeSymbol = (IPolymorphicTypeSymbol) typeSymbol;
            methodSymbol = (IMethodSymbol) polymorphicTypeSymbol.resolveWithFallbackToParent(identifier);

            IWrongCallReporter wrongCallReporter = new IWrongCallReporter()
            {
                @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
                @Override
                public void report(ITSPHPAst identifier, List<ITSPHPAst> actualParameters,
                        List<IMethodSymbol> methods) {
                    typeCheckErrorReporter.wrongMethodCall(identifier, actualParameters, methods);
                }
            };
            if (methodSymbol != null) {
                methodSymbol = resolveCallOverload(identifier, arguments, methodSymbol, wrongCallReporter);
                methodVisibilityCheck(methodSymbol, polymorphicTypeSymbol, callee, identifier);
            } else {
                DefinitionException exception = typeCheckErrorReporter.methodNotDefined(
                        callee, identifier);
                ITypeSymbol erroneousTypeSymbol = symbolFactory.createErroneousTypeSymbol(identifier, exception);
                methodSymbol = symbolFactory.createErroneousMethodSymbol(identifier, exception);
                methodSymbol.setType(erroneousTypeSymbol);
            }
        } else {
            ReferenceException exception = typeCheckErrorReporter.wrongTypeMethodCall(callee);
            ITypeSymbol erroneousTypeSymbol = symbolFactory.createErroneousTypeSymbol(identifier, exception);
            methodSymbol = symbolFactory.createErroneousMethodSymbol(identifier, exception);
            methodSymbol.setType(erroneousTypeSymbol);
        }
        return methodSymbol;
    }

    private void methodVisibilityCheck(IMethodSymbol methodSymbol, IPolymorphicTypeSymbol polymorphicTypeSymbol,
            ITSPHPAst callee, ITSPHPAst identifier) {

        IAccessResolver.IViolationCaller visibilityViolationCaller = new IAccessResolver.IViolationCaller()
        {
            @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
            @Override
            public void callAppropriateMethod(ITSPHPAst identifier, ISymbolWithAccessModifier symbol,
                    int accessedFrom) {
                typeCheckErrorReporter.visibilityViolationMethodCall(identifier, symbol, accessedFrom);
            }
        };

        accessResolver.checkVisibility(methodSymbol, polymorphicTypeSymbol, visibilityViolationCaller, callee,
                identifier);
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public IMethodSymbol resolveStaticMethodCall(ITSPHPAst callee, ITSPHPAst identifier, ITSPHPAst arguments) {
        IMethodSymbol symbol = resolveMethodCall(callee, identifier, arguments);
        if (!symbol.isStatic()) {
            typeCheckErrorReporter.notStatic(identifier);
        }
        return symbol;
    }

    private OverloadDto resolveCallOverload(final ITSPHPAst identifier, ITSPHPAst arguments,
            List<IMethodSymbol> methods, IWrongCallReporter wrongCallReporter) {

        OverloadDto overloadDto;

        List<ITSPHPAst> children = arguments.getChildren();
        final List<ITSPHPAst> actualParameters = children != null ? children : new ArrayList<ITSPHPAst>();
        IAmbiguousCallReporter ambiguousCallReporter = new IAmbiguousCallReporter()
        {
            @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
            @Override
            public void report(AmbiguousCallException exception) {
                typeCheckErrorReporter.ambiguousCall(identifier, exception, actualParameters);
            }
        };
        overloadDto = getMostSpecificOverload(methods, actualParameters, ambiguousCallReporter);
        if (overloadDto == null) {
            wrongCallReporter.report(identifier, actualParameters, methods);
        }

        return overloadDto;
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public void checkEquality(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right) {
        if (areNotErroneousTypes(left.getEvalType(), right.getEvalType()) && areNotSameAndNoneIsSubType(left, right)) {

            IVariableSymbol leftSymbol = getVariableSymbolFromExpression(left);
            IVariableSymbol rightSymbol = getVariableSymbolFromExpression(right);

            CastingDto leftToRight = overloadResolver.getCastingDtoAlwaysCasting(rightSymbol, left);
            CastingDto rightToLeft = overloadResolver.getCastingDtoAlwaysCasting(leftSymbol, right);

            if (haveBothSideCast(leftToRight, rightToLeft)) {
                typeCheckErrorReporter.operatorAmbiguousCasts(operator, left, right,
                        leftToRight, rightToLeft, leftToRight.ambiguousCasts, rightToLeft.ambiguousCasts);

            } else if (haveNoSideCast(leftToRight, rightToLeft)) {
                typeCheckErrorReporter.wrongEqualityUsage(operator, left, right);

            } else if (hasAmbiguousCast(rightToLeft)) {
                typeCheckErrorReporter.ambiguousCasts(operator, left, right, rightToLeft.ambiguousCasts);

            } else if (hasAmbiguousCast(leftToRight)) {
                typeCheckErrorReporter.ambiguousCasts(operator, left, right, leftToRight.ambiguousCasts);
            }
        }
    }

    private boolean haveBothSideCast(CastingDto leftToRight, CastingDto rightToLeft) {
        return leftToRight != null && rightToLeft != null;
    }

    private boolean haveNoSideCast(CastingDto leftToRight, CastingDto rightToLeft) {
        return leftToRight == null && rightToLeft == null;
    }

    private boolean hasAmbiguousCast(CastingDto castingDto) {
        return castingDto != null && castingDto.ambiguousCasts != null && !castingDto.ambiguousCasts.isEmpty();
    }

    private IVariableSymbol getVariableSymbolFromExpression(ITSPHPAst expression) {
        IVariableSymbol variableSymbol;

        ISymbol symbol = expression.getSymbol();
        if (symbol != null && symbol instanceof IVariableSymbol) {
            variableSymbol = (IVariableSymbol) symbol;
        } else {
            variableSymbol = symbolFactory.createVariableSymbol(null, expression);
            variableSymbol.setType(expression.getEvalType());
        }
        return variableSymbol;
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public void checkAssignment(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right) {
        ISymbol leftSymbol = getSymbolFromVariableOrField(left);
        if (leftSymbol != null && leftSymbol instanceof IVariableSymbol) {
            if (!(leftSymbol instanceof IErroneousVariableSymbol)
                    && areNotErroneousTypes(leftSymbol.getType(), right.getEvalType())) {
                CastingDto castingDto = overloadResolver.getCastingDto((IVariableSymbol) leftSymbol, right);
                if (castingDto != null) {
                    if (castingDto.castingMethods != null) {
                        astHelper.prependCasting(castingDto);
                    }
                    if (castingDto.ambiguousCasts != null) {
                        typeCheckErrorReporter.ambiguousCasts(
                                operator, left, right, castingDto.ambiguousCasts);
                    }
                } else {
                    typeCheckErrorReporter.wrongAssignment(operator, left, right);
                }
            }
        } else {
            typeCheckErrorReporter.variableExpected(left);
        }
    }

    private boolean areNotErroneousTypes(ITypeSymbol leftType, ITypeSymbol rightType) {
        return !(leftType instanceof IErroneousTypeSymbol) && !(rightType instanceof IErroneousTypeSymbol);
    }

    private ISymbol getSymbolFromVariableOrField(ITSPHPAst left) {
        ISymbol symbol;
        switch (left.getType()) {
            case TSPHPDefinitionWalker.VariableId:
                symbol = left.getSymbol();
                break;
            case TSPHPDefinitionWalker.CLASS_MEMBER_ACCESS:
            case TSPHPDefinitionWalker.CLASS_STATIC_ACCESS:
                symbol = left.getChild(1).getSymbol();
                break;
            default:
                symbol = null;
        }
        return symbol;
    }

    private boolean areNotSameAndNoneIsSubType(ITSPHPAst left, ITSPHPAst right) {
        IVariableSymbol leftSymbol = getVariableSymbolFromExpression(left);
        IVariableSymbol rightSymbol = getVariableSymbolFromExpression(right);

        boolean areNotSameAndNoneIsSubType = !overloadResolver.isSameOrParentTypeConsiderNull(rightSymbol, left);
        if (areNotSameAndNoneIsSubType) {
            areNotSameAndNoneIsSubType = !overloadResolver.isSameOrParentTypeConsiderNull(leftSymbol, right);
        }
        return areNotSameAndNoneIsSubType;
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public void checkIdentity(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right) {
        if (areNotErroneousTypes(left.getEvalType(), right.getEvalType()) && areNotSameAndNoneIsSubType(left, right)) {
            typeCheckErrorReporter.wrongIdentityUsage(operator, left, right);
        }

    }

    @Override
    public void checkCast(final ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right) {
        CastingDto castingDto = checkAndGetCast(operator, left, right);
        if (castingDto != null && castingDto.castingMethods != null) {
            ITSPHPAst newRoot = astHelper.prependCasting(castingDto);
            int childIndex = operator.getChildIndex();
            operator.getParent().replaceChildren(childIndex, childIndex, newRoot);
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private CastingDto checkAndGetCast(final ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right) {
        CastingDto castingDto = null;
        if (!(right.getEvalType() instanceof IErroneousTypeSymbol)) {
            operator.setText("==");

            ISymbol symbol = left.getSymbol();
            IVariableSymbol leftSymbol;
            if (symbol instanceof IVariableSymbol) {
                leftSymbol = (IVariableSymbol) symbol;
            } else {
                ITypeSymbol typeSymbol = (ITypeSymbol) symbol;
                left.setEvalType(typeSymbol);
                leftSymbol = symbolFactory.createVariableSymbol(null, left);
                leftSymbol.setType(typeSymbol);
            }

            castingDto = overloadResolver.getCastingDtoAlwaysCasting(leftSymbol, right);
            if (castingDto == null) {
                typeCheckErrorReporter.wrongCast(operator, left, right);
            } else if (castingDto.ambiguousCasts != null && !castingDto.ambiguousCasts.isEmpty()) {
                typeCheckErrorReporter.ambiguousCasts(operator, left, right, castingDto.ambiguousCasts);
            }
            operator.setText("casting");
        }
        return castingDto;
    }

    @Override
    public void checkCastAssignment(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right) {
        CastingDto castingDto = checkAndGetCast(operator, left, right);
        if (castingDto != null) {
            if (castingDto.castingMethods == null) {
                //even thought a casting is not really necessary we do one to be consistent
                ICastingMethod castingMethod = typeSystem.getStandardCastingMethod(left.getEvalType());
                castingDto.castingMethods = new ArrayList<>();
                castingDto.castingMethods.add(castingMethod);
            }
            astHelper.prependCasting(castingDto);
        }
        operator.setText("=");
        operator.getToken().setType(TSPHPDefinitionWalker.Assign);
    }

    @Override
    public void checkIf(final ITSPHPAst ifRoot, final ITSPHPAst expression) {
        final ITypeSymbol typeSymbol = typeSystem.getBoolTypeSymbol();
        checkIsSameOrSubType(expression, typeSymbol, new IErrorReporterCaller()
        {
            @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
            @Override
            public void callAppropriateMethod() {
                typeCheckErrorReporter.wrongTypeIf(ifRoot, expression, typeSymbol);
            }
        });
    }

    private void checkIsSameOrSubType(ITSPHPAst expression, ITypeSymbol typeSymbol,
            IErrorReporterCaller caller) {
        ITypeSymbol expressionType = expression.getEvalType();
        if (areNotErroneousTypes(expressionType, typeSymbol)) {
            int promotionCount = overloadResolver.getPromotionLevelFromToConsiderNull(expressionType, typeSymbol);
            if (!overloadResolver.isSameOrParentType(promotionCount)) {
                caller.callAppropriateMethod();
            }
        }
    }

    @Override
    public void checkSwitch(final ITSPHPAst switchRoot, final ITSPHPAst expression) {
        final ITypeSymbol typeSymbol = typeSystem.getStringNullableTypeSymbol();
        checkIsSameOrSubType(expression, typeSymbol, new IErrorReporterCaller()
        {
            @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
            @Override
            public void callAppropriateMethod() {
                typeCheckErrorReporter.wrongTypeSwitch(switchRoot, expression, typeSymbol);
            }
        });
    }

    @Override
    public void checkSwitchCase(final ITSPHPAst switchRoot, final ITSPHPAst switchCase) {
        final ITypeSymbol typeSymbol = switchRoot.getEvalType();
        checkIsSameOrSubType(switchCase, typeSymbol, new IErrorReporterCaller()
        {
            @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
            @Override
            public void callAppropriateMethod() {
                typeCheckErrorReporter.wrongTypeSwitchCase(switchRoot, switchCase, typeSymbol);
            }
        });
    }

    @Override
    public void checkFor(final ITSPHPAst forRoot, final ITSPHPAst expression) {
        final ITypeSymbol typeSymbol = typeSystem.getBoolTypeSymbol();
        checkIsSameOrSubType(expression, typeSymbol, new IErrorReporterCaller()
        {
            @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
            @Override
            public void callAppropriateMethod() {
                typeCheckErrorReporter.wrongTypeFor(forRoot, expression, typeSymbol);
            }
        });
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public void checkForeach(ITSPHPAst foreachRoot, ITSPHPAst array,
            ITSPHPAst keyVariableId, ITSPHPAst valueVariableId) {

        ITypeSymbol keyTypeSymbol = null;
        ITypeSymbol valueTypeSymbol = null;
        ITypeSymbol evalType = array.getEvalType();
        if (!(evalType instanceof IErroneousSymbol)) {
            IArrayTypeSymbol arrayTypeSymbol = typeSystem.getArrayTypeSymbol();
            int promotionCount = overloadResolver.getPromotionLevelFromTo(evalType, arrayTypeSymbol);
            if (overloadResolver.isSameOrParentType(promotionCount)) {
                IArrayTypeSymbol arrayType = (IArrayTypeSymbol) evalType;
                keyTypeSymbol = arrayType.getKeyTypeSymbol();
                valueTypeSymbol = arrayType.getValueTypeSymbol();
            } else {
                typeCheckErrorReporter.wrongTypeForeach(foreachRoot, array, arrayTypeSymbol);
            }
        }
        if (keyVariableId != null) {
            if (keyTypeSymbol == null) {
                keyTypeSymbol = typeSystem.getStringTypeSymbol();
            }
            checkIsSameOrParentType(keyVariableId, keyVariableId, keyTypeSymbol);
        }
        if (valueTypeSymbol != null) {
            checkIsSameOrParentType(valueVariableId, valueVariableId, valueTypeSymbol);
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void checkIsSameOrParentType(ITSPHPAst statement, ITSPHPAst expression, ITypeSymbol typeSymbol) {
        ITypeSymbol expressionType = expression.getEvalType();
        if (areNotErroneousTypes(expressionType, typeSymbol)) {
            if (!overloadResolver.isSameOrParentTypeConsiderNull(expressionType, typeSymbol)) {
                typeCheckErrorReporter.notSameOrParentType(statement, expression, typeSymbol);
            }
        }
    }

    @Override
    public void checkWhile(final ITSPHPAst whileRoot, final ITSPHPAst expression) {
        final ITypeSymbol typeSymbol = typeSystem.getBoolTypeSymbol();
        checkIsSameOrSubType(expression, typeSymbol, new IErrorReporterCaller()
        {
            @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
            @Override
            public void callAppropriateMethod() {
                typeCheckErrorReporter.wrongTypeWhile(whileRoot, expression, typeSymbol);
            }
        });
    }

    @Override
    public void checkDoWhile(final ITSPHPAst doWhileRoot, final ITSPHPAst expression) {
        final ITypeSymbol typeSymbol = typeSystem.getBoolTypeSymbol();
        checkIsSameOrSubType(expression, typeSymbol, new IErrorReporterCaller()
        {
            @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
            @Override
            public void callAppropriateMethod() {
                typeCheckErrorReporter.wrongTypeDoWhile(doWhileRoot, expression, typeSymbol);
            }
        });
    }

    @Override
    public void checkThrow(final ITSPHPAst throwRoot, final ITSPHPAst expression) {
        final ITypeSymbol typeSymbol = typeSystem.getExceptionTypeSymbol();
        checkIsSameOrSubType(expression, typeSymbol, new IErrorReporterCaller()
        {
            @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
            @Override
            public void callAppropriateMethod() {
                typeCheckErrorReporter.wrongTypeThrow(throwRoot, expression, typeSymbol);
            }
        });
    }

    @Override
    public void checkCatch(final ITSPHPAst castRoot, final ITSPHPAst variableId) {
        final ITypeSymbol typeSymbol = typeSystem.getExceptionTypeSymbol();
        checkIsSameOrSubType(variableId, typeSymbol, new IErrorReporterCaller()
        {
            @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
            @Override
            public void callAppropriateMethod() {
                typeCheckErrorReporter.wrongTypeCatch(castRoot, variableId, typeSymbol);
            }
        });
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public void checkReturn(final ITSPHPAst returnRoot, final ITSPHPAst expression) {
        IMethodSymbol methodSymbol = getEnclosingMethod(returnRoot);

        if (!(methodSymbol instanceof IErroneousMethodSymbol)) {
            final ITypeSymbol typeSymbol = methodSymbol.getType();
            if (typeSymbol instanceof IVoidTypeSymbol) {
                if (expression != null) {
                    typeCheckErrorReporter.noReturnValueExpected(returnRoot, expression, typeSymbol);
                }
            } else {
                if (expression == null) {
                    typeCheckErrorReporter.returnValueExpected(returnRoot, typeSymbol);
                } else {
                    checkIsSameOrSubType(expression, typeSymbol, new IErrorReporterCaller()
                    {
                        @Override
                        public void callAppropriateMethod() {
                            typeCheckErrorReporter.wrongTypeReturn(returnRoot, expression, typeSymbol);
                        }
                    });
                }
            }
        }
    }

    @Override
    public void checkInitialValue(final ITSPHPAst variableId, final ITSPHPAst expression) {
        ITSPHPAst operator = AstHelperRegistry.get().createAst(variableId);
        operator.setText("=");
        operator.getToken().setType(TSPHPDefinitionWalker.Assign);
        checkAssignment(operator, variableId, expression);
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public void checkConstantInitialValue(ITSPHPAst variableId, ITSPHPAst expression) {
        if (expression.getType() != TSPHPDefinitionWalker.TypeArray && expression.getChildCount() > 1) {
            typeCheckErrorReporter.onlySingleValue(variableId, expression);
        } else if (isNotConstantValue(expression)) {
            typeCheckErrorReporter.onlyConstantValue(variableId, expression);
        } else {
            int tokenType = variableId.getToken().getType();
            variableId.getToken().setType(TSPHPDefinitionWalker.VariableId);
            checkInitialValue(variableId, expression);
            variableId.getToken().setType(tokenType);
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public void checkClassMemberInitialValue(final ITSPHPAst variableId, final ITSPHPAst expression) {
        IVariableSymbol symbol = (IVariableSymbol) variableId.getSymbol();
        if (symbol.isAlwaysCasting()) {
            final ITypeSymbol typeSymbol = variableId.getEvalType();
            checkIsSameOrSubType(expression, typeSymbol, new IErrorReporterCaller()
            {
                @Override
                public void callAppropriateMethod() {
                    typeCheckErrorReporter.wrongClassMemberInitialValue(variableId, expression,
                            typeSymbol);
                }
            });
        }
        checkConstantInitialValue(variableId, expression);
    }

    private boolean isNotConstantValue(ITSPHPAst expression) {
        boolean isNotConstantValue;
        switch (expression.getType()) {
            case TSPHPDefinitionWalker.Bool:
            case TSPHPDefinitionWalker.Int:
            case TSPHPDefinitionWalker.Float:
            case TSPHPDefinitionWalker.String:
            case TSPHPDefinitionWalker.CONSTANT:
            case TSPHPDefinitionWalker.Null:
                isNotConstantValue = false;
                break;
            case TSPHPDefinitionWalker.TypeArray:
                //TODO TSPHP-789 check that it is a constant array
                isNotConstantValue = false;
                break;
            case TSPHPDefinitionWalker.UNARY_MINUS:
            case TSPHPDefinitionWalker.UNARY_PLUS:
                isNotConstantValue = isNotConstantValue(expression.getChild(0));
                break;
            case TSPHPDefinitionWalker.CLASS_STATIC_ACCESS:
                isNotConstantValue = expression.getChild(1).getType() == TSPHPDefinitionWalker.CONSTANT;
                break;
            default:
                isNotConstantValue = true;
        }
        return isNotConstantValue;
    }

    @Override
    public void checkEcho(final ITSPHPAst expression) {
        final ITypeSymbol typeSymbol = typeSystem.getStringNullableTypeSymbol();
        checkIsSameOrSubType(expression, typeSymbol, new IErrorReporterCaller()
        {
            @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
            @Override
            public void callAppropriateMethod() {
                typeCheckErrorReporter.wrongTypeEcho(expression, typeSymbol);
            }
        });
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public void checkClone(ITSPHPAst clone, ITSPHPAst expression) {
        ITypeSymbol typeSymbol = expression.getEvalType();
        if (!(typeSymbol instanceof IErroneousSymbol) && !(typeSymbol instanceof IPolymorphicTypeSymbol)) {
            typeCheckErrorReporter.wrongTypeClone(clone, expression);
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public void checkInstanceof(ITSPHPAst operator, ITSPHPAst expression, ITSPHPAst typeAst) {
        ITypeSymbol leftType = expression.getEvalType();
        ITypeSymbol rightType = typeAst.getEvalType();

        if (leftType instanceof IPolymorphicTypeSymbol && rightType instanceof IPolymorphicTypeSymbol) {
            checkIdentity(operator, expression, typeAst);
        } else {
            if (areNotErroneousTypes(leftType, rightType)) {
                if (!(leftType instanceof IPolymorphicTypeSymbol)) {
                    typeCheckErrorReporter.wrongTypeInstanceof(expression);
                }
                if (!(rightType instanceof IPolymorphicTypeSymbol)) {
                    typeCheckErrorReporter.wrongTypeInstanceof(typeAst);
                }
            }
        }
    }

    @Override
    public void checkNew(ITSPHPAst identifier, ITSPHPAst arguments) {
        //TODO rstoll TSPHP-422 type check new operator
    }

    @Override
    public void checkPolymorphism(ITSPHPAst identifier) {
        checkAbstractImplemented(identifier);
        //TODO rstoll TSPHP-523 check constant override
        //TODO rstoll TSPHP-424 check method override
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void checkAbstractImplemented(ITSPHPAst identifier) {
        IPolymorphicTypeSymbol typeSymbol = (IPolymorphicTypeSymbol) identifier.getSymbol();
        if (!typeSymbol.isAbstract()) {
            Set<ISymbol> symbols = typeSymbol.getAbstractSymbols();
            if (!symbols.isEmpty()) {
                typeCheckErrorReporter.missingAbstractImplementations(identifier, symbols);
            }
        }
    }


    @Override
    public void addDefaultValue(ITSPHPAst variableId) {
        ITypeSymbol typeSymbol = variableId.getSymbol().getType();
        ITSPHPAst initValue = typeSymbol.getDefaultValue();
        if (typeSymbol.isNullable()) {
            initValue.setEvalType(typeSystem.getNullTypeSymbol());
        } else {
            initValue.setEvalType(typeSymbol);
        }
        variableId.addChild(initValue);

    }

    @Override
    public IErroneousTypeSymbol createErroneousTypeForMissingSymbol(ITSPHPAst identifier) {
        ReferenceException exception = new ReferenceException(
                "identifier " + identifier.getText() + "does not have a symbol.", identifier);
        return symbolFactory.createErroneousTypeSymbol(identifier, exception);
    }

    /**
     * A "delegate" which represents a call of a method on an ITypeCheckerErrorReporter.
     */
    private interface IErrorReporterCaller
    {

        void callAppropriateMethod();
    }

    /**
     * A "delegate" which represents a call of a method on an ITypeCheckerErrorReporter.
     */
    private interface IWrongOperatorUsageReporter
    {

        ReferenceException report(List<IMethodSymbol> methods);
    }

    /**
     * A "delegate" which represents a call of a method on an ITypeCheckerErrorReporter.
     */
    private interface IAmbiguousCallReporter
    {

        void report(AmbiguousCallException exception);
    }

    //CHECKSTYLE:OFF:VisibilityModifier|ParameterNumber
    private class OperatorResolvingDto
    {

        ITSPHPAst operator;
        Map<Integer, List<IMethodSymbol>> operators;
        IErroneousChecker erroneousChecker;
        IActualParameterGetter actualParameterGetter;
        IAmbiguousCallReporter ambiguousCallReporter;
        IWrongOperatorUsageReporter wrongOperatorUsageReporter;

        public OperatorResolvingDto(ITSPHPAst theOperator, Map<Integer, List<IMethodSymbol>> theOperators,
                IActualParameterGetter theActualParameterGetter,
                IErroneousChecker theErroneousChecker,
                IAmbiguousCallReporter theAmbiguousCastCaller,
                IWrongOperatorUsageReporter theWrongOperatorUsageCaller) {

            operator = theOperator;
            operators = theOperators;
            erroneousChecker = theErroneousChecker;
            actualParameterGetter = theActualParameterGetter;
            ambiguousCallReporter = theAmbiguousCastCaller;
            wrongOperatorUsageReporter = theWrongOperatorUsageCaller;
        }
    }
    //CHECKSTYLE:ON:VisibilityModifier|ParameterNumber

    /**
     * A "delegate" which returns an IErroneousTypeSymbol if it founds one otherwise null.
     */
    private interface IErroneousChecker
    {

        IErroneousTypeSymbol getErroneousTypeSymbol();
    }

    private static class BinaryOperatorErroneousChecker implements IErroneousChecker
    {

        private final ITypeSymbol leftType;
        private final ITypeSymbol rightType;

        public BinaryOperatorErroneousChecker(ITypeSymbol theLeftType, ITypeSymbol theRightType) {
            leftType = theLeftType;
            rightType = theRightType;
        }

        @Override
        public IErroneousTypeSymbol getErroneousTypeSymbol() {
            IErroneousTypeSymbol erroneousTypeSymbol = null;
            if (leftType instanceof IErroneousTypeSymbol) {
                erroneousTypeSymbol = (IErroneousTypeSymbol) leftType;
            } else if (rightType instanceof IErroneousTypeSymbol) {
                erroneousTypeSymbol = (IErroneousTypeSymbol) rightType;
            }
            return erroneousTypeSymbol;
        }
    }

    private static class UnaryOperatorErroneousChecker implements IErroneousChecker
    {

        private final ITypeSymbol leftType;

        public UnaryOperatorErroneousChecker(ITypeSymbol theLeftType) {
            leftType = theLeftType;
        }

        @Override
        public IErroneousTypeSymbol getErroneousTypeSymbol() {
            IErroneousTypeSymbol erroneousTypeSymbol = null;
            if (leftType instanceof IErroneousTypeSymbol) {
                erroneousTypeSymbol = (IErroneousTypeSymbol) leftType;
            }
            return erroneousTypeSymbol;
        }
    }

    /**
     * Provides a method which returns the actual parameters, e.g. of a binary operation
     */
    private interface IActualParameterGetter
    {

        List<ITSPHPAst> getActualParameters();
    }

    /**
     * Returns the actual parameters of a binary operation.
     */
    private static class BinaryActualParameterGetter implements IActualParameterGetter
    {

        private final ITSPHPAst left;
        private final ITSPHPAst right;

        public BinaryActualParameterGetter(ITSPHPAst leftHandSide, ITSPHPAst rightHandSide) {
            left = leftHandSide;
            right = rightHandSide;
        }

        @Override
        public List<ITSPHPAst> getActualParameters() {
            List<ITSPHPAst> actualParameterTypes = new ArrayList<>();
            actualParameterTypes.add(left);
            actualParameterTypes.add(right);
            return actualParameterTypes;
        }
    }

    /**
     * Returns the actual parameters of an unary operation.
     */
    private static class UnaryActualParameterGetter implements IActualParameterGetter
    {

        private final ITSPHPAst expression;

        public UnaryActualParameterGetter(ITSPHPAst theExpression) {
            expression = theExpression;
        }

        @Override
        public List<ITSPHPAst> getActualParameters() {
            List<ITSPHPAst> actualParameterTypes = new ArrayList<>();
            actualParameterTypes.add(expression);
            return actualParameterTypes;
        }
    }

    /**
     * "Delegate" to report a wrong call (method or function).
     */
    private interface IWrongCallReporter
    {

        void report(ITSPHPAst identifier, List<ITSPHPAst> actualParameters, List<IMethodSymbol> methods);
    }
}