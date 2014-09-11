/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.unit;

import ch.tsphp.common.AstHelperRegistry;
import ch.tsphp.common.IAstHelper;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.typechecker.IAccessResolver;
import ch.tsphp.typechecker.IOverloadResolver;
import ch.tsphp.typechecker.ISymbolResolver;
import ch.tsphp.typechecker.ITypeCheckPhaseController;
import ch.tsphp.typechecker.ITypeSystem;
import ch.tsphp.typechecker.TypeCheckPhaseController;
import ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tsphp.typechecker.error.ITypeCheckerErrorReporter;
import ch.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tsphp.typechecker.symbols.erroneous.IErroneousTypeSymbol;
import ch.tsphp.typechecker.utils.ITypeCheckerAstHelper;
import org.antlr.runtime.Token;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TypeCheckPhaseControllerTest
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
        ITSPHPAst ast = mock(ITSPHPAst.class);
        when(ast.getToken()).thenReturn(mock(Token.class));
        IAstHelper astHelper = mock(IAstHelper.class);
        when(astHelper.createAst(any(ITSPHPAst.class))).thenReturn(ast);
        AstHelperRegistry.set(astHelper);
    }

    @Test
    public void checkConstantInitialValue_ForConstant_TypeRemainsConstant() {
        ITSPHPAst expression = getConstantExpression();
        Token token = mock(Token.class);
        when(token.getType()).thenReturn(TSPHPDefinitionWalker.CONSTANT);
        ITSPHPAst ast = mock(ITSPHPAst.class);
        when(ast.getToken()).thenReturn(token);
        //In order that the assignment check is not done
        when(ast.getEvalType()).thenReturn(mock(IErroneousTypeSymbol.class));

        ITypeCheckPhaseController controller = createTypeCheckController();
        controller.checkConstantInitialValue(ast, expression);

        verify(token).setType(TSPHPDefinitionWalker.VariableId);
        verify(token).setType(TSPHPDefinitionWalker.CONSTANT);
    }

    @Test
    public void checkConstantInitialValue_ForVariable_TypeRemainsVariableId() {
        ITSPHPAst expression = getConstantExpression();
        Token token = mock(Token.class);
        when(token.getType()).thenReturn(TSPHPDefinitionWalker.VariableId);
        ITSPHPAst ast = mock(ITSPHPAst.class);
        when(ast.getToken()).thenReturn(token);
        //In order that the assignment check is not done
        when(ast.getEvalType()).thenReturn(mock(IErroneousTypeSymbol.class));

        ITypeCheckPhaseController controller = createTypeCheckController();
        controller.checkConstantInitialValue(ast, expression);

        verify(token, times(2)).setType(TSPHPDefinitionWalker.VariableId);
    }

    private ITSPHPAst getConstantExpression() {
        ITSPHPAst expression = mock(ITSPHPAst.class);
        when(expression.getType()).thenReturn(TSPHPDefinitionWalker.Int);
        when(expression.getChildCount()).thenReturn(1);
        return expression;
    }

    @Test
    public void resolveTernaryOperatorEvalType_CaseTrueIsErroneousType_ReturnCaseFalseType() {
        ITypeSymbol caseTrueType = mock(IErroneousTypeSymbol.class);
        ITypeSymbol caseFalseType = mock(IClassTypeSymbol.class);
        ITSPHPAst caseTrueAst = mock(ITSPHPAst.class);
        when(caseTrueAst.getEvalType()).thenReturn(caseTrueType);
        ITSPHPAst caseFalseAst = mock(ITSPHPAst.class);
        when(caseFalseAst.getEvalType()).thenReturn(caseFalseType);

        ITypeCheckPhaseController controller = createTypeCheckController();
        ITypeSymbol result = controller.resolveTernaryOperatorEvalType(
                mock(ITSPHPAst.class), mock(ITSPHPAst.class), caseTrueAst, caseFalseAst);

        assertThat(result, is(caseFalseType));
    }

    @Test
    public void resolveTernaryOperatorEvalType_CaseFalseIsErroneousType_ReturnCaseTrueType() {
        ITypeSymbol caseTrueType = mock(IClassTypeSymbol.class);
        ITypeSymbol caseFalseType = mock(IErroneousTypeSymbol.class);
        ITSPHPAst caseTrueAst = mock(ITSPHPAst.class);
        when(caseTrueAst.getEvalType()).thenReturn(caseTrueType);
        ITSPHPAst caseFalseAst = mock(ITSPHPAst.class);
        when(caseFalseAst.getEvalType()).thenReturn(caseFalseType);

        ITypeCheckPhaseController controller = createTypeCheckController();
        ITypeSymbol result = controller.resolveTernaryOperatorEvalType(
                mock(ITSPHPAst.class), mock(ITSPHPAst.class), caseTrueAst, caseFalseAst);

        assertThat(result, is(caseTrueType));
    }

    @Test
    public void resolveTernaryOperatorEvalType_CaseTrueAndFalseAreErroneousTypes_ReturnCaseTrueType() {
        ITypeSymbol caseTrueType = mock(IErroneousTypeSymbol.class);
        ITypeSymbol caseFalseType = mock(IErroneousTypeSymbol.class);
        ITSPHPAst caseTrueAst = mock(ITSPHPAst.class);
        when(caseTrueAst.getEvalType()).thenReturn(caseTrueType);
        ITSPHPAst caseFalseAst = mock(ITSPHPAst.class);
        when(caseFalseAst.getEvalType()).thenReturn(caseFalseType);

        ITypeCheckPhaseController controller = createTypeCheckController();
        ITypeSymbol result = controller.resolveTernaryOperatorEvalType(
                mock(ITSPHPAst.class), mock(ITSPHPAst.class), caseTrueAst, caseFalseAst);

        assertThat(result, is(caseTrueType));
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
