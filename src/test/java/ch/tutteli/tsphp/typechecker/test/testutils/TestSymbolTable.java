/*
 * Copyright 2012 Robert Stoll <rstoll@tutteli.ch>
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
package ch.tutteli.tsphp.typechecker.test.testutils;

import ch.tutteli.tsphp.common.ITSPHPAstAdaptor;
import ch.tutteli.tsphp.typechecker.IDefiner;
import ch.tutteli.tsphp.typechecker.ISymbolTable;
import ch.tutteli.tsphp.typechecker.SymbolTable;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class TestSymbolTable extends SymbolTable implements ISymbolTable
{

    private IDefiner definer;

    public TestSymbolTable(TestSymbolFactory testSymbolFactory, TestScopeFactory testScopeFactory,
            ITSPHPAstAdaptor astAdaptor) {
        super(testSymbolFactory, testScopeFactory, astAdaptor);
        definer = new TestDefiner(testSymbolFactory, testScopeFactory, astAdaptor, super.getGlobalNamespaceScopes());
    }

    @Override
    public IDefiner getDefiner() {
        return definer;
    }
}
