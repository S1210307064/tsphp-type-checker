/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.unit;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.typechecker.IAccessResolver;
import ch.tsphp.typechecker.IOverloadResolver;
import ch.tsphp.typechecker.ISymbolResolver;
import ch.tsphp.typechecker.ITypeCheckPhaseController;
import ch.tsphp.typechecker.ITypeSystem;
import ch.tsphp.typechecker.TypeCheckPhaseController;
import ch.tsphp.typechecker.error.ITypeCheckerErrorReporter;
import ch.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tsphp.typechecker.symbols.IVariableSymbol;
import ch.tsphp.typechecker.symbols.ModifierSet;
import ch.tsphp.typechecker.symbols.TypeWithModifiersDto;
import ch.tsphp.typechecker.utils.ITypeCheckerAstHelper;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
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
    private ITypeCheckerErrorReporter typeCheckerErrorReporter;

    @Before
    public void setUp() {
        symbolFactory = mock(ISymbolFactory.class);
        typeSystem = mock(ITypeSystem.class);
        symbolResolver = mock(ISymbolResolver.class);
        overloadResolver = mock(IOverloadResolver.class);
        visibilityChecker = mock(IAccessResolver.class);
        astHelper = mock(ITypeCheckerAstHelper.class);
        typeCheckerErrorReporter = mock(ITypeCheckerErrorReporter.class);
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void checkClassMemberInitialValue_CastModifierAndValueHasWrongType_WrongClassMemberInitialValueReported() {
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);
        ITypeSymbol wrongTypeSymbol = mock(ITypeSymbol.class);
        IVariableSymbol variableSymbol = mock(IVariableSymbol.class);
        when(variableSymbol.isAlwaysCasting()).thenReturn(true);
        when(variableSymbol.toTypeWithModifiersDto()).thenReturn(new TypeWithModifiersDto(wrongTypeSymbol,
                new ModifierSet()));
        ITSPHPAst variableId = createAst(typeSymbol);
        when(variableId.getSymbol()).thenReturn(variableSymbol);
        ITSPHPAst expression = createAst(wrongTypeSymbol);
        when(overloadResolver.getPromotionLevelFromToConsiderFalseAndNull(
                any(ITypeSymbol.class), anyString(), any(TypeWithModifiersDto.class))).thenReturn(-1);

        ITypeCheckPhaseController typeCheckerController = createTypeCheckController();
        typeCheckerController.checkClassMemberInitialValue(variableId, expression);

        verify(typeCheckerErrorReporter).wrongClassMemberInitialValue(variableId, expression, typeSymbol);
    }

    private ITSPHPAst createAst(ITypeSymbol typeSymbol) {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        when(ast.getEvalType()).thenReturn(typeSymbol);
        return ast;
    }

    protected ITypeCheckPhaseController createTypeCheckController() {
        return new TypeCheckPhaseController(
                symbolFactory,
                symbolResolver,
                typeCheckerErrorReporter,
                typeSystem,
                overloadResolver,
                visibilityChecker,
                astHelper);
    }
}
