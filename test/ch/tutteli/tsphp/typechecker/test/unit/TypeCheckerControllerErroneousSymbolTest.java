package ch.tutteli.tsphp.typechecker.test.unit;

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.IDefiner;
import ch.tutteli.tsphp.typechecker.IOverloadResolver;
import ch.tutteli.tsphp.typechecker.ISymbolResolver;
import ch.tutteli.tsphp.typechecker.ITypeCheckerController;
import ch.tutteli.tsphp.typechecker.ITypeSystem;
import ch.tutteli.tsphp.typechecker.TypeCheckerController;
import ch.tutteli.tsphp.typechecker.error.ITypeCheckErrorReporter;
import ch.tutteli.tsphp.typechecker.error.TypeCheckErrorReporterRegistry;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.IErroneousMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.IErroneousSymbol;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.IErroneousTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.IErroneousVariableSymbol;
import ch.tutteli.tsphp.typechecker.utils.ITypeCheckerAstHelper;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
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


    private ITypeCheckerController createTypeCheckController() {
        return new TypeCheckerController(
            symbolFactory, typeSystem, definer, symbolResolver, overloadResolver, astHelper);
    }


}
