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
package ch.tutteli.tsphp.typechecker.test.definition;

import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.typechecker.test.utils.ATypeCheckerTest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import junit.framework.Assert;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
@RunWith(Parameterized.class)
public class GlobalNamespaceTest extends ATypeCheckerTest
{

    String[] namespaces;

    public GlobalNamespaceTest(String testString, String[] theNamespaces) {
        super(testString);
        namespaces = theNamespaces;
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Override
    protected void verify() {
        Map<String, IScope> globalNamespaceScopes = scopeFactory.getGlobalNamespaceScopes();
        Assert.assertEquals(testString + " failed. size wrong ", namespaces.length, globalNamespaceScopes.size());

        for (String namespace : namespaces) {
            Assert.assertTrue(testString + " failed. Global namespace " + namespace + " did not exists in "
                    + globalNamespaceScopes.keySet(), globalNamespaceScopes.containsKey(namespace));
        }
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                    {"int $a=1;", new String[]{"default"}},
                    {"namespace{}", new String[]{"default"}},
                    {"namespace{} namespace{}", new String[]{"default"}},
                    {"namespace{} namespace b{} namespace a\\b{}", new String[]{"default", "b", "a\\b"}},
                    {"namespace{} namespace{}  namespace a\\b{}", new String[]{"default", "a\\b"}},
                    {"namespace{} namespace b{} namespace{} ", new String[]{"default", "b"}},
                    {"namespace{} namespace{} namespace{} ", new String[]{"default"}},
                    {"namespace a;", new String[]{"a"}}
                });
    }
}
