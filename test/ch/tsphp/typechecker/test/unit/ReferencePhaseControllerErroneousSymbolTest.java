/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.unit;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.IReferencePhaseController;
import ch.tsphp.typechecker.ISymbolResolver;
import ch.tsphp.typechecker.ReferencePhaseController;
import ch.tsphp.typechecker.error.ITypeCheckerErrorReporter;
import ch.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tsphp.typechecker.symbols.erroneous.IErroneousMethodSymbol;
import ch.tsphp.typechecker.symbols.erroneous.IErroneousSymbol;
import ch.tsphp.typechecker.symbols.erroneous.IErroneousTypeSymbol;
import ch.tsphp.typechecker.symbols.erroneous.IErroneousVariableSymbol;
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
    private ITypeCheckerErrorReporter typeCheckerErrorReporter;

    @Before
    public void setUp() {
        symbolFactory = mock(ISymbolFactory.class);
        symbolResolver = mock(ISymbolResolver.class);
        typeCheckerErrorReporter = mock(ITypeCheckerErrorReporter.class);
    }

    @Test
    public void checkIsInterface_ErroneousTypeSymbol_ReturnTrueDoesNotCallErrorReporter() {
        IErroneousTypeSymbol erroneousTypeSymbol = mock(IErroneousTypeSymbol.class);

        IReferencePhaseController controller = createReferencePhaseController();
        boolean result = controller.checkIsInterface(mock(ITSPHPAst.class), erroneousTypeSymbol);

        assertTrue(result);
        verifyNoMoreInteractions(typeCheckerErrorReporter);
    }


    @Test
    public void checkIsClass_ErroneousTypeSymbol_ReturnTrueDoesNotCallErrorReporter() {
        IErroneousTypeSymbol erroneousTypeSymbol = mock(IErroneousTypeSymbol.class);

        IReferencePhaseController typeCheckerController = createReferencePhaseController();
        boolean result = typeCheckerController.checkIsClass(mock(ITSPHPAst.class), erroneousTypeSymbol);

        assertTrue(result);
        verifyNoMoreInteractions(typeCheckerErrorReporter);
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
        boolean result = controller.checkIsNotForwardReference(ast);

        assertTrue(result);
        verifyNoMoreInteractions(symbol);
        verifyNoMoreInteractions(typeCheckerErrorReporter);
    }


    protected IReferencePhaseController createReferencePhaseController() {
        return new ReferencePhaseController(
                symbolFactory, symbolResolver, typeCheckerErrorReporter, mock(IGlobalNamespaceScope.class));
    }
}
