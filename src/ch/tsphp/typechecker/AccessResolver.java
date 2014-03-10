/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.ITypeSymbol;
import ch.tsphp.common.exceptions.DefinitionException;
import ch.tsphp.common.exceptions.ReferenceException;
import ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tsphp.typechecker.error.ITypeCheckerErrorReporter;
import ch.tsphp.typechecker.symbols.IPolymorphicTypeSymbol;
import ch.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tsphp.typechecker.symbols.ISymbolWithAccessModifier;
import ch.tsphp.typechecker.symbols.IVariableSymbol;
import ch.tsphp.typechecker.symbols.erroneous.IErroneousSymbol;

public class AccessResolver implements IAccessResolver
{

    private final ISymbolFactory symbolFactory;
    private final ITypeCheckerErrorReporter typeCheckErrorReporter;

    public AccessResolver(ISymbolFactory theSymbolFactory, ITypeCheckerErrorReporter theTypeCheckerErrorReporter) {
        symbolFactory = theSymbolFactory;
        typeCheckErrorReporter = theTypeCheckerErrorReporter;
    }

    @Override
    public IVariableSymbol resolveClassConstantAccess(ITSPHPAst accessor, ITSPHPAst identifier) {
        return resolveStaticClassMemberOrClassConstant(accessor, identifier, new IViolationCaller()
        {
            @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
            @Override
            public void callAppropriateMethod(ITSPHPAst identifier, ISymbolWithAccessModifier symbol, int accessFrom) {
                typeCheckErrorReporter.visibilityViolationClassConstantAccess(
                        identifier, symbol, accessFrom);
            }
        });
    }

    @Override
    public IVariableSymbol resolveStaticMemberAccess(ITSPHPAst accessor, ITSPHPAst identifier) {
        return resolveStaticClassMemberOrClassConstant(accessor, identifier, new IViolationCaller()
        {
            @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
            @Override
            public void callAppropriateMethod(ITSPHPAst identifier, ISymbolWithAccessModifier symbol, int accessFrom) {
                typeCheckErrorReporter.visibilityViolationStaticClassMemberAccess(
                        identifier, symbol, accessFrom);
            }
        });
    }


    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private IVariableSymbol resolveStaticClassMemberOrClassConstant(ITSPHPAst accessor, ITSPHPAst id,
            IViolationCaller caller) {
        IVariableSymbol variableSymbol = checkAccessorAndResolveAccess(accessor, id, caller);
        if (!variableSymbol.isStatic()) {
            typeCheckErrorReporter.notStatic(accessor);
        }
        return variableSymbol;
    }

    @Override
    public IVariableSymbol resolveClassMemberAccess(ITSPHPAst expression, ITSPHPAst identifier) {
        String variableName = identifier.getText();
        identifier.setText("$" + variableName);
        IViolationCaller visibilityViolationCaller = new IViolationCaller()
        {
            @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
            @Override
            public void callAppropriateMethod(ITSPHPAst identifier, ISymbolWithAccessModifier symbol, int accessFrom) {
                typeCheckErrorReporter.visibilityViolationClassMemberAccess(
                        identifier, symbol, accessFrom);
            }
        };
        IVariableSymbol variableSymbol = checkAccessorAndResolveAccess(
                expression, identifier, visibilityViolationCaller);

        identifier.setText(variableName);
        return variableSymbol;
    }

    private IVariableSymbol checkAccessorAndResolveAccess(
            ITSPHPAst accessor, ITSPHPAst identifier, IViolationCaller visibilityViolationCaller) {

        IVariableSymbol variableSymbol;
        ITypeSymbol evalType = accessor.getEvalType();
        if (!(evalType instanceof IErroneousSymbol)) {
            if (evalType instanceof IPolymorphicTypeSymbol) {
                variableSymbol = resolveAccess(
                        (IPolymorphicTypeSymbol) evalType, accessor, identifier, visibilityViolationCaller);
            } else {
                ReferenceException exception = typeCheckErrorReporter.wrongTypeClassMemberAccess
                        (identifier);
                variableSymbol = symbolFactory.createErroneousVariableSymbol(identifier, exception);
                variableSymbol.setType(symbolFactory.createErroneousTypeSymbol(identifier, exception));
            }
        } else {
            IErroneousSymbol erroneousSymbol = (IErroneousSymbol) evalType;
            variableSymbol = symbolFactory.createErroneousVariableSymbol(accessor, erroneousSymbol.getException());
            variableSymbol.setType(evalType);
        }
        return variableSymbol;
    }

    private IVariableSymbol resolveAccess(IPolymorphicTypeSymbol polymorphicTypeSymbol,
            ITSPHPAst accessor, ITSPHPAst identifier, IViolationCaller visibilityViolationCaller) {

        IVariableSymbol symbol = (IVariableSymbol) polymorphicTypeSymbol.resolveWithFallbackToParent(identifier);

        if (symbol != null) {
            checkVisibility(symbol, polymorphicTypeSymbol, visibilityViolationCaller, accessor, identifier);
        } else {
            DefinitionException exception = typeCheckErrorReporter.memberNotDefined(accessor, identifier);
            symbol = symbolFactory.createErroneousVariableSymbol(identifier, exception);
        }
        return symbol;
    }

    @Override
    public void checkVisibility(ISymbolWithAccessModifier methodSymbol, IPolymorphicTypeSymbol polymorphicTypeSymbol,
            IViolationCaller visibilityViolationCaller, ITSPHPAst calleeOrAccessor, ITSPHPAst identifier) {

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
}