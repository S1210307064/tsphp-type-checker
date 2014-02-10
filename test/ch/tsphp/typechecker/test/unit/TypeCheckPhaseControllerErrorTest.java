package ch.tsphp.typechecker.test.unit;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.ITypeSymbol;
import ch.tsphp.typechecker.IAccessResolver;
import ch.tsphp.typechecker.IOverloadResolver;
import ch.tsphp.typechecker.ISymbolResolver;
import ch.tsphp.typechecker.ITypeCheckPhaseController;
import ch.tsphp.typechecker.ITypeSystem;
import ch.tsphp.typechecker.TypeCheckPhaseController;
import ch.tsphp.typechecker.error.ITypeCheckErrorReporter;
import ch.tsphp.typechecker.error.TypeCheckErrorReporterRegistry;
import ch.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tsphp.typechecker.symbols.IVariableSymbol;
import ch.tsphp.typechecker.utils.ITypeCheckerAstHelper;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TypeCheckPhaseControllerErrorTest
{
    private ISymbolFactory symbolFactory;
    private ITypeSystem typeSystem;
    private ISymbolResolver symbolResolver;
    private IOverloadResolver overloadResolver;
    private IAccessResolver visibilityChecker;
    private ITypeCheckerAstHelper astHelper;
    private ITypeCheckErrorReporter errorReporter;

    @Before
    public void setUp() {
        symbolFactory = mock(ISymbolFactory.class);
        typeSystem = mock(ITypeSystem.class);
        symbolResolver = mock(ISymbolResolver.class);
        overloadResolver = mock(IOverloadResolver.class);
        visibilityChecker = mock(IAccessResolver.class);
        astHelper = mock(ITypeCheckerAstHelper.class);
        errorReporter = mock(ITypeCheckErrorReporter.class);
        TypeCheckErrorReporterRegistry.set(errorReporter);
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void checkClassMemberInitialValue_CastModifierAndValueHasWrongType_WrongClassMemberInitialValueReported() {
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);
        ITSPHPAst variableId = createAst(typeSymbol);
        IVariableSymbol symbol = mock(IVariableSymbol.class);
        when(symbol.isAlwaysCasting()).thenReturn(true);
        when(variableId.getSymbol()).thenReturn(symbol);
        ITypeSymbol wrongTypeSymbol = mock(ITypeSymbol.class);
        ITSPHPAst expression = createAst(wrongTypeSymbol);
        when(overloadResolver.getPromotionLevelFromToConsiderNull(typeSymbol, wrongTypeSymbol)).thenReturn(-1);

        ITypeCheckPhaseController typeCheckerController = createTypeCheckController();
        typeCheckerController.checkClassMemberInitialValue(variableId, expression);

        verify(errorReporter).wrongClassMemberInitialValue(variableId, expression, typeSymbol);
    }

    private ITSPHPAst createAst(ITypeSymbol typeSymbol) {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        when(ast.getEvalType()).thenReturn(typeSymbol);
        return ast;
    }

    protected ITypeCheckPhaseController createTypeCheckController() {
        return new TypeCheckPhaseController(
                symbolFactory, symbolResolver, typeSystem, overloadResolver, visibilityChecker, astHelper);
    }
}
