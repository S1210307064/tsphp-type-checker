/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.reference;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.common.symbols.ITypeSymbol;
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
    public void testResolveExistingType_ReturnExisting() {
        INamespaceScope scope = definitionPhaseController.defineNamespace("\\");
        ISymbol typeSymbol = definitionPhaseController.getGlobalNamespaceScopes().get("\\").getSymbols()
                .get(type).get(0);
        ITSPHPAst ast = AstTestHelper.getAstWithTokenText(type, scope);

        ITypeSymbol result = referencePhaseController.resolvePrimitiveType(ast, null);

        assertThat(result, is(typeSymbol));
    }

    @Test
    public void testResolveExistingTypeFromOtherNamespace_ReturnExisting() {
        INamespaceScope scope = definitionPhaseController.defineNamespace("\\a\\a\\");
        ISymbol typeSymbol = definitionPhaseController.getGlobalNamespaceScopes().get("\\").getSymbols()
                .get(type).get(0);
        ITSPHPAst ast = AstTestHelper.getAstWithTokenText(type, scope);

        ITypeSymbol result = referencePhaseController.resolvePrimitiveType(ast, null);

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
