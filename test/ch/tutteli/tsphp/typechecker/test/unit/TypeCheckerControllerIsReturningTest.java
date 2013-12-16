package ch.tutteli.tsphp.typechecker.test.unit;

import ch.tutteli.tsphp.typechecker.EReturnState;
import ch.tutteli.tsphp.typechecker.IDefiner;
import ch.tutteli.tsphp.typechecker.IOverloadResolver;
import ch.tutteli.tsphp.typechecker.ISymbolResolver;
import ch.tutteli.tsphp.typechecker.ITypeCheckerController;
import ch.tutteli.tsphp.typechecker.ITypeSystem;
import ch.tutteli.tsphp.typechecker.TypeCheckerController;
import ch.tutteli.tsphp.typechecker.error.ITypeCheckErrorReporter;
import ch.tutteli.tsphp.typechecker.error.TypeCheckErrorReporterRegistry;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tutteli.tsphp.typechecker.utils.ITypeCheckerAstHelper;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class TypeCheckerControllerIsReturningTest
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
    public void evaluateReturnStateAnd_FirstIsReturningSecondIsNotReturning_ReturnIsPartiallyReturning() {
        // no arrange necessary

        ITypeCheckerController controller = createTypeCheckController();
        EReturnState result = controller.evaluateReturnStateAnd(
                EReturnState.IsReturning, EReturnState.IsNotReturning);

        assertThat(result, is(EReturnState.IsPartiallyReturning));
    }

    @Test
    public void evaluateReturnStateAnd_FirstIsNotReturningSecondIsReturning_ReturnIsPartiallyReturning() {
        // no arrange necessary

        ITypeCheckerController controller = createTypeCheckController();
        EReturnState result = controller.evaluateReturnStateAnd(
                EReturnState.IsNotReturning, EReturnState.IsReturning);

        assertThat(result, is(EReturnState.IsPartiallyReturning));
    }

    @Test
    public void evaluateReturnStateAnd_FirstIsNotReturningSecondIsNotReturning_ReturnIsNotReturning() {
        // no arrange necessary

        ITypeCheckerController controller = createTypeCheckController();
        EReturnState result = controller.evaluateReturnStateAnd(
                EReturnState.IsNotReturning, EReturnState.IsNotReturning);

        assertThat(result, is(EReturnState.IsNotReturning));
    }

    @Test
    public void evaluateReturnStateAnd_FirstIsReturningSecondIsReturning_ReturnIsReturning() {
        // no arrange necessary

        ITypeCheckerController controller = createTypeCheckController();
        EReturnState result = controller.evaluateReturnStateAnd(
                EReturnState.IsReturning, EReturnState.IsReturning);

        assertThat(result, is(EReturnState.IsReturning));
    }

    @Test
    public void evaluateReturnStateAnd_FirstNullSecondIsReturning_ReturnIsReturning() {
        // no arrange necessary

        ITypeCheckerController controller = createTypeCheckController();
        EReturnState result = controller.evaluateReturnStateAnd(
                null, EReturnState.IsReturning);

        assertThat(result, is(EReturnState.IsReturning));
    }

    @Test
    public void evaluateReturnStateAnd_FirstIsReturningSecondNull_ReturnIsReturning() {
        // no arrange necessary

        ITypeCheckerController controller = createTypeCheckController();
        EReturnState result = controller.evaluateReturnStateAnd(
                EReturnState.IsReturning, null);

        assertThat(result, is(EReturnState.IsReturning));
    }

    @Test
    public void evaluateReturnStateAnd_FirstNullSecondNull_ReturnIsNotReturning() {
        // no arrange necessary

        ITypeCheckerController controller = createTypeCheckController();
        EReturnState result = controller.evaluateReturnStateAnd(
                null, null);

        assertNull(result);
    }

    @Test
    public void evaluateReturnStateAnd_FirstIsReturningSecondIsPartiallyReturning_ReturnIsPartiallyReturning() {
        // no arrange necessary

        ITypeCheckerController controller = createTypeCheckController();
        EReturnState result = controller.evaluateReturnStateAnd(
                EReturnState.IsReturning, EReturnState.IsPartiallyReturning);

        assertThat(result, is(EReturnState.IsPartiallyReturning));
    }

    @Test
    public void evaluateReturnStateAnd_FirstIsPartiallyReturningSecondIsReturning_ReturnIsPartiallyReturning() {
        // no arrange necessary

        ITypeCheckerController controller = createTypeCheckController();
        EReturnState result = controller.evaluateReturnStateAnd(
                EReturnState.IsPartiallyReturning, EReturnState.IsReturning);

        assertThat(result, is(EReturnState.IsPartiallyReturning));
    }

    @Test
    public void evaluateReturnStateAnd_FirstIsPartiallyReturningSecondIsPartiallyReturning_ReturnIsPartiallyReturning
            () {
        // no arrange necessary

        ITypeCheckerController controller = createTypeCheckController();
        EReturnState result = controller.evaluateReturnStateAnd(
                EReturnState.IsPartiallyReturning, EReturnState.IsPartiallyReturning);

        assertThat(result, is(EReturnState.IsPartiallyReturning));
    }

    @Test
    public void evaluateReturnStateAnd_FirstIsPartiallyReturningSecondIsNotReturning_ReturnIsPartiallyReturning() {
        // no arrange necessary

        ITypeCheckerController controller = createTypeCheckController();
        EReturnState result = controller.evaluateReturnStateAnd(
                EReturnState.IsPartiallyReturning, EReturnState.IsNotReturning);

        assertThat(result, is(EReturnState.IsPartiallyReturning));
    }

    @Test
    public void evaluateReturnStateAnd_FirstIsNotReturningSecondIsPartiallyReturning_ReturnIsPartiallyReturning() {
        // no arrange necessary

        ITypeCheckerController controller = createTypeCheckController();
        EReturnState result = controller.evaluateReturnStateAnd(
                EReturnState.IsNotReturning, EReturnState.IsPartiallyReturning);

        assertThat(result, is(EReturnState.IsPartiallyReturning));
    }

    @Test
    public void evaluateReturnStateAnd_FirstIsPartiallyReturningSecondNull_ReturnIsPartiallyReturning() {
        // no arrange necessary

        ITypeCheckerController controller = createTypeCheckController();
        EReturnState result = controller.evaluateReturnStateAnd(
                EReturnState.IsPartiallyReturning, null);

        assertThat(result, is(EReturnState.IsPartiallyReturning));
    }

    @Test
    public void evaluateReturnStateAnd_FirstNullSecondIsPartiallyReturning_ReturnIsPartiallyReturning() {
        // no arrange necessary

        ITypeCheckerController controller = createTypeCheckController();
        EReturnState result = controller.evaluateReturnStateAnd(
                null, EReturnState.IsPartiallyReturning);

        assertThat(result, is(EReturnState.IsPartiallyReturning));
    }


    @Test
    public void evaluateReturnStateOr_FirstIsReturningSecondIsNotReturning_ReturnIsReturning() {
        // no arrange necessary

        ITypeCheckerController controller = createTypeCheckController();
        EReturnState result = controller.evaluateReturnStateOr(
                EReturnState.IsReturning, EReturnState.IsNotReturning);

        assertThat(result, is(EReturnState.IsReturning));
    }

    @Test
    public void evaluateReturnStateOr_FirstIsNotReturningSecondIsReturning_ReturnIsReturning() {
        // no arrange necessary

        ITypeCheckerController controller = createTypeCheckController();
        EReturnState result = controller.evaluateReturnStateOr(
                EReturnState.IsNotReturning, EReturnState.IsReturning);

        assertThat(result, is(EReturnState.IsReturning));
    }

    @Test
    public void evaluateReturnStateOr_FirstIsNotReturningSecondIsNotReturning_ReturnIsNotReturning() {
        // no arrange necessary

        ITypeCheckerController controller = createTypeCheckController();
        EReturnState result = controller.evaluateReturnStateOr(
                EReturnState.IsNotReturning, EReturnState.IsNotReturning);

        assertThat(result, is(EReturnState.IsNotReturning));
    }

    @Test
    public void evaluateReturnStateOr_FirstIsReturningSecondIsReturning_ReturnIsReturning() {
        // no arrange necessary

        ITypeCheckerController controller = createTypeCheckController();
        EReturnState result = controller.evaluateReturnStateOr(
                EReturnState.IsReturning, EReturnState.IsReturning);

        assertThat(result, is(EReturnState.IsReturning));
    }

    @Test
    public void evaluateReturnStateOr_FirstIsReturningSecondNull_ReturnIsReturning() {
        // no arrange necessary

        ITypeCheckerController controller = createTypeCheckController();
        EReturnState result = controller.evaluateReturnStateOr(
                EReturnState.IsReturning, null);

        assertThat(result, is(EReturnState.IsReturning));
    }

    @Test
    public void evaluateReturnStateOr_FirstNullSecondIsReturning_ReturnIsReturning() {
        // no arrange necessary

        ITypeCheckerController controller = createTypeCheckController();
        EReturnState result = controller.evaluateReturnStateOr(
                null, EReturnState.IsReturning);

        assertThat(result, is(EReturnState.IsReturning));
    }

    @Test
    public void evaluateReturnStateOr_FirstNullSecondNull_ReturnIsNotReturning() {
        // no arrange necessary

        ITypeCheckerController controller = createTypeCheckController();
        EReturnState result = controller.evaluateReturnStateOr(
                null, null);

        assertNull(result);
    }

    @Test
    public void evaluateReturnStateOr_FirstIsReturningSecondIsPartiallyReturning_ReturnIsReturning() {
        // no arrange necessary

        ITypeCheckerController controller = createTypeCheckController();
        EReturnState result = controller.evaluateReturnStateOr(
                EReturnState.IsReturning, EReturnState.IsPartiallyReturning);

        assertThat(result, is(EReturnState.IsReturning));
    }

    @Test
    public void evaluateReturnStateOr_FirstIsPartiallyReturningSecondIsReturning_ReturnIsReturning() {
        // no arrange necessary

        ITypeCheckerController controller = createTypeCheckController();
        EReturnState result = controller.evaluateReturnStateOr(
                EReturnState.IsPartiallyReturning, EReturnState.IsReturning);

        assertThat(result, is(EReturnState.IsReturning));
    }

    @Test
    public void evaluateReturnStateOr_FirstIsPartiallyReturningSecondIsPartiallyReturning_ReturnIsPartiallyReturning() {
        // no arrange necessary

        ITypeCheckerController controller = createTypeCheckController();
        EReturnState result = controller.evaluateReturnStateOr(
                EReturnState.IsPartiallyReturning, EReturnState.IsPartiallyReturning);

        assertThat(result, is(EReturnState.IsPartiallyReturning));
    }

    @Test
    public void evaluateReturnStateOr_FirstIsPartiallyReturningSecondIsNotReturning_ReturnIsPartiallyReturning() {
        // no arrange necessary

        ITypeCheckerController controller = createTypeCheckController();
        EReturnState result = controller.evaluateReturnStateOr(
                EReturnState.IsPartiallyReturning, EReturnState.IsNotReturning);

        assertThat(result, is(EReturnState.IsPartiallyReturning));
    }


    @Test
    public void evaluateReturnStateOr_FirstIsNotReturningSecondIsPartiallyReturning_ReturnIsPartiallyReturning() {
        // no arrange necessary

        ITypeCheckerController controller = createTypeCheckController();
        EReturnState result = controller.evaluateReturnStateOr(
                EReturnState.IsNotReturning, EReturnState.IsPartiallyReturning);

        assertThat(result, is(EReturnState.IsPartiallyReturning));
    }

    @Test
    public void evaluateReturnStateOr_FirstIsPartiallyReturningSecondNull_ReturnIsPartiallyReturning() {
        // no arrange necessary

        ITypeCheckerController controller = createTypeCheckController();
        EReturnState result = controller.evaluateReturnStateOr(
                EReturnState.IsPartiallyReturning, null);

        assertThat(result, is(EReturnState.IsPartiallyReturning));
    }


    @Test
    public void evaluateReturnStateOr_FirstNullSecondIsPartiallyReturning_ReturnIsPartiallyReturning() {
        // no arrange necessary

        ITypeCheckerController controller = createTypeCheckController();
        EReturnState result = controller.evaluateReturnStateOr(
                null, EReturnState.IsPartiallyReturning);

        assertThat(result, is(EReturnState.IsPartiallyReturning));
    }


