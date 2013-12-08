package ch.tutteli.tsphp.typechecker.test.unit;

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.common.exceptions.TypeCheckerException;
import ch.tutteli.tsphp.typechecker.IDefiner;
import ch.tutteli.tsphp.typechecker.IOverloadResolver;
import ch.tutteli.tsphp.typechecker.ISymbolResolver;
import ch.tutteli.tsphp.typechecker.ITypeCheckerController;
import ch.tutteli.tsphp.typechecker.ITypeSystem;
import ch.tutteli.tsphp.typechecker.TypeCheckerController;
import ch.tutteli.tsphp.typechecker.error.ITypeCheckErrorReporter;
import ch.tutteli.tsphp.typechecker.error.TypeCheckErrorReporterRegistry;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.IErroneousMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.IErroneousSymbol;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.IErroneousTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.IErroneousVariableSymbol;
import ch.tutteli.tsphp.typechecker.utils.ITypeCheckerAstHelper;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class TypeCheckerControllerErroneousSymbolTest
{
    private ISymbolFactory symbolFactory;
    private ITypeSystem typeSystem;
    private IDefiner definer;
    private ISymbolResolver symbolResolver;
    private IOverloadResolver overloadResolver;
    private ITypeCheckerAstHelper astHelper;
    private ITypeCheckErrorReporter errorReporter;

    @Before
    public void setUp() {
        symbolFactory = mock(ISymbolFactory.class);
        typeSystem = mock(ITypeSystem.class);
        definer = mock(IDefiner.class);
        symbolResolver = mock(ISymbolResolver.class);
        overloadResolver = mock(IOverloadResolver.class);
        astHelper = mock(ITypeCheckerAstHelper.class);
        errorReporter = mock(ITypeCheckErrorReporter.class);
        TypeCheckErrorReporterRegistry.set(errorReporter);
    }

    @Test
    public void checkIsInterface_ErroneousTypeSymbol_ReturnTrueDoesNotCallErrorReporter() {
        IErroneousTypeSymbol erroneousTypeSymbol = mock(IErroneousTypeSymbol.class);

        ITypeCheckerController typeCheckerController = createTypeCheckController();
        boolean result = typeCheckerController.checkIsInterface(mock(ITSPHPAst.class), erroneousTypeSymbol);

        assertTrue(result);
        verifyNoMoreInteractions(errorReporter);
    }

    @Test
    public void checkIsClass_ErroneousTypeSymbol_ReturnTrueDoesNotCallErrorReporter() {
        IErroneousTypeSymbol erroneousTypeSymbol = mock(IErroneousTypeSymbol.class);

        ITypeCheckerController typeCheckerController = createTypeCheckController();
        boolean result = typeCheckerController.checkIsClass(mock(ITSPHPAst.class), erroneousTypeSymbol);

        assertTrue(result);
        verifyNoMoreInteractions(errorReporter);
    }

    @Test
    public void checkIsForwardReference_ErroneousSymbol_ReturnTrueDoesNotInteractWithSymbolNorWithErrorReporter() {
        checkIsForwardReferenceTest(mock(IErroneousSymbol.class));
    }

    @Test
    public void checkIsForwardReference_ErroneousTypeSymbol_ReturnTrueDoesNotInteractWithSymbolNorWithErrorReporter() {
        checkIsForwardReferenceTest(mock(IErroneousTypeSymbol.class));
    }

    @Test
    public void checkIsForwardReference_ErroneousVariableSymbol_ReturnTrueDoesNotInteractWithSymbolNorWithErrorReporter
        () {
        checkIsForwardReferenceTest(mock(IErroneousVariableSymbol.class));
    }

    @Test
    public void checkIsForwardReference_ErroneousMethodSymbol_ReturnTrueDoesNotInteractWithSymbolNorWithErrorReporter
        () {
        checkIsForwardReferenceTest(mock(IErroneousMethodSymbol.class));
    }

    private void checkIsForwardReferenceTest(
        IErroneousSymbol symbol
    ) {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        when(ast.getSymbol()).thenReturn(symbol);

        ITypeCheckerController typeCheckerController = createTypeCheckController();
        boolean result = typeCheckerController.checkIsForwardReference(ast);

        assertTrue(result);
        verifyNoMoreInteractions(symbol);
        verifyNoMoreInteractions(errorReporter);
    }

    @Test
    public void resolveMethodCall_CalleeIsErroneousTypeSymbol_UseSymbolFactoryToCreateErroneousMethodSymbolAssignType() {
        ITSPHPAst callee = mock(ITSPHPAst.class);
        IErroneousTypeSymbol typeSymbol = mock(IErroneousTypeSymbol.class);
        when(callee.getEvalType()).thenReturn(typeSymbol);
        TypeCheckerException exception = new TypeCheckerException();
        when(typeSymbol.getException()).thenReturn(exception);
        ITSPHPAst identifier = mock(ITSPHPAst.class);
        IErroneousMethodSymbol methodSymbol = mock(IErroneousMethodSymbol.class);
        when(symbolFactory.createErroneousMethodSymbol(identifier, exception)).thenReturn(methodSymbol);

        ITypeCheckerController typeCheckerController = createTypeCheckController();
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

        ITypeCheckerController typeCheckerController = createTypeCheckController();
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
                public void check(ITypeCheckerController typeCheckerController, ITSPHPAst left, ITSPHPAst right) {
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
                public void check(ITypeCheckerController typeCheckerController, ITSPHPAst left, ITSPHPAst right) {
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
                public void check(ITypeCheckerController typeCheckerController, ITSPHPAst left, ITSPHPAst right) {
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
                public void check(ITypeCheckerController typeCheckerController, ITSPHPAst left, ITSPHPAst right) {
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
                public void check(ITypeCheckerController typeCheckerController, ITSPHPAst left, ITSPHPAst right) {
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
                public void check(ITypeCheckerController typeCheckerController, ITSPHPAst left, ITSPHPAst right) {
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
                public void check(ITypeCheckerController typeCheckerController, ITSPHPAst left, ITSPHPAst right) {
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
                public void check(ITypeCheckerController typeCheckerController, ITSPHPAst left, ITSPHPAst right) {
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
                public void check(ITypeCheckerController typeCheckerController, ITSPHPAst left, ITSPHPAst right) {
                    typeCheckerController.checkIdentity(mock(ITSPHPAst.class), left, right);
                }
            });
    }

    private ITypeCheckerController createTypeCheckController() {
        return new TypeCheckerController(
            symbolFactory, typeSystem, definer, symbolResolver, overloadResolver, astHelper);
    }


    private void doesNotCheckAnythingTest(ITypeSymbol leftTypeSymbol, ITypeSymbol rightTypeSymbol,
        IDoesNotCheckAnythingDelegate delegate) {
        ITSPHPAst left = mock(ITSPHPAst.class);
        ITSPHPAst right = mock(ITSPHPAst.class);
        when(left.getEvalType()).thenReturn(leftTypeSymbol);
        when(right.getEvalType()).thenReturn(rightTypeSymbol);

        ITypeCheckerController typeCheckerController = createTypeCheckController();
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
        public void check(ITypeCheckerController typeCheckerController,
            ITSPHPAst leftTypeSymbol, ITSPHPAst rightTypeSymbol);
    }

}
