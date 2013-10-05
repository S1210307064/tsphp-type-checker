package ch.tutteli.tsphp.typechecker.test.unit.scopes;

import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.INamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.NamespaceScope;
import ch.tutteli.tsphp.typechecker.symbols.IAliasSymbol;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class NamespaceScopeTest
{
    @Test
    public void define_Standard_DelegateToEnclosingScopeAndSetDefinitionScope() {
        IGlobalNamespaceScope globalNamespaceScope = mock(IGlobalNamespaceScope.class);
        ISymbol symbol = mock(ISymbol.class);

        INamespaceScope namespaceScope = new NamespaceScope("test", globalNamespaceScope);
        namespaceScope.define(symbol);

        verify(globalNamespaceScope).define(symbol);
        verify(symbol).setDefinitionScope(namespaceScope);
    }

    @Test
    public void define_TwoSymbolsWithSameName_DelegateToEnclosingScopeAndSetDefinitionScopeForBoth() {
        IGlobalNamespaceScope globalNamespaceScope = mock(IGlobalNamespaceScope.class);
        ISymbol symbol = mock(ISymbol.class);
        when(symbol.getName()).thenReturn("a");
        ISymbol symbol2 = mock(ISymbol.class);
        when(symbol2.getName()).thenReturn("a");

        INamespaceScope namespaceScope = new NamespaceScope("test", globalNamespaceScope);
        namespaceScope.define(symbol);
        namespaceScope.define(symbol2);

        verify(globalNamespaceScope).define(symbol);
        verify(symbol).setDefinitionScope(namespaceScope);
        verify(globalNamespaceScope).define(symbol2);
        verify(symbol2).setDefinitionScope(namespaceScope);
    }

    @Test
    public void resolve_Standard_DelegateToEnclosingScope() {
        IGlobalNamespaceScope globalNamespaceScope = mock(IGlobalNamespaceScope.class);
        ITSPHPAst ast = mock(ITSPHPAst.class);

        INamespaceScope namespaceScope = new NamespaceScope("test", globalNamespaceScope);
        namespaceScope.resolve(ast);

        verify(globalNamespaceScope).resolve(ast);
    }

    @Test
    public void doubleDefinitionCheck_standard_DelegateToEnclosingScope() {
        IGlobalNamespaceScope globalNamespaceScope = mock(IGlobalNamespaceScope.class);
        ISymbol symbol = mock(ISymbol.class);

        INamespaceScope namespaceScope = new NamespaceScope("test", globalNamespaceScope);
        namespaceScope.doubleDefinitionCheck(symbol);

        verify(globalNamespaceScope).doubleDefinitionCheck(symbol);
    }

    @Test
    public void doubleDefinitionCheckCaseInsensitive_standard_DelegateToEnclosingScope() {
        IGlobalNamespaceScope globalNamespaceScope = mock(IGlobalNamespaceScope.class);
        ISymbol symbol = mock(ISymbol.class);

        INamespaceScope namespaceScope = new NamespaceScope("test", globalNamespaceScope);
        namespaceScope.doubleDefinitionCheckCaseInsensitive(symbol);

        verify(globalNamespaceScope).doubleDefinitionCheckCaseInsensitive(symbol);
    }


    @Test
    public void defineUse_standard_SetDefinitionScope() {
        IAliasSymbol aliasSymbol = createAliasSymbol("aliasName");

        INamespaceScope namespaceScope = createNamespaceScope();
        namespaceScope.defineUse(aliasSymbol);

        verify(aliasSymbol).setDefinitionScope(namespaceScope);
    }


    @Test
    public void getUse_NothingDefined_ReturnsNull() {
        //no arrange needed

        INamespaceScope namespaceScope = createNamespaceScope();
        List<IAliasSymbol> symbols = namespaceScope.getUse("nonExistingAlias");

        assertNull(symbols);
    }


    @Test
    public void getCaseInsensitiveFirstUseDefinitionAst_NotDefined_ReturnsNull() {
        //no arrange needed

        INamespaceScope namespaceScope = createNamespaceScope();
        ITSPHPAst ast = namespaceScope.getCaseInsensitiveFirstUseDefinitionAst("nonExistingAlias");

        assertNull(ast);
    }

    @Test
    public void getUse_WrongName_ReturnsNull() {
        IAliasSymbol aliasSymbol = createAliasSymbol("aliasName");

        INamespaceScope namespaceScope = createNamespaceScope();
        namespaceScope.defineUse(aliasSymbol);
        List<IAliasSymbol> symbols = namespaceScope.getUse("notExistingAlias");

        assertNull(symbols);
    }


    @Test
    public void getCaseInsensitiveFirstUseDefinitionAst_WrongName_ReturnsNull() {
        IAliasSymbol aliasSymbol = createAliasSymbol("aliasName");

        INamespaceScope namespaceScope = createNamespaceScope();
        namespaceScope.defineUse(aliasSymbol);
        ITSPHPAst ast = namespaceScope.getCaseInsensitiveFirstUseDefinitionAst("nonExistingAlias");

        assertNull(ast);
    }


    @Test
    public void getUse_CaseWrong_ReturnsNull() {
        IAliasSymbol aliasSymbol = createAliasSymbol("aliasName");

        INamespaceScope namespaceScope = createNamespaceScope();
        namespaceScope.defineUse(aliasSymbol);
        List<IAliasSymbol> symbols = namespaceScope.getUse("ALIASName");

        assertNull(symbols);
    }

    @Test
    public void getCaseInsensitiveFirstUseDefinitionAst_CaseWrong_ReturnsAst() {
        ITSPHPAst expectedAst = mock(ITSPHPAst.class);
        IAliasSymbol aliasSymbol = createAliasSymbol("aliasName", expectedAst);

        INamespaceScope namespaceScope = createNamespaceScope();
        namespaceScope.defineUse(aliasSymbol);
        ITSPHPAst ast = namespaceScope.getCaseInsensitiveFirstUseDefinitionAst("ALIASName");

        assertThat(ast, is(expectedAst));
    }


    @Test
    public void getUse_OneDefined_ReturnsListWithOne() {
        IAliasSymbol aliasSymbol = createAliasSymbol("aliasName");

        INamespaceScope namespaceScope = createNamespaceScope();
        namespaceScope.defineUse(aliasSymbol);
        List<IAliasSymbol> symbols = namespaceScope.getUse("aliasName");

        assertThat(symbols.size(), is(1));
        assertThat(symbols, hasItem(aliasSymbol));
    }


    @Test
    public void getCaseInsensitiveFirstUseDefinitionAst_OneDefined_ReturnsAst() {
        ITSPHPAst expectedAst = mock(ITSPHPAst.class);
        IAliasSymbol aliasSymbol = createAliasSymbol("aliasName", expectedAst);

        INamespaceScope namespaceScope = createNamespaceScope();
        namespaceScope.defineUse(aliasSymbol);
        ITSPHPAst ast = namespaceScope.getCaseInsensitiveFirstUseDefinitionAst("aliasName");

        assertThat(ast, is(expectedAst));
    }

    @Test
    public void getUse_TwoDefined_ReturnsListWithTwo() {
        IAliasSymbol aliasSymbol1 = createAliasSymbol("aliasName");
        IAliasSymbol aliasSymbol2 = createAliasSymbol("aliasName");

        INamespaceScope namespaceScope = createNamespaceScope();
        namespaceScope.defineUse(aliasSymbol1);
        namespaceScope.defineUse(aliasSymbol2);
        List<IAliasSymbol> symbols = namespaceScope.getUse("aliasName");

        assertThat(symbols.size(), is(2));
        assertThat(symbols, hasItems(aliasSymbol1, aliasSymbol2));
    }

    @Test
    public void getCaseInsensitiveFirstUseDefinitionAst_TwoDefined_ReturnsFirstAst() {
        ITSPHPAst expectedAst = mock(ITSPHPAst.class);
        IAliasSymbol aliasSymbol = createAliasSymbol("aliasName", expectedAst);
        ITSPHPAst notThisAst = mock(ITSPHPAst.class);
        IAliasSymbol aliasSymbol2 = createAliasSymbol("aliasName", notThisAst);

        INamespaceScope namespaceScope = createNamespaceScope();
        namespaceScope.defineUse(aliasSymbol);
        namespaceScope.defineUse(aliasSymbol2);
        ITSPHPAst ast = namespaceScope.getCaseInsensitiveFirstUseDefinitionAst("aliasName");

        assertThat(ast, is(expectedAst));
    }

    @Test
    @Ignore //TODO rstoll TSPHP-604 remove hidden dependency ScopeHelper
    public void useDefinitionCheck_NothingDefined_ReturnsTrue() {
        IAliasSymbol symbol = createAliasSymbol("aliasName");

        INamespaceScope namespaceScope = createNamespaceScope();
        boolean result = namespaceScope.useDefinitionCheck(symbol);

        assertTrue(result);
    }

    private IAliasSymbol createAliasSymbol(String name) {
        IAliasSymbol aliasSymbol = mock(IAliasSymbol.class);
        when(aliasSymbol.getName()).thenReturn(name);
        return aliasSymbol;
    }

    private IAliasSymbol createAliasSymbol(String name, ITSPHPAst ast) {
        IAliasSymbol aliasSymbol = createAliasSymbol(name);
        when(aliasSymbol.getDefinitionAst()).thenReturn(ast);
        return aliasSymbol;
    }

    private INamespaceScope createNamespaceScope() {
        IGlobalNamespaceScope globalNamespaceScope = mock(IGlobalNamespaceScope.class);
        return new NamespaceScope("test", globalNamespaceScope);
    }
}