//    @Test
//    public void checkReturnsFromFunction_IsReturning_NoErrorIsReported() {
//        // no arrange necessary
//
//        ITypeCheckerController controller = createTypeCheckController();
//        controller.checkReturnsFromFunction(EReturnState.IsReturning, mock(ITSPHPAst.class));
//
//        verifyNoMoreInteractions(errorReporter);
//    }
//
//    @Test
//    public void checkReturnsFromMethod_IsReturning_NoErrorIsReported() {
//        // no arrange necessary
//
//        ITypeCheckerController controller = createTypeCheckController();
//        controller.checkReturnsFromMethod(EReturnState.IsReturning, mock(ITSPHPAst.class));
//
//        verifyNoMoreInteractions(errorReporter);
//    }
//
//    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
//    @Test
//    public void checkReturnsFromFunction_IsPartiallyReturning_PartialReturnFromFunctionIsReported() {
//        ITSPHPAst identifier = mock(ITSPHPAst.class);
//
//        ITypeCheckerController controller = createTypeCheckController();
//        controller.checkReturnsFromFunction(EReturnState.IsPartiallyReturning, identifier);
//
//        verify(errorReporter).partialReturnFromFunction(identifier);
//    }
//
//    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
//    @Test
//    public void checkReturnsFromMethod_IsPartiallyReturning_PartialReturnFromMethodIsReported() {
//        ITSPHPAst identifier = mock(ITSPHPAst.class);
//
//        ITypeCheckerController controller = createTypeCheckController();
//        controller.checkReturnsFromMethod(EReturnState.IsPartiallyReturning, identifier);
//
//        verify(errorReporter).partialReturnFromMethod(identifier);
//    }
//
//    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
//    @Test
//    public void checkReturnsFromFunction_IsNotReturning_NoReturnFromFunctionIsReported() {
//        ITSPHPAst identifier = mock(ITSPHPAst.class);
//
//        ITypeCheckerController controller = createTypeCheckController();
//        controller.checkReturnsFromFunction(EReturnState.IsNotReturning, identifier);
//
//        verify(errorReporter).noReturnFromFunction(identifier);
//    }
//
//    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
//    @Test
//    public void checkReturnsFromMethod_IsNotReturning_NoReturnFromMethodIsReported() {
//        ITSPHPAst identifier = mock(ITSPHPAst.class);
//
//        ITypeCheckerController controller = createTypeCheckController();
//        controller.checkReturnsFromMethod(EReturnState.IsNotReturning, identifier);
//
//        verify(errorReporter).noReturnFromMethod(identifier);
//    }


    private ITypeCheckerController createTypeCheckController() {
        return new TypeCheckerController(
                symbolFactory, typeSystem, definer, symbolResolver, overloadResolver, astHelper);
    }
}
