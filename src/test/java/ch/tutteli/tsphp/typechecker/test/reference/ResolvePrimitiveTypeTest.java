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

import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.typechecker.IDefiner;
import ch.tutteli.tsphp.typechecker.scopes.INamespaceScope;
import ch.tutteli.tsphp.typechecker.test.testutils.AstTestHelper;
import ch.tutteli.tsphp.typechecker.test.testutils.TypeHelper;
import ch.tutteli.tsphp.typechecker.test.testutils.reference.ATypeSystemTest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
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
