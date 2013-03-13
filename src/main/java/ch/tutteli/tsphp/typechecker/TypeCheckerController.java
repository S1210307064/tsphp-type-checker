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

import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.common.exceptions.DefinitionException;
import ch.tutteli.tsphp.common.exceptions.ReferenceException;
import ch.tutteli.tsphp.common.exceptions.TypeCheckerException;
import ch.tutteli.tsphp.typechecker.error.ErrorReporterRegistry;
import ch.tutteli.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tutteli.tsphp.typechecker.symbols.IAliasTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IArrayTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.ICanBeStatic;
import ch.tutteli.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IInterfaceTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IPolymorphicTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IScalarTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tutteli.tsphp.typechecker.symbols.IVariableSymbol;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.IErroneousSymbol;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.IErroneousTypeSymbol;
import ch.tutteli.tsphp.typechecker.utils.IAstHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class TypeCheckerController implements ITypeCheckerController
{

    private ISymbolFactory symbolFactory;
    private ISymbolResolver symbolResolver;
    private ISymbolTable symbolTable;
    private IDefiner definer;
    private IOverloadResolver overloadResolver;
    private IAstHelper astHelper;
    //
    private Map<Integer, List<IMethodSymbol>> unaryOperators = new HashMap<>();
    private Map<Integer, List<IMethodSymbol>> binaryOperators = new HashMap<>();
    //
    private IGlobalNamespaceScope globalDefaultNamespace;

    public TypeCheckerController(ISymbolFactory theSymbolFactory, ISymbolTable theSymbolTable,
            IDefiner theDefiner, ISymbolResolver theSymbolResolver, IOverloadResolver theMethodResolver,
            IAstHelper theAstHelper) {
        symbolFactory = theSymbolFactory;
        symbolTable = theSymbolTable;
        definer = theDefiner;
        symbolResolver = theSymbolResolver;
        overloadResolver = theMethodResolver;
        astHelper = theAstHelper;

        symbolTable.initTypeSystem();
        unaryOperators = symbolTable.getUnaryOperators();
        binaryOperators = symbolTable.getBinaryOperators();
        globalDefaultNamespace = definer.getGlobalDefaultNamespace();
    }

    @Override
    public IDefiner getDefiner() {
        return definer;
    }

    @Override
    public ISymbolTable getSymbolTable() {
        return symbolTable;
    }

    @Override
    public boolean checkIfInterface(ITSPHPAst typeAst, ITypeSymbol symbol) {
        boolean isInterface = symbol instanceof IInterfaceTypeSymbol || symbol instanceof IErroneousTypeSymbol;
        if (!isInterface) {
            ErrorReporterRegistry.get().interfaceExpected(typeAst);
        }
        return isInterface;
    }

    @Override
    public boolean checkIfClass(ITSPHPAst typeAst, ITypeSymbol symbol) {
        boolean isClass = symbol instanceof IClassTypeSymbol || symbol instanceof IErroneousTypeSymbol;
        if (!isClass) {
            ErrorReporterRegistry.get().classExpected(typeAst);
        }
        return isClass;
    }

    @Override
    public boolean checkForwardReference(ITSPHPAst ast) {
        ISymbol symbol = ast.getSymbol();
        boolean isNotUsedBefore = true;
        //only check if not already an error occured in conjunction with this ast (for instance missing declaration)
        if (!(symbol instanceof IErroneousSymbol)) {
            ITSPHPAst definitionAst = symbol.getDefinitionAst();
            isNotUsedBefore = definitionAst.isDefinedEarlierThan(ast);
            if (!isNotUsedBefore) {
                DefinitionException exception = ErrorReporterRegistry.get().forwardReference(ast, definitionAst);
                symbol = symbolFactory.createErroneousVariableSymbol(ast, exception);
                ast.setSymbol(symbol);
            }
        }
        return isNotUsedBefore;
    }

    @Override
    public IVariableSymbol resolveConstant(ITSPHPAst ast) {
        IVariableSymbol symbol = symbolResolver.resolveConstant(ast);

        if (symbol == null) {
            ReferenceException exception = ErrorReporterRegistry.get().notDefined(ast);
            symbol = symbolFactory.createErroneousVariableSymbol(ast, exception);
        }
        return symbol;
    }

    @Override
    public IMethodSymbol resolveStaticMethod(ITSPHPAst callee, ITSPHPAst id) {
        IMethodSymbol symbol;
        ISymbol calleeSymbol = callee.getSymbol();
        if (!(calleeSymbol instanceof IErroneousSymbol)) {
            symbol = (IMethodSymbol) resolveStatic(callee, id);
        } else {
            IErroneousSymbol erroneousSymbol = (IErroneousSymbol) calleeSymbol;
            symbol = symbolFactory.createErroneousMethodSymbol(id, erroneousSymbol.getException());
        }
        if (symbol == null) {
            ReferenceException exception = ErrorReporterRegistry.get().notDefined(id);
            symbol = symbolFactory.createErroneousMethodSymbol(id, exception);
        }
        return symbol;
    }

    private ICanBeStatic resolveStatic(ITSPHPAst callee, ITSPHPAst id) {
        IClassTypeSymbol classTypeSymbol = (IClassTypeSymbol) callee.getSymbol();
        ICanBeStatic symbol = (ICanBeStatic) classTypeSymbol.resolve(id);
        if (isDefinedButNotStatic(symbol)) {
            ErrorReporterRegistry.get().notStatic(callee);
        }
        return symbol;
    }

    private boolean isDefinedButNotStatic(ICanBeStatic symbol) {
        return symbol != null && !symbol.isStatic();
    }

    @Override
    public IVariableSymbol resolveStaticMemberOrClassConstant(ITSPHPAst accessor, ITSPHPAst id) {
        IVariableSymbol symbol;
        ISymbol accessorSymbol = accessor.getSymbol();
        if (!(accessorSymbol instanceof IErroneousSymbol)) {
            symbol = (IVariableSymbol) resolveStatic(accessor, id);
        } else {
            IErroneousSymbol erroneousSymbol = (IErroneousSymbol) accessorSymbol;
            symbol = symbolFactory.createErroneousVariableSymbol(id, erroneousSymbol.getException());
        }
        if (symbol == null) {
            ReferenceException exception = ErrorReporterRegistry.get().notDefined(id);
            symbol = symbolFactory.createErroneousVariableSymbol(id, exception);
        }
        return symbol;
    }

    @Override
    public IMethodSymbol resolveFunction(ITSPHPAst ast) {
        ISymbol symbol = symbolResolver.resolveGlobalIdentifierWithFallback(ast);
        if (symbol == null) {
            ReferenceException exception = ErrorReporterRegistry.get().notDefined(ast);
            symbol = symbolFactory.createErroneousMethodSymbol(ast, exception);
        }
        return (IMethodSymbol) symbol;
    }

    @Override
    public IVariableSymbol resolveClassConstant(ITSPHPAst ast) {
        return resolveClassMember(ast);
    }

    private ISymbol resolveInClassSymbol(ITSPHPAst ast) {
        IClassTypeSymbol classTypeSymbol = getEnclosingClass(ast);
        ISymbol symbol;
        if (classTypeSymbol != null) {
            symbol = classTypeSymbol.resolveWithFallbackToParent(ast);
        } else {
            ReferenceException exception = ErrorReporterRegistry.get().notInClass(ast);
            symbol = symbolFactory.createErroneousAccessSymbol(ast, exception);
        }
        return symbol;
    }

    @Override
    public IVariableSymbol resolveClassMember(ITSPHPAst ast) {
        ISymbol symbol = resolveInClassSymbol(ast);
        if (symbol == null) {
            ReferenceException exception = ErrorReporterRegistry.get().notDefined(ast);
            symbol = symbolFactory.createErroneousVariableSymbol(ast, exception);
        }
        return (IVariableSymbol) symbol;
    }

    @Override
    public IMethodSymbol resolveMethod(ITSPHPAst callee, ITSPHPAst id) {
        IMethodSymbol methodSymbol;

        IVariableSymbol variableSymbol = (IVariableSymbol) callee.getSymbol();
        if (!(variableSymbol instanceof IErroneousSymbol)) {
            ITypeSymbol typeSymbol = variableSymbol.getType();
            if (!(typeSymbol instanceof IErroneousSymbol)) {
                methodSymbol = resolveMethod(typeSymbol, callee, id);
            } else {
                methodSymbol = symbolFactory.createErroneousMethodSymbol(id,
                        ((IErroneousSymbol) typeSymbol).getException());
            }
        } else {
            methodSymbol = symbolFactory.createErroneousMethodSymbol(id,
                    ((IErroneousSymbol) variableSymbol).getException());
        }
        if (methodSymbol == null) {
            DefinitionException exception = ErrorReporterRegistry.get().methodNotDefined(callee, id);
            methodSymbol = symbolFactory.createErroneousMethodSymbol(id, exception);
        }
        return methodSymbol;
    }

    private IMethodSymbol resolveMethod(ITypeSymbol typeSymbol, ITSPHPAst callee, ITSPHPAst id) {
        IMethodSymbol methodSymbol;
        if (typeSymbol instanceof IPolymorphicTypeSymbol) {
            IPolymorphicTypeSymbol inheritableTypeSymbol = (IPolymorphicTypeSymbol) typeSymbol;
            methodSymbol = (IMethodSymbol) inheritableTypeSymbol.resolveWithFallbackToParent(id);
        } else {
            DefinitionException exception = ErrorReporterRegistry.get().objectExpected(callee,
                    typeSymbol.getDefinitionAst());

            methodSymbol = symbolFactory.createErroneousMethodSymbol(id, exception);
        }
        return methodSymbol;
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
            ReferenceException exception = ErrorReporterRegistry.get().notInClass($this);
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
            ReferenceException exception = ErrorReporterRegistry.get().notDefined(ast);
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
            ReferenceException ex = ErrorReporterRegistry.get().unkownType(typeAst);
            symbol = symbolFactory.createErroneousTypeSymbol(typeAst, ex);

        } else if (symbol instanceof IAliasTypeSymbol) {

            typeAst.setText(symbol.getName());
            ReferenceException ex = ErrorReporterRegistry.get().unkownType(typeAst);
            symbol = symbolFactory.createErroneousTypeSymbol(symbol.getDefinitionAst(), ex);
        }
        return symbol;
    }

    /**
     * Return the absolute name of a type which could not be found (prefix the enclosing namespace)
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
            ReferenceException ex = ErrorReporterRegistry.get().unkownType(typeAst);
            typeSymbol = symbolFactory.createErroneousTypeSymbol(typeAst, ex);

        }
        return typeSymbol;
    }

    private IClassTypeSymbol getEnclosingClass(ITSPHPAst ast) {
        IClassTypeSymbol classTypeSymbol = symbolResolver.getEnclosingClass(ast);
        if (classTypeSymbol == null) {
            ReferenceException ex = ErrorReporterRegistry.get().notInClass(ast);
            classTypeSymbol = symbolFactory.createErroneousClassSymbol(ast, ex);
        }
        return classTypeSymbol;
    }

    private IClassTypeSymbol getParent(ITSPHPAst ast) {
        IClassTypeSymbol classTypeSymbol = getEnclosingClass(ast);
        IClassTypeSymbol parent = classTypeSymbol.getParent();
        if (parent == null) {
            TypeCheckerException ex = ErrorReporterRegistry.get().noParentClass(classTypeSymbol.getDefinitionAst());
            parent = symbolFactory.createErroneousClassSymbol(ast, ex);
        }
        return parent;
    }

    @Override
    public ITypeSymbol getBinaryOperatorEvalType(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right) {
        ITypeSymbol typeSymbol;

        IErroneousTypeSymbol erroneousTypeSymbol = getFirstErroneousTypeSymbol(left.getEvalType(), right.getEvalType());

        if (erroneousTypeSymbol == null) {
            //TODO Code duplication -> change behaviour, use "delegate" to reduce code duplication
            if (binaryOperators.containsKey(operator.getToken().getType())) {
                try {
                    typeSymbol = resolveEvalType(operator, left, right);
                } catch (TypeCheckerException ex) {
                    //Error reporting is done in resolveBinary
                    typeSymbol = symbolFactory.createErroneousTypeSymbol(operator, ex);
                }
            } else {
                TypeCheckerException exception = ErrorReporterRegistry.get().unsupportedOperator(operator);
                typeSymbol = symbolFactory.createErroneousTypeSymbol(operator, exception);
            }

        } else {
            typeSymbol = erroneousTypeSymbol;
        }

        return typeSymbol;
    }

    private IErroneousTypeSymbol getFirstErroneousTypeSymbol(ITypeSymbol... actualParameterTypes) {
        IErroneousTypeSymbol erroneousTypeSymbol = null;
        for (ITypeSymbol typeSymbol : actualParameterTypes) {
            if (typeSymbol instanceof IErroneousTypeSymbol) {
                erroneousTypeSymbol = (IErroneousTypeSymbol) typeSymbol;
                break;
            }
        }
        return erroneousTypeSymbol;
    }

    private ITypeSymbol resolveEvalType(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right)
            throws TypeCheckerException {
        ITypeSymbol typeSymbol = null;
        OverloadDto methodDto;

        try {
            methodDto = resolveBinaryOperator(operator, left, right);
        } catch (AmbiguousCallException ex) {
            ErrorReporterRegistry.get().ambiguousBinaryOperatorUsage(operator, left, right, ex);
            methodDto = ex.getAmbiguousOverloads().get(0);
        }
        if (methodDto != null) {
            typeSymbol = methodDto.methodSymbol.getType();
            if (methodDto.parametersNeedCasting != null) {
                addCastingsToAst(methodDto.parametersNeedCasting);
            }
        }
        return typeSymbol;
    }

    private OverloadDto resolveBinaryOperator(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right)
            throws AmbiguousCallException, TypeCheckerException {


        int tokenType = operator.getToken().getType();
        List<ITSPHPAst> actualParameterTypes = new ArrayList<>();
        actualParameterTypes.add(left);
        actualParameterTypes.add(right);

        OverloadDto methodDto = null;

        List<IMethodSymbol> methods = binaryOperators.get(tokenType);
        List<OverloadDto> goodMethods = overloadResolver.getApplicableOverloads(methods, actualParameterTypes);
        if (!goodMethods.isEmpty()) {
            methodDto = overloadResolver.getMostSpecificApplicableMethod(goodMethods);
        }
        if (methodDto == null) {
            throw ErrorReporterRegistry.get().wrongBinaryOperatorUsage(operator, left, right, methods);
        }
        return methodDto;
    }

    private void addCastingsToAst(List<CastingDto> parametersNeedCasting) {
        for (CastingDto parameterPromotionDto : parametersNeedCasting) {
            astHelper.prependCasting(parameterPromotionDto);
        }
    }

    //TODO code duplication !!!!!!!!!!!!!!!!!
    @Override
    public ITypeSymbol getUnaryOperatorEvalType(ITSPHPAst operator, ITSPHPAst expression) {
        ITypeSymbol typeSymbol;
        if (unaryOperators.containsKey(operator.getToken().getType())) {
            try {
                typeSymbol = resolveEvalType(operator, expression);
            } catch (TypeCheckerException ex) {
                //Error reporting is done in resolveBinary
                typeSymbol = symbolFactory.createErroneousTypeSymbol(expression, ex);
            }
        } else {
            TypeCheckerException exception = ErrorReporterRegistry.get().unsupportedOperator(operator);
            typeSymbol = symbolFactory.createErroneousTypeSymbol(operator, exception);
        }
        return typeSymbol;
    }

    //TODO code duplication !!!!!!!!!!!!!!!!!
    private ITypeSymbol resolveEvalType(ITSPHPAst operator, ITSPHPAst expression)
            throws TypeCheckerException {
        ITypeSymbol typeSymbol = null;
        OverloadDto methodDto;

        try {
            methodDto = resolveUnaryOperator(operator, expression);
        } catch (AmbiguousCallException ex) {
            ErrorReporterRegistry.get().ambiguousUnaryOperatorUsage(operator, expression, ex);
            methodDto = ex.getAmbiguousOverloads().get(0);
        }
        if (methodDto != null) {
            typeSymbol = methodDto.methodSymbol.getType();
            if (methodDto.parametersNeedCasting != null) {
                addCastingsToAst(methodDto.parametersNeedCasting);
            }
        }
        return typeSymbol;
    }

    //TODO code duplication !!!!!!!!!!!!!!!!!
    private OverloadDto resolveUnaryOperator(ITSPHPAst operator, ITSPHPAst expression)
            throws AmbiguousCallException, TypeCheckerException {

        List<ITSPHPAst> actualParameters = new ArrayList<>();
        actualParameters.add(expression);

        int tokenType = operator.getToken().getType();

        OverloadDto methodDto = null;

        List<IMethodSymbol> methods = unaryOperators.get(tokenType);
        List<OverloadDto> goodMethods = overloadResolver.getApplicableOverloads(methods, actualParameters);
        if (!goodMethods.isEmpty()) {
            methodDto = overloadResolver.getMostSpecificApplicableMethod(goodMethods);
        }
        if (methodDto == null) {
            throw ErrorReporterRegistry.get().wrongUnaryOperatorUsage(operator, expression, methods);
        }
        return methodDto;
    }

    @Override
    public void checkEquality(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right) {
        if (areNotSameAndNoneIsSubType(left, right)) {

            IVariableSymbol leftSymbol = getVariableSymbolFromExpression(left);
            IVariableSymbol rightSymbol = getVariableSymbolFromExpression(right);

            CastingDto leftToRight = overloadResolver.getCastingDtoAlwaysCasting(rightSymbol, left);
            CastingDto rightToLeft = overloadResolver.getCastingDtoAlwaysCasting(leftSymbol, right);

            if (leftToRight != null && rightToLeft != null) {

                ErrorReporterRegistry.get().operatorAmbiguousCasts(operator, left, right, leftToRight, rightToLeft,
                        leftToRight.ambiguousCasts, rightToLeft.ambiguousCasts);

            } else if (leftToRight == null && rightToLeft == null) {

                ErrorReporterRegistry.get().wrongEqualityUsage(operator, left, right);

            } else if (leftToRight == null
                    && rightToLeft.ambiguousCasts != null && !rightToLeft.ambiguousCasts.isEmpty()) {

                ErrorReporterRegistry.get().ambiguousCasts(operator, left, right, rightToLeft.ambiguousCasts);

            } else if (rightToLeft == null
                    && leftToRight.ambiguousCasts != null && !leftToRight.ambiguousCasts.isEmpty()) {

                ErrorReporterRegistry.get().ambiguousCasts(operator, left, right, leftToRight.ambiguousCasts);

            }
        }
    }

    private IVariableSymbol getVariableSymbolFromExpression(ITSPHPAst expression) {
        IVariableSymbol variableSymbol;

        ISymbol symbol = expression.getSymbol();
        if (symbol != null) {
            variableSymbol = (IVariableSymbol) symbol;
        } else {
            variableSymbol = symbolFactory.createVariableSymbol(null, expression);
            variableSymbol.setType(expression.getEvalType());
        }
        return variableSymbol;
    }

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
                        ErrorReporterRegistry.get().ambiguousCasts(operator, left, right, castingDto.ambiguousCasts);
                    }
                } else {
                    ErrorReporterRegistry.get().wrongAssignment(operator, left, right);
                }
            } else {
                ErrorReporterRegistry.get().variableExpected(left);
            }
        }
    }

    private boolean areNotErroneousTypes(ITSPHPAst left, ITSPHPAst right) {
        return areNotErroneousTypes(left.getEvalType(), right.getEvalType());

    }

    private boolean areNotErroneousTypes(ITypeSymbol leftType, ITypeSymbol rightType) {
        IErroneousTypeSymbol erroneousTypeSymbol = getFirstErroneousTypeSymbol(leftType, rightType);
        return erroneousTypeSymbol == null;
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

    @Override
    public void checkPrePostIncrementDecrement(ITSPHPAst operator, ITSPHPAst expression) {
        if (!(expression.getSymbol() instanceof IVariableSymbol)) {
            ErrorReporterRegistry.get().variableExpected(expression);
        }
    }

    @Override
    public void checkIdentity(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right) {
        if (areNotSameAndNoneIsSubType(left, right)) {
            ErrorReporterRegistry.get().wrongIdentityUsage(operator, left, right);
        }

    }

    @Override
    public void checkCast(final ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right) {
        if (right.getEvalType() instanceof IErroneousSymbol) {
            operator.setText("==");

            IVariableSymbol leftSymbol = getVariableSymbolFromExpression(left);
            CastingDto rightToLeft = overloadResolver.getCastingDtoAlwaysCasting(leftSymbol, right);
            if (rightToLeft == null) {
                ErrorReporterRegistry.get().wrongCast(operator, left, right);
            } else if (rightToLeft.ambiguousCasts != null && !rightToLeft.ambiguousCasts.isEmpty()) {
                ErrorReporterRegistry.get().ambiguousCasts(operator, left, right, rightToLeft.ambiguousCasts);
            }
            operator.setText("casting");
        }
    }

    @Override
    public void checkCastAssignment(ITSPHPAst operator, ITSPHPAst left, ITSPHPAst right) {
        checkCast(operator, left, right);
        operator.setText("=");
    }

    @Override
    public void checkIf(final ITSPHPAst ifRoot, final ITSPHPAst expression) {
        final ITypeSymbol typeSymbol = symbolTable.getBoolTypeSymbol();
        checkIsSameOrSubType(expression, typeSymbol, new IErrorReporterCaller()
        {
            @Override
            public void callAppropriateMethod() {
                ErrorReporterRegistry.get().wrongTypeIf(ifRoot, expression, typeSymbol);
            }
        });
    }

    private void checkIsSameOrSubType(ITSPHPAst expression, ITypeSymbol typeSymbol,
            IErrorReporterCaller caller) {
        ITypeSymbol expressionType = expression.getEvalType();
        if (areNotErroneousTypes(expressionType, typeSymbol)) {
            int promotionCount = overloadResolver.getPromotionLevelFromTo(expressionType, typeSymbol);
            if (!overloadResolver.isSameOrParentType(promotionCount)) {
                caller.callAppropriateMethod();
            }
        }
    }

    @Override
    public void checkSwitch(final ITSPHPAst switchRoot, final ITSPHPAst expression) {
        final ITypeSymbol typeSymbol = symbolTable.getStringNullableTypeSymbol();
        checkIsSameOrSubType(expression, typeSymbol, new IErrorReporterCaller()
        {
            @Override
            public void callAppropriateMethod() {
                ErrorReporterRegistry.get().wrongTypeSwitch(switchRoot, expression, typeSymbol);
            }
        });
    }

    @Override
    public void checkSwitchCase(final ITSPHPAst switchRoot, final ITSPHPAst switchCase) {
        final ITypeSymbol typeSymbol = switchRoot.getEvalType();
        checkIsSameOrSubType(switchCase, typeSymbol, new IErrorReporterCaller()
        {
            @Override
            public void callAppropriateMethod() {
                ErrorReporterRegistry.get().wrongTypeSwitchCase(switchRoot, switchCase, typeSymbol);
            }
        });
    }

    @Override
    public void checkFor(final ITSPHPAst forRoot, final ITSPHPAst expression) {
        final ITypeSymbol typeSymbol = symbolTable.getBoolTypeSymbol();
        checkIsSameOrSubType(expression, typeSymbol, new IErrorReporterCaller()
        {
            @Override
            public void callAppropriateMethod() {
                ErrorReporterRegistry.get().wrongTypeFor(forRoot, expression, typeSymbol);
            }
        });
    }

    @Override
    public void checkForeach(ITSPHPAst foreachRoot, ITSPHPAst array,
            ITSPHPAst keyVariableId, ITSPHPAst valueVariableId) {

        ITypeSymbol keyTypeSymbol = null;
        ITypeSymbol valueTypeSymbol = null;
        ITypeSymbol evalType = array.getEvalType();
        if (!(evalType instanceof IErroneousSymbol)) {
            IArrayTypeSymbol arrayTypeSymbol = symbolTable.getArrayTypeSymbol();
            int promotionCount = overloadResolver.getPromotionLevelFromTo(evalType, arrayTypeSymbol);
            if (overloadResolver.isSameOrParentType(promotionCount)) {
                IArrayTypeSymbol arrayType = (IArrayTypeSymbol) evalType;
                keyTypeSymbol = arrayType.getKeyTypeSymbol();
                valueTypeSymbol = arrayType.getValueTypeSymbol();
            } else {
                ErrorReporterRegistry.get().wrongTypeForeach(foreachRoot, array, arrayTypeSymbol);
            }
        }
        if (keyVariableId != null) {
            if (keyTypeSymbol == null) {
                keyTypeSymbol = symbolTable.getStringTypeSymbol();
            }
            checkIsSameOrParentType(keyVariableId, keyVariableId, keyTypeSymbol);
        }
        if (valueTypeSymbol != null) {
            checkIsSameOrParentType(valueVariableId, valueVariableId, valueTypeSymbol);
        }
    }

    private void checkIsSameOrParentType(ITSPHPAst statement, ITSPHPAst expression, ITypeSymbol typeSymbol) {
        ITypeSymbol expressionType = expression.getEvalType();
        if (areNotErroneousTypes(expressionType, typeSymbol)) {
            if (!overloadResolver.isSameOrParentTypeConsiderNull(expressionType, typeSymbol)) {
                ErrorReporterRegistry.get().notSameOrParentType(statement, expression, typeSymbol);
            }
        }
    }

    @Override
    public void checkWhile(final ITSPHPAst whileRoot, final ITSPHPAst expression) {
        final ITypeSymbol typeSymbol = symbolTable.getBoolTypeSymbol();
        checkIsSameOrSubType(expression, typeSymbol, new IErrorReporterCaller()
        {
            @Override
            public void callAppropriateMethod() {
                ErrorReporterRegistry.get().wrongTypeWhile(whileRoot, expression, typeSymbol);
            }
        });
    }

    @Override
    public void checkDoWhile(final ITSPHPAst doWhileRoot, final ITSPHPAst expression) {
        final ITypeSymbol typeSymbol = symbolTable.getBoolTypeSymbol();
        checkIsSameOrSubType(expression, typeSymbol, new IErrorReporterCaller()
        {
            @Override
            public void callAppropriateMethod() {
                ErrorReporterRegistry.get().wrongTypeDoWhile(doWhileRoot, expression, typeSymbol);
            }
        });
    }

    
    @Override
    public void checkThrow(final ITSPHPAst throwRoot, final ITSPHPAst expression) {
        final ITypeSymbol typeSymbol = symbolTable.getExceptionTypeSymbol();
        checkIsSameOrSubType(expression, typeSymbol, new IErrorReporterCaller()
        {
            @Override
            public void callAppropriateMethod() {
                ErrorReporterRegistry.get().wrongTypeThrow(throwRoot, expression, typeSymbol);
            }
        });
    }
 
    @Override
    public ITypeSymbol getReturnTypeArrayAccess(ITSPHPAst statement, final ITSPHPAst expression, final ITSPHPAst index) {

        ITypeSymbol returnTypeArrayAccess;

        ITypeSymbol keyTypeSymbol = null;
        ITypeSymbol evalType = expression.getEvalType();
        if (!(evalType instanceof IErroneousSymbol)) {
            IArrayTypeSymbol arrayTypeSymbol = symbolTable.getArrayTypeSymbol();
            int promotionCount = overloadResolver.getPromotionLevelFromTo(evalType, arrayTypeSymbol);
            if (overloadResolver.isSameOrParentType(promotionCount)) {
                IArrayTypeSymbol arrayType = (IArrayTypeSymbol) evalType;
                keyTypeSymbol = arrayType.getKeyTypeSymbol();
                returnTypeArrayAccess = arrayType.getValueTypeSymbol();
            } else {
                ReferenceException exception = ErrorReporterRegistry.get().arrayExpected(expression, arrayTypeSymbol);
                returnTypeArrayAccess = symbolFactory.createErroneousTypeSymbol(statement, exception);
            }
        } else {
            returnTypeArrayAccess = evalType;
        }

        if (keyTypeSymbol == null) {
            keyTypeSymbol = symbolTable.getStringTypeSymbol();
        }

        final ITypeSymbol typeSymbol = keyTypeSymbol;
        checkIsSameOrSubType(index, typeSymbol, new IErrorReporterCaller()
        {
            @Override
            public void callAppropriateMethod() {
                ErrorReporterRegistry.get().wrongArrayIndexType(expression, index, typeSymbol);
            }
        });

        return returnTypeArrayAccess;
    }

    /**
     * A "Delegate" which represents a call of a method of an IErrrorReporter
     */
    private interface IErrorReporterCaller
    {

        void callAppropriateMethod();
    }
}