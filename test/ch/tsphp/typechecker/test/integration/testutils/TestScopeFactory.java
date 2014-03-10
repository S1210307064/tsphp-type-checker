/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.testutils;

import ch.tsphp.typechecker.error.ITypeCheckerErrorReporter;
import ch.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tsphp.typechecker.scopes.INamespaceScope;
import ch.tsphp.typechecker.scopes.IScopeHelper;
import ch.tsphp.typechecker.scopes.ScopeFactory;

import java.util.ArrayList;
import java.util.List;

public class TestScopeFactory extends ScopeFactory
{

    public List<INamespaceScope> scopes = new ArrayList<>();

    public TestScopeFactory(IScopeHelper theScopeHelper, ITypeCheckerErrorReporter theTypeCheckerErrorReporter) {
        super(theScopeHelper, theTypeCheckerErrorReporter);
    }

    @Override
    public INamespaceScope createNamespaceScope(String name, IGlobalNamespaceScope currentScope) {
        INamespaceScope scope = super.createNamespaceScope(name, currentScope);
        scopes.add(scope);
        return scope;

    }
}
