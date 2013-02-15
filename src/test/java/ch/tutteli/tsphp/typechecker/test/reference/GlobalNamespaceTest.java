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
package ch.tutteli.tsphp.typechecker.test.reference;

import ch.tutteli.tsphp.typechecker.scopes.GlobalNamespaceScope;
import ch.tutteli.tsphp.typechecker.symbols.ScalarTypeSymbol;
import ch.tutteli.tsphp.typechecker.test.testutils.ATypeCheckerTest;
import ch.tutteli.tsphp.typechecker.test.testutils.AstTestHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class GlobalNamespaceTest extends ATypeCheckerTest
{    
    @Test
    public void testResolveTypeLenghtLessThanNamespace() {
        GlobalNamespaceScope globalNamespace = new GlobalNamespaceScope("\\a\\b\\c");
        ScalarTypeSymbol symbol = new ScalarTypeSymbol("int");
        globalNamespace.define(symbol);
        Assert.assertEquals(symbol, globalNamespace.resolveType(AstTestHelper.getAstWithTokenText("int")));
    }

    @Test
    public void testResolveTypeLenghtEqualToNamespace() {
        GlobalNamespaceScope globalNamespace = new GlobalNamespaceScope("\\a\\b\\");
         ScalarTypeSymbol symbol = new ScalarTypeSymbol("float");
        globalNamespace.define(symbol);
        Assert.assertEquals(symbol,globalNamespace.resolveType(AstTestHelper.getAstWithTokenText("float")));
    }
    
    
    @Test
    public void testResolveTypeLenghtGreaterThanNamespace() {
        GlobalNamespaceScope globalNamespace = new GlobalNamespaceScope("\\");
         ScalarTypeSymbol symbol = new ScalarTypeSymbol("float");
        globalNamespace.define(symbol);
        Assert.assertEquals(symbol,globalNamespace.resolveType(AstTestHelper.getAstWithTokenText("float")));
    }
    
    @Test
    public void testResolveAbsoluteType() {
        GlobalNamespaceScope globalNamespace = new GlobalNamespaceScope("\\a\\b\\");
         ScalarTypeSymbol symbol = new ScalarTypeSymbol("float");
        globalNamespace.define(symbol);
        Assert.assertEquals(symbol,globalNamespace.resolveType(AstTestHelper.getAstWithTokenText("\\a\\b\\float")));
    }
    
    
    @Test
    public void testResolveTypeNotFound() {
        GlobalNamespaceScope globalNamespace = new GlobalNamespaceScope("\\a\\b\\");
         ScalarTypeSymbol symbol = new ScalarTypeSymbol("float");
        globalNamespace.define(symbol);
        Assert.assertNull(globalNamespace.resolveType(AstTestHelper.getAstWithTokenText("float2")));
    }
}
