package ch.tsphp.typechecker.test.unit.scopes;

import ch.tsphp.common.IScope;
import ch.tsphp.typechecker.scopes.AScope;
import ch.tsphp.typechecker.scopes.GlobalNamespaceScope;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

public class GlobalNamespaceScope_AScope_LSPTest extends AScopeTest
{
    protected AScope createScope(String name, IScope enclosingScope) {
        return new GlobalNamespaceScope(scopeHelper, name);
    }

    @Override
    @Test
    public void getEnclosingScope_Standard_ReturnEnclosingScope() {
        // different behaviour - returns always null
        // yet, does not really violate the Liskov Substitution Principle
        // since it returns the enclosing scope (which is always null for a GlobalNamespaceScope)

        IScope enclosingScope = mock(IScope.class);

        AScope scope = createScope("scope", enclosingScope);
        IScope result = scope.getEnclosingScope();

        assertNull(result);
    }
}
