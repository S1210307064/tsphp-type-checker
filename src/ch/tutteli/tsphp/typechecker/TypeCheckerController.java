package ch.tutteli.tsphp.typechecker;

import ch.tutteli.tsphp.common.AstHelperRegistry;
import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.common.exceptions.DefinitionException;
import ch.tutteli.tsphp.common.exceptions.ReferenceException;
import ch.tutteli.tsphp.common.exceptions.TypeCheckerException;
import ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tutteli.tsphp.typechecker.error.TypeCheckErrorReporterRegistry;
import ch.tutteli.tsphp.typechecker.scopes.IConditionalScope;
import ch.tutteli.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tutteli.tsphp.typechecker.symbols.IAliasTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IArrayTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IInterfaceTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IPolymorphicTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IScalarTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolWithAccessModifier;
import ch.tutteli.tsphp.typechecker.symbols.IVariableSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IVoidTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.IErroneousMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.IErroneousSymbol;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.IErroneousTypeSymbol;
import ch.tutteli.tsphp.typechecker.utils.IAstHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeCheckerController implements ITypeCheckerController
{

    private final ISymbolFactory symbolFactory;
    private final ISymbolResolver symbolResolver;
    private final ITypeSystem typeSystem;
    private final IDefiner definer;
    private final IOverloadResolver overloadResolver;
    private final IAstHelper astHelper;
    //
    private Map<Integer, List<IMethodSymbol>> unaryOperators = new HashMap<>();
    private Map<Integer, List<IMethodSymbol>> binaryOperators = new HashMap<>();
    //
    private final IGlobalNamespaceScope globalDefaultNamespace;

    public TypeCheckerController(
            ISymbolFactory theSymbolFactory,
            ITypeSystem theTypeSystem,
            IDefiner theDefiner,
            ISymbolResolver theSymbolResolver,
            IOverloadResolver theMethodResolver,
            IAstHelper theAstHelper) {
        symbolFactory = theSymbolFactory;
        typeSystem = theTypeSystem;
        definer = theDefiner;
        symbolResolver = theSymbolResolver;
        overloadResolver = theMethodResolver;
        astHelper = theAstHelper;

        typeSystem.initTypeSystem();
        unaryOperators = typeSystem.getUnaryOperators();
        binaryOperators = typeSystem.getBinaryOperators();
        globalDefaultNamespace = definer.getGlobalDefaultNamespace();
    }

    @Override
    public IDefiner getDefiner() {
        return definer;
    }

    @Override
    public ITypeSystem getTypeSystem() {
        return typeSystem;
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public boolean checkIsInterface(ITSPHPAst typeAst, ITypeSymbol symbol) {
        boolean isInterface = symbol instanceof IInterfaceTypeSymbol || symbol instanceof IErroneousTypeSymbol;
        if (!isInterface) {
            TypeCheckErrorReporterRegistry.get().interfaceExpected(typeAst);
        }
        return isInterface;
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public boolean checkIsClass(ITSPHPAst typeAst, ITypeSymbol symbol) {
        boolean isClass = symbol instanceof IClassTypeSymbol || symbol instanceof IErroneousTypeSymbol;
        if (!isClass) {
            TypeCheckErrorReporterRegistry.get().classExpected(typeAst);
        }
        return isClass;
    }

    @Override
    public boolean checkIsForwardReference(ITSPHPAst ast) {
        ISymbol symbol = ast.getSymbol();
        boolean isNotUsedBefore = true;
        //only check if not already an error occurred in conjunction with this ast (for instance missing declaration)
        if (!(symbol instanceof IErroneousSymbol)) {
            ITSPHPAst definitionAst = symbol.getDefinitionAst();
            isNotUsedBefore = definitionAst.isDefinedEarlierThan(ast);
            if (!isNotUsedBefore) {
                DefinitionException exception = TypeCheckErrorReporterRegistry.get().forwardReference(ast, definitionAst);
                symbol = symbolFactory.createErroneousVariableSymbol(ast, exception);
                ast.setSymbol(symbol);
            }
        }
        return isNotUsedBefore;
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public boolean checkIsOutOfConditionalScope(ITSPHPAst ast) {
        boolean ok = true;
        ISymbol symbol = ast.getSymbol();
        if (symbol.getDefinitionScope() instanceof IConditionalScope) {
            IScope currentScope = ast.getScope();
            if (!(currentScope instanceof IConditionalScope)) {
                ok = false;
                TypeCheckErrorReporterRegistry.get().variableDefinedInConditionalScope(ast.getSymbol().getDefinitionAst(), ast);
            } else if (isNotDefinedInThisNorOuterScope(symbol, currentScope)) {
                ok = false;
                TypeCheckErrorReporterRegistry.get().variableDefinedInOtherConditionalScope(symbol.getDefinitionAst(), ast);
            }
        }
        return ok;
    }

    private boolean isNotDefinedInThisNorOuterScope(ISymbol symbol, IScope scope) {
        boolean isNotDefinedInThisNorOuterScope = true;
        IScope definitionScope = symbol.getDefinitionScope();
        while (scope != null && scope instanceof IConditionalScope) {
            if (scope == definitionScope) {
                isNotDefinedInThisNorOuterScope = false;
                break;
            }
            scope = scope.getEnclosingScope();
        }
        return isNotDefinedInThisNorOuterScope;
    }

    @Override
    public IVariableSymbol resolveConstant(ITSPHPAst ast) {
        IVariableSymbol symbol = symbolResolver.resolveConstant(ast);

        if (symbol == null) {
            ReferenceException exception = TypeCheckErrorReporterRegistry.get().notDefined(ast);
            symbol = symbolFactory.createErroneousVariableSymbol(ast, exception);
        }
        return symbol;
    }

    @Override
    public IVariableSymbol resolveStaticMember(ITSPHPAst accessor, ITSPHPAst identifier) {
        return resolveStaticClassMemberOrClassConstant(accessor, identifier, new IVisibilityViolationCaller()
        {
            @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
            @Override
            public void callAppropriateMethod(ITSPHPAst identifier, ISymbolWithAccessModifier symbol, int accessFrom) {
                TypeCheckErrorReporterRegistry.get().visibilityViolationStaticClassMemberAccess(identifier, symbol, accessFrom);
            }
        });
    }

    @Override
    public IVariableSymbol resolveClassConstant(ITSPHPAst accessor, ITSPHPAst identifier) {
        return resolveStaticClassMemberOrClassConstant(accessor, identifier, new IVisibilityViolationCaller()
        {
            @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
            @Override
            public void callAppropriateMethod(ITSPHPAst identifier, ISymbolWithAccessModifier symbol, int accessFrom) {
                TypeCheckErrorReporterRegistry.get().visibilityViolationClassConstantAccess(identifier, symbol, accessFrom);
            }
        });
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private IVariableSymbol resolveStaticClassMemberOrClassConstant(ITSPHPAst accessor, ITSPHPAst id,
            IVisibilityViolationCaller caller) {
        IVariableSymbol variableSymbol = resolveClassMemberOrStaticMemberOrConstantAccess(accessor, id, caller);
        if (!variableSymbol.isStatic()) {
            TypeCheckErrorReporterRegistry.get().notStatic(accessor);
        }
        return variableSymbol;
    }

    private void checkAccess(ISymbolWithAccessModifier methodSymbol, IPolymorphicTypeSymbol polymorphicTypeSymbol,
            IVisibilityViolationCaller visibilityViolationCaller, ITSPHPAst calleeOrAccessor, ITSPHPAst identifier) {

        int accessedFrom;


        String calleeOrAccessorText = calleeOrAccessor.getText();
        switch (calleeOrAccessorText) {
            case "$this":
            case "self":
                accessedFrom = methodSymbol.getDefinitionScope() == polymorphicTypeSymbol
                        ? TSPHPDefinitionWalker.Private
                        : TSPHPDefinitionWalker.Protected;

                break;
            case "parent":
                accessedFrom = TSPHPDefinitionWalker.Protected;
                break;
            default:
                accessedFrom = TSPHPDefinitionWalker.Public;
                break;
        }

        if (!methodSymbol.canBeAccessedFrom(accessedFrom)) {
            visibilityViolationCaller.callAppropriateMethod(identifier, methodSymbol, accessedFrom);
        }
    }

    @Override
    public IVariableSymbol resolveThisSelf(ITSPHPAst ast) {
        return resolveThis(getEnclosingClass(ast), ast);
    }

    private IVariableSymbol resolveThis(IClassTypeSymbol classTypeSymbol, ITSPHPAst $this) {
        IVariableSymbol variableSymbol;
        if (classTypeSymbol != null) {
            variableSymbol = classTypeSymbol.getThis();
            if (variableSymbol == null) {
                variableSymbol = symbolFactory.createThisSymbol($this, classTypeSymbol);
                classTypeSymbol.setThis(variableSymbol);
            }
        } else {
            ReferenceException exception = TypeCheckErrorReporterRegistry.get().notInClass($this);
            variableSymbol = symbolFactory.createErroneousVariableSymbol($this, exception);
        }
        return variableSymbol;
    }

    @Override
    public IVariableSymbol resolveParent(ITSPHPAst ast) {
        return resolveThis(getParent(ast), ast);
    }

    @Override
    public IVariableSymbol resolveVariable(ITSPHPAst ast) {
        ISymbol symbol = ast.getScope().resolve(ast);
        if (symbol == null) {
            ReferenceException exception = TypeCheckErrorReporterRegistry.get().notDefined(ast);
            symbol = symbolFactory.createErroneousVariableSymbol(ast, exception);
        }
        return (IVariableSymbol) symbol;
    }

    @Override
    public ITypeSymbol resolveUseType(ITSPHPAst typeAst, ITSPHPAst alias) {
        ITypeSymbol aliasTypeSymbol = symbolResolver.resolveUseType(typeAst, alias);
        if (aliasTypeSymbol == null) {
            aliasTypeSymbol = symbolFactory.createAliasTypeSymbol(typeAst, typeAst.getText());
        }
        return aliasTypeSymbol;
    }

    @Override
    public ITypeSymbol resolveType(ITSPHPAst typeAst) {
        ITypeSymbol symbol = (ITypeSymbol) symbolResolver.resolveGlobalIdentifier(typeAst);

        if (symbol == null) {
            rewriteToAbsoluteNotFoundType(typeAst);
            ReferenceException ex = TypeCheckErrorReporterRegistry.get().unknownType(typeAst);
            symbol = symbolFactory.createErroneousClassTypeSymbol(typeAst, ex);

        } else if (symbol instanceof IAliasTypeSymbol) {

            typeAst.setText(symbol.getName());
            ReferenceException ex = TypeCheckErrorReporterRegistry.get().unknownType(typeAst);
            symbol = symbolFactory.createErroneousClassTypeSymbol(symbol.getDefinitionAst(), ex);
        }
        return symbol;
    }

    /**
     * Return the absolute name of a type which could not be found (prefix the enclosing namespace).
     */
    private void rewriteToAbsoluteNotFoundType(ITSPHPAst typeAst) {
        String typeName = typeAst.getText();
        if (!symbolResolver.isAbsolute(typeName)) {
            String namespace = symbolResolver.getEnclosingGlobalNamespaceScope(typeAst.getScope()).getScopeName();
            typeAst.setText(namespace + typeName);
        }
    }

    @Override
    public IScalarTypeSymbol resolveScalarType(ITSPHPAst typeAst, boolean isNullable) {
        String typeName = typeAst.getText();
        if (isNullable) {
            typeAst.setText(typeName + "?");
        }
        IScalarTypeSymbol typeSymbol = (IScalarTypeSymbol) resolvePrimitiveType(typeAst);
        if (isNullable) {
            typeAst.setText(typeName);
        }
        return typeSymbol;
    }

    @Override
    public ITypeSymbol resolvePrimitiveType(ITSPHPAst typeAst) {
        ITypeSymbol typeSymbol = (ITypeSymbol) globalDefaultNamespace.resolve(typeAst);
        if (typeSymbol == null) {
            rewriteToAbsoluteNotFoundType(typeAst);
            ReferenceException ex = TypeCheckErrorReporterRegistry.get().unknownType(typeAst);
            typeSymbol = symbolFactory.createErroneousTypeSymbol(typeAst, ex);

        }
        return typeSymbol;
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public void checkBreakContinueLevel(ITSPHPAst root, ITSPHPAst expression) {
        int levels = expression == null ? 1 : Integer.parseInt(expression.getText());
        int count = 0;

        @SuppressWarnings("unchecked") //has to be ITSPHPAst
                List<ITSPHPAst> ancestors = (List<ITSPHPAst>) root.getAncestors();
        for (ITSPHPAst ancestor : ancestors) {
            int type = ancestor.getType();
            if (isLoop(type)) {
                ++count;
            }
        }
        if (count < levels) {
            TypeCheckErrorReporterRegistry.get().toManyBreakContinueLevels(root);
        }
    }

    private boolean isLoop(int type) {
        //CHECKSTYLE:OFF:BooleanExpressionComplexity
        return type == TSPHPDefinitionWalker.Switch
                || type == TSPHPDefinitionWalker.For
                || type == TSPHPDefinitionWalker.Foreach
                || type == TSPHPDefinitionWalker.Do
                || type == TSPHPDefinitionWalker.While;
        //CHECKSTYLE:ON:BooleanExpressionComplexity
    }

    private IClassTypeSymbol getEnclosingClass(ITSPHPAst ast) {
        IClassTypeSymbol classTypeSymbol = symbolResolver.getEnclosingClass(ast);
        if (classTypeSymbol == null) {
            ReferenceException ex = TypeCheckErrorReporterRegistry.get().notInClass(ast);
            classTypeSymbol = symbolFactory.createErroneousClassTypeSymbol(ast, ex);
        }
        return classTypeSymbol;
    }

    private IMethodSymbol getEnclosingMethod(ITSPHPAst ast) {
        IMethodSymbol methodSymbol = symbolResolver.getEnclosingMethod(ast);
        if (methodSymbol == null) {
            ReferenceException ex = TypeCheckErrorReporterRegistry.get().notInMethod(ast);
            methodSymbol = symbolFactory.createErroneousMethodSymbol(ast, ex);
        }
        return methodSymbol;
    }

    private IClassTypeSymbol getParent(ITSPHPAst ast) {
        IClassTypeSymbol classTypeSymbol = getEnclosingClass(ast);
        IClassTypeSymbol parent = classTypeSymbol.getParent();
        if (parent == null) {
            TypeCheckerException ex = TypeCheckErrorReporterRegistry.get().noParentClass(ast);
            parent = symbolFactory.createErroneousClassTypeSymbol(ast, ex);
        }
        return parent;
    }

    @Override
    public ITypeSymbol resolveBinaryOperatorEvalType(final ITSPHPAst operator, final ITSPHPAst left,
            final ITSPHPAst right) {

        IAmbiguousCallReporter caller = new IAmbiguousCallReporter()
        {
            @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
            @Override
            public void report(AmbiguousCallException exception) {
                TypeCheckErrorReporterRegistry.get().ambiguousBinaryOperatorUsage(operator, left, right, exception);
            }
        };
        IWrongOperatorUsageReporter wrongOperatorUsageCaller = new IWrongOperatorUsageReporter()
        {
            @Override
            public ReferenceException report(List<IMethodSymbol> methods) {
                return TypeCheckErrorReporterRegistry.get().wrongBinaryOperatorUsage(operator, left, right, methods);
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
                TypeCheckerException exception = TypeCheckErrorReporterRegistry.get().unsupportedOperator(dto.operator);
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
                TypeCheckErrorReporterRegistry.get().ambiguousUnaryOperatorUsage(operator, expression, exception);
            }
        };
        IWrongOperatorUsageReporter wrongOperatorUsageReporter = new IWrongOperatorUsageReporter()
        {
            @Override
            public ReferenceException report(List<IMethodSymbol> methods) {
                return TypeCheckErrorReporterRegistry.get().wrongUnaryOperatorUsage(operator, expression, methods);
            }
        };

        return resolveOperatorEvalType(new OperatorResolvingDto(operator, unaryOperators,
                new UnaryActualParameterGetter(expression),
                new UnaryOperatorErroneousChecker(expression.getEvalType()),
                ambiguousCallReporter, wrongOperatorUsageReporter));
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public ITypeSymbol resolveTernaryOperatorEvalType(ITSPHPAst operator, ITSPHPAst condition,
            ITSPHPAst caseTrue, ITSPHPAst caseFalse) {

        checkTernaryCondition(operator, condition);
        ITypeSymbol typeSymbol = caseTrue.getEvalType();

        if (areNotSameAndNoneIsSubType(caseTrue, caseFalse)) {
            TypeCheckErrorReporterRegistry.get().wrongTypeTernaryCases(caseTrue, caseFalse);
        } else {
            ITypeSymbol caseFalseType = caseFalse.getEvalType();
            int promotionLevel = overloadResolver.getPromotionLevelFromTo(typeSymbol, caseFalseType);
            if (caseFalseTypeIsParentType(promotionLevel)) {
                typeSymbol = caseFalseType;
            }
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
                TypeCheckErrorReporterRegistry.get().wrongTypeTernaryCondition(operator, condition, typeExpected);
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
                ReferenceException exception = TypeCheckErrorReporterRegistry.get().wrongTypeArrayAccess(expression,
                        arrayTypeSymbol);
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
                TypeCheckErrorReporterRegistry.get().wrongArrayIndexType(expression, index, typeSymbol);
            }
        });

        return returnTypeArrayAccess;
    }

    @Override
    public IVariableSymbol resolveClassMemberAccess(ITSPHPAst expression, ITSPHPAst identifier) {
        String variableName = identifier.getText();
        identifier.setText("$" + variableName);
        IVisibilityViolationCaller visibilityViolationCaller = new IVisibilityViolationCaller()
        {
            @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
            @Override
            public void callAppropriateMethod(ITSPHPAst identifier, ISymbolWithAccessModifier symbol,
                    int accessFrom) {
                TypeCheckErrorReporterRegistry.get().visibilityViolationClassMemberAccess(
                        identifier, symbol, accessFrom);
            }
        };
        IVariableSymbol variableSymbol = resolveClassMemberOrStaticMemberOrConstantAccess(expression, identifier,
                visibilityViolationCaller);

        identifier.setText(variableName);
        return variableSymbol;
    }

    private IVariableSymbol resolveClassMemberOrStaticMemberOrConstantAccess(ITSPHPAst expression,
            ITSPHPAst identifier, IVisibilityViolationCaller visibilityViolationCaller) {

        IVariableSymbol variableSymbol;
        ITypeSymbol evalType = expression.getEvalType();
        if (!(evalType instanceof IErroneousSymbol)) {
            variableSymbol = checkAccessorAndResolveAccess(evalType, expression, identifier, visibilityViolationCaller);
        } else {
            IErroneousSymbol erroneousSymbol = (IErroneousSymbol) evalType;
            variableSymbol = symbolFactory.createErroneousVariableSymbol(expression, erroneousSymbol.getException());
            variableSymbol.setType(evalType);
        }
        return variableSymbol;
    }

    private IVariableSymbol checkAccessorAndResolveAccess(ITypeSymbol evalType, ITSPHPAst expression,
            ITSPHPAst identifier, IVisibilityViolationCaller visibilityViolationCaller) {
        IVariableSymbol variableSymbol;
        if (evalType instanceof IPolymorphicTypeSymbol) {
            variableSymbol = resolveAccess((IPolymorphicTypeSymbol) evalType, expression,
                    identifier, visibilityViolationCaller);
        } else {
            ReferenceException exception = TypeCheckErrorReporterRegistry.get().wrongTypeClassMemberAccess(identifier);
            variableSymbol = symbolFactory.createErroneousVariableSymbol(identifier, exception);
            variableSymbol.setType(symbolFactory.createErroneousTypeSymbol(identifier, exception));
        }
        return variableSymbol;
    }

    private IVariableSymbol resolveAccess(IPolymorphicTypeSymbol polymorphicTypeSymbol,
            ITSPHPAst accessor, ITSPHPAst identifier, IVisibilityViolationCaller visibilityViolationCaller) {

        IVariableSymbol symbol =
                (IVariableSymbol) polymorphicTypeSymbol.resolveWithFallbackToParent(identifier);

        if (symbol != null) {
            checkAccess(symbol, polymorphicTypeSymbol, visibilityViolationCaller, accessor, identifier);
        } else {
            DefinitionException exception = TypeCheckErrorReporterRegistry.get().memberNotDefined(accessor, identifier);
            symbol = symbolFactory.createErroneousVariableSymbol(identifier, exception);
        }
        return symbol;
    }

    @Override
    public IMethodSymbol resolveFunctionCall(ITSPHPAst identifier, ITSPHPAst arguments) {

        IMethodSymbol methodSymbol = (IMethodSymbol) symbolResolver.resolveGlobalIdentifierWithFallback(identifier);
        IWrongCallReporter wrongCallReporter = new WrongFunctionCallReporter();
        if (methodSymbol != null) {
            resolveCallOverload(identifier, arguments, methodSymbol, wrongCallReporter);
        } else {
            ReferenceException exception = TypeCheckErrorReporterRegistry.get().notDefined(identifier);
            ITypeSymbol typeSymbol = symbolFactory.createErroneousClassTypeSymbol(identifier, exception);
            methodSymbol = symbolFactory.createErroneousMethodSymbol(identifier, exception);
            methodSymbol.setType(typeSymbol);
        }

        return methodSymbol;
    }

    private IMethodSymbol resolveCallOverload(final ITSPHPAst identifier, ITSPHPAst arguments,
            IMethodSymbol methodSymbol, IWrongCallReporter wrongCallReporter) {

        List<IMethodSymbol> methods = new ArrayList<>();
        methods.add(methodSymbol);
        OverloadDto overloadDto = resolveCallOverload(identifier, arguments, methods, wrongCallReporter);
        //error reporting if overloadDto == null happens in resolveCallOverload
        if (overloadDto != null) {
            methodSymbol = overloadDto.methodSymbol;
        }
        return methodSymbol;
    }

    @Override
    public IMethodSymbol resolveMethodCall(ITSPHPAst callee, ITSPHPAst identifier, ITSPHPAst arguments) {
        IMethodSymbol methodSymbol;

        ITypeSymbol typeSymbol = callee.getEvalType();
        if (!(typeSymbol instanceof IErroneousTypeSymbol)) {
            methodSymbol = checkCalleeAndResolveCall(typeSymbol, callee, identifier, arguments);
        } else {
            TypeCheckerException exception = ((IErroneousTypeSymbol) typeSymbol).getException();
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

            IWrongCallReporter wrongCallReporter = new WrongFunctionCallReporter();
            if (methodSymbol != null) {
                methodSymbol = resolveCallOverload(identifier, arguments, methodSymbol, wrongCallReporter);
                methodVisibilityCheck(methodSymbol, polymorphicTypeSymbol, callee, identifier);
            } else {
                DefinitionException exception = TypeCheckErrorReporterRegistry.get().methodNotDefined(callee, identifier);
                typeSymbol = symbolFactory.createErroneousClassTypeSymbol(identifier, exception);
                methodSymbol = symbolFactory.createErroneousMethodSymbol(identifier, exception);
                methodSymbol.setType(typeSymbol);
            }
        } else {
            ReferenceException exception = TypeCheckErrorReporterRegistry.get().wrongTypeMethodCall(callee);
            typeSymbol = symbolFactory.createErroneousClassTypeSymbol(identifier, exception);
            methodSymbol = symbolFactory.createErroneousMethodSymbol(identifier, exception);
            methodSymbol.setType(typeSymbol);
        }
        return methodSymbol;
    }

    private void methodVisibilityCheck(IMethodSymbol methodSymbol, IPolymorphicTypeSymbol polymorphicTypeSymbol,
            ITSPHPAst callee, ITSPHPAst identifier) {

        IVisibilityViolationCaller visibilityViolationCaller = new IVisibilityViolationCaller()
        {
            @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
            @Override
            public void callAppropriateMethod(ITSPHPAst identifier, ISymbolWithAccessModifier symbol,
                    int accessedFrom) {
                TypeCheckErrorReporterRegistry.get().visibilityViolationMethodCall(identifier, symbol, accessedFrom);
            }
        };

        checkAccess(methodSymbol, polymorphicTypeSymbol, visibilityViolationCaller, callee, identifier);
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public IMethodSymbol resolveStaticMethodCall(ITSPHPAst callee, ITSPHPAst identifier, ITSPHPAst arguments) {
        IMethodSymbol symbol = resolveMethodCall(callee, identifier, arguments);
        if (!symbol.isStatic()) {
            TypeCheckErrorReporterRegistry.get().notStatic(identifier);
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
                TypeCheckErrorReporterRegistry.get().ambiguousCall(identifier, exception, actualParameters);
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
        if (areNotSameAndNoneIsSubType(left, right)) {

            IVariableSymbol leftSymbol = getVariableSymbolFromExpression(left);
            IVariableSymbol rightSymbol = getVariableSymbolFromExpression(right);

            CastingDto leftToRight = overloadResolver.getCastingDtoAlwaysCasting(rightSymbol, left);
            CastingDto rightToLeft = overloadResolver.getCastingDtoAlwaysCasting(leftSymbol, right);

            if (haveBothSideCast(leftToRight, rightToLeft)) {
                TypeCheckErrorReporterRegistry.get().operatorAmbiguousCasts(operator, left, right, leftToRight, rightToLeft,
                        leftToRight.ambiguousCasts, rightToLeft.ambiguousCasts);

            } else if (haveNoSideCast(leftToRight, rightToLeft)) {
                TypeCheckErrorReporterRegistry.get().wrongEqualityUsage(operator, left, right);

            } else if (hasAmbiguousCast(rightToLeft)) {
                TypeCheckErrorReporterRegistry.get().ambiguousCasts(operator, left, right, rightToLeft.ambiguousCasts);

            } else if (hasAmbiguousCast(leftToRight)) {
                TypeCheckErrorReporterRegistry.get().ambiguousCasts(operator, left, right, leftToRight.ambiguousCasts);
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
        if (areNotErroneousTypes(left, right)) {
            ISymbol leftSymbol = left.getSymbol();
            if (leftSymbol != null && leftSymbol instanceof IVariableSymbol) {
                CastingDto castingDto = overloadResolver.getCastingDto((IVariableSymbol) leftSymbol, right);
                if (castingDto != null) {
                    if (castingDto.castingMethods != null) {
                        astHelper.prependCasting(castingDto);
                    }
                    if (castingDto.ambiguousCasts != null) {
                        TypeCheckErrorReporterRegistry.get().ambiguousCasts(operator, left, right, castingDto.ambiguousCasts);
                    }
                } else {
                    TypeCheckErrorReporterRegistry.get().wrongAssignment(operator, left, right);
                }
            } else {
                TypeCheckErrorReporterRegistry.get().variableExpected(left);
            }
        }
    }

    private boolean areNotErroneousTypes(ITSPHPAst left, ITSPHPAst right) {
        return areNotErroneousTypes(left.getEvalType(), right.getEvalType());

    }

    private boolean areNotErroneousTypes(ITypeSymbol leftType, ITypeSymbol rightType) {
        return !(leftType instanceof IErroneousTypeSymbol) && !(rightType instanceof IErroneousTypeSymbol);
    }

    private boolean areNotSameAndNoneIsSubType(ITSPHPAst left, ITSPHPAst right) {
        ITypeSymbol leftType = left.getEvalType();
        ITypeSymbol rightType = right.getEvalType();
        boolean areNotSameAndNoneIsSubType = false;
        if (areNotErroneousTypes(leftType, rightType)) {

            IVariableSymbol leftSymbol = getVariableSymbolFromExpression(left);
            IVariableSymbol rightSymbol = getVariableSymbolFromExpression(right);

            areNotSameAndNoneIsSubType = !overloadResolver.isSameOrParentTypeConsiderNull(rightSymbol, left);
            if (areNotSameAndNoneIsSubType) {
                areNotSameAndNoneIsSubType = !overloadResolver.isSameOrParentTypeConsiderNull(leftSymbol, right);
            }
        }
        return areNotSameAndNoneIsSubType;
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public void checkIdentity(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right) {
        if (areNotSameAndNoneIsSubType(left, right)) {
            TypeCheckErrorReporterRegistry.get().wrongIdentityUsage(operator, left, right);
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
        if (!(right.getEvalType() instanceof IErroneousSymbol)) {
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
                TypeCheckErrorReporterRegistry.get().wrongCast(operator, left, right);
            } else if (castingDto.ambiguousCasts != null && !castingDto.ambiguousCasts.isEmpty()) {
                TypeCheckErrorReporterRegistry.get().ambiguousCasts(operator, left, right, castingDto.ambiguousCasts);
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
                TypeCheckErrorReporterRegistry.get().wrongTypeIf(ifRoot, expression, typeSymbol);
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
                TypeCheckErrorReporterRegistry.get().wrongTypeSwitch(switchRoot, expression, typeSymbol);
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
                TypeCheckErrorReporterRegistry.get().wrongTypeSwitchCase(switchRoot, switchCase, typeSymbol);
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
                TypeCheckErrorReporterRegistry.get().wrongTypeFor(forRoot, expression, typeSymbol);
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
                TypeCheckErrorReporterRegistry.get().wrongTypeForeach(foreachRoot, array, arrayTypeSymbol);
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
                TypeCheckErrorReporterRegistry.get().notSameOrParentType(statement, expression, typeSymbol);
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
                TypeCheckErrorReporterRegistry.get().wrongTypeWhile(whileRoot, expression, typeSymbol);
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
                TypeCheckErrorReporterRegistry.get().wrongTypeDoWhile(doWhileRoot, expression, typeSymbol);
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
                TypeCheckErrorReporterRegistry.get().wrongTypeThrow(throwRoot, expression, typeSymbol);
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
                TypeCheckErrorReporterRegistry.get().wrongTypeCatch(castRoot, variableId, typeSymbol);
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
                    TypeCheckErrorReporterRegistry.get().noReturnValueExpected(returnRoot, expression, typeSymbol);
                }
            } else {
                if (expression == null) {
                    TypeCheckErrorReporterRegistry.get().returnValueExpected(returnRoot, expression, typeSymbol);
                } else {
                    checkIsSameOrSubType(expression, typeSymbol, new IErrorReporterCaller()
                    {
                        @Override
                        public void callAppropriateMethod() {
                            TypeCheckErrorReporterRegistry.get().wrongTypeReturn(returnRoot, expression, typeSymbol);
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
            TypeCheckErrorReporterRegistry.get().onlySingleValue(variableId, expression);
        } else if (isNotConstantValue(expression)) {
            TypeCheckErrorReporterRegistry.get().onlyConstantValue(variableId, expression);
        } else {
            checkInitialValue(variableId, expression);
        }
    }

    private boolean isNotConstantValue(ITSPHPAst expression) {
        boolean isNotConstantValue;
        switch (expression.getType()) {
            case TSPHPDefinitionWalker.Bool:
            case TSPHPDefinitionWalker.Int:
            case TSPHPDefinitionWalker.Float:
            case TSPHPDefinitionWalker.String:
            case TSPHPDefinitionWalker.TypeArray:
            case TSPHPDefinitionWalker.CONSTANT:
            case TSPHPDefinitionWalker.Null:
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
                TypeCheckErrorReporterRegistry.get().wrongTypeEcho(expression, typeSymbol);
            }
        });
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public void checkClone(ITSPHPAst clone, ITSPHPAst expression) {
        ITypeSymbol typeSymbol = expression.getEvalType();
        if (!(typeSymbol instanceof IErroneousSymbol) && !(typeSymbol instanceof IPolymorphicTypeSymbol)) {
            TypeCheckErrorReporterRegistry.get().wrongTypeClone(clone, expression);
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
                    TypeCheckErrorReporterRegistry.get().wrongTypeInstanceof(expression);
                }
                if (!(rightType instanceof IPolymorphicTypeSymbol)) {
                    TypeCheckErrorReporterRegistry.get().wrongTypeInstanceof(typeAst);
                }
            }
        }
    }

    @Override
    public void checkNew(ITSPHPAst identifier, ITSPHPAst arguments) {
        //TODO rstoll TSPHP-422 type check new operator
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

    /**
     * A "delegate" which represents a call of a method on an ITypeCheckErrorReporter.
     */
    private interface IErrorReporterCaller
    {

        void callAppropriateMethod();
    }

    /**
     * A "delegate" which represents a call of a method on an ITypeCheckErrorReporter.
     */
    private interface IWrongOperatorUsageReporter
    {

        ReferenceException report(List<IMethodSymbol> methods);
    }

    /**
     * A "delegate" which represents a call of a method on an ITypeCheckErrorReporter.
     */
    private interface IAmbiguousCallReporter
    {

        void report(AmbiguousCallException exception);
    }

    /**
     * A "delegate" which represents a call of a visibility violation method on an ITypeCheckErrorReporter.
     */
    private interface IVisibilityViolationCaller
    {

        void callAppropriateMethod(ITSPHPAst identifier, ISymbolWithAccessModifier symbol, int accessFrom);
    }

    private static class WrongFunctionCallReporter implements IWrongCallReporter
    {
        @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
        @Override
        public void report(ITSPHPAst identifier, List<ITSPHPAst> actualParameters, List<IMethodSymbol> methods) {
            TypeCheckErrorReporterRegistry.get().wrongFunctionCall(identifier, actualParameters, methods);
        }
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
     * A "delegate" which returns an IErroneousTypeSymbol if it founds one otherwise null
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

    private interface IActualParameterGetter
    {

        List<ITSPHPAst> getActualParameters();
    }

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

    private interface IWrongCallReporter
    {

        void report(ITSPHPAst identifier, List<ITSPHPAst> actualParameters, List<IMethodSymbol> methods);
    }
}