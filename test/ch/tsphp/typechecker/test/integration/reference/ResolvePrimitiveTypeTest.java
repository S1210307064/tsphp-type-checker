package ch.tsphp.typechecker.test.integration.reference;

import ch.tsphp.common.ISymbol;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.ITypeSymbol;
import ch.tsphp.typechecker.scopes.INamespaceScope;
import ch.tsphp.typechecker.test.integration.testutils.AstTestHelper;
import ch.tsphp.typechecker.test.integration.testutils.TypeHelper;
import ch.tsphp.typechecker.test.integration.testutils.reference.ATypeSystemTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(Parameterized.class)
public class ResolvePrimitiveTypeTest extends ATypeSystemTest
{

    private String type;

    public ResolvePrimitiveTypeTest(String theType) {
        type = theType;
    }

    @Test
    public void testResolveType() {
        INamespaceScope scope = definer.defineNamespace("\\");
        ISymbol typeSymbol = definer.getGlobalNamespaceScopes().get("\\").getSymbols().get(type).get(0);
        ITSPHPAst ast = AstTestHelper.getAstWithTokenText(type, scope);

        ITypeSymbol result = referencePhaseController.resolvePrimitiveType(ast);

        assertThat(result, is(typeSymbol));
    }

    @Test
    public void testResolveTypeFromOtherNamespace() {
        INamespaceScope scope = definer.defineNamespace("\\a\\a\\");
        ISymbol typeSymbol = definer.getGlobalNamespaceScopes().get("\\").getSymbols().get(type).get(0);
        ITSPHPAst ast = AstTestHelper.getAstWithTokenText(type, scope);

        ITypeSymbol result = referencePhaseController.resolvePrimitiveType(ast);

        assertThat(result, is(typeSymbol));
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
