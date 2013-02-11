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
import ch.tutteli.tsphp.typechecker.scopes.INamespaceScope;
import ch.tutteli.tsphp.typechecker.test.utils.ATypeCheckerTest;
import ch.tutteli.tsphp.typechecker.test.utils.ScopeHelper;
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
public class NamespaceTest extends ATypeCheckerTest
{

    String namespaces;

    public NamespaceTest(String testString, String theNamespaces) {
        super(testString);
        namespaces = theNamespaces;
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Override
    protected void verify() {
        Assert.assertEquals(testString + " failed.", namespaces, getNamespacesAsString());
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        String deflt = "global.default.default.";
        String b = "global.b.b.";
        String ab = "global.a\\b.a\\b.";

        return Arrays.asList(new Object[][]{
                    {"int $a=1;", deflt},
                    {"namespace{}", deflt},
                    {"namespace a\\b;", ab},
                    {"namespace a\\b{}", ab},
                    {"namespace{} namespace{}", deflt + " " + deflt},
                    {"namespace b{} namespace b{}", b + " " + b},
                    {"namespace{} namespace b{} namespace a\\b{}", deflt + " " + b + " " + ab},
                    {"namespace{} namespace{}  namespace a\\b{}", deflt + " " + deflt + " " + ab},
                    {"namespace{} namespace b{} namespace{} ", deflt + " " + b + " " + deflt},
                    {"namespace{} namespace{} namespace{} ", deflt + " " + deflt + " " + deflt},
                    {"namespace b{} namespace b{} namespace a\\b{} ", b + " " + b + " " + ab},
                    {"namespace b{} namespace{} namespace b{} ", b + " " + deflt + " " + b},
                    {"namespace b{} namespace b{} namespace b{} ", b + " " + b + " " + b}
                });
    }

    private String getNamespacesAsString() {
        StringBuilder stringBuilder = new StringBuilder();
        boolean isNotFirst = false;
        for (IScope scope : scopeFactory.scopes) {
            if (isNotFirst) {
                stringBuilder.append(" ");
            }
            isNotFirst = true;
            stringBuilder.append(ScopeHelper.getEnclosingScopeNames(scope));

        }
        return stringBuilder.toString();
    }
}
