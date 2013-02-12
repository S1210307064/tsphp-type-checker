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
package ch.tutteli.tsphp.typechecker.test.testutils;

import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Ignore;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
@Ignore
public abstract class ATypeCheckerDoubleDefinitionTest extends ATypeCheckerDefinitionTest
{

    protected ScopeTestStruct[] testStructs;
    protected String namespace;
    protected String identifier;
    protected int occurence;

    public ATypeCheckerDoubleDefinitionTest(String testString, String theNamespace, String theIdentifier, int howMany) {
        super(testString);
        namespace = theNamespace;
        identifier = theIdentifier;
        occurence = howMany;
    }

    @Override
    protected void verifyDefinitions() {
        Map<String, IScope> globalNamespaces = symbolTable.getGlobalNamespaceScopes();
        IScope globalNamespace = globalNamespaces.get(namespace);
        Assert.assertNotNull(testString + " failed, global namespace " + namespace + " could not be found.",
                globalNamespace);

        Map<String, List<ISymbol>> symbols = globalNamespace.getSymbols();
        Assert.assertNotNull(testString + " failed, symbols was null.", symbols);
        Assert.assertTrue(testString + " failed. " + identifier + " not found.", symbols.containsKey(identifier));
        Assert.assertEquals(testString + " failed. size was wrong", occurence, symbols.get(identifier).size());
    }

    protected static Collection<Object[]> getDifferentNamespaces(String statements, String identifiers, int occurence) {
        return Arrays.asList(new Object[][]{
                    {statements, "\\", identifiers, occurence},
                    {"namespace b;" + statements, "\\b\\", identifiers, occurence},
                    {"namespace b\\c;" + statements, "\\b\\c\\", identifiers, occurence},
                    {"namespace{" + statements + "}", "\\", identifiers, occurence},
                    {"namespace b{" + statements + "}", "\\b\\", identifiers, occurence},
                    {"namespace b\\c\\e\\R{" + statements + "}", "\\b\\c\\e\\R\\", identifiers, occurence},
                    {"namespace{" + statements + "} namespace{" + statements + "}", "\\", identifiers, occurence * 2},
                    {
                        "namespace b{" + statements + "} namespace b{" + statements + "}",
                        "\\b\\", identifiers, occurence * 2
                    },
                    {
                        "namespace c{" + statements + "} namespace a{" + statements + "} "
                        + "namespace b{" + statements + "}",
                        "\\c\\", identifiers, occurence
                    },
                    {
                        "namespace c{" + statements + "} namespace a{" + statements + "} "
                        + "namespace c{" + statements + "}",
                        "\\c\\", identifiers, 2 * occurence
                    },
                    {
                        "namespace{" + statements + "} namespace {" + statements + "} namespace c{" + statements + "}",
                        "\\", identifiers, 2 * occurence
                    }
                });
    }
}
