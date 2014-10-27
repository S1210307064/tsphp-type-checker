/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.typechecker.test.integration.testutils;

import ch.tsphp.common.IScope;
import ch.tsphp.typechecker.error.ITypeCheckerErrorReporter;
import ch.tsphp.typechecker.scopes.IConditionalScope;
import ch.tsphp.typechecker.scopes.IScopeHelper;

public class TestConditionalScopeFactory extends TestNamespaceScopeFactory
{

    public TestConditionalScopeFactory(IScopeHelper scopeHelper, ITypeCheckerErrorReporter checkerErrorReporter) {
        super(scopeHelper, checkerErrorReporter);
    }

    @Override
    public IConditionalScope createConditionalScope(IScope currentScope) {
        IConditionalScope scope = super.createConditionalScope(currentScope);
        scopes.add(scope);
        return scope;
    }
}
