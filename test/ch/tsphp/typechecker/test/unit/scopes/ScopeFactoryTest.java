package ch.tsphp.typechecker.test.unit.scopes;

import ch.tsphp.common.IScope;
import ch.tsphp.typechecker.error.ITypeCheckerErrorReporter;
import ch.tsphp.typechecker.scopes.IConditionalScope;
import ch.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tsphp.typechecker.scopes.INamespaceScope;
import ch.tsphp.typechecker.scopes.IScopeFactory;
import ch.tsphp.typechecker.scopes.IScopeHelper;
import ch.tsphp.typechecker.scopes.ScopeFactory;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class ScopeFactoryTest
{
    public static final String SCOPE_NAME = "scopeName";
    private IScopeHelper scopeHelper;

    @Before
    public void setUp() {
        scopeHelper = mock(IScopeHelper.class);
    }

    @Test
    public void createGlobalNamespaceScope_GetScopeNameAfterwards_ReturnName() {
        //no arrange needed

        IScopeFactory scopeFactory = createScopeFactory();
        IGlobalNamespaceScope result = scopeFactory.createGlobalNamespaceScope(SCOPE_NAME);

        assertThat(result.getScopeName(), is(SCOPE_NAME));
    }

    @Test
    public void createConditionalScope_GetEnclosingScopeAfterwards_ReturnPassedScope() {
        INamespaceScope namespaceScope = mock(INamespaceScope.class);

        IScopeFactory scopeFactory = createScopeFactory();
        IConditionalScope result = scopeFactory.createConditionalScope(namespaceScope);

        assertThat(result.getEnclosingScope(), is((IScope) namespaceScope));
    }

    @Test
    public void createNamespaceScope_GetEnclosingScopeAfterwards_ReturnPassedScope() {
        IGlobalNamespaceScope globalNamespaceScope = mock(IGlobalNamespaceScope.class);

        IScopeFactory scopeFactory = createScopeFactory();
        INamespaceScope result = scopeFactory.createNamespaceScope(SCOPE_NAME, globalNamespaceScope);

        assertThat(result.getEnclosingScope(), is((IScope) globalNamespaceScope));
    }

    @Test
    public void createNamespaceScope_GetScopeNameAfterwards_ReturnName() {
        IGlobalNamespaceScope globalNamespaceScope = mock(IGlobalNamespaceScope.class);

        IScopeFactory scopeFactory = createScopeFactory();
        INamespaceScope result = scopeFactory.createNamespaceScope(SCOPE_NAME, globalNamespaceScope);

        assertThat(result.getScopeName(), is(SCOPE_NAME));
    }

    @Test
    public void createGlobalNamespaceScope_Twice_ReturnToDifferentObjects() {
        //no arrange needed

        IScopeFactory scopeFactory = createScopeFactory();
        IGlobalNamespaceScope result = scopeFactory.createGlobalNamespaceScope(SCOPE_NAME);
        IGlobalNamespaceScope result2 = scopeFactory.createGlobalNamespaceScope(SCOPE_NAME);

        assertThat(result, not(result2));
    }

    @Test
    public void createConditionalScope_Twice_ReturnToDifferentObjects() {
        INamespaceScope namespaceScope = mock(INamespaceScope.class);

        IScopeFactory scopeFactory = createScopeFactory();
        IConditionalScope result = scopeFactory.createConditionalScope(namespaceScope);
        IConditionalScope result2 = scopeFactory.createConditionalScope(namespaceScope);

        assertThat(result, not(result2));
    }

    @Test
    public void createNamespaceScope_Twice_ReturnToDifferentObjects() {
        IGlobalNamespaceScope globalNamespaceScope = mock(IGlobalNamespaceScope.class);

        IScopeFactory scopeFactory = createScopeFactory();
        INamespaceScope result = scopeFactory.createNamespaceScope(SCOPE_NAME, globalNamespaceScope);
        INamespaceScope result2 = scopeFactory.createNamespaceScope(SCOPE_NAME, globalNamespaceScope);

        assertThat(result, not(result2));
    }

    private IScopeFactory createScopeFactory() {
        return new ScopeFactory(scopeHelper, mock(ITypeCheckerErrorReporter.class));
    }

}
