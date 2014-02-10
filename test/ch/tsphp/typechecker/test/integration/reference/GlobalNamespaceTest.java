package ch.tsphp.typechecker.test.integration.reference;

import ch.tsphp.common.ITypeSymbol;
import ch.tsphp.typechecker.scopes.GlobalNamespaceScope;
import ch.tsphp.typechecker.scopes.ScopeHelper;
import ch.tsphp.typechecker.symbols.ScalarTypeSymbol;
import ch.tsphp.typechecker.test.integration.testutils.ATest;
import ch.tsphp.typechecker.test.integration.testutils.AstTestHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.Bool;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.Float;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.TypeBool;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.TypeFloat;

public class GlobalNamespaceTest extends ATest
{

    @Test
    public void testResolveLengthLessThanNamespace() {
        GlobalNamespaceScope globalNamespace = createGlobalNamespaceScope("\\a\\b\\c");
        ScalarTypeSymbol symbol = new ScalarTypeSymbol("int", (Set<ITypeSymbol>) null, TypeBool, false,
                Bool, "false");
        globalNamespace.define(symbol);
        Assert.assertEquals(symbol, globalNamespace.resolve(AstTestHelper.getAstWithTokenText("int")));
    }

    @Test
    public void testResolveLengthEqualToNamespace() {
        GlobalNamespaceScope globalNamespace = createGlobalNamespaceScope("\\a\\b\\");
        ScalarTypeSymbol symbol = new ScalarTypeSymbol("float", (Set<ITypeSymbol>) null, TypeFloat, false,
                Float, "0.0");
        globalNamespace.define(symbol);
        Assert.assertEquals(symbol, globalNamespace.resolve(AstTestHelper.getAstWithTokenText("float")));
    }

    @Test
    public void testResolveLengthGreaterThanNamespace() {
        GlobalNamespaceScope globalNamespace = createGlobalNamespaceScope("\\");
        ScalarTypeSymbol symbol = new ScalarTypeSymbol("float", (Set<ITypeSymbol>) null, TypeFloat, false,
                Float, "0.0");
        globalNamespace.define(symbol);
        Assert.assertEquals(symbol, globalNamespace.resolve(AstTestHelper.getAstWithTokenText("float")));
    }

    @Test
    public void testResolveAbsolute() {
        GlobalNamespaceScope globalNamespace = createGlobalNamespaceScope("\\a\\b\\");
        ScalarTypeSymbol symbol = new ScalarTypeSymbol("float", (Set<ITypeSymbol>) null, TypeFloat, false,
                Float, "0.0");
        globalNamespace.define(symbol);
        Assert.assertEquals(symbol, globalNamespace.resolve(AstTestHelper.getAstWithTokenText("\\a\\b\\float")));
    }

    @Test
    public void testResolveNotFound() {
        GlobalNamespaceScope globalNamespace = createGlobalNamespaceScope("\\a\\b\\");
        ScalarTypeSymbol symbol = new ScalarTypeSymbol("float", (Set<ITypeSymbol>) null, TypeFloat, false,
                Float, "0.0");
        globalNamespace.define(symbol);
        Assert.assertNull(globalNamespace.resolve(AstTestHelper.getAstWithTokenText("float2")));
    }

    private GlobalNamespaceScope createGlobalNamespaceScope(String scopeName) {
        return new GlobalNamespaceScope(new ScopeHelper(), scopeName);
    }

}
