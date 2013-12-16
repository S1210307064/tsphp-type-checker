package ch.tutteli.tsphp.typechecker.test.unit.scopes;

import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.typechecker.scopes.AScope;
import ch.tutteli.tsphp.typechecker.scopes.ConditionalScope;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class ConditionalScope_AScope_LSPTest extends AScopeTest
{
    protected AScope createScope(String name, IScope enclosingScope) {
        return new ConditionalScope(scopeHelper, enclosingScope);
    }

    @Override
    @Test
    public void getScopeName_Standard_ReturnName() {
        // different behaviour - returns always "cScope"
        // yet, does not really violate the Liskov Substitution Principle since it returns the name
        // it's just not possible to set the name for a ConditionalScope

        String name = "doesn't matter";

        AScope scope = createScope(name, mock(IScope.class));
        String result = scope.getScopeName();

        assertThat(result, is("cScope"));
    }
}
