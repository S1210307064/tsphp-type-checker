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

    @Override
    @Test
    public void doubleDefinitionCheck_Standard_DelegateToScopeHelper() {
        // different behaviour - check takes into account, that definitions could have been defined in the
        // enclosing scope already and since conditional scopes aren't real scopes
        // (see http://tsphp.tutteli.ch/wiki/display/TSPHP/Variable+Scope#VariableScope-ConditionalScopes)
        // this would be a double definition as well.
        // please have a look at the tests ConditionalScopeTest#doubleDefinitionCheck_*

        // This behaviour breaks the LSP on purpose
    }
}
