package ch.tutteli.tsphp.typechecker.test.unit.scopes;

import ch.tutteli.tsphp.common.ILowerCaseStringMap;
import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.scopes.ConditionalScope;
import ch.tutteli.tsphp.typechecker.scopes.IAlreadyDefinedMethodCaller;
import ch.tutteli.tsphp.typechecker.scopes.IConditionalScope;
import ch.tutteli.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.INamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.IScopeHelper;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class ConditionalScopeTest
{
    private IScopeHelper scopeHelper;

    @Before
    public void setUp() {
        scopeHelper = mock(IScopeHelper.class);
    }

    @Test
    public void define_Standard_DelegateToEnclosingScopeAndSetDefinitionScope() {
        INamespaceScope namespaceScope = mock(INamespaceScope.class);
        ISymbol symbol = mock(ISymbol.class);

        IConditionalScope conditionalScope = createConditionalScope(namespaceScope);
        conditionalScope.define(symbol);

        verify(namespaceScope).define(symbol);
        verify(symbol).setDefinitionScope(conditionalScope);
    }

    @Test
    public void define_TwoSymbolsWithSameName_DelegateToEnclosingScopeAndSetDefinitionScopeForBoth() {
        INamespaceScope namespaceScope = mock(INamespaceScope.class);
        ISymbol symbol = createSymbol("a");
        ISymbol symbol2 = createSymbol("a");

        IConditionalScope conditionalScope = createConditionalScope(namespaceScope);
        conditionalScope.define(symbol);
        conditionalScope.define(symbol2);

        verify(namespaceScope).define(symbol);
        verify(symbol).setDefinitionScope(conditionalScope);
        verify(namespaceScope).define(symbol2);
        verify(symbol2).setDefinitionScope(conditionalScope);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void doubleDefinitionCheck_InNamespace_DelegateToScopeHelperAndUseGlobalNamespace(){
        ILowerCaseStringMap symbols = mock(ILowerCaseStringMap.class);
        IGlobalNamespaceScope globalNamespaceScope = createGlobalNamespaceScope(symbols);
        INamespaceScope namespaceScope = createNamespaceScope(globalNamespaceScope);
        ISymbol symbol = createSymbol("a", createAst(namespaceScope));

        IConditionalScope conditionalScope = createConditionalScope(namespaceScope);
        conditionalScope.doubleDefinitionCheck(symbol);

        verifyScopeWasUsed(globalNamespaceScope, symbols, symbol);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void doubleDefinitionCheck_InConditionalScopeInNamespace_DelegateToScopeHelperAndUseGlobalNamespace(){
        ILowerCaseStringMap symbols = mock(ILowerCaseStringMap.class);
        IGlobalNamespaceScope globalNamespaceScope = createGlobalNamespaceScope(symbols);
        INamespaceScope namespaceScope = createNamespaceScope(globalNamespaceScope);
        IConditionalScope conditionalScopeOuter = createConditionalScope(namespaceScope);
        ISymbol symbol = createSymbol("a", createAst(conditionalScopeOuter));

        IConditionalScope conditionalScope = createConditionalScope(conditionalScopeOuter);
        conditionalScope.doubleDefinitionCheck(symbol);

        verifyScopeWasUsed(globalNamespaceScope, symbols, symbol);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void doubleDefinitionCheck_InMethod_DelegateToScopeHelperAndUseGlobalNamespace(){
        ILowerCaseStringMap symbols = mock(ILowerCaseStringMap.class);
        IMethodSymbol methodSymbol = createMethodSymbol(symbols);
        ISymbol symbol = createSymbol("a", createAst(methodSymbol));

        IConditionalScope conditionalScope = createConditionalScope(methodSymbol);
        conditionalScope.doubleDefinitionCheck(symbol);

        verifyScopeWasUsed(methodSymbol, symbols, symbol);
    }


    @SuppressWarnings("unchecked")
    @Test
    public void doubleDefinitionCheck_InConditionalInMethod_DelegateToScopeHelperAndUseGlobalNamespace(){
        ILowerCaseStringMap symbols = mock(ILowerCaseStringMap.class);
        IMethodSymbol methodSymbol = createMethodSymbol(symbols);
        IConditionalScope conditionalScopeOuter = createConditionalScope(methodSymbol);
        ISymbol symbol = createSymbol("a", createAst(conditionalScopeOuter));

        IConditionalScope conditionalScope = createConditionalScope(conditionalScopeOuter);
        conditionalScope.doubleDefinitionCheck(symbol);

        verifyScopeWasUsed(methodSymbol, symbols, symbol);
    }

    @Test
    public void resolve_InNamespaceScope_DelegateToEnclosingScope(){
        INamespaceScope namespaceScope = mock(INamespaceScope.class);
        ITSPHPAst ast = mock(ITSPHPAst.class);

        IConditionalScope conditionalScope = createConditionalScope(namespaceScope);
        conditionalScope.resolve(ast);

        verify(namespaceScope).resolve(ast);
    }

    @Test
    public void resolve_InMethodSymbol_DelegateToEnclosingScope(){
        IMethodSymbol methodSymbol = mock(IMethodSymbol.class);
        ITSPHPAst ast = mock(ITSPHPAst.class);

        IConditionalScope conditionalScope = createConditionalScope(methodSymbol);
        conditionalScope.resolve(ast);

        verify(methodSymbol).resolve(ast);
    }


    @SuppressWarnings("unchecked")
    private void verifyScopeWasUsed(IScope scope, ILowerCaseStringMap symbols, ISymbol symbol) {
        verify(scope).getSymbols();
        ArgumentCaptor<ILowerCaseStringMap> symbolsArg = ArgumentCaptor.forClass(ILowerCaseStringMap.class);
        ArgumentCaptor<ISymbol> symbolArg = ArgumentCaptor.forClass(ISymbol.class);
        verify(scopeHelper).doubleDefinitionCheck(symbolsArg.capture(),symbolArg.capture(), any(IAlreadyDefinedMethodCaller.class));
        assertThat(symbolsArg.getValue(), is(symbols));
        assertThat(symbolArg.getValue(), is(symbol));
    }

    private INamespaceScope createNamespaceScope(IGlobalNamespaceScope globalNamespaceScope) {
        INamespaceScope namespaceScope = mock(INamespaceScope.class);
        when(namespaceScope.getEnclosingScope()).thenReturn(globalNamespaceScope);
        return namespaceScope;
    }

    private ITSPHPAst createAst(IScope scope) {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        when(ast.getScope()).thenReturn(scope);
        return ast;
    }

    private IConditionalScope createConditionalScope(IScope scope) {
        return new ConditionalScope(scopeHelper, scope);
    }

    @SuppressWarnings("unchecked")
    private IMethodSymbol createMethodSymbol(ILowerCaseStringMap symbols) {
        IMethodSymbol methodSymbol = mock(IMethodSymbol.class);
        when(methodSymbol.getSymbols()).thenReturn(symbols);
        return methodSymbol;
    }

    @SuppressWarnings("unchecked")
    private IGlobalNamespaceScope createGlobalNamespaceScope(ILowerCaseStringMap symbols) {
        IGlobalNamespaceScope globalNamespaceScope = mock(IGlobalNamespaceScope.class);
        when(globalNamespaceScope.getSymbols()).thenReturn(symbols);
        return globalNamespaceScope;
    }

    private ISymbol createSymbol(String name) {
        ISymbol symbol = mock(ISymbol.class);
        when(symbol.getName()).thenReturn(name);
        return symbol;
    }

    private ISymbol createSymbol(String name, ITSPHPAst ast) {
        ISymbol symbol = createSymbol(name);
        when(symbol.getDefinitionAst()).thenReturn(ast);
        return symbol;
    }
}
