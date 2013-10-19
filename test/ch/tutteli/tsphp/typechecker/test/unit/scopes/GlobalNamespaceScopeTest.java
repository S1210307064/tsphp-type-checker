package ch.tutteli.tsphp.typechecker.test.unit.scopes;

import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.scopes.GlobalNamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.IScopeHelper;
import ch.tutteli.tsphp.typechecker.utils.MapHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class GlobalNamespaceScopeTest
{

    public static final String GLOBAL_NAMESPACE_NAME = "\\globalNamespace\\";
    private IScopeHelper scopeHelper;

    @Before
    public void setUp() {
        scopeHelper = mock(IScopeHelper.class);
    }


    @Test
    public void getEnclosingScope_Standard_ReturnNull(){
        //no arrange necessary

        IGlobalNamespaceScope globalNamespaceScope = createGlobalScope();
        IScope scope = globalNamespaceScope.getEnclosingScope();

        assertNull(scope);
    }

    @Test
    public void getScopeName_Standard_ReturnName(){
        //no arrange necessary

        IGlobalNamespaceScope globalNamespaceScope = createGlobalScope();
        String name = globalNamespaceScope.getScopeName();

        assertThat(name, is(GLOBAL_NAMESPACE_NAME));
    }

    @Test
    public void define_Standard_DoesNotInteractWithTheSymbolOtherThanGetName(){
        ISymbol symbol = createSymbol("symbol");

        IGlobalNamespaceScope globalNamespaceScope = createGlobalScope();
        globalNamespaceScope.define(symbol);

        verify(symbol).getName();
        verifyNoMoreInteractions(symbol);
        verify(scopeHelper).define(globalNamespaceScope, symbol);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void doubleDefinitionCheckCaseInsensitive_Standard_DelegateToScopeHelper(){
        ISymbol symbol = createSymbol("symbol");

        IGlobalNamespaceScope globalNamespaceScope = createGlobalScope();
        globalNamespaceScope.doubleDefinitionCheckCaseInsensitive(symbol);

        ArgumentCaptor<ISymbol> argument = ArgumentCaptor.forClass(ISymbol.class);
        verify(scopeHelper).doubleDefinitionCheck(anyMap(),argument.capture());
        assertThat(argument.getValue(), is(symbol));
    }

    @Test
    public void doubleDefinitionCheck_Standard_DelegateToScopeHelper(){
        ISymbol symbol = createSymbol("symbol");

        IGlobalNamespaceScope globalNamespaceScope = createGlobalScope();
        globalNamespaceScope.doubleDefinitionCheck(symbol);

        ArgumentCaptor<ISymbol> argument = ArgumentCaptor.forClass(ISymbol.class);
        verify(scopeHelper).doubleDefinitionCheck(anyMap(), argument.capture());
        assertThat(argument.getValue(), is(symbol));
    }

    @Test
    public void resolve_NothingDefined_ReturnNull(){
        ITSPHPAst ast = createAst("symbol");

        IGlobalNamespaceScope globalNamespaceScope = createGlobalScope();
        ISymbol result = globalNamespaceScope.resolve(ast);

        assertNull(result);
    }

    @Test
    public void resolve_AbsoluteTypeNothingDefined_ReturnNull(){
        ITSPHPAst ast = createAst(GLOBAL_NAMESPACE_NAME + "symbol");

        IGlobalNamespaceScope globalNamespaceScope = createGlobalScope();
        ISymbol result = globalNamespaceScope.resolve(ast);

        assertNull(result);
    }

    @Test
    public void resolve_CaseWrong_ReturnNull(){
        ISymbol symbol = createSymbol("symbol");
        ITSPHPAst ast = createAst("SYMBOL");

        IGlobalNamespaceScope globalNamespaceScope = createGlobalScope();
        addSymbolToScope(globalNamespaceScope, symbol);

        ISymbol result = globalNamespaceScope.resolve(ast);

        assertNull(result);
    }

    @Test
    public void resolve_AbsoluteTypeDifferentNamespace_ReturnNull(){
        ISymbol symbol = createSymbol("symbol");
        ITSPHPAst ast = createAst("\\otherNamespace\\symbol");

        IGlobalNamespaceScope globalNamespaceScope = createGlobalScope();
        addSymbolToScope(globalNamespaceScope, symbol);
        ISymbol result = globalNamespaceScope.resolve(ast);

        assertNull(result);
    }

    @Test
    public void resolve_AbsoluteTypeSubNamespace_ReturnNull(){
        ISymbol symbol = createSymbol("symbol");
        ITSPHPAst ast = createAst(GLOBAL_NAMESPACE_NAME + "a\\symbol");

        IGlobalNamespaceScope globalNamespaceScope = createGlobalScope();
        addSymbolToScope(globalNamespaceScope, symbol);
        ISymbol result = globalNamespaceScope.resolve(ast);

        assertNull(result);
    }

    @Test
    public void resolve_Standard_ReturnSymbol(){
        ISymbol symbol = createSymbol("symbol");
        ITSPHPAst ast = createAst("symbol");

        IGlobalNamespaceScope globalNamespaceScope = createGlobalScope();
        addSymbolToScope(globalNamespaceScope, symbol);
        ISymbol result = globalNamespaceScope.resolve(ast);

        assertThat(result, is(symbol));
    }

    @Test
    public void resolve_AbsoluteType_ReturnSymbol(){
        ISymbol symbol = createSymbol("symbol");
        ITSPHPAst ast = createAst(GLOBAL_NAMESPACE_NAME + "symbol");

        IGlobalNamespaceScope globalNamespaceScope = createGlobalScope();
        addSymbolToScope(globalNamespaceScope, symbol);
        ISymbol result = globalNamespaceScope.resolve(ast);

        assertThat(result, is(symbol));
    }

    private IGlobalNamespaceScope createGlobalScope() {
        return new GlobalNamespaceScope(scopeHelper, GLOBAL_NAMESPACE_NAME);
    }

    private ISymbol createSymbol(String name) {
        ISymbol symbol = mock(ISymbol.class);
        when(symbol.getName()).thenReturn(name);
        return symbol;
    }

    private ITSPHPAst createAst(String name) {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        when(ast.getText()).thenReturn(name);
        return ast;
    }

    private void addSymbolToScope(IGlobalNamespaceScope globalNamespaceScope, ISymbol symbol) {
        MapHelper.addToListMap(globalNamespaceScope.getSymbols(), symbol.getName(), symbol);
    }
}
