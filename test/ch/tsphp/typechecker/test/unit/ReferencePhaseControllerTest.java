/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.unit;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.typechecker.IReferencePhaseController;
import ch.tsphp.typechecker.ISymbolResolver;
import ch.tsphp.typechecker.ITypeSystem;
import ch.tsphp.typechecker.ReferencePhaseController;
import ch.tsphp.typechecker.error.ITypeCheckerErrorReporter;
import ch.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tsphp.typechecker.symbols.IModifierHelper;
import ch.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tsphp.typechecker.symbols.erroneous.IErroneousSymbol;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ReferencePhaseControllerTest
{

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void checkBreakContinueLevel_Level0_CallsErrorReporter0NotAllowed() {
        ITypeCheckerErrorReporter errorReporter = mock(ITypeCheckerErrorReporter.class);
        ITSPHPAst root = mock(ITSPHPAst.class);
        ITSPHPAst level = mock(ITSPHPAst.class);
        when(level.getText()).thenReturn("0");

        IReferencePhaseController controller = createController(errorReporter);
        controller.checkBreakContinueLevel(root, level);

        verify(errorReporter).breakContinueLevelZeroNotAllowed(root);
    }

    @Test
    public void checkIsNotForwardReference_IsErroneousSymbol_ReturnsTrue() {
        IErroneousSymbol symbol = mock(IErroneousSymbol.class);
        ITSPHPAst ast = mock(ITSPHPAst.class);
        when(ast.getSymbol()).thenReturn(symbol);

        IReferencePhaseController controller = createController();
        boolean result = controller.checkIsNotForwardReference(ast);

        assertThat(result, is(true));
    }

    @Test
    public void checkIsNotForwardReference_IsDefinedEarlier_ReturnsTrue() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        ISymbol symbol = mock(ISymbol.class);
        when(ast.getSymbol()).thenReturn(symbol);
        ITSPHPAst definitionAst = mock(ITSPHPAst.class);
        when(symbol.getDefinitionAst()).thenReturn(definitionAst);
        when(definitionAst.isDefinedEarlierThan(ast)).thenReturn(true);

        IReferencePhaseController controller = createController();
        boolean result = controller.checkIsNotForwardReference(ast);

        verify(definitionAst).isDefinedEarlierThan(ast);
        assertThat(result, is(true));
    }

    @Test
    public void checkIsNotForwardReference_IsDefinedLaterOwn_ReturnsFalseAndReportsForwardUsage() {
        ITypeCheckerErrorReporter errorReporter = mock(ITypeCheckerErrorReporter.class);
        ITSPHPAst ast = mock(ITSPHPAst.class);
        ISymbol symbol = mock(ISymbol.class);
        when(ast.getSymbol()).thenReturn(symbol);
        ITSPHPAst definitionAst = mock(ITSPHPAst.class);
        when(symbol.getDefinitionAst()).thenReturn(definitionAst);
        when(definitionAst.isDefinedEarlierThan(ast)).thenReturn(false);

        IReferencePhaseController controller = createController(errorReporter);
        boolean result = controller.checkIsNotForwardReference(ast);

        verify(definitionAst).isDefinedEarlierThan(ast);
        verify(errorReporter).forwardReference(definitionAst, ast);
        assertThat(result, is(false));
    }

    private IReferencePhaseController createController() {
        return createController(mock(ITypeCheckerErrorReporter.class));
    }

    private IReferencePhaseController createController(ITypeCheckerErrorReporter theTypeCheckerErrorReporter) {
        return createController(
                mock(ISymbolFactory.class),
                mock(ISymbolResolver.class),
                theTypeCheckerErrorReporter,
                mock(ITypeSystem.class),
                mock(IModifierHelper.class),
                mock(IGlobalNamespaceScope.class)
        );
    }

    protected IReferencePhaseController createController(
            ISymbolFactory theSymbolFactory,
            ISymbolResolver theSymbolResolver,
            ITypeCheckerErrorReporter theTypeCheckerErrorReporter,
            ITypeSystem theTypeSystem,
            IModifierHelper theModifierHelper,
            IGlobalNamespaceScope theGlobalDefaultNamespace) {
        return new ReferencePhaseController(
                theSymbolFactory,
                theSymbolResolver,
                theTypeCheckerErrorReporter,
                theTypeSystem,
                theModifierHelper,
                theGlobalDefaultNamespace);
    }

}

