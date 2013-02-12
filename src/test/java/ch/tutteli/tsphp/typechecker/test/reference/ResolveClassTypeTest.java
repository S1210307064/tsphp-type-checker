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

import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.common.TSPHPAst;
import ch.tutteli.tsphp.typechecker.TSPHPErroneusTypeSymbol;
import ch.tutteli.tsphp.typechecker.scopes.INamespaceScope;
import ch.tutteli.tsphp.typechecker.symbols.AliasSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IClassSymbol;
import ch.tutteli.tsphp.typechecker.test.testutils.ASymbolTableTest;
import ch.tutteli.tsphp.typechecker.test.testutils.AstHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class ResolveClassTypeTest extends ASymbolTableTest
{

    @Test
    public void testNotFound() {
        INamespaceScope scope = symbolTable.defineNamespace("\\");
        TSPHPAst ast = AstHelper.getAstWithTokenText("NotDefinedType", scope);
        ITypeSymbol typeSymbol = symbolTable.resolveType(ast);
        Assert.assertTrue(typeSymbol instanceof TSPHPErroneusTypeSymbol);
        TSPHPErroneusTypeSymbol errorSymbol = (TSPHPErroneusTypeSymbol) typeSymbol;
        Assert.assertEquals(ast, errorSymbol.getDefinitionAst());
    }

    @Test
    public void testNoFallback() {
        INamespaceScope scope = symbolTable.defineNamespace("\\");
        TSPHPAst ast = AstHelper.getAstWithTokenText("MyClass", scope);
        IClassSymbol classSymbol = symbolTable.defineClass(scope, new TSPHPAst(), ast, new TSPHPAst(), new TSPHPAst());
        Assert.assertEquals(classSymbol, symbolTable.resolveType(ast));

        scope = symbolTable.defineNamespace("\\a\\");
        ast = AstHelper.getAstWithTokenText("MyClass", scope);
        ITypeSymbol typeSymbol = symbolTable.resolveType(ast);
        Assert.assertTrue(typeSymbol instanceof TSPHPErroneusTypeSymbol);
        TSPHPErroneusTypeSymbol errorSymbol = (TSPHPErroneusTypeSymbol) typeSymbol;
        Assert.assertEquals(ast, errorSymbol.getDefinitionAst());
    }

    @Test
    public void testAliasTypeNotFound() {
        INamespaceScope scope = symbolTable.defineNamespace("\\");
        TSPHPAst ast = AstHelper.getAstWithTokenText("MyClass", scope);

        AliasSymbol aliasSymbol = new AliasSymbol(ast, "test");
        scope.defineUse(aliasSymbol);

        TSPHPAst ast2 = AstHelper.getAstWithTokenText("test", scope);
        ITypeSymbol typeSymbol = symbolTable.resolveType(ast2);
        Assert.assertTrue(typeSymbol instanceof TSPHPErroneusTypeSymbol);
        TSPHPErroneusTypeSymbol errorSymbol = (TSPHPErroneusTypeSymbol) typeSymbol;
        Assert.assertEquals(ast2, errorSymbol.getDefinitionAst());
    }
}
