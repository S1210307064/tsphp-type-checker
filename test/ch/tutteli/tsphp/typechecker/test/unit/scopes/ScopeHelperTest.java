package ch.tutteli.tsphp.typechecker.test.unit.scopes;

import ch.tutteli.tsphp.common.ILowerCaseStringMap;
import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.LowerCaseStringMap;
import ch.tutteli.tsphp.typechecker.error.ITypeCheckErrorReporter;
import ch.tutteli.tsphp.typechecker.error.TypeCheckErrorReporterRegistry;
import ch.tutteli.tsphp.typechecker.scopes.IAlreadyDefinedMethodCaller;
import ch.tutteli.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.INamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.IScopeHelper;
import ch.tutteli.tsphp.typechecker.scopes.ScopeHelper;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class ScopeHelperTest
{

    public static final String SYMBOL_NAME = "symbolName";

    @Test
    public void define_OneSymbol_AddToScopeSymbolsAndSetDefinitionScope() {
        Map<String, List<ISymbol>> symbols = new HashMap<>();
        IScope scope = createScope(symbols);
        ISymbol symbol = createSymbol(SYMBOL_NAME);

        IScopeHelper scopeHelper = new ScopeHelper();
        scopeHelper.define(scope, symbol);

        assertThat(symbols, hasKey(SYMBOL_NAME));
        List<ISymbol> definedSymbols = symbols.get(SYMBOL_NAME);
        assertThat(definedSymbols.size(), is(1));
        assertThat(definedSymbols, hasItem(symbol));

        verify(symbol).setDefinitionScope(scope);
    }

    @Test
    public void define_TwoSymbolsSameName_AddBothToScopeSymbolsAndBothDefinitionScopeAreSet() {
        Map<String, List<ISymbol>> symbols = new HashMap<>();
        IScope scope = createScope(symbols);
        ISymbol symbol1 = createSymbol(SYMBOL_NAME);
        ISymbol symbol2 = createSymbol(SYMBOL_NAME);

        IScopeHelper scopeHelper = new ScopeHelper();
        scopeHelper.define(scope, symbol1);
        scopeHelper.define(scope, symbol2);

        assertThat(symbols, hasKey(SYMBOL_NAME));
        List<ISymbol> definedSymbols = symbols.get(SYMBOL_NAME);
        assertThat(definedSymbols.size(), is(2));
        assertThat(definedSymbols, hasItems(symbol1, symbol2));

        verify(symbol1).setDefinitionScope(scope);
        verify(symbol2).setDefinitionScope(scope);
    }

    @Test
    public void define_TwoSymbolsDifferentName_AddBothToScopeSymbolsAndBothDefinitionScopeAreSet() {
        Map<String, List<ISymbol>> symbols = new HashMap<>();
        IScope scope = createScope(symbols);
        ISymbol symbol1 = createSymbol("symbolName1");
        ISymbol symbol2 = createSymbol("symbolName2");

        IScopeHelper scopeHelper = new ScopeHelper();
        scopeHelper.define(scope, symbol1);
        scopeHelper.define(scope, symbol2);

        assertThat(symbols, hasKey("symbolName1"));
        List<ISymbol> definedSymbols1 = symbols.get("symbolName1");
        assertThat(definedSymbols1.size(), is(1));
        assertThat(definedSymbols1, hasItem(symbol1));
        List<ISymbol> definedSymbols2 = symbols.get("symbolName2");
        assertThat(definedSymbols2.size(), is(1));
        assertThat(definedSymbols2, hasItem(symbol2));

        verify(symbol1).setDefinitionScope(scope);
        verify(symbol2).setDefinitionScope(scope);
    }

    @Test(expected = NullPointerException.class)
    public void doubleDefinitionCheck_NoSymbol_ThrowNullPointer() {
        ISymbol symbol = createSymbol(SYMBOL_NAME);
        Map<String, List<ISymbol>> symbols = new HashMap<>();

        IScopeHelper scopeHelper = new ScopeHelper();
        scopeHelper.checkIsNotDoubleDefinition(symbols, symbol);
    }

    @Test(expected = NullPointerException.class)
    public void doubleDefinitionCheckWithReporter_NoSymbol_ThrowNullPointer() {
        ISymbol symbol = createSymbol(SYMBOL_NAME);
        Map<String, List<ISymbol>> symbols = new HashMap<>();
        IAlreadyDefinedMethodCaller reporter = mock(IAlreadyDefinedMethodCaller.class);

        IScopeHelper scopeHelper = new ScopeHelper();
        scopeHelper.checkIsNotDoubleDefinition(symbols, symbol, reporter);
    }

    @Test(expected = NullPointerException.class)
    public void doubleDefinitionCheck_SymbolNotInSymbols_ThrowNullPointer() {
        ISymbol symbol = createSymbol(SYMBOL_NAME);
        Map<String, List<ISymbol>> symbols = CreateSymbolsForStandardName(symbol);

        ISymbol wrongSymbol = createSymbol("wrongSymbol");

        IScopeHelper scopeHelper = new ScopeHelper();
        scopeHelper.checkIsNotDoubleDefinition(symbols, wrongSymbol);
    }

    @Test(expected = NullPointerException.class)
    public void doubleDefinitionCheckWithReporter_SymbolNotInSymbols_ThrowNullPointer() {
        ISymbol symbol = createSymbol(SYMBOL_NAME);
        Map<String, List<ISymbol>> symbols = CreateSymbolsForStandardName(symbol);
        IAlreadyDefinedMethodCaller reporter = mock(IAlreadyDefinedMethodCaller.class);

        ISymbol wrongSymbol = createSymbol("wrongSymbol");

        IScopeHelper scopeHelper = new ScopeHelper();
        scopeHelper.checkIsNotDoubleDefinition(symbols, wrongSymbol, reporter);
    }

    @Test
    public void doubleDefinitionCheck_DefinedOnce_ReturnTrue() {
        ISymbol symbol = createSymbol(SYMBOL_NAME);
        Map<String, List<ISymbol>> symbols = CreateSymbolsForStandardName(symbol);

        IScopeHelper scopeHelper = new ScopeHelper();
        boolean result = scopeHelper.checkIsNotDoubleDefinition(symbols, symbol);

        assertTrue(result);
    }

    @Test
    public void doubleDefinitionCheckWithReporter_DefinedOnce_ReturnTrue() {
        ISymbol symbol = createSymbol(SYMBOL_NAME);
        Map<String, List<ISymbol>> symbols = CreateSymbolsForStandardName(symbol);
        IAlreadyDefinedMethodCaller reporter = mock(IAlreadyDefinedMethodCaller.class);

        IScopeHelper scopeHelper = new ScopeHelper();
        boolean result = scopeHelper.checkIsNotDoubleDefinition(symbols, symbol, reporter);

        assertTrue(result);
        verifyNoMoreInteractions(reporter);
    }

    @Test
    public void doubleDefinitionCheck_DefinedTwiceCheckingFirst_ReturnTrue() {
        ISymbol symbol1 = createSymbol(SYMBOL_NAME);
        ISymbol symbol2 = createSymbol(SYMBOL_NAME);
        Map<String, List<ISymbol>> symbols = CreateSymbolsForStandardName(symbol1, symbol2);

        IScopeHelper scopeHelper = new ScopeHelper();
        boolean result = scopeHelper.checkIsNotDoubleDefinition(symbols, symbol1);

        assertTrue(result);
    }

    @Test
    public void doubleDefinitionCheckWithReporter_DefinedTwiceCheckingFirst_ReturnTrue() {
        ISymbol symbol1 = createSymbol(SYMBOL_NAME);
        ISymbol symbol2 = createSymbol(SYMBOL_NAME);
        Map<String, List<ISymbol>> symbols = CreateSymbolsForStandardName(symbol1, symbol2);
        IAlreadyDefinedMethodCaller reporter = mock(IAlreadyDefinedMethodCaller.class);

        IScopeHelper scopeHelper = new ScopeHelper();
        boolean result = scopeHelper.checkIsNotDoubleDefinition(symbols, symbol1, reporter);

        assertTrue(result);
        verifyNoMoreInteractions(reporter);
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void doubleDefinitionCheck_DefinedTwiceCheckingSecond_ReturnFalse() {
        ITypeCheckErrorReporter errorReporter = mock(ITypeCheckErrorReporter.class);
        TypeCheckErrorReporterRegistry.set(errorReporter);
        ISymbol symbol1 = createSymbol(SYMBOL_NAME);
        ISymbol symbol2 = createSymbol(SYMBOL_NAME);
        Map<String, List<ISymbol>> symbols = CreateSymbolsForStandardName(symbol1, symbol2);

        IScopeHelper scopeHelper = new ScopeHelper();
        boolean result = scopeHelper.checkIsNotDoubleDefinition(symbols, symbol2);

        assertFalse(result);
        verify(errorReporter).alreadyDefined(symbol1, symbol2);
    }

    @Test
    public void doubleDefinitionCheckWithReport_DefinedTwiceCheckingSecond_ReturnFalse() {
        ISymbol symbol1 = createSymbol(SYMBOL_NAME);
        ISymbol symbol2 = createSymbol(SYMBOL_NAME);
        Map<String, List<ISymbol>> symbols = CreateSymbolsForStandardName(symbol1, symbol2);
        IAlreadyDefinedMethodCaller reporter = mock(IAlreadyDefinedMethodCaller.class);

        IScopeHelper scopeHelper = new ScopeHelper();
        boolean result = scopeHelper.checkIsNotDoubleDefinition(symbols, symbol2, reporter);

        assertFalse(result);
        verify(reporter).callAccordingAlreadyDefinedMethod(symbol1, symbol2);
    }

    @Test
    public void getCorrespondingGlobalNamespace_GlobalNamespaceNotInMap_ReturnNull() {
        ILowerCaseStringMap<IGlobalNamespaceScope> scopes = new LowerCaseStringMap<>();

        IScopeHelper scopeHelper = new ScopeHelper();
        IGlobalNamespaceScope result = scopeHelper.getCorrespondingGlobalNamespace(scopes, "\\notExistingGlobalScope");

        assertNull(result);
    }

    @Test
    public void getCorrespondingGlobalNamespace_NamespaceInMapButNotSubNamespace_ReturnNull() {
        ILowerCaseStringMap<IGlobalNamespaceScope> scopes = new LowerCaseStringMap<>();
        IGlobalNamespaceScope globalNamespaceScope = mock(IGlobalNamespaceScope.class);
        scopes.put("\\a\\", globalNamespaceScope);

        IScopeHelper scopeHelper = new ScopeHelper();
        IGlobalNamespaceScope result = scopeHelper.getCorrespondingGlobalNamespace(scopes, "\\a\\b\\");

        assertNull(result);
    }


    @Test
    public void getCorrespondingGlobalNamespace_NamespaceName_ReturnCorrespondingGlobalNamespace() {
        ILowerCaseStringMap<IGlobalNamespaceScope> scopes = new LowerCaseStringMap<>();
        IGlobalNamespaceScope globalNamespaceScope = mock(IGlobalNamespaceScope.class);
        scopes.put("\\a\\", globalNamespaceScope);

        IScopeHelper scopeHelper = new ScopeHelper();
        IGlobalNamespaceScope result = scopeHelper.getCorrespondingGlobalNamespace(scopes, "\\a\\");

        assertThat(result, is(globalNamespaceScope));
    }

    @Test
    public void getCorrespondingGlobalNamespace_SubNamespaceName_ReturnCorrespondingGlobalNamespace() {
        ILowerCaseStringMap<IGlobalNamespaceScope> scopes = new LowerCaseStringMap<>();
        IGlobalNamespaceScope globalNamespaceScope = mock(IGlobalNamespaceScope.class);
        scopes.put("\\a\\b\\", globalNamespaceScope);

        IScopeHelper scopeHelper = new ScopeHelper();
        IGlobalNamespaceScope result = scopeHelper.getCorrespondingGlobalNamespace(scopes, "\\a\\b\\");

        assertThat(result, is(globalNamespaceScope));
    }

    @Test
    public void resolve_NotInScope_ReturnNull() {
        IScope scope = createScope(new HashMap<String, List<ISymbol>>());
        ITSPHPAst ast = createAst("astText");

        IScopeHelper scopeHelper = new ScopeHelper();
        ISymbol result = scopeHelper.resolve(scope, ast);

        assertNull(result);
    }

    @Test
    public void resolve_WrongCaseUseCaseSensitiveMap_ReturnNull() {
        Map<String, List<ISymbol>> symbols = CreateSymbolsForStandardName(createSymbol("AstText"));
        IScope scope = createScope(symbols);
        ITSPHPAst ast = createAst("astText");

        IScopeHelper scopeHelper = new ScopeHelper();
        ISymbol result = scopeHelper.resolve(scope, ast);

        assertNull(result);
    }

    @Test
    public void resolve_DefinedInScope_ReturnCorrespondingSymbol() {
        ISymbol symbol = createSymbol(SYMBOL_NAME);
        Map<String, List<ISymbol>> symbols = CreateSymbolsForStandardName(symbol);
        IScope scope = createScope(symbols);
        ITSPHPAst ast = createAst(SYMBOL_NAME);

        IScopeHelper scopeHelper = new ScopeHelper();
        ISymbol result = scopeHelper.resolve(scope, ast);

        assertThat(result, is(symbol));
    }

    @Test
    public void getEnclosingNamespaceScope_InNamespace_ReturnNamespace() {
        INamespaceScope namespaceScope = mock(INamespaceScope.class);
        ITSPHPAst ast = createAst(SYMBOL_NAME);
        when(ast.getScope()).thenReturn(namespaceScope);

        IScopeHelper scopeHelper = new ScopeHelper();
        IScope result = scopeHelper.getEnclosingNamespaceScope(ast);

        assertThat(result, is((IScope) namespaceScope));
    }

    @Test
    public void getEnclosingNamespaceScope_InMethod_ReturnNamespace() {
        INamespaceScope namespaceScope = mock(INamespaceScope.class);
        ITSPHPAst ast = createAst(SYMBOL_NAME);
        IMethodSymbol methodSymbol = mock(IMethodSymbol.class);
        when(methodSymbol.getEnclosingScope()).thenReturn(namespaceScope);
        when(ast.getScope()).thenReturn(methodSymbol);

        IScopeHelper scopeHelper = new ScopeHelper();
        IScope result = scopeHelper.getEnclosingNamespaceScope(ast);

        assertThat(result, is((IScope) namespaceScope));
    }

    @Test
    public void getEnclosingNamespaceScope_AstHasNoScope_ReturnNull() {
        ITSPHPAst ast = createAst(SYMBOL_NAME);

        IScopeHelper scopeHelper = new ScopeHelper();
        IScope result = scopeHelper.getEnclosingNamespaceScope(ast);

        assertNull(result);
    }

    @Test
    public void getEnclosingNamespaceScope_InMethodHasNoScope_ReturnNull() {
        ITSPHPAst ast = createAst(SYMBOL_NAME);
        IMethodSymbol methodSymbol = mock(IMethodSymbol.class);
        when(methodSymbol.getEnclosingScope()).thenReturn(null);
        when(ast.getScope()).thenReturn(methodSymbol);

        IScopeHelper scopeHelper = new ScopeHelper();
        IScope result = scopeHelper.getEnclosingNamespaceScope(ast);

        assertNull(result);
    }

    private Map<String, List<ISymbol>> CreateSymbolsForStandardName(ISymbol... symbols) {
        Map<String, List<ISymbol>> map = new HashMap<>();
        List<ISymbol> definedSymbols = new ArrayList<>();
        definedSymbols.addAll(Arrays.asList(symbols));
        map.put(SYMBOL_NAME, definedSymbols);

        return map;
    }


    private IScope createScope(Map<String, List<ISymbol>> symbols) {
        IScope scope = mock(IScope.class);
        when(scope.getSymbols()).thenReturn(symbols);
        return scope;
    }

    private ISymbol createSymbol(String symbolName) {
        ISymbol symbol = mock(ISymbol.class);
        when(symbol.getName()).thenReturn(symbolName);
        return symbol;
    }

    private ITSPHPAst createAst(String text) {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        when(ast.getText()).thenReturn(text);
        return ast;
    }

}
