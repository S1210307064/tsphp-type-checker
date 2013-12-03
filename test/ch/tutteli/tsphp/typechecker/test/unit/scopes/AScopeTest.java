package ch.tutteli.tsphp.typechecker.test.unit.scopes;

import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.scopes.AScope;
import ch.tutteli.tsphp.typechecker.scopes.IScopeHelper;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;


import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AScopeTest
{
    protected IScopeHelper scopeHelper;

    @Before
    public void setUp() {
        scopeHelper = mock(IScopeHelper.class);
    }

    private class DummyScope extends AScope
    {
        public DummyScope(IScopeHelper theScopeHelper, String theScopeName, IScope theEnclosingScope) {
            super(theScopeHelper, theScopeName, theEnclosingScope);
        }

        @Override
        public void define(ISymbol sym) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ISymbol resolve(ITSPHPAst typeAst) {
            throw new UnsupportedOperationException();
        }
    }

    @Test
    public void doubleDefinitionCheck_Standard_DelegateToScopeHelper() {
        ISymbol symbol = mock(ISymbol.class);

        AScope scope = createScope("scope", mock(IScope.class));
        scope.doubleDefinitionCheck(symbol);

        verify(scopeHelper).doubleDefinitionCheck(scope.getSymbols(), symbol);
    }

    @Test
    public void getEnclosingScope_Standard_ReturnEnclosingScope() {
        IScope enclosingScope = mock(IScope.class);

        AScope scope = createScope("scope", enclosingScope);
        IScope result = scope.getEnclosingScope();

        assertThat(result, is(enclosingScope));
    }

    @Test
    public void getScopeName_Standard_ReturnName() {
        String name = "scope";

        AScope scope = createScope(name, mock(IScope.class));
        String result = scope.getScopeName();

        assertThat(result, is(name));
    }

    @Test
    public void getSymbols_NothingDefined_ReturnEmptyMap() {
        //no arrange needed

        AScope scope = createScope("scope", mock(IScope.class));
        Map<String, List<ISymbol>> result = scope.getSymbols();

        assertThat(result.size(), is(0));
    }

    protected AScope createScope(String name, IScope enclosingScope) {
        return new DummyScope(scopeHelper, name, enclosingScope);
    }
}
