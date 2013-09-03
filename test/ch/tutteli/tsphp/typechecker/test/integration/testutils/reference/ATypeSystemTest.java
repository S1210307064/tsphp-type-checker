/*
 * Copyright 2013 Robert Stoll <rstoll@tutteli.ch>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package ch.tutteli.tsphp.typechecker.test.integration.testutils.reference;

import ch.tutteli.tsphp.common.AstHelperRegistry;
import ch.tutteli.tsphp.typechecker.IDefiner;
import ch.tutteli.tsphp.typechecker.IOverloadResolver;
import ch.tutteli.tsphp.typechecker.ISymbolResolver;
import ch.tutteli.tsphp.typechecker.ITypeCheckerController;
import ch.tutteli.tsphp.typechecker.ITypeSystem;
import ch.tutteli.tsphp.typechecker.OverloadResolver;
import ch.tutteli.tsphp.typechecker.SymbolResolver;
import ch.tutteli.tsphp.typechecker.TypeCheckerController;
import ch.tutteli.tsphp.typechecker.TypeSystem;
import ch.tutteli.tsphp.typechecker.scopes.IScopeFactory;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.ATest;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.TestDefiner;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.TestScopeFactory;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.TestSymbolFactory;
import ch.tutteli.tsphp.typechecker.utils.AstHelper;
import org.junit.Ignore;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
@Ignore
public class ATypeSystemTest extends ATest
{

    protected IScopeFactory scopeFactory;
    protected ITypeCheckerController controller;

    public ATypeSystemTest() {
        super();
        scopeFactory = new TestScopeFactory();
        TestSymbolFactory symbolFactory = new TestSymbolFactory();
        IDefiner definer = new TestDefiner(symbolFactory, scopeFactory);
        ITypeSystem typeSystem = new TypeSystem(symbolFactory, AstHelperRegistry.get(),
                definer.getGlobalDefaultNamespace());
        ISymbolResolver symbolResolver = new SymbolResolver(symbolFactory, definer.getGlobalNamespaceScopes(),
                definer.getGlobalDefaultNamespace());
        IOverloadResolver methodResolver = new OverloadResolver(typeSystem);

        controller = new TypeCheckerController(
                symbolFactory,
                typeSystem,
                definer,
                symbolResolver,
                methodResolver,
                new AstHelper());
    }
}
