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
import ch.tutteli.tsphp.typechecker.scopes.INamespaceScope;
import ch.tutteli.tsphp.typechecker.test.testutils.ASymbolTableTest;
import ch.tutteli.tsphp.typechecker.test.testutils.AstHelper;
import ch.tutteli.tsphp.typechecker.test.testutils.TypeHelper;
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
public class ClassTypeTest extends ASymbolTableTest
{

    private String type;

    public ClassTypeTest(String theType) {
        super();
        type = theType;
    }

    @Test
    public void testResolveType() {
        INamespaceScope scope = symbolTable.defineNamespace("\\");
        ISymbol typeSymbol = symbolTable.getGlobalNamespaceScopes().get("\\").getSymbols().get(type).get(0);
        Assert.assertEquals(typeSymbol, symbolTable.resolveType(AstHelper.getAstWithTokenText(type,scope)));
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
