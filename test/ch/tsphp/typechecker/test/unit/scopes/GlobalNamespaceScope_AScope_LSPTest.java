/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.unit.scopes;

import ch.tsphp.common.IScope;
import ch.tsphp.typechecker.scopes.AScope;
import ch.tsphp.typechecker.scopes.GlobalNamespaceScope;
import ch.tsphp.typechecker.scopes.IScopeHelper;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

public class GlobalNamespaceScope_AScope_LSPTest extends AScopeTest
{
    @Override
    protected AScope createScope(IScopeHelper scopeHelper, String name, IScope enclosingScope) {
        return new GlobalNamespaceScope(scopeHelper, name);
    }

    @Override
    @Test
    public void getEnclosingScope_Standard_ReturnsScopePassedInConstructor() {
        // different behaviour - returns always null
        // yet, does not really violate the Liskov Substitution Principle
        // since it returns the enclosing scope (which is always null for a GlobalNamespaceScope)

        IScope enclosingScope = mock(IScope.class);

        AScope scope = createScope(enclosingScope);
        IScope result = scope.getEnclosingScope();

        assertNull(result);
    }
}
