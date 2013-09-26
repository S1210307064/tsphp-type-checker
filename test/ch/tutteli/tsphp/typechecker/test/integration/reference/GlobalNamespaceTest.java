package ch.tutteli.tsphp.typechecker.test.integration.reference;

import ch.tutteli.tsphp.common.ITypeSymbol;
import static ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker.*;
import ch.tutteli.tsphp.typechecker.scopes.GlobalNamespaceScope;
import ch.tutteli.tsphp.typechecker.symbols.ScalarTypeSymbol;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.ATest;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.AstTestHelper;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

public class GlobalNamespaceTest extends ATest
{

    @Test
    public void testResolveLenghtLessThanNamespace() {
        GlobalNamespaceScope globalNamespace = new GlobalNamespaceScope("\\a\\b\\c");
        ScalarTypeSymbol symbol = new ScalarTypeSymbol("int", (Set<ITypeSymbol>) null, TypeBool, false,
                Bool, "false");
        globalNamespace.define(symbol);
        Assert.assertEquals(symbol, globalNamespace.resolve(AstTestHelper.getAstWithTokenText("int")));
    }

    @Test
    public void testResolveLenghtEqualToNamespace() {
        GlobalNamespaceScope globalNamespace = new GlobalNamespaceScope("\\a\\b\\");
        ScalarTypeSymbol symbol = new ScalarTypeSymbol("float", (Set<ITypeSymbol>) null, TypeFloat, false,
                Float, "0.0");
        globalNamespace.define(symbol);
        Assert.assertEquals(symbol, globalNamespace.resolve(AstTestHelper.getAstWithTokenText("float")));
    }

    @Test
    public void testResolveLenghtGreaterThanNamespace() {
        GlobalNamespaceScope globalNamespace = new GlobalNamespaceScope("\\");
        ScalarTypeSymbol symbol = new ScalarTypeSymbol("float", (Set<ITypeSymbol>) null, TypeFloat, false,
                Float, "0.0");
        globalNamespace.define(symbol);
        Assert.assertEquals(symbol, globalNamespace.resolve(AstTestHelper.getAstWithTokenText("float")));
    }

    @Test
    public void testResolveAbsolute() {
        GlobalNamespaceScope globalNamespace = new GlobalNamespaceScope("\\a\\b\\");
        ScalarTypeSymbol symbol = new ScalarTypeSymbol("float", (Set<ITypeSymbol>) null, TypeFloat, false,
                Float, "0.0");
        globalNamespace.define(symbol);
        Assert.assertEquals(symbol, globalNamespace.resolve(AstTestHelper.getAstWithTokenText("\\a\\b\\float")));
    }

    @Test
    public void testResolveNotFound() {
        GlobalNamespaceScope globalNamespace = new GlobalNamespaceScope("\\a\\b\\");
        ScalarTypeSymbol symbol = new ScalarTypeSymbol("float", (Set<ITypeSymbol>) null, TypeFloat, false,
                Float, "0.0");
        globalNamespace.define(symbol);
        Assert.assertNull(globalNamespace.resolve(AstTestHelper.getAstWithTokenText("float2")));
    }
}
