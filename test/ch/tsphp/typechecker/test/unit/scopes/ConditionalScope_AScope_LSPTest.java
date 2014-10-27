/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.unit.scopes;

import ch.tsphp.common.IScope;
import ch.tsphp.typechecker.error.ITypeCheckerErrorReporter;
import ch.tsphp.typechecker.scopes.AScope;
import ch.tsphp.typechecker.scopes.ConditionalScope;
import ch.tsphp.typechecker.scopes.IScopeHelper;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class ConditionalScope_AScope_LSPTest extends AScopeTest
{
    @Override
    protected AScope createScope(IScopeHelper scopeHelper, String name, IScope enclosingScope) {
        return new ConditionalScope(scopeHelper, enclosingScope, mock(ITypeCheckerErrorReporter.class));
    }

    @Override
    @Test
    public void getScopeName_Standard_ReturnsNamePassedInConstructor() {
        // different behaviour - returns always "cScope"
        // yet, does not really violate the Liskov Substitution Principle since it returns the name
        // it's just not possible to set the name for a ConditionalScope

        String name = "doesn't matter";

        AScope scope = createScope(mock(IScope.class));
        String result = scope.getScopeName();

        assertThat(result, is("cScope"));
    }
}
