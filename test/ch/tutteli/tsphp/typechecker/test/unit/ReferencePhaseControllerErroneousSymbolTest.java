package ch.tutteli.tsphp.typechecker.test.unit;

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.IAccessResolver;
import ch.tutteli.tsphp.typechecker.IReferencePhaseController;
import ch.tutteli.tsphp.typechecker.ISymbolResolver;
import ch.tutteli.tsphp.typechecker.ReferencePhaseController;
import ch.tutteli.tsphp.typechecker.error.ITypeCheckErrorReporter;
import ch.tutteli.tsphp.typechecker.error.TypeCheckErrorReporterRegistry;
import ch.tutteli.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.IErroneousMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.IErroneousSymbol;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.IErroneousTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.IErroneousVariableSymbol;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class ReferencePhaseControllerErroneousSymbolTest
{
    private ISymbolFactory symbolFactory;
    private ISymbolResolver symbolResolver;
    private IAccessResolver visibilityChecker;
    private ITypeCheckErrorReporter errorReporter;

    @Before
    public void setUp() {
        symbolFactory = mock(ISymbolFactory.class);
        symbolResolver = mock(ISymbolResolver.class);
        visibilityChecker = mock(IAccessResolver.class);
        errorReporter = mock(ITypeCheckErrorReporter.class);
        TypeCheckErrorReporterRegistry.set(errorReporter);
    }

    @Test
    public void checkIsInterface_ErroneousTypeSymbol_ReturnTrueDoesNotCallErrorReporter() {
        IErroneousTypeSymbol erroneousTypeSymbol = mock(IErroneousTypeSymbol.class);

        IReferencePhaseController controller = createReferencePhaseController();
        boolean result = controller.checkIsInterface(mock(ITSPHPAst.class), erroneousTypeSymbol);

        assertTrue(result);
        verifyNoMoreInteractions(errorReporter);
    }


    @Test
    public void checkIsClass_ErroneousTypeSymbol_ReturnTrueDoesNotCallErrorReporter() {
        IErroneousTypeSymbol erroneousTypeSymbol = mock(IErroneousTypeSymbol.class);

        IReferencePhaseController typeCheckerController = createReferencePhaseController();
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
    public void checkIsForwardReference_ErroneousVariableSymbol_ReturnTrueNotInteractWithSymbolNorWithErrorReporter() {
        checkIsForwardReferenceTest(mock(IErroneousVariableSymbol.class));
    }

    @Test
    public void checkIsForwardReference_ErroneousMethodSymbol_ReturnTrueNotInteractWithSymbolNorWithErrorReporter() {
        checkIsForwardReferenceTest(mock(IErroneousMethodSymbol.class));
    }

    private void checkIsForwardReferenceTest(
            IErroneousSymbol symbol
    ) {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        when(ast.getSymbol()).thenReturn(symbol);

        IReferencePhaseController controller = createReferencePhaseController();
        boolean result = controller.checkIsForwardReference(ast);

        assertTrue(result);
        verifyNoMoreInteractions(symbol);
        verifyNoMoreInteractions(errorReporter);
    }


    protected IReferencePhaseController createReferencePhaseController() {
        return new ReferencePhaseController(
                symbolFactory, symbolResolver, mock(IGlobalNamespaceScope.class));
    }
}
