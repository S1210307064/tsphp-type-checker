/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.unit;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.ITypeSymbol;
import ch.tsphp.common.exceptions.TypeCheckerException;
import ch.tsphp.typechecker.IAccessResolver;
import ch.tsphp.typechecker.IOverloadResolver;
import ch.tsphp.typechecker.ISymbolResolver;
import ch.tsphp.typechecker.ITypeCheckPhaseController;
import ch.tsphp.typechecker.ITypeSystem;
import ch.tsphp.typechecker.TypeCheckPhaseController;
import ch.tsphp.typechecker.error.ITypeCheckerErrorReporter;
import ch.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tsphp.typechecker.symbols.erroneous.IErroneousMethodSymbol;
import ch.tsphp.typechecker.symbols.erroneous.IErroneousTypeSymbol;
import ch.tsphp.typechecker.utils.ITypeCheckerAstHelper;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class TypeCheckPhaseControllerErroneousSymbolTest
{
    private ISymbolFactory symbolFactory;
    private ISymbolResolver symbolResolver;
    private ITypeSystem typeSystem;
    private IOverloadResolver overloadResolver;
    private IAccessResolver accessResolver;
    private ITypeCheckerAstHelper astHelper;
    private ITypeCheckerErrorReporter typeCheckerErrorReporter;

    @Before
    public void setUp() {
        symbolFactory = mock(ISymbolFactory.class);
        symbolResolver = mock(ISymbolResolver.class);
        typeSystem = mock(ITypeSystem.class);
        overloadResolver = mock(IOverloadResolver.class);
        accessResolver = mock(IAccessResolver.class);
        astHelper = mock(ITypeCheckerAstHelper.class);
        typeCheckerErrorReporter = mock(ITypeCheckerErrorReporter.class);
    }

    @Test
    public void resolveMethodCall_CalleeIsErroneousTypeSymbol_UseSymbolFactoryToCreateErroneousMethodSymbolAssignType
            () {
        ITSPHPAst callee = mock(ITSPHPAst.class);
        IErroneousTypeSymbol typeSymbol = mock(IErroneousTypeSymbol.class);
        when(callee.getEvalType()).thenReturn(typeSymbol);
        TypeCheckerException exception = new TypeCheckerException();
        when(typeSymbol.getException()).thenReturn(exception);
        ITSPHPAst identifier = mock(ITSPHPAst.class);
        IErroneousMethodSymbol methodSymbol = mock(IErroneousMethodSymbol.class);
        when(symbolFactory.createErroneousMethodSymbol(identifier, exception)).thenReturn(methodSymbol);

        ITypeCheckPhaseController typeCheckerController = createTypeCheckController();
        IMethodSymbol result = typeCheckerController.resolveMethodCall(callee, identifier, mock(ITSPHPAst.class));

        assertThat(result, is((IMethodSymbol) methodSymbol));
        verify(methodSymbol).setType(typeSymbol);
        verifyNoMoreInteractions(typeCheckerErrorReporter);
    }

    @Test
    public void resolveStaticMethodCall_CalleeIsErroneousTypeSymbol_SetErroneousTypeSymbolToReturnedMethodSymbol() {
        ITSPHPAst callee = mock(ITSPHPAst.class);
        IErroneousTypeSymbol typeSymbol = mock(IErroneousTypeSymbol.class);
        when(callee.getEvalType()).thenReturn(typeSymbol);
        TypeCheckerException exception = new TypeCheckerException();
        when(typeSymbol.getException()).thenReturn(exception);
        ITSPHPAst identifier = mock(ITSPHPAst.class);
        IErroneousMethodSymbol methodSymbol = mock(IErroneousMethodSymbol.class);
        when(symbolFactory.createErroneousMethodSymbol(identifier, exception)).thenReturn(methodSymbol);
        when(methodSymbol.isStatic()).thenReturn(true);

        ITypeCheckPhaseController typeCheckerController = createTypeCheckController();
        IMethodSymbol result = typeCheckerController.resolveStaticMethodCall(callee, identifier, mock(ITSPHPAst.class));

        assertThat(result, is((IMethodSymbol) methodSymbol));
        verify(methodSymbol).setType(typeSymbol);
        verifyNoMoreInteractions(typeCheckerErrorReporter);
    }

    @Test
    public void checkEquality_LeftIsErroneousTypeSymbol_DoesNotCheckAnything() {
        doesNotCheckAnythingTest(mock(IErroneousTypeSymbol.class), mock(ITypeSymbol.class),
                new IDoesNotCheckAnythingDelegate()
                {
                    @Override
                    public void check(ITypeCheckPhaseController typeCheckerController, ITSPHPAst left,
                            ITSPHPAst right) {
                        typeCheckerController.checkEquality(mock(ITSPHPAst.class), left, right);
                    }
                });
    }

    @Test
    public void checkEquality_RightIsErroneousTypeSymbol_DoesNotCheckAnything() {
        doesNotCheckAnythingTest(mock(ITypeSymbol.class), mock(IErroneousTypeSymbol.class),
                new IDoesNotCheckAnythingDelegate()
                {
                    @Override
                    public void check(ITypeCheckPhaseController typeCheckerController, ITSPHPAst left,
                            ITSPHPAst right) {
                        typeCheckerController.checkEquality(mock(ITSPHPAst.class), left, right);
                    }
                });
    }


    @Test
    public void checkEquality_LeftAndRightIsErroneousTypeSymbol_DoesNotCheckAnything() {
        doesNotCheckAnythingTest(mock(IErroneousTypeSymbol.class), mock(IErroneousTypeSymbol.class),
                new IDoesNotCheckAnythingDelegate()
                {
                    @Override
                    public void check(ITypeCheckPhaseController typeCheckerController, ITSPHPAst left,
                            ITSPHPAst right) {
                        typeCheckerController.checkEquality(mock(ITSPHPAst.class), left, right);
                    }
                });
    }

    @Test
    public void checkAssignment_LeftIsErroneousTypeSymbol_DoesNotCheckAnything() {
        doesNotCheckAnythingTest(mock(IErroneousTypeSymbol.class), mock(ITypeSymbol.class),
                new IDoesNotCheckAnythingDelegate()
                {
                    @Override
                    public void check(ITypeCheckPhaseController typeCheckerController, ITSPHPAst left,
                            ITSPHPAst right) {
                        typeCheckerController.checkAssignment(mock(ITSPHPAst.class), left, right);
                    }
                });
    }

    @Test
    public void checkAssignment_RightIsErroneousTypeSymbol_DoesNotCheckAnything() {
        doesNotCheckAnythingTest(mock(ITypeSymbol.class), mock(IErroneousTypeSymbol.class),
                new IDoesNotCheckAnythingDelegate()
                {
                    @Override
                    public void check(ITypeCheckPhaseController typeCheckerController, ITSPHPAst left,
                            ITSPHPAst right) {
                        typeCheckerController.checkAssignment(mock(ITSPHPAst.class), left, right);
                    }
                });
    }

    @Test
    public void checkAssignment_LeftAndRightIsErroneousTypeSymbol_DoesNotCheckAnything() {
        doesNotCheckAnythingTest(mock(IErroneousTypeSymbol.class), mock(IErroneousTypeSymbol.class),
                new IDoesNotCheckAnythingDelegate()
                {
                    @Override
                    public void check(ITypeCheckPhaseController typeCheckerController, ITSPHPAst left,
                            ITSPHPAst right) {
                        typeCheckerController.checkAssignment(mock(ITSPHPAst.class), left, right);
                    }
                });
    }

    @Test
    public void checkIdentity_LeftIsErroneousTypeSymbol_DoesNotCheckAnything() {
        doesNotCheckAnythingTest(mock(IErroneousTypeSymbol.class), mock(ITypeSymbol.class),
                new IDoesNotCheckAnythingDelegate()
                {
                    @Override
                    public void check(ITypeCheckPhaseController typeCheckerController, ITSPHPAst left,
                            ITSPHPAst right) {
                        typeCheckerController.checkIdentity(mock(ITSPHPAst.class), left, right);
                    }
                });
    }

    @Test
    public void checkIdentity_RightIsErroneousTypeSymbol_DoesNotCheckAnything() {
        doesNotCheckAnythingTest(mock(ITypeSymbol.class), mock(IErroneousTypeSymbol.class),
                new IDoesNotCheckAnythingDelegate()
                {
                    @Override
                    public void check(ITypeCheckPhaseController typeCheckerController, ITSPHPAst left,
                            ITSPHPAst right) {
                        typeCheckerController.checkIdentity(mock(ITSPHPAst.class), left, right);
                    }
                });
    }

    @Test
    public void checkIdentity_LeftAndRightIsErroneousTypeSymbol_DoesNotCheckAnything() {
        doesNotCheckAnythingTest(mock(IErroneousTypeSymbol.class), mock(IErroneousTypeSymbol.class),
                new IDoesNotCheckAnythingDelegate()
                {
                    @Override
                    public void check(ITypeCheckPhaseController typeCheckerController, ITSPHPAst left,
                            ITSPHPAst right) {
                        typeCheckerController.checkIdentity(mock(ITSPHPAst.class), left, right);
                    }
                });
    }

    protected ITypeCheckPhaseController createTypeCheckController() {
        return new TypeCheckPhaseController(
                symbolFactory, symbolResolver, typeCheckerErrorReporter, typeSystem, overloadResolver, accessResolver, astHelper);
    }


    private void doesNotCheckAnythingTest(ITypeSymbol leftTypeSymbol, ITypeSymbol rightTypeSymbol,
            IDoesNotCheckAnythingDelegate delegate) {
        ITSPHPAst left = mock(ITSPHPAst.class);
        ITSPHPAst right = mock(ITSPHPAst.class);
        when(left.getEvalType()).thenReturn(leftTypeSymbol);
        when(right.getEvalType()).thenReturn(rightTypeSymbol);

        ITypeCheckPhaseController typeCheckerController = createTypeCheckController();
        delegate.check(typeCheckerController, left, right);

        verify(left).getEvalType();
        verify(right).getEvalType();
        verifyNoMoreInteractions(
                left,
                right,
                leftTypeSymbol,
                rightTypeSymbol,
                typeCheckerErrorReporter
        );
    }

    private interface IDoesNotCheckAnythingDelegate
    {
        public void check(ITypeCheckPhaseController typeCheckerController,
                ITSPHPAst leftTypeSymbol, ITSPHPAst rightTypeSymbol);
    }

}
