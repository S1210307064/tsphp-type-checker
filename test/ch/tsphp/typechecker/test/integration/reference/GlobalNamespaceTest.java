/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.reference;

import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.typechecker.scopes.GlobalNamespaceScope;
import ch.tsphp.typechecker.scopes.ScopeHelper;
import ch.tsphp.typechecker.symbols.ScalarTypeSymbol;
import ch.tsphp.typechecker.test.integration.testutils.ATest;
import ch.tsphp.typechecker.test.integration.testutils.AstTestHelper;
import org.junit.Test;

import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.Bool;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.Float;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.TypeBool;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.TypeFloat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;

public class GlobalNamespaceTest extends ATest
{

    @Test
    public void testResolveLengthLessThanNamespace() {
        ISymbol symbol = new ScalarTypeSymbol("int", null, TypeBool, Bool, "false");

        GlobalNamespaceScope globalNamespace = createGlobalNamespaceScope("\\a\\b\\c");
        globalNamespace.define(symbol);
        ISymbol result = globalNamespace.resolve(AstTestHelper.getAstWithTokenText("int"));

        assertThat(result, is(symbol));
    }

    @Test
    public void testResolveLengthEqualToNamespace() {
        ISymbol symbol = new ScalarTypeSymbol("float", null, TypeFloat, Float, "0.0");

        GlobalNamespaceScope globalNamespace = createGlobalNamespaceScope("\\a\\b\\");
        globalNamespace.define(symbol);
        ISymbol result = globalNamespace.resolve(AstTestHelper.getAstWithTokenText("float"));

        assertThat(result, is(symbol));
    }

    @Test
    public void testResolveLengthGreaterThanNamespace() {
        ISymbol symbol = new ScalarTypeSymbol("float", null, TypeFloat, Float, "0.0");

        GlobalNamespaceScope globalNamespace = createGlobalNamespaceScope("\\");
        globalNamespace.define(symbol);
        ISymbol result = globalNamespace.resolve(AstTestHelper.getAstWithTokenText("float"));

        assertThat(result, is(symbol));
    }

    @Test
    public void testResolveAbsolute() {
        ISymbol symbol = new ScalarTypeSymbol("float", null, TypeFloat, Float, "0.0");

        GlobalNamespaceScope globalNamespace = createGlobalNamespaceScope("\\a\\b\\");
        globalNamespace.define(symbol);
        ISymbol result = globalNamespace.resolve(AstTestHelper.getAstWithTokenText("\\a\\b\\float"));

        assertThat(result, is(symbol));
    }

    @Test
    public void testResolveNotFound() {
        ISymbol symbol = new ScalarTypeSymbol("float", null, TypeFloat, Float, "0.0");

        GlobalNamespaceScope globalNamespace = createGlobalNamespaceScope("\\a\\b\\");
        globalNamespace.define(symbol);

        ISymbol result = globalNamespace.resolve(AstTestHelper.getAstWithTokenText("float2"));

        assertThat(result, is(nullValue()));
    }

    private GlobalNamespaceScope createGlobalNamespaceScope(String scopeName) {
        return new GlobalNamespaceScope(new ScopeHelper(typeCheckErrorReporter), scopeName);
    }

}
