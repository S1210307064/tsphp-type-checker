package ch.tutteli.tsphp.typechecker.test.unit;

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.common.exceptions.TypeCheckerException;
import ch.tutteli.tsphp.typechecker.IOverloadResolver;
import ch.tutteli.tsphp.typechecker.ISymbolResolver;
import ch.tutteli.tsphp.typechecker.ITypeCheckPhaseController;
import ch.tutteli.tsphp.typechecker.ITypeSystem;
import ch.tutteli.tsphp.typechecker.IVisibilityChecker;
import ch.tutteli.tsphp.typechecker.TypeCheckPhaseController;
import ch.tutteli.tsphp.typechecker.error.ITypeCheckErrorReporter;
import ch.tutteli.tsphp.typechecker.error.TypeCheckErrorReporterRegistry;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.IErroneousMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.IErroneousTypeSymbol;
import ch.tutteli.tsphp.typechecker.utils.ITypeCheckerAstHelper;
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
    private IVisibilityChecker visibilityChecker;
    private ITypeCheckerAstHelper astHelper;
    private ITypeCheckErrorReporter errorReporter;

    @Before
    public void setUp() {
        symbolFactory = mock(ISymbolFactory.class);
        symbolResolver = mock(ISymbolResolver.class);
        typeSystem = mock(ITypeSystem.class);
        overloadResolver = mock(IOverloadResolver.class);
        visibilityChecker = mock(IVisibilityChecker.class);
        astHelper = mock(ITypeCheckerAstHelper.class);
        errorReporter = mock(ITypeCheckErrorReporter.class);
        TypeCheckErrorReporterRegistry.set(errorReporter);
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
        verifyNoMoreInteractions(errorReporter);
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
        verifyNoMoreInteractions(errorReporter);
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

    private ITypeCheckPhaseController createTypeCheckController() {
        return new TypeCheckPhaseController(
                symbolFactory, symbolResolver, typeSystem, overloadResolver, visibilityChecker, astHelper);
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
                errorReporter
        );
    }

    private interface IDoesNotCheckAnythingDelegate
    {
        public void check(ITypeCheckPhaseController typeCheckerController,
                ITSPHPAst leftTypeSymbol, ITSPHPAst rightTypeSymbol);
    }

}
