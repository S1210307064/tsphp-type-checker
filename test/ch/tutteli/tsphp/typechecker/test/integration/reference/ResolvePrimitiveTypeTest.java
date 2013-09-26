package ch.tutteli.tsphp.typechecker.test.integration.reference;

import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.typechecker.IDefiner;
import ch.tutteli.tsphp.typechecker.scopes.INamespaceScope;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.AstTestHelper;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.TypeHelper;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.reference.ATypeSystemTest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class ResolvePrimitiveTypeTest extends ATypeSystemTest
{

    private String type;

    public ResolvePrimitiveTypeTest(String theType) {
        type = theType;
    }

    @Test
    public void testResolveType() {
        IDefiner definer = controller.getDefiner();
        INamespaceScope scope = definer.defineNamespace("\\");
        ISymbol typeSymbol = definer.getGlobalNamespaceScopes().get("\\").getSymbols().get(type).get(0);
        Assert.assertEquals(typeSymbol, controller.resolvePrimitiveType(AstTestHelper.getAstWithTokenText(type, scope)));
    }

    @Test
    public void testResolveTypeFromOtherNamespace() {
        IDefiner definer = controller.getDefiner();
        INamespaceScope scope = definer.defineNamespace("\\a\\a\\");
        ISymbol typeSymbol = definer.getGlobalNamespaceScopes().get("\\").getSymbols().get(type).get(0);
        Assert.assertEquals(typeSymbol, controller.resolvePrimitiveType(AstTestHelper.getAstWithTokenText(type, scope)));
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();
        List<String> types = TypeHelper.getPrimitiveTypes();
        for (String type : types) {
            collection.add(new Object[]{type});
        }
        return collection;
    }
}
