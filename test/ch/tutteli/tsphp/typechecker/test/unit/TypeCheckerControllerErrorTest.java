package ch.tutteli.tsphp.typechecker.test.unit;

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.typechecker.IDefiner;
import ch.tutteli.tsphp.typechecker.IOverloadResolver;
import ch.tutteli.tsphp.typechecker.ISymbolResolver;
import ch.tutteli.tsphp.typechecker.ITypeCheckerController;
import ch.tutteli.tsphp.typechecker.ITypeSystem;
import ch.tutteli.tsphp.typechecker.TypeCheckerController;
import ch.tutteli.tsphp.typechecker.error.ITypeCheckErrorReporter;
import ch.tutteli.tsphp.typechecker.error.TypeCheckErrorReporterRegistry;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tutteli.tsphp.typechecker.symbols.IVariableSymbol;
import ch.tutteli.tsphp.typechecker.utils.ITypeCheckerAstHelper;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TypeCheckerControllerErrorTest
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

        ITypeCheckerController typeCheckerController = createTypeCheckController();
        typeCheckerController.checkClassMemberInitialValue(variableId, expression);

        verify(errorReporter).wrongClassMemberInitialValue(variableId, expression, typeSymbol);
    }

    private ITSPHPAst createAst(ITypeSymbol typeSymbol) {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        when(ast.getEvalType()).thenReturn(typeSymbol);
        return ast;
    }

    private ITypeCheckerController createTypeCheckController() {
        return new TypeCheckerController(
                symbolFactory, typeSystem, definer, symbolResolver, overloadResolver, astHelper);
    }
}
